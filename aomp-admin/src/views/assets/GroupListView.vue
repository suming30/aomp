<template>
  <div class="group-manage-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">{{ isZh ? '主机组管理' : 'Host Group Management' }}</h1>
        <p class="page-subtitle">{{ isZh ? '支持主机组的新增、编辑和删除，自动回退演示数据。' : 'Supports create, edit, and delete with demo fallback.' }}</p>
      </div>
      <div class="header-actions">
        <button class="action-btn secondary" @click="reloadGroups">
          <span class="material-symbols-outlined">refresh</span>
          {{ isZh ? '刷新' : 'Refresh' }}
        </button>
        <button class="action-btn primary litho-gradient" @click="openCreateDialog">
          <span class="material-symbols-outlined">add</span>
          {{ isZh ? '新增主机组' : 'Create Group' }}
        </button>
      </div>
    </div>

    <div class="filter-bar glass-card">
      <div class="filter-left">
        <div class="search-box">
          <span class="material-symbols-outlined">search</span>
          <input v-model.trim="searchKey" :placeholder="isZh ? '搜索组名、描述、创建人' : 'Search by name, description, creator'" @keyup.enter="reloadGroups" />
        </div>
        <select v-model="filterType" class="filter-select" @change="reloadGroups">
          <option value="">{{ isZh ? '全部类型' : 'All Types' }}</option>
          <option value="static">{{ isZh ? '静态组' : 'Static' }}</option>
          <option value="dynamic">{{ isZh ? '动态组' : 'Dynamic' }}</option>
        </select>
      </div>
      <span class="result-count font-mono">{{ groups.length }} {{ isZh ? '个主机组' : 'groups' }}</span>
    </div>

    <div class="group-list">
      <div v-for="group in groups" :key="group.id" class="group-card glass-card">
        <div class="card-left">
          <div :class="['type-badge', group.groupType]">
            <span class="material-symbols-outlined">{{ group.groupType === 'dynamic' ? 'auto_awesome' : 'folder' }}</span>
            {{ group.groupType === 'dynamic' ? (isZh ? '动态组' : 'Dynamic') : (isZh ? '静态组' : 'Static') }}
          </div>
          <h3 class="group-name">{{ group.groupName }}</h3>
          <p class="group-desc">{{ group.description || '-' }}</p>
        </div>
        <div class="card-center">
          <span class="font-mono stat">{{ isZh ? '主机数' : 'Hosts' }}: {{ group.hostCount || 0 }}</span>
          <span>{{ isZh ? '创建人' : 'Creator' }}: {{ group.createBy || '-' }}</span>
          <span class="font-mono">{{ formatDate(group.updateTime) }}</span>
        </div>
        <div class="card-actions">
          <button class="icon-btn" @click="openEditDialog(group)">
            <span class="material-symbols-outlined">edit</span>
          </button>
          <button class="icon-btn danger" @click="handleDelete(group)">
            <span class="material-symbols-outlined">delete</span>
          </button>
        </div>
      </div>
      <div v-if="groups.length === 0" class="no-data glass-card">
        {{ isZh ? '暂无主机组数据' : 'No group data' }}
      </div>
    </div>

    <el-dialog v-model="showDialog" :title="editingId ? (isZh ? '编辑主机组' : 'Edit Group') : (isZh ? '新增主机组' : 'Create Group')" width="560px">
      <div class="form-field">
        <label>{{ isZh ? '组名称' : 'Group Name' }}</label>
        <input v-model.trim="formData.groupName" />
      </div>
      <div class="form-field">
        <label>{{ isZh ? '组类型' : 'Group Type' }}</label>
        <select v-model="formData.groupType">
          <option value="static">{{ isZh ? '静态组' : 'Static' }}</option>
          <option value="dynamic">{{ isZh ? '动态组' : 'Dynamic' }}</option>
        </select>
      </div>
      <div class="form-field">
        <label>{{ isZh ? '主机数量' : 'Host Count' }}</label>
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
import { createHostGroup, deleteHostGroup, getHostGroupList, updateHostGroup } from '../../api/host'
import { createMockGroup, deleteMockGroup, importMockGroups, listMockGroups, updateMockGroup } from '../../utils/assetMetaMockStore'

const { locale } = useI18n()
const isZh = computed(() => locale.value === 'zh')

const groups = ref([])
const searchKey = ref('')
const filterType = ref('')
const showDialog = ref(false)
const editingId = ref(null)
const useFallback = ref(false)

const formData = reactive({
  groupName: '',
  groupType: 'static',
  hostCount: 0,
  description: ''
})

const formatDate = value => (value ? String(value).replace('T', ' ').slice(0, 19) : '-')

const resetForm = () => {
  editingId.value = null
  formData.groupName = ''
  formData.groupType = 'static'
  formData.hostCount = 0
  formData.description = ''
}

const normalizeGroup = item => ({
  id: item.id,
  groupName: item.groupName || item.name || `GROUP-${item.id}`,
  groupType: item.groupType || item.type || 'static',
  description: item.description || item.desc || '',
  hostCount: item.hostCount || 0,
  createBy: item.createBy || item.creator || '-',
  updateTime: item.updateTime || item.updatedAt
})

const loadFallback = () => {
  useFallback.value = true
  groups.value = listMockGroups({
    keyword: searchKey.value,
    groupType: filterType.value
  }).map(normalizeGroup)
}

