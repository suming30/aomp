<template>
  <div class="user-manage-page">
    <div class="page-header">
      <!-- <div>
        <h1 class="page-title">{{ t('userManage.title') }}</h1>
        <p class="page-subtitle">{{ t('userManage.subtitle') }}</p>
      </div> -->
      <div class="header-actions">
        <button class="action-btn secondary">
          <span class="material-symbols-outlined">file_upload</span>
          {{ t('userManage.batchImport') }}
        </button>
        <button class="action-btn primary litho-gradient" @click="showAddDialog = true">
          <span class="material-symbols-outlined">person_add</span>
          {{ t('userManage.addUser') }}
        </button>
      </div>
    </div>

    <!-- Stats Bar -->
    <div class="stats-bar glass-card">
      <div class="stat-item" v-for="(s, idx) in stats" :key="idx">
        <span class="stat-value font-mono">{{ s.value }}</span>
        <span class="stat-label">{{ s.label }}</span>
      </div>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar glass-card">
      <div class="filter-left">
        <div class="search-box">
          <span class="material-symbols-outlined">search</span>
          <input :placeholder="t('userManage.searchPlaceholder')" v-model="searchKey" @keyup.enter="handleSearch" />
        </div>
        <select class="filter-select" v-model="filterRole">
          <option value="">{{ t('userManage.allRoles') }}</option>
          <option v-for="role in roles" :key="role.id" :value="role.code">{{ role.name }}</option>
        </select>
        <select class="filter-select" v-model="filterStatus">
          <option value="">{{ t('userManage.allStatus') }}</option>
          <option value="active">{{ t('common.active') }}</option>
          <option value="disabled">{{ t('common.disabled') }}</option>
        </select>
      </div>
      <div class="filter-right">
        <span class="result-count font-mono">{{ pagination.total }} {{ t('userManage.totalUsers') }}</span>
      </div>
    </div>

    <!-- Data Table -->
    <div class="table-container glass-card" v-loading="loading">
      <table class="data-table">
        <thead>
          <tr>
            <th><input type="checkbox" /></th>
            <th>{{ t('userManage.colUser') }}</th>
            <th>{{ t('userManage.colEmail') }}</th>
            <th>{{ t('userManage.colRole') }}</th>
            <th>{{ t('userManage.colDept') }}</th>
            <th>{{ t('userManage.colLastLogin') }}</th>
            <th>{{ t('userManage.colStatus') }}</th>
            <th>{{ t('userManage.colActions') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in users" :key="u.id">
            <td><input type="checkbox" /></td>
            <td>
              <div class="user-cell">
                <img :src="`https://ui-avatars.com/api/?name=${encodeURIComponent(u.displayName || u.username)}&background=0F62FE&color=fff`" class="user-avatar" />
                <div class="user-info">
                  <span class="user-name">{{ u.displayName || u.username }}</span>
                  <span class="user-id font-mono">@{{ u.username }}</span>
                </div>
              </div>
            </td>
            <td class="text-muted">{{ u.email }}</td>
            <td><span :class="['role-badge', 'role-' + (u.roleCode || 'default')]">{{ u.roleName || '-' }}</span></td>
            <td>{{ u.dept || '-' }}</td>
            <td class="font-mono text-muted text-sm">{{ u.lastLoginTime || '-' }}</td>
            <td>
              <span :class="['status-dot', u.status === 'active' ? 'online' : 'offline']"></span>
              <span :class="['status-text', u.status]">{{ u.status === 'active' ? t('common.active') : t('common.disabled') }}</span>
            </td>
            <td>
              <div class="row-actions">
                <button class="action-link primary" @click="openEditDialog(u)">
                  <span class="material-symbols-outlined">edit</span> {{ t('common.edit') }}
                </button>
                <button class="action-link warning" @click="handleToggleStatus(u)">
                  <span class="material-symbols-outlined">{{ u.status === 'active' ? 'lock' : 'lock_open' }}</span>
                  {{ u.status === 'active' ? (locale === 'zh' ? '禁用' : 'Disable') : (locale === 'zh' ? '启用' : 'Enable') }}
                </button>
                <button class="action-link info" @click="handleResetPassword(u)">
                  <span class="material-symbols-outlined">key</span> {{ locale === 'zh' ? '重置密码' : 'Reset Pwd' }}
                </button>
                <button class="action-link danger" @click="handleDelete(u)">
                  <span class="material-symbols-outlined">delete</span> {{ t('common.delete') }}
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="users.length === 0 && !loading">
            <td colspan="8" style="text-align: center; padding: 40px; color: var(--on-surface-variant);">
              {{ locale === 'zh' ? '暂无数据' : 'No data' }}
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Pagination -->
      <div class="pagination" v-if="pagination.total > 0">
        <span class="page-info font-mono">{{ (pagination.pageNum - 1) * pagination.pageSize + 1 }}-{{ Math.min(pagination.pageNum * pagination.pageSize, pagination.total) }} / {{ pagination.total }} {{ t('common.total') }}</span>
        <div class="page-controls">
          <button class="page-btn" :disabled="pagination.pageNum <= 1" @click="handlePageChange(pagination.pageNum - 1)">&lt;</button>
          <button 
            v-for="p in Math.ceil(pagination.total / pagination.pageSize)" 
            :key="p" 
            v-show="Math.abs(p - pagination.pageNum) <= 2 || p === 1 || p === Math.ceil(pagination.total / pagination.pageSize)"
            class="page-btn" 
            :class="{ active: p === pagination.pageNum }"
            @click="handlePageChange(p)"
          >{{ p }}</button>
          <button class="page-btn" :disabled="pagination.pageNum >= Math.ceil(pagination.total / pagination.pageSize)" @click="handlePageChange(pagination.pageNum + 1)">&gt;</button>
        </div>
      </div>
    </div>

    <!-- Add/Edit Dialog -->
    <div v-if="showAddDialog || showEditDialog" class="dialog-overlay" @click.self="closeDialogs">
      <div class="dialog glass-card">
        <div class="dialog-header">
          <h3>{{ showEditDialog ? t('userManage.editUser') : t('userManage.addUser') }}</h3>
          <button class="close-btn" @click="closeDialogs"><span class="material-symbols-outlined">close</span></button>
        </div>
        <div class="dialog-body">
          <div class="form-grid-2">
            <div class="form-field">
              <label>{{ t('userManage.formUsername') }} *</label>
              <input type="text" v-model="formData.username" :disabled="showEditDialog" placeholder="zhang_san" />
            </div>
            <div class="form-field">
              <label>{{ t('userManage.formDisplayName') }}</label>
              <input type="text" v-model="formData.displayName" placeholder="张三" />
            </div>
            <div class="form-field">
              <label>{{ t('userManage.formEmail') }} *</label>
              <input type="email" v-model="formData.email" placeholder="xxx@autoops.io" />
            </div>
            <div class="form-field">
              <label>{{ t('userManage.formRole') }}</label>
              <select v-model="formData.roleId">
                <option :value="null">{{ locale === 'zh' ? '请选择角色' : 'Select Role' }}</option>
                <option v-for="role in roles" :key="role.id" :value="role.id">{{ role.name }}</option>
              </select>
            </div>
            <div class="form-field">
              <label>{{ t('userManage.formDept') }}</label>
              <input type="text" v-model="formData.dept" placeholder="运维部" />
            </div>
            <div class="form-field">
              <label>{{ t('userManage.formPhone') }}</label>
              <input type="text" v-model="formData.phone" placeholder="+86 138****8888" />
            </div>
          </div>
          <div class="form-field full">
            <label>{{ t('userManage.formRemark') }}</label>
            <textarea rows="3" v-model="formData.remark" placeholder=""></textarea>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="closeDialogs">{{ t('common.cancel') }}</button>
          <button class="btn-confirm litho-gradient" @click="handleSubmit">{{ t('common.confirm') }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, createUser, updateUser, updateUserStatus, resetUserPassword } from '../../api/user'
import { getRoleList } from '../../api/role'

const { t, locale } = useI18n()

const loading = ref(false)
const searchKey = ref('')
const filterRole = ref('')
const filterStatus = ref('')
const showAddDialog = ref(false)
const showEditDialog = ref(false)
const editingUser = ref(null)

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const users = ref([])
const roles = ref([])

const stats = computed(() => [
  { value: pagination.total, label: t('userManage.statTotal') },
  { value: users.value.filter(u => u.status === 'active').length, label: t('userManage.statActive') },
  { value: users.value.filter(u => u.status === 'disabled').length, label: t('userManage.statDisabled') }
])

const formData = reactive({
  username: '',
  displayName: '',
  email: '',
  roleId: null,
  dept: '',
  phone: '',
  remark: ''
})

async function fetchUsers() {
  loading.value = true
  try {
    const res = await getUserList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      account: searchKey.value,
      status: filterStatus.value,
      roleCode: filterRole.value
    })
    users.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('Failed to fetch users:', error)
  } finally {
    loading.value = false
  }
}

