<template>
  <div class="scripts-page">
    <div class="page-header">
      <!-- <div>
        <h1 class="page-title">{{ t('scripts.title') }}</h1>
        <p class="page-subtitle font-mono">{{ t('scripts.path') }} — {{ t('scripts.subtitle') }}</p>
      </div> -->
      <div class="header-actions">
        <button class="action-btn primary litho-gradient" @click="goToNew">
          <span class="material-symbols-outlined">add</span>
          {{ t('scripts.createScript') }}
        </button>
      </div>
    </div>

    <div class="toolbar">
      <div class="search-area">
        <span class="material-symbols-outlined search-icon">search</span>
        <input type="text" v-model="searchKey" :placeholder="t('scripts.searchPlaceholder')" @keyup.enter="handleSearch" />
      </div>
      <div class="filter-group">
        <select class="filter-select" v-model="filterType" @change="handleSearch">
          <option value="">{{ t('scripts.allTypes') }}</option>
          <option value="bash">Bash/Shell</option>
          <option value="python">Python</option>
          <option value="powershell">PowerShell</option>
        </select>
        <label class="private-toggle">
          <input type="checkbox" v-model="filterPrivate" @change="handleSearch" />
          {{ t('scripts.privateOnly') }}
        </label>
      </div>
    </div>

    <div class="script-list" v-loading="loading">
      <div v-for="item in scripts" :key="item.id" class="script-card" @click="goToEditor(item)">
        <div class="card-left">
          <span :class="['type-icon', item.typeClass]">{{ item.typeIcon }}</span>
          <div>
            <h3 class="script-name">{{ item.name }}</h3>
            <p class="script-meta">
              <span class="font-mono">v{{ item.version }}</span>
              ·
              <span>{{ item.author }}</span>
              ·
              <span>{{ item.date }}</span>
            </p>
          </div>
        </div>
        <div class="card-right">
          <span :class="['status-badge', item.statusClass]">{{ item.statusLabel }}</span>
          <span class="perm-badge">{{ item.permission }}</span>
          <el-dropdown trigger="click" @click.stop>
            <span class="card-actions">
              <span class="material-symbols-outlined">more_vert</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToEditor(item)">{{ locale === 'zh' ? '编辑' : 'Edit' }}</el-dropdown-item>
                <el-dropdown-item @click="handleCopy(item)">{{ locale === 'zh' ? '复制' : 'Copy' }}</el-dropdown-item>
                <el-dropdown-item v-if="item.statusLabel === 'DRAFT'" @click="handlePublish(item)">{{ locale === 'zh' ? '发布' : 'Publish' }}</el-dropdown-item>
                <el-dropdown-item divided @click="handleDelete(item)" style="color: var(--error)">{{ locale === 'zh' ? '删除' : 'Delete' }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      <div v-if="scripts.length === 0 && !loading" class="no-data">
        {{ locale === 'zh' ? '暂无脚本数据' : 'No script data' }}
      </div>
    </div>

    <div class="pagination">
      <span class="page-info font-mono">{{ locale === 'zh' ? `显示 ${((pagination.pageNum - 1) * pagination.pageSize) + 1}-${Math.min(pagination.pageNum * pagination.pageSize, pagination.total)} 条，共 ${pagination.total} 条` : `Showing ${((pagination.pageNum - 1) * pagination.pageSize) + 1}-${Math.min(pagination.pageNum * pagination.pageSize, pagination.total)} of ${pagination.total} scripts` }}</span>
      <div class="page-nav">
        <button v-for="p in Math.ceil(pagination.total / pagination.pageSize)" :key="p" v-show="Math.abs(p - pagination.pageNum) <= 1 || p === 1 || p === Math.ceil(pagination.total / pagination.pageSize)" :class="['page-btn', { active: p === pagination.pageNum }]" @click="handlePageChange(p)">{{ p }}</button>
        <button class="page-btn" v-if="pagination.pageNum < Math.ceil(pagination.total / pagination.pageSize)" @click="handlePageChange(pagination.pageNum + 1)">Next →</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getScriptList, getScriptById, createScript, updateScript, deleteScript, publishScript, copyScript } from '../../api/script'

const { t, locale } = useI18n()
const router = useRouter()

const loading = ref(false)
const scripts = ref([])
const selectedIds = ref([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

const searchKey = ref('')
const filterType = ref('')
const filterPrivate = ref(false)

async function fetchScripts() {
  loading.value = true
  try {
    const res = await getScriptList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKey.value,
      scriptType: filterType.value,
      scope: filterPrivate.value ? 'private' : ''
    })
    scripts.value = (res.data.records || []).map(s => ({
      id: s.id,
      name: s.name,
      version: s.version || '1.0.0',
      author: s.createByName || '-',
      date: s.createTime ? s.createTime.substring(0, 10) : '-',
      typeIcon: getTypeIcon(s.scriptType),
      typeClass: getTypeClass(s.scriptType),
      statusClass: getStatusClass(s.status),
      statusLabel: getStatusLabel(s.status),
      permission: s.scope?.toUpperCase() || 'PRIVATE',
      description: s.description
    }))
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('Failed to fetch scripts:', error)
  } finally {
    loading.value = false
  }
}

function getTypeIcon(type) {
  const map = {
    'bash': '#!',
    'shell': '#!',
    'python': 'PY',
    'powershell': 'PS'
  }
  return map[type?.toLowerCase()] || '#!'
}

function getTypeClass(type) {
  const map = {
    'bash': 'type-bash',
    'shell': 'type-bash',
    'python': 'type-python',
    'powershell': 'type-powershell'
  }
  return map[type?.toLowerCase()] || 'type-bash'
}

