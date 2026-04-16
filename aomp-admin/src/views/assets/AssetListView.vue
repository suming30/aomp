<template>
  <div class="assets-page">
    <div class="page-header">
      <div class="header-copy">
        <h1 class="page-title">{{ isZh ? '主机管理' : 'Host Management' }}</h1>
        <p class="page-subtitle">
          {{ isZh ? '补齐主机新增、编辑、删除与连通性检测，并支持演示数据验证。' : 'Complete host CRUD and connectivity checks with demo-data support.' }}
        </p>
      </div>
      <div class="header-actions">
        <button class="action-btn secondary" @click="toggleDemoMode">
          <span class="material-symbols-outlined">science</span>
          {{ useDemoMode ? (isZh ? '切到真实接口' : 'Use Live API') : (isZh ? '切到演示数据' : 'Use Demo Data') }}
        </button>
        <button class="action-btn secondary" @click="handleSeedImport">
          <span class="material-symbols-outlined">upload</span>
          {{ isZh ? '导入虚拟数据' : 'Import Demo Data' }}
        </button>
        <button class="action-btn primary litho-gradient" @click="openCreateDialog">
          <span class="material-symbols-outlined">add</span>
          {{ isZh ? '新增主机' : 'Add Host' }}
        </button>
      </div>
    </div>

    <div v-if="useDemoMode" class="demo-banner">
      <span class="material-symbols-outlined">info</span>
      <span>{{ isZh ? '当前为演示模式，增删改查会写入浏览器本地虚拟数据。' : 'Demo mode is active. CRUD changes are stored in browser-local demo data.' }}</span>
    </div>

    <div class="toolbar">
      <div class="search-area">
        <span class="material-symbols-outlined search-icon">search</span>
        <input
          v-model.trim="searchKey"
          type="text"
          :placeholder="isZh ? '搜索 IP、主机名、别名、负责人' : 'Search IP, hostname, alias, owner'"
          @keyup.enter="handleSearch"
        />
      </div>
      <div class="filter-group">
        <select v-model="filterStatus" class="filter-select" @change="handleSearch">
          <option value="">{{ isZh ? '全部状态' : 'All Status' }}</option>
          <option value="online">{{ isZh ? '在线' : 'Online' }}</option>
          <option value="offline">{{ isZh ? '离线' : 'Offline' }}</option>
        </select>
        <button class="filter-btn" @click="handleSearch">
          <span class="material-symbols-outlined">filter_alt</span>
        </button>
        <button class="filter-btn" :disabled="selectedIds.length === 0" @click="handleBatchCheck">
          <span class="material-symbols-outlined">network_check</span>
        </button>
      </div>
    </div>

    <div class="table-section" v-loading="loading">
      <table class="data-table">
        <thead>
          <tr>
            <th>
              <input
                type="checkbox"
                :checked="hosts.length > 0 && selectedIds.length === hosts.length"
                @change="handleSelectAll"
              />
            </th>
            <th>{{ isZh ? '主机名' : 'Hostname' }}</th>
            <th>{{ isZh ? 'IP 地址' : 'IP Address' }}</th>
            <th>{{ isZh ? '操作系统' : 'OS' }}</th>
            <th>{{ isZh ? '状态' : 'Status' }}</th>
            <th>{{ isZh ? '负责人' : 'Owner' }}</th>
            <th>{{ isZh ? '最后检测' : 'Last Check' }}</th>
            <th>{{ isZh ? '操作' : 'Actions' }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in hosts" :key="item.id">
            <td>
              <input
                type="checkbox"
                :checked="selectedIds.includes(item.id)"
                @change="toggleSelect(item.id)"
              />
            </td>
            <td>
              <div class="host-name-cell">
                <span class="font-mono id-cell">{{ item.hostname }}</span>
                <span v-if="item.alias" class="sub-text">{{ item.alias }}</span>
              </div>
            </td>
            <td class="font-mono">{{ item.ipAddress }}</td>
            <td>{{ item.osType || '-' }}</td>
            <td>
              <span :class="['status-dot', statusClassMap[item.status] || 'dot-offline']"></span>
              {{ statusLabel(item.status) }}
            </td>
            <td>{{ item.managerName || '-' }}</td>
            <td class="font-mono text-muted">{{ formatDate(item.lastCheckTime) }}</td>
            <td>
              <div class="action-links">
                <a href="#" @click.prevent="openEditDialog(item)">{{ isZh ? '编辑' : 'Edit' }}</a>
                <a href="#" @click.prevent="handleCheckConnectivity(item)">{{ isZh ? '检测' : 'Check' }}</a>
                <a href="#" class="danger" @click.prevent="handleDelete(item)">{{ isZh ? '删除' : 'Delete' }}</a>
              </div>
            </td>
          </tr>
          <tr v-if="hosts.length === 0 && !loading">
            <td colspan="8" class="no-data">
              {{ isZh ? '暂无主机数据，可点击“导入虚拟数据”快速验证流程。' : 'No host data yet. Import demo data to verify the flow quickly.' }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination">
      <span class="page-info font-mono">
        {{ pageSummary }}
      </span>
      <div class="page-nav">
        <button class="page-btn" :disabled="pagination.pageNum === 1" @click="handlePageChange(pagination.pageNum - 1)">
          {{ isZh ? '上一页' : 'Prev' }}
        </button>
        <button
          v-for="page in visiblePages"
          :key="page"
          :class="['page-btn', { active: page === pagination.pageNum }]"
          @click="handlePageChange(page)"
        >
          {{ page }}
        </button>
        <button class="page-btn" :disabled="pagination.pageNum >= totalPages" @click="handlePageChange(pagination.pageNum + 1)">
          {{ isZh ? '下一页' : 'Next' }}
        </button>
      </div>
    </div>

    <el-dialog
      v-model="showFormDialog"
      :title="editingId ? (isZh ? '编辑主机' : 'Edit Host') : (isZh ? '新增主机' : 'Add Host')"
      width="720px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <div class="form-grid">
        <div class="form-field">
          <label>{{ isZh ? '主机名' : 'Hostname' }} *</label>
          <input v-model.trim="hostForm.hostname" :placeholder="isZh ? '如 prod-web-01' : 'e.g. prod-web-01'" />
        </div>
        <div class="form-field">
          <label>{{ isZh ? '主机别名' : 'Alias' }}</label>
          <input v-model.trim="hostForm.alias" :placeholder="isZh ? '如 生产应用节点01' : 'Optional alias'" />
        </div>
        <div class="form-field">
          <label>{{ isZh ? 'IP 地址' : 'IP Address' }} *</label>
          <input
            v-model.trim="hostForm.ipAddress"
            :disabled="Boolean(editingId)"
            placeholder="192.168.1.100"
          />
        </div>
        <div class="form-field">
          <label>{{ isZh ? 'SSH 端口' : 'SSH Port' }}</label>
          <input v-model.number="hostForm.sshPort" type="number" min="1" max="65535" />
        </div>
        <div class="form-field">
          <label>{{ isZh ? '操作系统' : 'Operating System' }}</label>
          <input v-model.trim="hostForm.osType" :placeholder="isZh ? '如 Ubuntu 22.04' : 'e.g. Ubuntu 22.04'" />
        </div>
        <div class="form-field">
          <label>{{ isZh ? 'SSH 用户' : 'SSH User' }}</label>
          <input v-model.trim="hostForm.sshUser" placeholder="root" />
        </div>
        <div class="form-field">
          <label>{{ isZh ? 'CPU 核数' : 'CPU Cores' }}</label>
          <input v-model.number="hostForm.cpuCores" type="number" min="1" />
        </div>
        <div class="form-field">
          <label>{{ isZh ? '内存(GB)' : 'Memory (GB)' }}</label>
          <input v-model.number="hostForm.memoryGb" type="number" min="1" />
        </div>
        <div class="form-field">
          <label>{{ isZh ? '磁盘(GB)' : 'Disk (GB)' }}</label>
          <input v-model.number="hostForm.diskGb" type="number" min="1" />
        </div>
        <div class="form-field">
          <label>{{ isZh ? '认证方式' : 'Auth Type' }}</label>
          <select v-model="hostForm.sshAuthType">
            <option value="password">{{ isZh ? '密码' : 'Password' }}</option>
            <option value="key">{{ isZh ? '密钥' : 'SSH Key' }}</option>
          </select>
        </div>
        <div class="form-field full-width" v-if="hostForm.sshAuthType === 'password'">
          <label>{{ isZh ? 'SSH 密码' : 'SSH Password' }}</label>
          <input v-model.trim="hostForm.sshPassword" type="password" :placeholder="editingId ? '******' : '******'" />
        </div>
        <div class="form-field full-width" v-else>
          <label>{{ isZh ? 'SSH 私钥' : 'SSH Private Key' }}</label>
          <textarea v-model.trim="hostForm.sshKey" rows="4" placeholder="-----BEGIN OPENSSH PRIVATE KEY-----"></textarea>
        </div>
        <div class="form-field full-width">
          <label>{{ isZh ? '备注' : 'Remark' }}</label>
          <textarea v-model.trim="hostForm.remark" rows="3" :placeholder="isZh ? '填写用途、机房位置等' : 'Describe the host purpose or location'" />
        </div>
      </div>
      <template #footer>
        <button class="btn-cancel" @click="showFormDialog = false">{{ isZh ? '取消' : 'Cancel' }}</button>
        <button class="btn-submit litho-gradient" :disabled="submitting" @click="handleSubmit">
          {{ submitting ? (isZh ? '提交中...' : 'Submitting...') : (isZh ? '保存' : 'Save') }}
        </button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  batchCheckConnectivity,
  checkHostConnectivity,
  createHost,
  deleteHost,
  getHostList,
  importHosts,
  updateHost
} from '../../api/host'
import {
  batchCheckMockHosts,
  checkMockHostConnectivity,
  createImportedDemoHosts,
  createMockHost,
  deleteMockHost,
  importMockHosts,
  isDemoModeEnabled,
  listMockHosts,
  resetMockHosts,
  setDemoModeEnabled,
  updateMockHost
} from '../../utils/hostMockStore'

const { locale } = useI18n()

const isZh = computed(() => locale.value === 'zh')
const loading = ref(false)
const submitting = ref(false)
const showFormDialog = ref(false)
const editingId = ref(null)
const useDemoMode = ref(isDemoModeEnabled())
const hosts = ref([])
const selectedIds = ref([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const searchKey = ref('')
const filterStatus = ref('')

const hostForm = reactive({
  hostname: '',
  alias: '',
  ipAddress: '',
  sshPort: 22,
  osType: '',
  cpuCores: null,
  memoryGb: null,
  diskGb: null,
  sshUser: 'root',
  sshAuthType: 'password',
  sshPassword: '',
  sshKey: '',
  remark: ''
})

const statusClassMap = {
  online: 'dot-running',
  offline: 'dot-offline'
}

const totalPages = computed(() => Math.max(1, Math.ceil(pagination.total / pagination.pageSize)))

const visiblePages = computed(() => {
  const start = Math.max(1, pagination.pageNum - 2)
  const end = Math.min(totalPages.value, pagination.pageNum + 2)
  const pages = []
  for (let page = start; page <= end; page += 1) {
    pages.push(page)
  }
  return pages
})

const pageSummary = computed(() => {
  if (pagination.total === 0) {
    return isZh.value ? '当前没有数据' : 'No data'
  }

  const start = (pagination.pageNum - 1) * pagination.pageSize + 1
  const end = Math.min(pagination.pageNum * pagination.pageSize, pagination.total)
  return isZh.value
    ? `显示 ${start}-${end} 条，共 ${pagination.total} 条`
    : `Showing ${start}-${end} of ${pagination.total}`
})

const statusLabel = status => {
  const labels = {
    online: isZh.value ? '在线' : 'Online',
    offline: isZh.value ? '离线' : 'Offline'
  }
  return labels[status] || status
}

const formatDate = value => {
  if (!value) {
    return '-'
  }
  return String(value).replace('T', ' ').slice(0, 19)
}

const toFormPayload = () => ({
  hostname: hostForm.hostname,
  alias: hostForm.alias,
  ipAddress: hostForm.ipAddress,
  sshPort: hostForm.sshPort || 22,
  osType: hostForm.osType,
  cpuCores: hostForm.cpuCores,
  memoryGb: hostForm.memoryGb,
  diskGb: hostForm.diskGb,
  sshUser: hostForm.sshUser,
  sshAuthType: hostForm.sshAuthType,
  sshPassword: hostForm.sshAuthType === 'password' ? hostForm.sshPassword : '',
  sshKey: hostForm.sshAuthType === 'key' ? hostForm.sshKey : '',
  remark: hostForm.remark
})

const resetForm = () => {
  editingId.value = null
  hostForm.hostname = ''
  hostForm.alias = ''
  hostForm.ipAddress = ''
  hostForm.sshPort = 22
  hostForm.osType = ''
  hostForm.cpuCores = null
  hostForm.memoryGb = null
  hostForm.diskGb = null
  hostForm.sshUser = 'root'
  hostForm.sshAuthType = 'password'
  hostForm.sshPassword = ''
  hostForm.sshKey = ''
  hostForm.remark = ''
}

const validateForm = () => {
  if (!hostForm.hostname || !hostForm.ipAddress) {
    ElMessage.warning(isZh.value ? '请填写主机名和 IP 地址。' : 'Hostname and IP address are required.')
    return false
  }

  const ipv4Pattern = /^(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}$/
  if (!ipv4Pattern.test(hostForm.ipAddress)) {
    ElMessage.warning(isZh.value ? '请输入正确的 IPv4 地址。' : 'Please enter a valid IPv4 address.')
    return false
  }

  if (!hostForm.sshPort || Number(hostForm.sshPort) < 1 || Number(hostForm.sshPort) > 65535) {
    ElMessage.warning(isZh.value ? 'SSH 端口需在 1-65535 之间。' : 'SSH port must be between 1 and 65535.')
    return false
  }

  return true
}

const applyHostResult = result => {
  hosts.value = result.records || []
  pagination.total = result.total || 0
  selectedIds.value = selectedIds.value.filter(id => hosts.value.some(item => item.id === id))
}

const fetchHosts = async () => {
  loading.value = true

  try {
    if (useDemoMode.value) {
      applyHostResult(listMockHosts({
        pageNum: pagination.pageNum,
        pageSize: pagination.pageSize,
        keyword: searchKey.value,
        status: filterStatus.value
      }))
      return
    }

    const res = await getHostList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKey.value,
      status: filterStatus.value
    })

    applyHostResult({
      records: (res.data.records || []).map(item => ({
        id: item.id,
        hostname: item.hostname || `HOST-${item.id}`,
        alias: item.alias || '',
        ipAddress: item.ipAddress,
        osType: item.osType,
        sshPort: item.sshPort,
        sshUser: item.sshUser,
        sshAuthType: item.sshAuthType,
        cpuCores: item.cpuCores,
        memoryGb: item.memoryGb,
        diskGb: item.diskGb,
        managerId: item.managerId,
        managerName: item.managerName,
        status: item.status,
        remark: item.remark,
        lastCheckTime: item.lastCheckTime,
        createTime: item.createTime,
        updateTime: item.updateTime
      })),
      total: res.data.total,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
  } catch (error) {
    console.error('Failed to fetch hosts:', error)
    useDemoMode.value = true
    setDemoModeEnabled(true)
    applyHostResult(listMockHosts({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKey.value,
      status: filterStatus.value
    }))
    ElMessage.warning(isZh.value ? '真实接口不可用，已自动切换到演示数据模式。' : 'Live API is unavailable. Switched to demo data mode.')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchHosts()
}

const handlePageChange = page => {
  pagination.pageNum = page
  fetchHosts()
}

const handleSelectAll = event => {
  selectedIds.value = event.target.checked ? hosts.value.map(item => item.id) : []
}

const toggleSelect = id => {
  const index = selectedIds.value.indexOf(id)
  if (index >= 0) {
    selectedIds.value.splice(index, 1)
    return
  }
  selectedIds.value.push(id)
}

const openCreateDialog = () => {
  resetForm()
  showFormDialog.value = true
}

const openEditDialog = host => {
  editingId.value = host.id
  hostForm.hostname = host.hostname || ''
  hostForm.alias = host.alias || ''
  hostForm.ipAddress = host.ipAddress || ''
  hostForm.sshPort = host.sshPort || 22
  hostForm.osType = host.osType || ''
  hostForm.cpuCores = host.cpuCores || null
  hostForm.memoryGb = host.memoryGb || null
  hostForm.diskGb = host.diskGb || null
  hostForm.sshUser = host.sshUser || 'root'
  hostForm.sshAuthType = host.sshAuthType || 'password'
  hostForm.sshPassword = ''
  hostForm.sshKey = ''
  hostForm.remark = host.remark || ''
  showFormDialog.value = true
}

const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  submitting.value = true

  try {
    const payload = toFormPayload()

    if (useDemoMode.value) {
      if (editingId.value) {
        updateMockHost(editingId.value, payload)
      } else {
        createMockHost(payload)
      }
    } else if (editingId.value) {
      await updateHost(editingId.value, payload)
    } else {
      await createHost(payload)
    }

    ElMessage.success(isZh.value ? '主机信息已保存。' : 'Host has been saved.')
    showFormDialog.value = false
    resetForm()
    await fetchHosts()
  } catch (error) {
    console.error('Failed to submit host form:', error)
    ElMessage.error(error?.message || (isZh.value ? '保存失败。' : 'Save failed.'))
  } finally {
    submitting.value = false
  }
}

const handleDelete = async host => {
  try {
    await ElMessageBox.confirm(
      isZh.value ? `删除后将移除主机 ${host.hostname}，是否继续？` : `Delete host ${host.hostname}?`,
      isZh.value ? '确认删除' : 'Confirm Delete',
      { type: 'warning' }
    )

    if (useDemoMode.value) {
      deleteMockHost(host.id)
    } else {
      await deleteHost(host.id)
    }

    ElMessage.success(isZh.value ? '主机已删除。' : 'Host deleted.')

    if (hosts.value.length === 1 && pagination.pageNum > 1) {
      pagination.pageNum -= 1
    }

    await fetchHosts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete host:', error)
      ElMessage.error(isZh.value ? '删除失败。' : 'Delete failed.')
    }
  }
}

const handleCheckConnectivity = async host => {
  try {
    if (useDemoMode.value) {
      checkMockHostConnectivity(host.id)
    } else {
      await checkHostConnectivity(host.id)
    }

    ElMessage.success(isZh.value ? '连通性检测已完成。' : 'Connectivity check completed.')
    await fetchHosts()
  } catch (error) {
    console.error('Failed to check connectivity:', error)
    ElMessage.error(isZh.value ? '连通性检测失败。' : 'Connectivity check failed.')
  }
}

const handleBatchCheck = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning(isZh.value ? '请先选择主机。' : 'Please select hosts first.')
    return
  }

  try {
    if (useDemoMode.value) {
      batchCheckMockHosts(selectedIds.value)
    } else {
      await batchCheckConnectivity(selectedIds.value)
    }

    ElMessage.success(isZh.value ? '批量检测已完成。' : 'Batch check completed.')
    await fetchHosts()
  } catch (error) {
    console.error('Failed to batch check hosts:', error)
    ElMessage.error(isZh.value ? '批量检测失败。' : 'Batch check failed.')
  }
}

