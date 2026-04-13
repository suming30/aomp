<template>
  <div class="role-manage-page">
    <div class="page-header">
      <!-- <div>
        <h1 class="page-title">{{ t('roleManage.title') }}</h1>
        <p class="page-subtitle">{{ t('roleManage.subtitle') }}</p>
      </div> -->
      <div class="header-actions">
        <button class="action-btn primary litho-gradient" @click="showCreateDialog = true">
          <span class="material-symbols-outlined">add</span>
          {{ t('roleManage.createRole') }}
        </button>
      </div>
    </div>

    <!-- Role Cards Grid -->
    <div class="roles-grid" v-loading="loading">
      <div v-for="role in roles" :key="role.id" :class="['role-card glass-card', { 'card-featured': role.featured }]">
        <div class="card-top">
          <div class="role-badge-row">
            <span :class="['role-indicator', role.color]"></span>
            <h3 class="role-name">{{ role.name }}</h3>
            <span v-if="role.builtIn" class="built-in-tag font-mono">BUILT-IN</span>
          </div>
          <p class="role-desc">{{ role.description || role.desc }}</p>
        </div>

        <div class="card-stats">
          <div class="stat" v-for="(s, sidx) in role.stats" :key="sidx">
            <span class="stat-num font-mono">{{ s.val }}</span>
            <span class="stat-lbl">{{ s.lbl }}</span>
          </div>
        </div>

        <div class="perm-section">
          <div class="perm-header">
            <span class="perm-title">{{ t('roleManage.permissions') }}</span>
            <button class="edit-perm-btn font-mono" @click="openPermEditor(role)">
              {{ t('roleManage.editPerms') }} &rarr;
            </button>
          </div>
          <div class="perm-chips">
            <span v-for="(p, pidx) in role.perms" :key="pidx" class="perm-chip">{{ p }}</span>
          </div>
        </div>

        <div class="card-footer">
          <div class="member-preview">
            <img v-for="(m, midx) in role.members.slice(0, 3)" :key="midx"
                 :src="m" class="member-avatar-sm" />
            <span v-if="role.memberCount > 3" class="more-count font-mono">+{{ role.memberCount - 3 }}</span>
          </div>
          <div class="card-actions">
            <button class="icon-btn" :title="t('common.edit')" @click="openEditDialog(role)"><span class="material-symbols-outlined">edit</span></button>
            <button v-if="!role.builtIn" class="icon-btn danger" :title="t('common.delete')" @click="handleDelete(role)"><span class="material-symbols-outlined">delete</span></button>
            <button class="icon-btn" :title="t('roleManage.duplicate')"><span class="material-symbols-outlined">content_copy</span></button>
          </div>
        </div>
      </div>
      <div v-if="roles.length === 0 && !loading" class="no-data">
        {{ locale === 'zh' ? '暂无角色数据' : 'No role data' }}
      </div>
    </div>

    <!-- Permission Matrix Section -->
    <div class="matrix-section glass-card">
      <div class="section-head">
        <h2>{{ t('roleManage.permMatrixTitle') }}</h2>
        <span class="section-desc">{{ t('roleManage.permMatrixDesc') }}</span>
      </div>
      <div class="matrix-table-wrap">
        <table class="matrix-table">
          <thead>
            <tr>
              <th class="module-col">{{ t('roleManage.matrixModule') }}</th>
              <th v-for="r in roles" :key="r.name" class="role-col">{{ r.name }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(mod, midx) in modules" :key="midx">
              <td class="module-cell">
                <span class="material-symbols-outlined module-icon">{{ mod.icon }}</span>
                {{ mod.label }}
              </td>
              <td v-for="r in roles" :key="r.name + mod.key" class="perm-cell">
                <span
                  v-for="(level, lidx) in permLevels" :key="lidx"
                  :class="['perm-dot', level, { active: getPerm(r, mod.key) >= lidx }]"
                  :title="getPermLabel(lidx)"
                ></span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create Role Dialog -->
    <div v-if="showCreateDialog" class="dialog-overlay" @click.self="closeDialog">
      <div class="dialog glass-card">
        <div class="dialog-header">
          <h3>{{ editingRole ? t('common.edit') : t('roleManage.createRole') }}</h3>
          <button class="close-btn" @click="closeDialog"><span class="material-symbols-outlined">close</span></button>
        </div>
        <div class="dialog-body">
          <div class="form-field">
            <label>{{ t('roleManage.formRoleName') }} *</label>
            <input type="text" v-model="formData.name" placeholder="" />
          </div>
          <div class="form-field">
            <label>{{ locale === 'zh' ? '角色编码' : 'Role Code' }} *</label>
            <input type="text" v-model="formData.code" :disabled="!!editingRole" placeholder="" />
          </div>
          <div class="form-field">
            <label>{{ t('roleManage.formDesc') }}</label>
            <textarea rows="3" v-model="formData.description" placeholder=""></textarea>
          </div>
          <div class="form-field" v-if="!editingRole">
            <label>{{ t('roleManage.formBaseRole') }}</label>
            <select v-model="formData.baseRoleId">
              <option :value="null">{{ locale === 'zh' ? '无（从零开始）' : 'None (Start from scratch)' }}</option>
              <option v-for="r in roles" :key="r.id" :value="r.id">{{ r.name }}</option>
            </select>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="closeDialog">{{ t('common.cancel') }}</button>
          <button class="btn-confirm litho-gradient" @click="handleSubmit">{{ t('common.confirm') }}</button>
        </div>
      </div>
    </div>

    <!-- Permission Editor Dialog -->
    <div v-if="showPermEditor" class="dialog-overlay" @click.self="showPermEditor = false">
      <div class="dialog glass-card perm-dialog">
        <div class="dialog-header">
          <h3>{{ locale === 'zh' ? '编辑权限' : 'Edit Permissions' }} - {{ editingRole?.name }}</h3>
          <button class="close-btn" @click="showPermEditor = false"><span class="material-symbols-outlined">close</span></button>
        </div>
        <div class="dialog-body">
          <div class="perm-tree">
            <div v-for="group in permissionTree" :key="group.id" class="perm-group">
              <div class="perm-group-header">
                <span class="material-symbols-outlined">{{ group.icon || 'folder' }}</span>
                <span>{{ group.name }}</span>
              </div>
              <div class="perm-items">
                <label v-for="perm in group.children" :key="perm.id" class="perm-item">
                  <input type="checkbox" v-model="selectedPermissions" :value="perm.id" />
                  <span>{{ perm.name }}</span>
                </label>
              </div>
            </div>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="showPermEditor = false">{{ t('common.cancel') }}</button>
          <button class="btn-confirm litho-gradient" @click="savePermissions">{{ t('common.confirm') }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, createRole, updateRole, deleteRole, getRolePermissions, updateRolePermissions, getPermissionTree } from '../../api/role'

const { t, locale } = useI18n()
const showCreateDialog = ref(false)
const showPermEditor = ref(false)
const loading = ref(false)
const roles = ref([])
const permissionTree = ref([])
const editingRole = ref(null)
const selectedPermissions = ref([])

const formData = reactive({
  name: '',
  code: '',
  description: '',
  baseRoleId: null
})

const modules = [
  { key: 'assets', label: t('roleManage.modAssets'), icon: 'inventory_2' },
  { key: 'scripts', label: t('roleManage.modScripts'), icon: 'terminal' },
  { key: 'tasks', label: t('roleManage.modTasks'), icon: 'play_circle' },
  { key: 'inspection', label: t('roleManage.modInspection'), icon: 'monitoring' },
  { key: 'audit', label: t('roleManage.modAudit'), icon: 'fact_check' },
  { key: 'security', label: t('roleManage.modSecurity'), icon: 'admin_panel_settings' }
]

const permLevels = ['none', 'read', 'write', 'execute', 'admin']

async function fetchRoles() {
  loading.value = true
  try {
    const res = await getRoleList({ pageNum: 1, pageSize: 100 })
    roles.value = (res.data.records || []).map(r => ({
      ...r,
      color: getRoleColor(r.code),
      builtIn: r.builtIn === 1 || r.builtIn === true,
      featured: r.code === 'admin',
      stats: [
        { val: r.userCount || 0, lbl: t('roleManage.statUsers') },
        { val: r.moduleCount || 'ALL', lbl: t('roleManage.statModules') },
        { val: r.level || 'MID', lbl: t('roleManage.statLevel') }
      ],
      perms: (r.permissionNames || []).slice(0, 5),
      memberCount: r.userCount || 0,
      members: [],
      permissions: r.permissionMap || {}
    }))
  } catch (error) {
    console.error('Failed to fetch roles:', error)
  } finally {
    loading.value = false
  }
}

async function fetchPermissionTree() {
  try {
    const res = await getPermissionTree()
    permissionTree.value = res.data || []
  } catch (error) {
    console.error('Failed to fetch permission tree:', error)
  }
}

function getRoleColor(code) {
  const colorMap = {
    'admin': 'red',
    'sre': 'blue',
    'ops': 'yellow',
    'dev': 'gray'
  }
  return colorMap[code] || 'blue'
}

function getPerm(role, modKey) {
  return (role.permissions || {})[modKey] || 0
}

function getPermLabel(idx) {
  return ['', 'Read Only', 'Read + Write', 'Full Execute', 'Admin'][idx] || ''
}

function openEditDialog(role) {
  editingRole.value = role
  formData.name = role.name
  formData.code = role.code
  formData.description = role.description
  formData.baseRoleId = null
  showCreateDialog.value = true
}

function closeDialog() {
  showCreateDialog.value = false
  editingRole.value = null
  resetForm()
}

function resetForm() {
  formData.name = ''
  formData.code = ''
  formData.description = ''
  formData.baseRoleId = null
}

async function handleSubmit() {
  if (!formData.name || !formData.code) {
    ElMessage.warning(locale.value === 'zh' ? '请填写必填项' : 'Please fill required fields')
    return
  }
  
  try {
    if (editingRole.value) {
      await updateRole(editingRole.value.id, formData)
      ElMessage.success(locale.value === 'zh' ? '更新成功' : 'Updated successfully')
    } else {
      await createRole(formData)
      ElMessage.success(locale.value === 'zh' ? '创建成功' : 'Created successfully')
    }
    closeDialog()
    fetchRoles()
  } catch (error) {
    console.error('Failed to save role:', error)
  }
}

async function handleDelete(role) {
  if (role.builtIn) {
    ElMessage.warning(locale.value === 'zh' ? '内置角色不能删除' : 'Built-in roles cannot be deleted')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      locale.value === 'zh' ? `确定要删除角色 ${role.name} 吗？` : `Are you sure to delete role ${role.name}?`,
      locale.value === 'zh' ? '确认删除' : 'Confirm Delete',
      { type: 'warning' }
    )
    await deleteRole(role.id)
    ElMessage.success(locale.value === 'zh' ? '删除成功' : 'Deleted successfully')
    fetchRoles()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete role:', error)
    }
  }
}

