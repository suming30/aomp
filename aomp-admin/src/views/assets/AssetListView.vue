<template>
  <div class="assets-page">
    <div class="page-header">
      <!-- <div>
        <h1 class="page-title">{{ t('assets.title') }}</h1>
        <p class="page-subtitle">{{ t('assets.subtitle') }}</p>
      </div> -->
      <div class="header-actions">
        <button class="action-btn primary litho-gradient" @click="showAddDialog = true">
          <span class="material-symbols-outlined">add</span>
           {{ t('assets.addHost') }}
        </button>
      </div>
    </div>

    <div class="toolbar">
      <div class="search-area">
        <span class="material-symbols-outlined search-icon">search</span>
        <input type="text" v-model="searchKey" :placeholder="t('assets.searchPlaceholder')" @keyup.enter="handleSearch" />
      </div>
      <div class="filter-group">
        <select class="filter-select" v-model="filterStatus" @change="handleSearch">
          <option value="">{{ t('assets.allStatus') }}</option>
          <option value="online">Running</option>
          <option value="high_load">High Load</option>
          <option value="offline">Offline</option>
        </select>
        <select class="filter-select" v-model="filterGroup" @change="handleSearch">
          <option value="">{{ t('assets.allGroup') }}</option>
          <option value="1">K8S-PROD-01</option>
          <option value="2">EDGE-GW-02</option>
        </select>
        <button class="filter-btn" @click="handleBatchCheck" :disabled="selectedIds.length === 0">
          <span class="material-symbols-outlined">network_check</span>
        </button>
      </div>
    </div>

    <div class="table-section" v-loading="loading">
      <table class="data-table">
        <thead>
          <tr>
            <th><input type="checkbox" :checked="selectedIds.length === hosts.length && hosts.length > 0" @change="handleSelectAll" /></th>
            <th>{{ t('assets.hostId') }}</th>
            <th>{{ t('assets.ipAddress') }}</th>
            <th>{{ t('assets.os') }}</th>
            <th>{{ t('assets.status') }}</th>
            <th>{{ t('assets.owner') }}</th>
            <th>{{ t('assets.actions') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in hosts" :key="item.id">
            <td><input type="checkbox" :checked="selectedIds.includes(item.id)" @change="handleSelect(item.id)" /></td>
            <td><span class="font-mono id-cell">{{ item.hostname || item.id }}</span></td>
            <td class="font-mono">{{ item.ip }}</td>
            <td>{{ item.os }}</td>
            <td><span :class="['status-dot', item.statusClass]"></span> {{ item.statusLabel }}</td>
            <td>{{ item.owner }}</td>
            <td>
              <div class="action-links">
                <a href="#" @click.prevent="goToDetail(item)">{{ t('assets.terminalConnect') }}</a>
                <a href="#" @click.prevent="handleCheckConnectivity(item)">{{ locale === 'zh' ? '连通性测试' : 'Check' }}</a>
                <a href="#" class="danger" @click.prevent="handleDelete(item)">{{ t('common.delete') }}</a>
              </div>
            </td>
          </tr>
          <tr v-if="hosts.length === 0 && !loading">
            <td colspan="7" class="no-data">{{ locale === 'zh' ? '暂无主机数据' : 'No host data' }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination">
      <span class="page-info font-mono">{{ locale === 'zh' ? `显示 ${((pagination.pageNum - 1) * pagination.pageSize) + 1}-${Math.min(pagination.pageNum * pagination.pageSize, pagination.total)} 条，共 ${pagination.total} 条` : `Showing ${((pagination.pageNum - 1) * pagination.pageSize) + 1}-${Math.min(pagination.pageNum * pagination.pageSize, pagination.total)} of ${pagination.total} hosts` }}</span>
      <div class="page-nav">
        <button class="page-btn" :disabled="pagination.pageNum === 1" @click="handlePageChange(pagination.pageNum - 1)">← Prev</button>
        <button v-for="p in Math.ceil(pagination.total / pagination.pageSize)" :key="p" v-show="Math.abs(p - pagination.pageNum) <= 2 || p === 1 || p === Math.ceil(pagination.total / pagination.pageSize)" :class="['page-btn', { active: p === pagination.pageNum }]" @click="handlePageChange(p)">{{ p }}</button>
        <button class="page-btn" :disabled="pagination.pageNum >= Math.ceil(pagination.total / pagination.pageSize)" @click="handlePageChange(pagination.pageNum + 1)">Next →</button>
      </div>
    </div>

    <!-- Add Host Dialog -->
    <el-dialog
      v-model="showAddDialog"
      :title="locale === 'zh' ? '新增主机' : 'Add Host'"
      width="640px"
      :close-on-click-modal="false"
      class="add-host-dialog"
      @closed="resetForm"
    >
      <el-tabs v-model="activeTab">
        <el-tab-pane :label="locale === 'zh' ? '单个添加' : 'Single Add'" name="single">
          <div class="form-grid">
            <div class="form-field">
              <label>{{ locale === 'zh' ? '主机名' : 'Hostname' }} *</label>
              <input v-model="formData.hostname" placeholder="e.g. web-server-01" />
            </div>
            <div class="form-field">
              <label>{{ locale === 'zh' ? 'IP地址' : 'IP Address' }} *</label>
              <input v-model="formData.ip" placeholder="192.168.1.100" />
            </div>
            <div class="form-field">
              <label>{{ locale === 'zh' ? '操作系统' : 'Operating System' }}</label>
              <input v-model="formData.osType" placeholder="CentOS" />
            </div>
            <div class="form-field">
              <label>{{ locale === 'zh' ? '系统版本' : 'OS Version' }}</label>
              <input v-model="formData.osVersion" placeholder="7.9" />
            </div>
            <div class="form-field">
              <label>{{ locale === 'zh' ? 'SSH端口' : 'SSH Port' }}</label>
              <input type="number" v-model="formData.sshPort" placeholder="22" />
            </div>
            <div class="form-field">
              <label>{{ locale === 'zh' ? 'SSH用户' : 'SSH User' }}</label>
              <input v-model="formData.sshUser" placeholder="root" />
            </div>
            <div class="form-field">
              <label>{{ locale === 'zh' ? '认证方式' : 'Auth Type' }}</label>
              <select v-model="formData.authType">
                <option value="password">{{ locale === 'zh' ? '密码' : 'Password' }}</option>
                <option value="key">{{ locale === 'zh' ? '密钥' : 'SSH Key' }}</option>
              </select>
            </div>
            <div class="form-field" v-if="formData.authType === 'password'">
              <label>{{ locale === 'zh' ? 'SSH密码' : 'SSH Password' }}</label>
              <input type="password" v-model="formData.sshPassword" placeholder="******" />
            </div>
            <div class="form-field full-width" v-if="formData.authType === 'key'">
              <label>{{ locale === 'zh' ? 'SSH私钥' : 'SSH Private Key' }}</label>
              <textarea rows="4" v-model="formData.sshKey" placeholder="-----BEGIN RSA PRIVATE KEY-----"></textarea>
            </div>
            <div class="form-field full-width">
              <label>{{ locale === 'zh' ? '描述' : 'Description' }}</label>
              <textarea rows="3" v-model="formData.description" placeholder="Enter description..."></textarea>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane :label="locale === 'zh' ? '批量导入 (CSV)' : 'Batch Import (CSV)'" name="batch">
          <div class="upload-zone">
            <span class="material-symbols-outlined upload-icon">cloud_upload</span>
            <p>{{ locale === 'zh' ? '拖拽CSV文件到此处或点击浏览' : 'Drag & drop CSV file here or click to browse' }}</p>
            <p class="hint">{{ locale === 'zh' ? '支持.csv格式，列: hostname, ip, os_type, os_version, ssh_port' : 'Supports .csv format with columns: hostname, ip, os_type, os_version, ssh_port' }}</p>
          </div>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <button class="btn-cancel" @click="showAddDialog = false">{{ locale === 'zh' ? '取消' : 'Cancel' }}</button>
        <button class="btn-submit litho-gradient" @click="handleSubmit">{{ locale === 'zh' ? '提交' : 'Submit' }}</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getHostList,
  getHostById,
  createHost,
  updateHost,
  deleteHost,
  checkHostConnectivity,
  exportHosts,
  batchCheckConnectivity
} from '../../api/host'

const { t, locale } = useI18n()
const router = useRouter()

const showAddDialog = ref(false)
const activeTab = ref('single')
const loading = ref(false)
const hosts = ref([])
const selectedIds = ref([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

const searchKey = ref('')
const filterStatus = ref('')
const filterGroup = ref('')

const formData = reactive({
  hostname: '',
  ip: '',
  port: 22,
  osType: '',
  osVersion: '',
  groupId: null,
  description: '',
  sshUser: '',
  sshPort: 22,
  authType: 'password',
  sshPassword: '',
  sshKey: ''
})

const hostData = ref([])

async function fetchHosts() {
  loading.value = true
  try {
    const res = await getHostList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKey.value,
      status: filterStatus.value,
      groupId: filterGroup.value
    })
    hosts.value = (res.data.records || []).map(h => ({
      id: h.id,
      hostname: h.hostname,
      ip: h.ip,
      os: `${h.osType || ''} ${h.osVersion || ''}`.trim(),
      statusClass: getStatusClass(h.status),
      statusLabel: getStatusLabel(h.status),
      owner: h.managerName || '-',
      port: h.sshPort || 22,
      groupId: h.groupId,
      groupName: h.groupName
    }))
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('Failed to fetch hosts:', error)
  } finally {
    loading.value = false
  }
}

function getStatusClass(status) {
  const map = {
    'online': 'dot-running',
    'offline': 'dot-offline',
    'high_load': 'dot-highload',
    'unknown': 'dot-offline'
  }
  return map[status] || 'dot-offline'
}

function getStatusLabel(status) {
  const map = {
    'online': 'Running',
    'offline': 'Offline',
    'high_load': 'High Load',
    'unknown': 'Unknown'
  }
  return map[status] || 'Unknown'
}

function handleSearch() {
  pagination.pageNum = 1
  fetchHosts()
}

function handlePageChange(page) {
  pagination.pageNum = page
  fetchHosts()
}

function resetForm() {
  formData.hostname = ''
  formData.ip = ''
  formData.port = 22
  formData.osType = ''
  formData.osVersion = ''
  formData.groupId = null
  formData.description = ''
  formData.sshUser = ''
  formData.sshPort = 22
  formData.authType = 'password'
  formData.sshPassword = ''
  formData.sshKey = ''
}

async function handleSubmit() {
  if (!formData.hostname || !formData.ip) {
    ElMessage.warning(locale.value === 'zh' ? '请填写必填项' : 'Please fill required fields')
    return
  }
  
  try {
    await createHost(formData)
    ElMessage.success(locale.value === 'zh' ? '创建成功' : 'Created successfully')
    showAddDialog.value = false
    resetForm()
    fetchHosts()
  } catch (error) {
    console.error('Failed to create host:', error)
  }
}

async function handleDelete(host) {
  try {
    await ElMessageBox.confirm(
      locale.value === 'zh' ? `确定要删除主机 ${host.hostname} 吗？` : `Are you sure to delete host ${host.hostname}?`,
      locale.value === 'zh' ? '确认删除' : 'Confirm Delete',
      { type: 'warning' }
    )
    await deleteHost(host.id)
    ElMessage.success(locale.value === 'zh' ? '删除成功' : 'Deleted successfully')
    fetchHosts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete host:', error)
    }
  }
}

async function handleCheckConnectivity(host) {
  try {
    await checkHostConnectivity(host.id)
    ElMessage.success(locale.value === 'zh' ? '连通性测试已发起' : 'Connectivity check initiated')
    setTimeout(fetchHosts, 2000)
  } catch (error) {
    console.error('Failed to check connectivity:', error)
  }
}

async function handleBatchCheck() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning(locale.value === 'zh' ? '请选择主机' : 'Please select hosts')
    return
  }
  
  try {
    await batchCheckConnectivity(selectedIds.value)
    ElMessage.success(locale.value === 'zh' ? '批量连通性测试已发起' : 'Batch connectivity check initiated')
    setTimeout(fetchHosts, 2000)
  } catch (error) {
    console.error('Failed to batch check connectivity:', error)
  }
}