const handleSeedImport = async () => {
  try {
    if (useDemoMode.value) {
      importMockHosts(createImportedDemoHosts())
    } else {
      await importHosts(createImportedDemoHosts())
    }

    ElMessage.success(isZh.value ? '已导入一批虚拟主机。' : 'Demo hosts imported.')
    pagination.pageNum = 1
    await fetchHosts()
  } catch (error) {
    console.error('Failed to import demo hosts:', error)
    ElMessage.error(isZh.value ? '导入虚拟数据失败。' : 'Failed to import demo hosts.')
  }
}

const toggleDemoMode = async () => {
  useDemoMode.value = !useDemoMode.value
  setDemoModeEnabled(useDemoMode.value)

  if (useDemoMode.value) {
    resetMockHosts()
    ElMessage.info(isZh.value ? '已切换到演示数据模式。' : 'Switched to demo data mode.')
  } else {
    ElMessage.info(isZh.value ? '已切换到真实接口模式。' : 'Switched to live API mode.')
  }

  pagination.pageNum = 1
  selectedIds.value = []
  await fetchHosts()
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

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.header-copy {
  max-width: 720px;
}

.page-title {
  font-size: 30px;
  font-weight: 900;
  color: #fff;
  letter-spacing: -0.02em;
}

.page-subtitle {
  margin-top: 6px;
  color: var(--on-surface-variant);
  font-size: 12px;
}

.header-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.action-btn {
  padding: 10px 18px;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 700;
}

.action-btn.secondary {
  background: var(--bg-surface-high);
  color: #fff;
  border: 1px solid rgba(66, 70, 86, 0.1);
}

.action-btn.primary {
  color: #fff;
}

.demo-banner {
  margin-top: 18px;
  display: flex;
  gap: 10px;
  align-items: center;
  padding: 12px 16px;
  border-radius: 12px;
  color: #d7e5ff;
  background: rgba(15, 98, 254, 0.14);
  border: 1px solid rgba(15, 98, 254, 0.22);
  font-size: 12px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-top: 24px;
}

.search-area {
  display: flex;
  align-items: center;
  min-width: 340px;
  padding: 10px 16px;
  border-radius: 9999px;
  background: var(--bg-surface-low);
  border: 1px solid rgba(66, 70, 86, 0.08);
}

.search-area input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: #fff;
  font-size: 13px;
  padding-left: 10px;
}

