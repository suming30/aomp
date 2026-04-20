<template>
  <div class="tag-manage-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">{{ isZh ? '标签管理' : 'Tag Management' }}</h1>
        <p class="page-subtitle">{{ isZh ? '支持标签增删改，接口异常时自动回退演示数据。' : 'Tag CRUD with automatic demo fallback.' }}</p>
      </div>
      <div class="header-actions">
        <button class="action-btn secondary" @click="reloadTags">
          <span class="material-symbols-outlined">refresh</span>
          {{ isZh ? '刷新' : 'Refresh' }}
        </button>
        <button class="action-btn primary litho-gradient" @click="openCreateDialog">
          <span class="material-symbols-outlined">add</span>
          {{ isZh ? '新增标签' : 'Create Tag' }}
        </button>
      </div>
    </div>

    <div class="filter-bar glass-card">
      <div class="search-box">
        <span class="material-symbols-outlined">search</span>
        <input v-model.trim="searchKey" :placeholder="isZh ? '搜索标签名、描述、创建人' : 'Search by tag name, description, creator'" @keyup.enter="reloadTags" />
      </div>
      <span class="result-count font-mono">{{ tags.length }} {{ isZh ? '个标签' : 'tags' }}</span>
    </div>

    <div class="tag-grid glass-card">
      <div v-for="tag in tags" :key="tag.id" class="tag-card">
        <div class="tag-color-bar" :style="{ background: tag.color || '#0F62FE' }"></div>
        <div class="tag-body">
          <div class="tag-top">
            <span class="tag-name" :style="{ color: tag.color || '#0F62FE' }">{{ tag.tagName }}</span>
            <span class="tag-count font-mono">{{ tag.hostCount || 0 }} {{ isZh ? '台主机' : 'hosts' }}</span>
          </div>
          <p class="tag-desc">{{ tag.description || '-' }}</p>
          <div class="tag-meta">
            <span>{{ isZh ? '创建人' : 'Creator' }}: {{ tag.createBy || '-' }}</span>
            <span class="font-mono">{{ formatDate(tag.createTime) }}</span>
          </div>
          <div class="tag-actions">
            <button class="tag-btn" @click="openEditDialog(tag)">
              <span class="material-symbols-outlined">edit</span>
            </button>
            <button class="tag-btn danger" @click="handleDelete(tag)">
              <span class="material-symbols-outlined">delete</span>
            </button>
          </div>
        </div>
      </div>
      <div v-if="tags.length === 0" class="no-data">
        {{ isZh ? '暂无标签数据' : 'No tag data' }}
      </div>
    </div>

    <el-dialog v-model="showDialog" :title="editingId ? (isZh ? '编辑标签' : 'Edit Tag') : (isZh ? '新增标签' : 'Create Tag')" width="520px">
      <div class="form-field">
        <label>{{ isZh ? '标签名' : 'Tag Name' }}</label>
        <input v-model.trim="formData.tagName" />
      </div>
      <div class="form-field">
        <label>{{ isZh ? '颜色' : 'Color' }}</label>
        <div class="color-picker">
          <button v-for="color in colorOptions" :key="color" class="color-dot" :style="{ background: color }" @click="formData.color = color"></button>
          <input v-model="formData.color" type="color" class="color-input" />
        </div>
      </div>
      <div class="form-field">
        <label>{{ isZh ? '关联主机数' : 'Host Count' }}</label>
        <input v-model.number="formData.hostCount" type="number" min="0" />
      </div>
      <div class="form-field">
        <label>{{ isZh ? '描述' : 'Description' }}</label>
        <textarea v-model.trim="formData.description" rows="3"></textarea>
      </div>
      <template #footer>
        <button class="btn-cancel" @click="showDialog = false">{{ isZh ? '取消' : 'Cancel' }}</button>
        <button class="btn-confirm litho-gradient" @click="handleSubmit">{{ isZh ? '保存' : 'Save' }}</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createTag, deleteTag, getTagList, updateTag } from '../../api/host'
import { createMockTag, deleteMockTag, importMockTags, listMockTags, updateMockTag } from '../../utils/assetMetaMockStore'

const { locale } = useI18n()
const isZh = computed(() => locale.value === 'zh')

const tags = ref([])
const searchKey = ref('')
const showDialog = ref(false)
const editingId = ref(null)
const useFallback = ref(false)

const colorOptions = ['#0F62FE', '#C84000', '#FFB800', '#24A148', '#8A3FFC', '#009D9A', '#EE5386', '#491D8B']
const formData = reactive({
  tagName: '',
  color: '#0F62FE',
  hostCount: 0,
  description: ''
})

const formatDate = value => (value ? String(value).replace('T', ' ').slice(0, 19) : '-')

const normalizeTag = item => ({
  id: item.id,
  tagName: item.tagName || item.name || `TAG-${item.id}`,
  color: item.color || '#0F62FE',
  description: item.description || item.desc || '',
  hostCount: item.hostCount || 0,
  createBy: item.createBy || item.creator || '-',
  createTime: item.createTime || item.createdAt
})

const resetForm = () => {
  editingId.value = null
  formData.tagName = ''
  formData.color = '#0F62FE'
  formData.hostCount = 0
  formData.description = ''
}

const loadFallback = () => {
  useFallback.value = true
  tags.value = listMockTags({ keyword: searchKey.value }).map(normalizeTag)
}