function getStatusClass(status) {
  const map = {
    'active': 'badge-active',
    'draft': 'badge-draft',
    'deprecated': 'badge-deprecated',
    'pending': 'badge-draft'
  }
  return map[status?.toLowerCase()] || 'badge-draft'
}

function getStatusLabel(status) {
  const map = {
    'active': 'ACTIVE',
    'draft': 'DRAFT',
    'deprecated': 'DEPRECATED',
    'pending': 'PENDING'
  }
  return map[status?.toLowerCase()] || 'DRAFT'
}

function handleSearch() {
  pagination.pageNum = 1
  fetchScripts()
}

function handlePageChange(page) {
  pagination.pageNum = page
  fetchScripts()
}

function goToEditor(script) {
  router.push(`/scripts/editor/${script.id}`)
}

function goToNew() {
  router.push('/scripts/editor/new')
}

async function handleDelete(script) {
  try {
    await ElMessageBox.confirm(
      locale.value === 'zh' ? `确定要删除脚本 ${script.name} 吗？` : `Are you sure to delete script ${script.name}?`,
      locale.value === 'zh' ? '确认删除' : 'Confirm Delete',
      { type: 'warning' }
    )
    await deleteScript(script.id)
    ElMessage.success(locale.value === 'zh' ? '删除成功' : 'Deleted successfully')
    fetchScripts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete script:', error)
    }
  }
}

async function handlePublish(script) {
  try {
    await ElMessageBox.confirm(
      locale.value === 'zh' ? `确定要发布脚本 ${script.name} 吗？` : `Are you sure to publish script ${script.name}?`,
      locale.value === 'zh' ? '确认发布' : 'Confirm Publish',
      { type: 'info' }
    )
    await publishScript(script.id)
    ElMessage.success(locale.value === 'zh' ? '发布成功' : 'Published successfully')
    fetchScripts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to publish script:', error)
    }
  }
}

async function handleCopy(script) {
  try {
    const { value: newName } = await ElMessageBox.prompt(
      locale.value === 'zh' ? '请输入新脚本名称' : 'Please enter new script name',
      locale.value === 'zh' ? '复制脚本' : 'Copy Script',
      {
        inputValue: `${script.name}_copy`,
        confirmButtonText: locale.value === 'zh' ? '确定' : 'OK',
        cancelButtonText: locale.value === 'zh' ? '取消' : 'Cancel'
      }
    )
    if (newName) {
      await copyScript(script.id, newName)
      ElMessage.success(locale.value === 'zh' ? '复制成功' : 'Copied successfully')
      fetchScripts()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to copy script:', error)
    }
  }
}

onMounted(() => {
  fetchScripts()
})
</script>

<style scoped>
.scripts-page {
  padding: 32px;
  max-width: 1600px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  gap: 16px;
}

.filter-group { display: flex; gap: 12px; align-items: center; }

.private-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: var(--on-surface-variant);
  cursor: pointer;
  font-family: var(--font-label);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}
.private-toggle input[type="checkbox"] { accent-color: var(--primary-container); }

.script-list {
  margin-top: 24px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.script-card {
  background: var(--bg-surface-container-low);
  border-radius: 12px;
  padding: 18px 22px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: all 0.15s;
  border: 1px solid rgba(66, 70, 86, 0.04);
}
.script-card:hover {
  background: var(--bg-surface-container);
  border-color: rgba(180, 197, 255, 0.12);
}

.card-left { display: flex; align-items: center; gap: 14px; }

.type-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 800;
  font-family: var(--font-mono);
}
.type-bash { background: rgba(15, 98, 254, 0.1); color: var(--primary-container); }
.type-python { background: rgba(200, 64, 0, 0.1); color: var(--tertiary); }
.type-powershell { background: rgba(57, 153, 174, 0.1); color: #3999AE; }

.script-name {
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 2px;
}

.script-meta {
  font-size: 11px;
  color: var(--on-surface-variant);
}

.card-right { display: flex; align-items: center; gap: 12px; }

.status-badge {
  font-size: 9px;
  font-weight: 800;
  padding: 4px 10px;
  border-radius: 9999px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}
.badge-active { background: rgba(15, 98, 254, 0.1); color: var(--primary-container); }
.badge-draft { background: rgba(53, 53, 52, 0.6); color: var(--on-surface-variant); }
.badge-deprecated { background: rgba(147, 0, 10, 0.1); color: var(--error); }

.perm-badge {
  font-size: 10px;
  font-weight: 600;
  color: var(--outline);
  text-transform: uppercase;
  letter-spacing: 0.06em;
  padding: 3px 8px;
  background: var(--bg-surface-high);
  border-radius: 4px;
}

.card-actions {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  cursor: pointer;
  color: var(--on-surface-variant);
  transition: all 0.15s;
}
.card-actions:hover { background: var(--bg-surface-high); color: #fff; }

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 12px 0;
}
.page-info { font-size: 11px; color: var(--on-surface-variant); }
.page-nav { display: flex; gap: 4px; }
.page-btn {
  padding: 6px 12px;
  background: transparent;
  border: 1px solid rgba(66, 70, 86, 0.1);
  border-radius: 6px;
  color: var(--on-surface-variant);
  font-size: 11px;
  cursor: pointer;
  transition: all 0.15s;
}
.page-btn:hover:not(:disabled) { background: var(--bg-surface-high); color: #fff; }
.page-btn.active { background: var(--primary-container); color: white; border-color: transparent; }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }

.no-data {
  text-align: center; padding: 60px;
  color: var(--on-surface-variant); font-size: 14px;
}
</style>
