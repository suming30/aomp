<template>
  <div class="scripts-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">{{ isZh ? '脚本管理' : 'Script Management' }}</h1>
        <p class="page-subtitle">{{ isZh ? '脚本列表已支持真实接口优先，异常时自动使用演示数据。' : 'Live API first with automatic demo fallback.' }}</p>
      </div>
      <div class="header-actions">
        <button class="action-btn secondary" @click="reloadScripts">
          <span class="material-symbols-outlined">refresh</span>
          {{ isZh ? '刷新' : 'Refresh' }}
        </button>
        <button class="action-btn primary litho-gradient" @click="goToNew">
          <span class="material-symbols-outlined">add</span>
          {{ isZh ? '新建脚本' : 'Create Script' }}
        </button>
      </div>
    </div>

    <div class="toolbar">
      <div class="search-area">
        <span class="material-symbols-outlined search-icon">search</span>
        <input v-model.trim="searchKey" :placeholder="isZh ? '搜索脚本名、描述、创建人' : 'Search script name, description, creator'" @keyup.enter="handleSearch" />
      </div>
      <div class="filter-group">
        <select v-model="filterType" class="filter-select" @change="handleSearch">
          <option value="">{{ isZh ? '全部类型' : 'All Types' }}</option>
          <option value="bash">Bash/Shell</option>
          <option value="python">Python</option>
          <option value="powershell">PowerShell</option>
        </select>
        <label class="private-toggle">
          <input v-model="filterPrivate" type="checkbox" @change="handleSearch" />
          {{ isZh ? '仅私有' : 'Private only' }}
        </label>
      </div>
    </div>

    <div class="script-list" v-loading="loading">
      <div v-for="script in scripts" :key="script.id" class="script-card" @click="goToEditor(script)">
        <div class="card-left">
          <span :class="['type-icon', script.typeClass]">{{ script.typeIcon }}</span>
          <div>
            <h3 class="script-name">{{ script.name }}</h3>
            <p class="script-meta">
              <span class="font-mono">v{{ script.version }}</span>
              ·
              <span>{{ script.author }}</span>
              ·
              <span>{{ script.date }}</span>
            </p>
          </div>
        </div>
        <div class="card-right">
          <span :class="['status-badge', script.statusClass]">{{ script.statusLabel }}</span>
          <span class="perm-badge">{{ script.permission }}</span>
          <el-dropdown trigger="click" @click.stop>
            <span class="card-actions">
              <span class="material-symbols-outlined">more_vert</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToEditor(script)">{{ isZh ? '编辑' : 'Edit' }}</el-dropdown-item>
                <el-dropdown-item @click="handleCopy(script)">{{ isZh ? '复制' : 'Copy' }}</el-dropdown-item>
                <el-dropdown-item v-if="script.isDraft" @click="handlePublish(script)">{{ isZh ? '发布' : 'Publish' }}</el-dropdown-item>
                <el-dropdown-item divided @click="handleDelete(script)" style="color: var(--error)">{{ isZh ? '删除' : 'Delete' }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      <div v-if="scripts.length === 0 && !loading" class="no-data">
        {{ isZh ? '暂无脚本数据' : 'No script data' }}
      </div>
    </div>

    <div class="pagination">
      <span class="page-info font-mono">{{ pageSummary }}</span>
      <div class="page-nav">
        <button class="page-btn" :disabled="pagination.pageNum <= 1" @click="handlePageChange(pagination.pageNum - 1)">{{ isZh ? '上一页' : 'Prev' }}</button>
        <button class="page-btn" :disabled="pagination.pageNum >= totalPages" @click="handlePageChange(pagination.pageNum + 1)">{{ isZh ? '下一页' : 'Next' }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { copyScript, deleteScript, getScriptList, publishScript } from '../../api/script'
import { copyMockScript, deleteMockScript, listMockScripts, publishMockScript, resetMockScripts } from '../../utils/scriptMockStore'

const { locale } = useI18n()
const isZh = computed(() => locale.value === 'zh')
const router = useRouter()

const loading = ref(false)
const scripts = ref([])
const useFallback = ref(false)

const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

const searchKey = ref('')
const filterType = ref('')
const filterPrivate = ref(false)

const totalPages = computed(() => Math.max(1, Math.ceil(pagination.total / pagination.pageSize)))

const pageSummary = computed(() => {
  if (!pagination.total) {
    return isZh.value ? '暂无数据' : 'No data'
  }
  const start = (pagination.pageNum - 1) * pagination.pageSize + 1
  const end = Math.min(pagination.pageNum * pagination.pageSize, pagination.total)
  return isZh.value ? `显示 ${start}-${end} 条，共 ${pagination.total} 条` : `Showing ${start}-${end} of ${pagination.total}`
})

const getTypeIcon = type => ({ bash: '#!', shell: '#!', python: 'PY', powershell: 'PS' }[String(type || '').toLowerCase()] || '#!')
const getTypeClass = type => ({ bash: 'type-bash', shell: 'type-bash', python: 'type-python', powershell: 'type-powershell' }[String(type || '').toLowerCase()] || 'type-bash')

const toViewModel = item => {
  const status = item.auditStatus || (item.isDraft ? 'draft' : 'approved')
  return {
    id: item.id,
    name: item.scriptName || item.name,
    version: item.version || 1,
    author: item.createBy || '-',
    date: item.createTime ? String(item.createTime).slice(0, 10) : '-',
    typeIcon: getTypeIcon(item.scriptType),
    typeClass: getTypeClass(item.scriptType),
    statusClass: status === 'approved' ? 'badge-active' : 'badge-draft',
    statusLabel: status === 'approved' ? 'APPROVED' : 'DRAFT',
    permission: String(item.scope || 'private').toUpperCase(),
    isDraft: Boolean(item.isDraft) || status !== 'approved'
  }
}

const loadFallback = () => {
  useFallback.value = true
  const result = listMockScripts({
    pageNum: pagination.pageNum,
    pageSize: pagination.pageSize,
    keyword: searchKey.value,
    scriptType: filterType.value,
    scope: filterPrivate.value ? 'private' : ''
  })
  scripts.value = result.records.map(toViewModel)
  pagination.total = result.total
}

const reloadScripts = async () => {
  loading.value = true
  try {
    if (useFallback.value) {
      loadFallback()
      return
    }
    const res = await getScriptList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKey.value,
      scriptType: filterType.value,
      scope: filterPrivate.value ? 'private' : ''
    })
    scripts.value = (res.data.records || []).map(toViewModel)
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('Failed to fetch scripts:', error)
    resetMockScripts()
    loadFallback()
    ElMessage.warning(isZh.value ? '脚本接口异常，已切换演示数据。' : 'Script API unavailable, switched to demo data.')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  reloadScripts()
}

const handlePageChange = page => {
  pagination.pageNum = page
  reloadScripts()
}

const goToEditor = script => router.push(`/scripts/editor/${script.id}`)
const goToNew = () => router.push('/scripts/editor/new')

const handleDelete = async script => {
  try {
    await ElMessageBox.confirm(
      isZh.value ? `确认删除脚本 ${script.name}？` : `Delete script ${script.name}?`,
      isZh.value ? '确认删除' : 'Confirm',
      { type: 'warning' }
    )
    if (useFallback.value) {
      deleteMockScript(script.id)
    } else {
      await deleteScript(script.id)
    }
    ElMessage.success(isZh.value ? '删除成功。' : 'Deleted.')
    await reloadScripts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete script:', error)
      ElMessage.error(isZh.value ? '删除失败。' : 'Delete failed.')
    }
  }
}