function handleSelectAll(e) {
  if (e.target.checked) {
    selectedIds.value = hosts.value.map(h => h.id)
  } else {
    selectedIds.value = []
  }
}

function handleSelect(id) {
  const idx = selectedIds.value.indexOf(id)
  if (idx > -1) {
    selectedIds.value.splice(idx, 1)
  } else {
    selectedIds.value.push(id)
  }
}

function goToDetail(host) {
  router.push(`/assets/${host.id}`)
}

onMounted(() => {
  fetchHosts()
})
</script>

<style scoped>
.assets-page {
  padding: 32px;
  max-width: 1600px;
  margin: 0 auto;
}

.page-title {
  font-size: 30px;
  font-weight: 900;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: -0.02em;
}
.page-subtitle {
  font-size: 12px;
  color: var(--on-surface-variant);
  margin-top: 4px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.action-btn-primary {
  padding: 10px 20px;
  background: linear-gradient(135deg, #0F62FE 0%, #4589FF 100%);
  border-radius: 8px;
  border: none;
  color: white;
  font-family: var(--font-label);
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: opacity 0.2s;
}
.action-btn-primary:hover { opacity: 0.9; }

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  gap: 16px;
}

.search-area {
  display: flex;
  align-items: center;
  background: var(--bg-surface-low);
  border-radius: 9999px;
  padding: 8px 16px;
  border: 1px solid rgba(66, 70, 86, 0.08);
  min-width: 300px;
}
.search-icon { font-size: 16px; color: var(--outline); }
.search-area input {
  background: transparent;
  border: none;
  outline: none;
  color: #fff;
  padding-left: 10px;
  font-size: 13px;
  flex: 1;
}
.search-area input::placeholder { color: var(--outline); }

.filter-group {
  display: flex;
  gap: 8px;
  align-items: center;
}

.filter-select {
  background: var(--bg-surface-low);
  border: 1px solid rgba(66, 70, 86, 0.08);
  border-radius: 8px;
  padding: 8px 14px;
  color: #fff;
  font-size: 12px;
  outline: none;
  cursor: pointer;
}
.filter-select option { background: var(--bg-surface-container); }

.filter-btn {
  padding: 8px 14px;
  background: var(--bg-surface-low);
  border: 1px solid rgba(66, 70, 86, 0.08);
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  color: var(--on-surface-variant);
  transition: background 0.2s;
}
.filter-btn:hover { background: var(--bg-surface-high); }

.table-section {
  margin-top: 20px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(66, 70, 86, 0.06);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}
.data-table th {
  text-align: left;
  padding: 14px 18px;
  font-family: var(--font-label);
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  background: var(--bg-surface-highest);
  border-bottom: 1px solid rgba(66, 70, 86, 0.08);
}
.data-table td {
  padding: 15px 18px;
  border-bottom: 1px solid rgba(66, 70, 86, 0.04);
  color: var(--on-surface);
}

.id-cell { color: var(--primary); font-weight: 600; }

.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 8px;
}
.dot-running { background: var(--primary-container); }
.dot-highload { background: #FFB800; }
.dot-offline { background: var(--error); }

.action-links {
  display: flex;
  gap: 12px;
}
.action-links a {
  font-size: 11px;
  color: var(--primary-container);
  text-decoration: none;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
.action-links a.danger { color: var(--error); }
.action-links a:hover { text-decoration: underline; }

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

/* Dialog styles */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.form-field { display: flex; flex-direction: column; gap: 6px; }
.form-field.full-width { grid-column: span 2; }
.form-field label {
  font-size: 11px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}
.form-field input,
.form-field select,
.form-field textarea {
  background: var(--bg-base);
  border: 1px solid var(--outline-variant);
  border-radius: 8px;
  padding: 10px 12px;
  color: #fff;
  font-size: 13px;
  outline: none;
  font-family: var(--font-body);
}
.form-field textarea { resize: vertical; }
.form-field input:focus,
.form-field select:focus,
.form-field textarea:focus {
  border-color: var(--primary-container);
}

.upload-zone {
  border: 2px dashed var(--outline-variant);
  border-radius: 12px;
  padding: 48px;
  text-align: center;
  cursor: pointer;
  transition: border-color 0.2s;
}
.upload-zone:hover { border-color: var(--primary-container); }
.upload-icon { font-size: 40px; color: var(--outline); }
.upload-zone p { margin-top: 12px; color: var(--on-surface-variant); font-size: 13px; }
.upload-zone .hint { font-size: 11px; color: var(--outline); margin-top: 8px; }

.btn-cancel {
  padding: 10px 24px;
  background: transparent;
  border: 1px solid var(--outline-variant);
  border-radius: 8px;
  color: var(--on-surface-variant);
  font-weight: 600;
  cursor: pointer;
  font-size: 13px;
}
.btn-submit {
  padding: 10px 28px;
  border: none;
  border-radius: 8px;
  color: var(--on-primary-container);
  font-weight: 700;
  font-size: 13px;
  cursor: pointer;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.no-data {
  text-align: center; padding: 60px;
  color: var(--on-surface-variant); font-size: 14px;
}
</style>