async function openPermEditor(role) {
  editingRole.value = role
  try {
    const res = await getRolePermissions(role.id)
    selectedPermissions.value = res.data || []
    showPermEditor.value = true
  } catch (error) {
    console.error('Failed to fetch role permissions:', error)
  }
}

async function savePermissions() {
  if (!editingRole.value) return
  
  try {
    await updateRolePermissions(editingRole.value.id, selectedPermissions.value)
    ElMessage.success(locale.value === 'zh' ? '权限保存成功' : 'Permissions saved successfully')
    showPermEditor.value = false
    fetchRoles()
  } catch (error) {
    console.error('Failed to save permissions:', error)
  }
}

onMounted(() => {
  fetchRoles()
  fetchPermissionTree()
})
</script>

<style scoped>
.role-manage-page { padding: 32px; max-width: 1600px; margin: 0 auto; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title { font-size: 26px; font-weight: 900; color: #fff; letter-spacing: -0.02em; }
.page-subtitle { font-size: 12px; color: var(--on-surface-variant); margin-top: 4px; }

.header-actions { display: flex; gap: 8px; }
.action-btn {
  padding: 9px 20px; border-radius: 8px; border: none;
  font-family: var(--font-label); font-size: 11px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.08em;
  cursor: pointer; display: flex; align-items: center; gap: 6px;
  color: var(--on-primary-container); box-shadow: 0 4px 15px rgba(15,98,254,0.25);
}

/* Role Cards */
.roles-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; margin-top: 24px; }

.role-card {
  border-radius: 16px; padding: 24px;
  transition: all 0.2s ease; display: flex; flex-direction: column; gap: 18px;
}
.role-card:hover { transform: translateY(-2px); box-shadow: 0 12px 40px rgba(0,0,0,0.3); }
.card-featured { border-left: 3px solid var(--tertiary); }

.card-top {}
.role-badge-row { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.role-indicator { width: 10px; height: 10px; border-radius: 50%; }
.role-indicator.red { background: var(--tertiary); box-shadow: 0 0 8px rgba(200,64,0,0.4); }
.role-indicator.blue { background: var(--primary-container); box-shadow: 0 0 8px rgba(15,98,254,0.4); }
.role-indicator.yellow { background: #FFB800; box-shadow: 0 0 8px rgba(255,184,0,0.4); }
.role-indicator.gray { background: var(--outline); }

.role-name { font-size: 15px; font-weight: 800; color: #fff; flex: 1; }
.built-in-tag {
  font-size: 8px; padding: 2px 8px; border-radius: 9999px;
  background: rgba(200,64,0,0.1); color: var(--tertiary);
  text-transform: uppercase; letter-spacing: 0.08em; font-weight: 800;
}

.role-desc { font-size: 11.5px; color: var(--on-surface-variant); line-height: 1.55; }

.card-stats { display: flex; gap: 24px; padding: 14px 0; border-top: 1px solid rgba(66,70,86,0.06); border-bottom: 1px solid rgba(66,70,86,0.06); }
.stat { display: flex; flex-direction: column; gap: 2px; }
.stat-num { font-size: 20px; font-weight: 900; color: #fff; }
.stat-lbl { font-size: 9px; color: var(--on-surface-variant); text-transform: uppercase; letter-spacing: 0.08em; }

.perm-section { display: flex; flex-direction: column; gap: 10px; }
.perm-header { display: flex; justify-content: space-between; align-items: center; }
.perm-title { font-size: 10px; font-weight: 800; color: var(--on-surface-variant); text-transform: uppercase; letter-spacing: 0.1em; }
.edit-perm-btn {
  background: none; border: none; color: var(--primary-container);
  font-size: 10px; cursor: pointer; letter-spacing: 0.04em;
}
.perm-chips { display: flex; flex-wrap: wrap; gap: 6px; }
.perm-chip {
  padding: 4px 10px; border-radius: 5px; font-size: 10px; font-weight: 600;
  background: var(--bg-base); color: var(--on-surface-variant);
}

.card-footer { display: flex; justify-content: space-between; align-items: center; margin-top: auto; }
.member-preview { display: flex; align-items: center; gap: -6px; }
.member-avatar-sm {
  width: 28px; height: 28px; border-radius: 7px;
  border: 2px solid var(--bg-surface-low); object-fit: cover;
  margin-right: -6px;
}
.more-count { font-size: 9px; color: var(--on-surface-variant); margin-left: 6px; }

.card-actions { display: flex; gap: 6px; }
.icon-btn {
  width: 30px; height: 30px; border-radius: 7px; border: none;
  background: var(--bg-surface-high); color: var(--on-surface-variant);
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: all 0.15s;
}
.icon-btn .material-symbols-outlined { font-size: 16px !important; }
.icon-btn:hover:not(.danger) { background: var(--primary-container); color: #fff; }
.icon-btn.danger:hover { background: var(--error); color: #fff; }

/* Matrix */
.matrix-section { margin-top: 32px; border-radius: 16px; padding: 28px; }
.section-head { margin-bottom: 24px; }
.section-head h2 { font-size: 17px; font-weight: 900; color: #fff; text-transform: uppercase; letter-spacing: -0.01em; }
.section-desc { font-size: 11px; color: var(--on-surface-variant); margin-top: 4px; }

.matrix-table-wrap { overflow-x: auto; }
.matrix-table { width: 100%; border-collapse: separate; border-spacing: 0; font-size: 12px; }
.matrix-table th {
  padding: 12px 16px; font-family: var(--font-label); font-size: 9px;
  font-weight: 800; color: var(--on-surface-variant); text-transform: uppercase;
  letter-spacing: 0.1em; background: var(--bg-surface-highest);
  border-bottom: 1px solid rgba(66,70,86,0.1);
}
.module-col { text-align: left; min-width: 160px; }
.role-col { text-align: center; min-width: 120px; }

.matrix-table td { padding: 14px 16px; vertical-align: middle; border-bottom: 1px solid rgba(66,70,86,0.04); }
.module-cell {
  display: flex; align-items: center; gap: 10px; font-weight: 600; color: #fff;
}
.module-icon { font-size: 18px !important; color: var(--primary-container); opacity: 0.6; }

.perm-cell { text-align: center; }
.perm-dot {
  width: 10px; height: 10px; border-radius: 50%; display: inline-block; margin: 0 3px;
  transition: all 0.15s;
}
.perm-dot.none { background: transparent; border: 1px solid rgba(66,70,86,0.15); }
.perm-dot.read { background: rgba(66,70,86,0.3); }
.perm-dot.write { background: rgba(255,184,0,0.5); }
.perm-dot.execute { background: rgba(15,98,254,0.6); }
.perm-dot.admin { background: var(--tertiary); opacity: 0.7; }
.perm-dot.active { transform: scale(1.35); box-shadow: 0 0 6px currentColor; }

/* Dialog */
.dialog-overlay {
  position: fixed; inset: 0; z-index: 500;
  background: rgba(0,0,0,0.65); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center;
}
.dialog { width: 520px; max-height: 85vh; overflow-y: auto; border-radius: 16px; }
.dialog-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 24px 16px; border-bottom: 1px solid rgba(66,70,86,0.08);
}
.dialog-header h3 { font-size: 16px; font-weight: 800; color: #fff; text-transform: uppercase; letter-spacing: 0.03em; }
.close-btn {
  width: 32px; height: 32px; border-radius: 8px; border: none;
  background: var(--bg-surface-high); color: var(--on-surface-variant);
  cursor: pointer; display: flex; align-items: center; justify-content: center;
}
.close-btn .material-symbols-outlined { font-size: 18px !important; }

.dialog-body { padding: 24px; display: flex; flex-direction: column; gap: 16px; }
.form-field { display: flex; flex-direction: column; gap: 6px; }
.form-field.full { grid-column: span 2; }
.form-field label {
  font-size: 10px; font-weight: 700; color: var(--on-surface-variant);
  text-transform: uppercase; letter-spacing: 0.08em;
}
.form-field input,
.form-field select,
.form-field textarea {
  background: var(--bg-base); border: 1px solid var(--outline-variant);
  border-radius: 8px; padding: 10px 12px; color: #fff; font-size: 13px;
  outline: none; font-family: var(--font-body);
}
.form-field select option { background: var(--bg-surface-container); }
.form-field textarea { resize: vertical; }

.copy-options { display: flex; gap: 12px; flex-wrap: wrap; }
.copy-option {
  display: flex; align-items: center; gap: 6px; padding: 8px 14px;
  border-radius: 8px; border: 1px solid rgba(66,70,86,0.1); cursor: pointer;
  font-size: 12px; color: var(--on-surface-variant); transition: all 0.15s;
}
.copy-option:hover { border-color: var(--primary-container); color: #fff; }

.dialog-footer {
  display: flex; justify-content: flex-end; gap: 10px;
  padding: 16px 24px; border-top: 1px solid rgba(66,70,86,0.08);
}
.btn-cancel {
  padding: 9px 22px; border-radius: 8px; border: 1px solid rgba(66,70,86,0.15);
  background: transparent; color: var(--on-surface-variant); font-size: 12px;
  font-weight: 600; cursor: pointer;
}
.btn-confirm {
  padding: 9px 28px; border-radius: 8px; border: none;
  color: var(--on-primary-container); font-size: 12px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.06em; cursor: pointer;
  box-shadow: 0 4px 15px rgba(15,98,254,0.25);
}

.perm-dialog { width: 640px; }
.perm-tree { display: flex; flex-direction: column; gap: 16px; }
.perm-group { background: var(--bg-base); border-radius: 8px; padding: 12px; }
.perm-group-header {
  display: flex; align-items: center; gap: 8px;
  font-weight: 700; color: #fff; margin-bottom: 10px;
  font-size: 13px;
}
.perm-group-header .material-symbols-outlined { font-size: 18px !important; color: var(--primary-container); }
.perm-items { display: flex; flex-wrap: wrap; gap: 8px; }
.perm-item {
  display: flex; align-items: center; gap: 6px;
  padding: 6px 12px; border-radius: 6px;
  background: var(--bg-surface-high); cursor: pointer;
  font-size: 11px; color: var(--on-surface-variant);
  transition: all 0.15s;
}
.perm-item:hover { background: var(--primary-container); color: #fff; }
.perm-item input { accent-color: var(--primary-container); }

.no-data {
  grid-column: span 2; text-align: center; padding: 60px;
  color: var(--on-surface-variant); font-size: 14px;
}
</style>