.search-icon {
  color: var(--outline);
  font-size: 18px;
}

.filter-group {
  display: flex;
  gap: 8px;
  align-items: center;
}

.filter-select,
.filter-btn,
.page-btn,
.btn-cancel,
.btn-submit,
.form-field input,
.form-field select,
.form-field textarea {
  border-radius: 10px;
}

.filter-select,
.filter-btn {
  background: var(--bg-surface-low);
  border: 1px solid rgba(66, 70, 86, 0.08);
  color: #fff;
}

.filter-select {
  padding: 10px 14px;
  min-width: 140px;
}

.filter-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 12px;
  cursor: pointer;
}

.table-section {
  margin-top: 20px;
  border: 1px solid rgba(66, 70, 86, 0.06);
  border-radius: 16px;
  overflow: hidden;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.data-table th {
  padding: 14px 16px;
  text-align: left;
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  background: var(--bg-surface-highest);
}

.data-table td {
  padding: 15px 16px;
  border-top: 1px solid rgba(66, 70, 86, 0.04);
  color: var(--on-surface);
}

.host-name-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.id-cell {
  color: var(--primary-container);
  font-weight: 700;
}

.sub-text,
.text-muted,
.page-info {
  color: var(--on-surface-variant);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
  margin-right: 8px;
}

.dot-running {
  background: var(--primary-container);
}

.dot-offline {
  background: var(--error);
}

.action-links {
  display: flex;
  gap: 12px;
}

.action-links a {
  color: var(--primary-container);
  text-decoration: none;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.action-links a.danger {
  color: var(--error);
}

.pagination {
  margin-top: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.page-nav {
  display: flex;
  gap: 6px;
}

.page-btn {
  padding: 8px 12px;
  background: transparent;
  border: 1px solid rgba(66, 70, 86, 0.1);
  color: var(--on-surface-variant);
  cursor: pointer;
}

.page-btn.active {
  background: var(--primary-container);
  color: #fff;
  border-color: transparent;
}

.page-btn:disabled,
.filter-btn:disabled,
.btn-submit:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-field.full-width {
  grid-column: span 2;
}

.form-field label {
  color: var(--on-surface-variant);
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}

.form-field input,
.form-field select,
.form-field textarea {
  background: var(--bg-base);
  border: 1px solid var(--outline-variant);
  color: #fff;
  padding: 10px 12px;
  outline: none;
  font-size: 13px;
}

.form-field textarea {
  resize: vertical;
}

.btn-cancel {
  padding: 10px 22px;
  background: transparent;
  color: var(--on-surface-variant);
  border: 1px solid var(--outline-variant);
  cursor: pointer;
}

.btn-submit {
  padding: 10px 24px;
  border: none;
  color: #fff;
  cursor: pointer;
}

.no-data {
  padding: 52px 16px;
  text-align: center;
  color: var(--on-surface-variant);
}

@media (max-width: 960px) {
  .page-header,
  .toolbar,
  .pagination {
    flex-direction: column;
    align-items: stretch;
  }

  .search-area {
    min-width: 0;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-field.full-width {
    grid-column: span 1;
  }
}
</style>