async function fetchRoles() {
  try {
    const res = await getRoleList({ pageNum: 1, pageSize: 100 })
    roles.value = res.data.records || []
  } catch (error) {
    console.error('Failed to fetch roles:', error)
  }
}

function handleSearch() {
  pagination.pageNum = 1
  fetchUsers()
}

function handlePageChange(page) {
  pagination.pageNum = page
  fetchUsers()
}

function closeDialogs() {
  showAddDialog.value = false
  showEditDialog.value = false
  editingUser.value = null
  resetForm()
}

function resetForm() {
  formData.username = ''
  formData.displayName = ''
  formData.email = ''
  formData.roleId = null
  formData.dept = ''
  formData.phone = ''
  formData.remark = ''
}

function openEditDialog(user) {
  editingUser.value = user
  formData.username = user.username
  formData.displayName = user.displayName || user.username
  formData.email = user.email
  formData.roleId = user.roleId
  formData.dept = user.dept
  formData.phone = user.phone
  formData.remark = user.remark || ''
  showEditDialog.value = true
  showAddDialog.value = false
}

async function handleSubmit() {
  if (!formData.username || !formData.email) {
    ElMessage.warning(locale.value === 'zh' ? '请填写必填项' : 'Please fill required fields')
    return
  }
  
  try {
    if (showEditDialog.value && editingUser.value) {
      await updateUser(editingUser.value.id, formData)
      ElMessage.success(locale.value === 'zh' ? '更新成功' : 'Updated successfully')
    } else {
      await createUser(formData)
      ElMessage.success(locale.value === 'zh' ? '创建成功' : 'Created successfully')
    }
    closeDialogs()
    fetchUsers()
  } catch (error) {
    console.error('Failed to save user:', error)
  }
}