const reloadTags = async () => {
  if (useFallback.value) {
    loadFallback()
    return
  }
  try {
    const res = await getTagList({ keyword: searchKey.value, pageNum: 1, pageSize: 200 })
    tags.value = (res.data.records || res.data || []).map(normalizeTag)
  } catch (error) {
    console.error('Failed to fetch tags:', error)
    importMockTags()
    loadFallback()
    ElMessage.warning(isZh.value ? '标签接口暂不可用，已切换演示数据。' : 'Tag API unavailable, switched to demo data.')
  }
}

const openCreateDialog = () => {
  resetForm()
  showDialog.value = true
}

const openEditDialog = tag => {
  editingId.value = tag.id
  formData.tagName = tag.tagName
  formData.color = tag.color || '#0F62FE'
  formData.hostCount = tag.hostCount || 0
  formData.description = tag.description || ''
  showDialog.value = true
}

const handleSubmit = async () => {
  if (!formData.tagName) {
    ElMessage.warning(isZh.value ? '请填写标签名。' : 'Tag name is required.')
    return
  }
  const payload = {
    tagName: formData.tagName,
    color: formData.color,
    hostCount: formData.hostCount,
    description: formData.description
  }
  try {
    if (useFallback.value) {
      if (editingId.value) {
        updateMockTag(editingId.value, payload)
      } else {
        createMockTag(payload)
      }
    } else if (editingId.value) {
      await updateTag(editingId.value, payload)
    } else {
      await createTag(payload)
    }
    ElMessage.success(isZh.value ? '标签已保存。' : 'Tag saved.')
    showDialog.value = false
    resetForm()
    await reloadTags()
  } catch (error) {
    console.error('Failed to save tag:', error)
    ElMessage.error(isZh.value ? '保存失败。' : 'Save failed.')
  }
}

const handleDelete = async tag => {
  try {
    await ElMessageBox.confirm(
      isZh.value ? `确认删除标签 ${tag.tagName}？` : `Delete tag ${tag.tagName}?`,
      isZh.value ? '确认删除' : 'Confirm',
      { type: 'warning' }
    )
    if (useFallback.value) {
      deleteMockTag(tag.id)
    } else {
      await deleteTag(tag.id)
    }
    ElMessage.success(isZh.value ? '删除成功。' : 'Deleted.')
    await reloadTags()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete tag:', error)
      ElMessage.error(isZh.value ? '删除失败。' : 'Delete failed.')
    }
  }
}

reloadTags()
</script>

<style scoped>
.tag-manage-page { padding: 32px; max-width: 1600px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-title { font-size: 26px; font-weight: 900; color: #fff; }
.page-subtitle { color: var(--on-surface-variant); margin-top: 6px; font-size: 12px; }
.header-actions { display: flex; gap: 8px; }
.action-btn { padding: 9px 16px; border: none; border-radius: 8px; cursor: pointer; display: flex; gap: 6px; align-items: center; font-size: 12px; font-weight: 700; }
.action-btn.secondary { background: var(--bg-surface-high); color: #fff; border: 1px solid rgba(66,70,86,0.1); }
.action-btn.primary { color: #fff; }
.filter-bar { margin-top: 16px; padding: 14px 18px; border-radius: 12px; display: flex; justify-content: space-between; align-items: center; }
.search-box { display: flex; align-items: center; gap: 8px; border: 1px solid rgba(66,70,86,0.1); border-radius: 9999px; padding: 8px 12px; min-width: 300px; }
.search-box input { background: transparent; border: none; color: #fff; outline: none; width: 100%; }
.tag-grid { margin-top: 16px; padding: 18px; border-radius: 12px; display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 12px; }
.tag-card { border-radius: 10px; overflow: hidden; background: var(--bg-base); display: flex; }
.tag-color-bar { width: 4px; flex-shrink: 0; }
.tag-body { padding: 14px; width: 100%; }
.tag-top { display: flex; justify-content: space-between; align-items: center; }
.tag-name { font-weight: 800; }
.tag-count { color: var(--on-surface-variant); font-size: 11px; }
.tag-desc { margin-top: 8px; color: var(--on-surface-variant); font-size: 12px; min-height: 36px; }
.tag-meta { margin-top: 10px; display: flex; justify-content: space-between; color: var(--outline); font-size: 11px; }
.tag-actions { margin-top: 10px; display: flex; justify-content: flex-end; gap: 8px; }
.tag-btn { width: 28px; height: 28px; border: none; border-radius: 6px; background: var(--bg-surface-high); color: var(--on-surface-variant); cursor: pointer; display: flex; align-items: center; justify-content: center; }
.tag-btn:hover { background: var(--primary-container); color: #fff; }
.tag-btn.danger:hover { background: var(--error); }
.no-data { grid-column: 1 / -1; text-align: center; color: var(--on-surface-variant); padding: 32px; }
.form-field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 12px; }
.form-field label { font-size: 11px; color: var(--on-surface-variant); }
.form-field input, .form-field textarea { background: var(--bg-base); color: #fff; border: 1px solid var(--outline-variant); border-radius: 8px; padding: 9px 12px; outline: none; }
.color-picker { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
.color-dot { width: 24px; height: 24px; border: none; border-radius: 9999px; cursor: pointer; }
.color-input { width: 28px; height: 28px; border: none; background: transparent; }
.btn-cancel, .btn-confirm { border-radius: 8px; border: none; padding: 8px 18px; cursor: pointer; }
.btn-cancel { background: transparent; color: var(--on-surface-variant); border: 1px solid var(--outline-variant); }
.btn-confirm { color: #fff; }
</style>