const reloadGroups = async () => {
  if (useFallback.value) {
    loadFallback()
    return
  }
  try {
    const res = await getHostGroupList({
      keyword: searchKey.value,
      groupType: filterType.value
    })
    groups.value = (res.data.records || res.data || []).map(normalizeGroup)
  } catch (error) {
    console.error('Failed to fetch groups:', error)
    importMockGroups()
    loadFallback()
    ElMessage.warning(isZh.value ? '主机组接口暂不可用，已切换演示数据。' : 'Group API unavailable, switched to demo data.')
  }
}

const openCreateDialog = () => {
  resetForm()
  showDialog.value = true
}

const openEditDialog = group => {
  editingId.value = group.id
  formData.groupName = group.groupName
  formData.groupType = group.groupType || 'static'
  formData.hostCount = group.hostCount || 0
  formData.description = group.description || ''
  showDialog.value = true
}

const handleSubmit = async () => {
  if (!formData.groupName) {
    ElMessage.warning(isZh.value ? '请填写组名称。' : 'Group name is required.')
    return
  }
  const payload = {
    groupName: formData.groupName,
    groupType: formData.groupType,
    hostCount: formData.hostCount,
    description: formData.description
  }
  try {
    if (useFallback.value) {
      if (editingId.value) {
        updateMockGroup(editingId.value, payload)
      } else {
        createMockGroup(payload)
      }
    } else if (editingId.value) {
      await updateHostGroup(editingId.value, payload)
    } else {
      await createHostGroup(payload)
    }
    ElMessage.success(isZh.value ? '主机组已保存。' : 'Group saved.')
    showDialog.value = false
    resetForm()
    await reloadGroups()
  } catch (error) {
    console.error('Failed to save group:', error)
    ElMessage.error(isZh.value ? '保存失败。' : 'Save failed.')
  }
}

const handleDelete = async group => {
  try {
    await ElMessageBox.confirm(
      isZh.value ? `确认删除主机组 ${group.groupName}？` : `Delete group ${group.groupName}?`,
      isZh.value ? '确认删除' : 'Confirm',
      { type: 'warning' }
    )
    if (useFallback.value) {
      deleteMockGroup(group.id)
    } else {
      await deleteHostGroup(group.id)
    }
    ElMessage.success(isZh.value ? '删除成功。' : 'Deleted.')
    await reloadGroups()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete group:', error)
      ElMessage.error(isZh.value ? '删除失败。' : 'Delete failed.')
    }
  }
}

reloadGroups()
</script>

<style scoped>
.group-manage-page { padding: 32px; max-width: 1600px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-title { font-size: 26px; font-weight: 900; color: #fff; }
.page-subtitle { color: var(--on-surface-variant); margin-top: 6px; font-size: 12px; }
.header-actions { display: flex; gap: 8px; }
.action-btn { padding: 9px 16px; border: none; border-radius: 8px; cursor: pointer; display: flex; gap: 6px; align-items: center; font-size: 12px; font-weight: 700; }
.action-btn.secondary { background: var(--bg-surface-high); color: #fff; border: 1px solid rgba(66,70,86,0.1); }
.action-btn.primary { color: #fff; }
.filter-bar { display: flex; justify-content: space-between; align-items: center; padding: 14px 18px; margin-top: 18px; border-radius: 12px; }
.filter-left { display: flex; gap: 10px; align-items: center; }
.search-box { display: flex; align-items: center; gap: 8px; border: 1px solid rgba(66,70,86,0.1); border-radius: 9999px; padding: 8px 12px; min-width: 280px; }
.search-box input { background: transparent; border: none; color: #fff; outline: none; width: 100%; }
.filter-select { background: var(--bg-base); color: #fff; border: 1px solid rgba(66,70,86,0.1); border-radius: 8px; padding: 8px 12px; }
.group-list { margin-top: 16px; display: flex; flex-direction: column; gap: 10px; }
.group-card { display: grid; grid-template-columns: 2fr 1.5fr auto; gap: 12px; padding: 16px 18px; border-radius: 12px; align-items: center; }
.type-badge { display: inline-flex; align-items: center; gap: 6px; border-radius: 6px; padding: 3px 10px; font-size: 10px; font-weight: 700; width: fit-content; margin-bottom: 8px; }
.type-badge.static { color: var(--primary-container); background: rgba(15,98,254,0.12); }
.type-badge.dynamic { color: #FFB800; background: rgba(255,184,0,0.12); }
.group-name { color: #fff; font-size: 14px; font-weight: 700; }
.group-desc { color: var(--on-surface-variant); margin-top: 4px; font-size: 12px; }
.card-center { display: flex; flex-direction: column; gap: 6px; color: var(--on-surface-variant); font-size: 12px; }
.stat { color: #fff; font-weight: 700; }
.card-actions { display: flex; gap: 8px; }
.icon-btn { width: 30px; height: 30px; border: none; border-radius: 7px; background: var(--bg-surface-high); color: var(--on-surface-variant); cursor: pointer; display: flex; align-items: center; justify-content: center; }
.icon-btn.danger:hover { background: var(--error); color: #fff; }
.icon-btn:hover { background: var(--primary-container); color: #fff; }
.no-data { text-align: center; padding: 44px; color: var(--on-surface-variant); border-radius: 12px; }
.form-field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 12px; }
.form-field label { font-size: 11px; color: var(--on-surface-variant); }
.form-field input, .form-field select, .form-field textarea { background: var(--bg-base); color: #fff; border: 1px solid var(--outline-variant); border-radius: 8px; padding: 9px 12px; outline: none; }
.btn-cancel, .btn-confirm { border-radius: 8px; border: none; padding: 8px 18px; cursor: pointer; }
.btn-cancel { background: transparent; color: var(--on-surface-variant); border: 1px solid var(--outline-variant); }
.btn-confirm { color: #fff; }
</style>