async function handleDelete(user) {
  try {
    await ElMessageBox.confirm(
      locale.value === 'zh' ? `确定要删除用户 ${user.username} 吗？` : `Are you sure to delete user ${user.username}?`,
      locale.value === 'zh' ? '确认删除' : 'Confirm Delete',
      { type: 'warning' }
    )
    await updateUserStatus(user.id, 'deleted')
    ElMessage.success(locale.value === 'zh' ? '删除成功' : 'Deleted successfully')
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete user:', error)
    }
  }
}

async function handleToggleStatus(user) {
  const newStatus = user.status === 'active' ? 'disabled' : 'active'
  const action = newStatus === 'disabled' ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(
      locale.value === 'zh' ? `确定要${action}用户 ${user.username} 吗？` : `Are you sure to ${newStatus === 'disabled' ? 'disable' : 'enable'} user ${user.username}?`,
      locale.value === 'zh' ? '确认操作' : 'Confirm',
      { type: 'warning' }
    )
    await updateUserStatus(user.id, newStatus)
    ElMessage.success(locale.value === 'zh' ? `${action}成功` : `Successfully ${newStatus === 'disabled' ? 'disabled' : 'enabled'}`)
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to update status:', error)
    }
  }
}

async function handleResetPassword(user) {
  try {
    await ElMessageBox.confirm(
      locale.value === 'zh' ? `确定要重置用户 ${user.username} 的密码吗？` : `Are you sure to reset password for ${user.username}?`,
      locale.value === 'zh' ? '确认重置' : 'Confirm Reset',
      { type: 'warning' }
    )
    await resetUserPassword(user.id)
    ElMessage.success(locale.value === 'zh' ? '密码已重置为默认密码' : 'Password has been reset to default')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to reset password:', error)
    }
  }
}