const handlePublish = async script => {
  try {
    if (useFallback.value) {
      publishMockScript(script.id)
    } else {
      await publishScript(script.id)
    }
    ElMessage.success(isZh.value ? '发布成功。' : 'Published.')
    await reloadScripts()
  } catch (error) {
    console.error('Failed to publish script:', error)
    ElMessage.error(isZh.value ? '发布失败。' : 'Publish failed.')
  }
}

const handleCopy = async script => {
  try {
    const { value } = await ElMessageBox.prompt(
      isZh.value ? '请输入新脚本名称' : 'Please enter new script name',
      isZh.value ? '复制脚本' : 'Copy Script',
      { inputValue: `${script.name}_copy` }
    )
    if (!value) {
      return
    }
    if (useFallback.value) {
      copyMockScript(script.id, value)
    } else {
      await copyScript(script.id, value)
    }
    ElMessage.success(isZh.value ? '复制成功。' : 'Copied.')
    await reloadScripts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to copy script:', error)
      ElMessage.error(isZh.value ? '复制失败。' : 'Copy failed.')
    }
  }
}

onMounted(() => {
  reloadScripts()
})
</script>

<style scoped>
.scripts-page { padding: 32px; max-width: 1600px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-title { font-size: 26px; color: #fff; font-weight: 900; }
.page-subtitle { margin-top: 6px; color: var(--on-surface-variant); font-size: 12px; }
.header-actions { display: flex; gap: 8px; }
.action-btn { padding: 9px 16px; border-radius: 8px; border: none; cursor: pointer; display: inline-flex; gap: 6px; align-items: center; font-size: 12px; font-weight: 700; }
.action-btn.secondary { background: var(--bg-surface-high); color: #fff; border: 1px solid rgba(66,70,86,0.1); }
.action-btn.primary { color: #fff; }
.toolbar { margin-top: 20px; display: flex; justify-content: space-between; align-items: center; gap: 16px; }
.search-area { display: flex; align-items: center; gap: 8px; min-width: 340px; border-radius: 9999px; border: 1px solid rgba(66,70,86,0.1); padding: 8px 12px; }
.search-area input { width: 100%; background: transparent; border: none; outline: none; color: #fff; }
.filter-group { display: flex; gap: 12px; align-items: center; }
.filter-select { background: var(--bg-base); color: #fff; border: 1px solid rgba(66,70,86,0.1); border-radius: 8px; padding: 8px 10px; }
.private-toggle { font-size: 12px; color: var(--on-surface-variant); display: flex; gap: 6px; align-items: center; }
.script-list { margin-top: 20px; display: flex; flex-direction: column; gap: 10px; }
.script-card { background: var(--bg-surface-container-low); border: 1px solid rgba(66,70,86,0.05); border-radius: 12px; padding: 16px 18px; display: flex; justify-content: space-between; align-items: center; cursor: pointer; }
.script-card:hover { background: var(--bg-surface-container); }
.card-left { display: flex; gap: 12px; align-items: center; }
.type-icon { width: 38px; height: 38px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-family: var(--font-mono); font-weight: 800; }
.type-bash { background: rgba(15,98,254,0.12); color: var(--primary-container); }
.type-python { background: rgba(200,64,0,0.12); color: #FFB800; }
.type-powershell { background: rgba(57,153,174,0.12); color: #7AD9F5; }
.script-name { color: #fff; font-size: 14px; font-weight: 700; }
.script-meta { color: var(--on-surface-variant); font-size: 11px; margin-top: 3px; }
.card-right { display: flex; align-items: center; gap: 10px; }
.status-badge { font-size: 10px; border-radius: 9999px; padding: 3px 10px; font-weight: 700; }
.badge-active { background: rgba(15,98,254,0.12); color: var(--primary-container); }
.badge-draft { background: rgba(120,120,120,0.18); color: var(--on-surface-variant); }
.perm-badge { font-size: 10px; color: var(--outline); background: var(--bg-surface-high); border-radius: 4px; padding: 3px 8px; }
.card-actions { width: 30px; height: 30px; border-radius: 6px; display: flex; align-items: center; justify-content: center; }
.card-actions:hover { background: var(--bg-surface-high); }
.pagination { margin-top: 18px; display: flex; justify-content: space-between; align-items: center; }
.page-info { color: var(--on-surface-variant); font-size: 11px; }
.page-nav { display: flex; gap: 8px; }
.page-btn { background: transparent; border: 1px solid rgba(66,70,86,0.1); border-radius: 6px; color: var(--on-surface-variant); padding: 6px 12px; cursor: pointer; }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.no-data { text-align: center; padding: 50px; color: var(--on-surface-variant); }
</style>