watch([filterRole, filterStatus], () => {
  pagination.pageNum = 1
  fetchUsers()
})

onMounted(() => {
  fetchUsers()
  fetchRoles()
})
</script>

<style scoped>
.user-manage-page { padding: 32px; max-width: 1600px; margin: 0 auto; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title { font-size: 26px; font-weight: 900; color: #fff; letter-spacing: -0.02em; }
.page-subtitle { font-size: 12px; color: var(--on-surface-variant); margin-top: 4px; }
.header-actions { display: flex; gap: 8px; }

.action-btn {
  padding: 9px 18px; border-radius: 8px; border: none;
  font-family: var(--font-label); font-size: 11px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.08em;
  cursor: pointer; display: flex; align-items: center; gap: 6px;
}
.action-btn.secondary { background: var(--bg-surface-high); color: #fff; border: 1px solid rgba(66,70,86,0.1); }
.action-btn.primary { color: var(--on-primary-container); box-shadow: 0 4px 15px rgba(15,98,254,0.25); }

.stats-bar {
  display: flex; gap: 32px; padding: 18px 24px; margin-top: 20px;
  align-items: center;
}
.stat-item { display: flex; flex-direction: column; gap: 2px; }
.stat-value { font-size: 28px; font-weight: 900; color: #fff; }
.stat-label { font-size: 10px; color: var(--on-surface-variant); text-transform: uppercase; letter-spacing: 0.08em; }

.filter-bar {
  display: flex; justify-content: space-between; align-items: center;
  padding: 14px 20px; margin-top: 16px;
}
.filter-left { display: flex; gap: 12px; align-items: center; }
.search-box {
  display: flex; align-items: center; background: var(--bg-base);
  border-radius: 9999px; padding: 7px 14px; gap: 8px;
  border: 1px solid rgba(66,70,86,0.1); min-width: 240px;
}
.search-box .material-symbols-outlined { font-size: 16px; color: var(--outline); }
.search-box input { background: transparent; border: none; outline: none; color: #fff; font-size: 12px; flex: 1; }

.filter-select {
  background: var(--bg-base); border: 1px solid rgba(66,70,86,0.1);
  border-radius: 8px; padding: 8px 12px; color: #fff; font-size: 12px; outline: none;
}
.filter-select option { background: var(--bg-surface-container); }

.result-count { font-size: 11px; color: var(--on-surface-variant); }

.table-container { margin-top: 16px; overflow-x: auto; }

.data-table { width: 100%; border-collapse: collapse; font-size: 12px; }
.data-table th {
  text-align: left; padding: 12px 16px;
  font-family: var(--font-label); font-size: 9px; font-weight: 800;
  color: var(--on-surface-variant); text-transform: uppercase; letter-spacing: 0.1em;
  background: var(--bg-surface-highest); border-bottom: 1px solid rgba(66,70,86,0.08);
}
.data-table td { padding: 13px 16px; border-bottom: 1px solid rgba(66,70,86,0.04); color: var(--on-surface); vertical-align: middle; }

.user-cell { display: flex; align-items: center; gap: 10px; }
.user-avatar { width: 34px; height: 34px; border-radius: 8px; object-fit: cover; }
.user-info { display: flex; flex-direction: column; }
.user-name { font-weight: 600; font-size: 13px; }
.user-id { font-size: 10px; color: var(--on-surface-variant); opacity: 0.6; }

.text-muted { color: var(--on-surface-variant); }
.text-sm { font-size: 11px; }

.role-badge {
  font-size: 10px; font-weight: 700; padding: 3px 10px; border-radius: 9999px;
  text-transform: uppercase; letter-spacing: 0.05em;
}
.role-admin { background: rgba(200,64,0,0.12); color: var(--tertiary); }
.role-sre { background: rgba(15,98,254,0.12); color: var(--primary-container); }
.role-ops { background: rgba(255,184,0,0.12); color: #FFB800; }
.role-dev { background: rgba(53,53,52,0.5); color: var(--on-surface-variant); }

.status-dot { width: 8px; height: 8px; border-radius: 50%; display: inline-block; margin-right: 6px; }
.status-dot.online { background: var(--primary-container); box-shadow: 0 0 6px var(--primary-container); }
.status-dot.offline { background: var(--outline); }
.status-text { font-size: 11px; font-weight: 600; }
.status-text.active { color: var(--primary-container); }
.status-text.disabled { color: var(--outline); }

.row-actions { display: flex; gap: 10px; }
.action-link {
  padding: 4px 10px; border-radius: 6px; border: none; cursor: pointer;
  font-family: var(--font-label); font-size: 10px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.04em;
  display: flex; align-items: center; gap: 4px;
  transition: all 0.15s; background: transparent;
}
.action-link .material-symbols-outlined { font-size: 14px !important; }
.action-link.primary { color: var(--primary-container); }
.action-link.primary:hover { background: rgba(15,98,254,0.08); }
.action-link.warning { color: #FFB800; }
.action-link.warning:hover { background: rgba(255,184,0,0.08); }
.action-link.info { color: #00D4FF; }
.action-link.info:hover { background: rgba(0,212,255,0.08); }
.action-link.danger { color: var(--error); }
.action-link.danger:hover { background: rgba(255,180,171,0.08); }

.pagination {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 20px; border-top: 1px solid rgba(66,70,86,0.06);
}
.page-info { font-size: 11px; color: var(--on-surface-variant); }
.page-controls { display: flex; gap: 4px; }
.page-btn {
  width: 30px; height: 30px; border-radius: 6px; border: none;
  background: transparent; color: var(--on-surface-variant);
  font-size: 12px; cursor: pointer; transition: all 0.15s;
  display: flex; align-items: center; justify-content: center;
}
.page-btn:hover:not(:disabled):not(.active) { background: var(--bg-surface-high); color: #fff; }
.page-btn.active { background: var(--primary-container); color: #fff; font-weight: 700; }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }

/* Dialog */
.dialog-overlay {
  position: fixed; inset: 0; z-index: 500;
  background: rgba(0,0,0,0.65); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center;
}
.dialog { width: 560px; max-height: 85vh; overflow-y: auto; border-radius: 16px; }
.dialog-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 24px 16px; border-bottom: 1px solid rgba(66,70,86,0.08);
}
.dialog-header h3 { font-size: 16px; font-weight: 800; color: #fff; text-transform: uppercase; letter-spacing: 0.03em; }
.close-btn {
  width: 32px; height: 32px; border-radius: 8px; border: none;
  background: var(--bg-surface-high); color: var(--on-surface-variant);
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: all 0.15s;
}
.close-btn:hover { background: var(--error); color: #fff; }
.close-btn .material-symbols-outlined { font-size: 18px !important; }

.dialog-body { padding: 24px; }
.form-grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
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

.dialog-footer {
  display: flex; justify-content: flex-end; gap: 10px;
  padding: 16px 24px; border-top: 1px solid rgba(66,70,86,0.08);
}

.btn-cancel {
  padding: 9px 22px; border-radius: 8px; border: 1px solid rgba(66,70,86,0.15);
  background: transparent; color: var(--on-surface-variant); font-size: 12px;
  font-weight: 600; cursor: pointer; transition: all 0.15s;
}
.btn-cancel:hover { background: var(--bg-surface-high); color: #fff; }
.btn-confirm {
  padding: 9px 28px; border-radius: 8px; border: none;
  color: var(--on-primary-container); font-size: 12px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.06em; cursor: pointer;
  box-shadow: 0 4px 15px rgba(15,98,254,0.25);
}
</style>
