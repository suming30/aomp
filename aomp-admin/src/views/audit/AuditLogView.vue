<template>
  <div class="audit-page">
    <div class="page-header">
      <div class="header-actions">
        <button class="action-btn secondary" @click="handleSearch">
          <span class="material-symbols-outlined">filter_list</span>
          {{ locale === 'zh' ? '高级筛选' : t('audit.advancedFilter') }}
        </button>
        <button class="action-btn primary litho-gradient" @click="handleExport">
          <span class="material-symbols-outlined">download</span>
          {{ locale === 'zh' ? '导出审计报告' : t('audit.exportAuditReport') }}
        </button>
      </div>
    </div>

    <div class="filter-bar">
      <div class="filter-group">
        <select class="filter-select" v-model="queryParams.module">
          <option v-for="opt in moduleOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
        </select>
        <select class="filter-select" v-model="queryParams.level">
          <option v-for="opt in levelOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
        </select>
        <input type="date" v-model="queryParams.startTime" class="date-input" />
        <span class="range-sep">—</span>
        <input type="date" v-model="queryParams.endTime" class="date-input" />
      </div>
      <div class="search-area">
        <span class="material-symbols-outlined search-icon">search</span>
        <input v-model="queryParams.username" :placeholder="locale === 'zh' ? '搜索操作人或资源...' : 'Search by operator or resource...'" @keyup.enter="handleSearch" />
      </div>
    </div>

    <div class="table-section glass-card" v-loading="loading">
      <table class="audit-table">
        <thead>
          <tr>
            <th>{{ locale === 'zh' ? '操作人' : t('audit.operator') }}</th>
            <th>{{ locale === 'zh' ? '操作类型' : t('audit.operationType') }}</th>
            <th>{{ locale === 'zh' ? '目标资源' : t('audit.targetResource') }}</th>
            <th>{{ locale === 'zh' ? '时间戳' : t('audit.timestamp') }}</th>
            <th>{{ locale === 'zh' ? '结果' : t('audit.result') }}</th>
            <th>{{ locale === 'zh' ? '来源IP' : t('audit.sourceIp') }}</th>
            <th>{{ locale === 'zh' ? '详情' : t('audit.details') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, idx) in tableData" :key="idx">
            <td>
              <div class="operator-cell">
                <img :src="`https://ui-avatars.com/api/?name=${row.username || 'SYS'}&background=333&color=fff`" class="op-avatar" />
                <span>{{ row.username || 'system' }}</span>
              </div>
            </td>
            <td><span :class="['op-type', getModuleClass(row.module)]">{{ row.action || row.module }}</span></td>
            <td class="font-mono text-muted">{{ row.resource || '-' }}</td>
            <td class="font-mono text-muted">{{ row.createdAt || row.time }}</td>
            <td><span :class="['result-tag', getResultClass(row.result)]">{{ row.result || 'SUCCESS' }}</span></td>
            <td class="font-mono text-muted">{{ row.ip || '-' }}</td>
            <td><a href="#" class="detail-link">{{ locale === 'zh' ? '查看' : 'View' }} →</a></td>
          </tr>
          <tr v-if="tableData.length === 0 && !loading">
            <td colspan="7" class="no-data">{{ locale === 'zh' ? '暂无审计日志数据' : 'No audit log data' }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination">
      <span class="page-info font-mono">{{ locale === 'zh' ? `显示 ${(queryParams.pageNum - 1) * queryParams.pageSize + 1}-${Math.min(queryParams.pageNum * queryParams.pageSize, total)} 条，共 ${total} 条` : `Showing ${(queryParams.pageNum - 1) * queryParams.pageSize + 1}-${Math.min(queryParams.pageNum * queryParams.pageSize, total)} of ${total} records` }}</span>
      <div class="page-nav">
        <button class="page-btn" :disabled="queryParams.pageNum <= 1" @click="handlePageChange(queryParams.pageNum - 1)">← {{ locale === 'zh' ? '上一页' : 'Prev' }}</button>
        <button v-for="p in Math.min(5, Math.ceil(total / queryParams.pageSize))" :key="p" class="page-btn" :class="{ active: p === queryParams.pageNum }" @click="handlePageChange(p)">{{ p }}</button>
        <button class="page-btn" :disabled="queryParams.pageNum >= Math.ceil(total / queryParams.pageSize)" @click="handlePageChange(queryParams.pageNum + 1)">{{ locale === 'zh' ? '下一页' : 'Next' }} →</button>
      </div>
    </div>

    <div class="security-notice glass-card">
      <span class="material-symbols-outlined notice-icon">shield</span>
      <div>
        <h4>{{ locale === 'zh' ? '合规信息' : 'Compliance Information' }}</h4>
        <p class="font-mono notice-text">{{ locale === 'zh' ? '所有审计日志使用 AES-256 加密存储，根据 SOX 合规要求保留 365 天。启用区块链哈希链实现防篡改日志记录。' : 'All audit logs are encrypted at rest (AES-256) and retained for 365 days per SOX compliance. Tamper-evident logging enabled via blockchain hash chain.' }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { getAuditLogList, exportAuditLogs } from '../../api/audit'
import { ElMessage } from 'element-plus'

const { t, locale } = useI18n()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  module: '',
  action: '',
  level: '',
  username: '',
  startTime: '',
  endTime: ''
})

const dateRange = ref([])

async function fetchAuditLogs() {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    const res = await getAuditLogList(params)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('Failed to fetch audit logs:', error)
    ElMessage.error(locale.value === 'zh' ? '获取审计日志失败' : 'Failed to fetch audit logs')
  } finally {
    loading.value = false
  }
}

async function handleExport() {
  try {
    const params = {
      module: queryParams.module,
      level: queryParams.level,
      startTime: queryParams.startTime,
      endTime: queryParams.endTime
    }
    const res = await exportAuditLogs(params)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `audit_logs_${new Date().toISOString().split('T')[0]}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success(locale.value === 'zh' ? '导出成功' : 'Export successful')
  } catch (error) {
    console.error('Failed to export audit logs:', error)
    ElMessage.error(locale.value === 'zh' ? '导出失败' : 'Export failed')
  }
}

function handleSearch() {
  queryParams.pageNum = 1
  fetchAuditLogs()
}

function handlePageChange(page) {
  queryParams.pageNum = page
  fetchAuditLogs()
}

function handleSizeChange(size) {
  queryParams.pageSize = size
  queryParams.pageNum = 1
  fetchAuditLogs()
}

function getModuleClass(module) {
  const map = {
    'auth': 'op-auth',
    'login': 'op-auth',
    'config': 'op-config',
    'script': 'op-exec',
    'task': 'op-exec',
    'user': 'op-config',
    'auto': 'op-auto',
    'security': 'op-deny'
  }
  return map[module?.toLowerCase()] || 'op-auth'
}

function getResultClass(result) {
  const map = {
    'SUCCESS': 'res-success',
    'success': 'res-success',
    'FAILED': 'res-error',
    'failed': 'res-error',
    'DENIED': 'res-error',
    'denied': 'res-error'
  }
  return map[result] || 'res-success'
}

const moduleOptions = computed(() => [
  { value: '', label: locale.value === 'zh' ? '全部模块' : 'All Modules' },
  { value: 'auth', label: locale.value === 'zh' ? '认证登录' : 'Auth/Login' },
  { value: 'config', label: locale.value === 'zh' ? '配置变更' : 'Config Change' },
  { value: 'script', label: locale.value === 'zh' ? '脚本执行' : 'Script Execution' },
  { value: 'task', label: locale.value === 'zh' ? '任务管理' : 'Task Management' },
  { value: 'user', label: locale.value === 'zh' ? '用户管理' : 'User Management' }
])

const levelOptions = computed(() => [
  { value: '', label: locale.value === 'zh' ? '全部结果' : 'All Results' },
  { value: 'SUCCESS', label: locale.value === 'zh' ? '成功' : 'Success' },
  { value: 'FAILED', label: locale.value === 'zh' ? '失败' : 'Failed' },
  { value: 'DENIED', label: locale.value === 'zh' ? '拒绝' : 'Denied' }
])

onMounted(() => {
  fetchAuditLogs()
})
</script>

<style scoped>
.audit-page {
  padding: 32px;
  max-width: 1600px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 28px; font-weight: 900; color: #fff;
  text-transform: uppercase; letter-spacing: -0.02em;
}
.page-subtitle { font-size: 12px; color: var(--on-surface-variant); margin-top: 4px; }

.header-actions { display: flex; gap: 8px; }

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  gap: 20px;
}

.filter-group { display: flex; align-items: center; gap: 8px; }

.filter-select {
  background: var(--bg-surface-low);
  border: 1px solid rgba(66, 70, 86, 0.08);
  border-radius: 8px;
  padding: 8px 12px;
  color: #fff;
  font-size: 11px;
  outline: none;
  cursor: pointer;
}
.filter-select option { background: var(--bg-surface-container); }

.date-input {
  background: var(--bg-surface-low);
  border: 1px solid rgba(66, 70, 86, 0.08);
  border-radius: 8px;
  padding: 8px 12px;
  color: #fff;
  font-size: 11px;
  outline: none;
  font-family: var(--font-body);
}
.range-sep { color: var(--outline); }

.search-area {
  display: flex;
  align-items: center;
  background: var(--bg-surface-low);
  border-radius: 9999px;
  padding: 8px 16px;
  min-width: 260px;
  border: 1px solid rgba(66, 70, 86, 0.08);
}
.search-icon { font-size: 16px; color: var(--outline); margin-right: 8px; }
.search-area input {
  background: transparent; border: none; outline: none;
  color: #fff; font-size: 12px; flex: 1;
}
.search-area input::placeholder { color: var(--outline); }

.table-section {
  border-radius: 16px;
  padding: 24px;
  margin-top: 20px;
  overflow-x: auto;
}

.audit-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 11.5px;
}
.audit-table th {
  text-align: left;
  padding: 11px 14px;
  font-family: var(--font-label);
  font-size: 9px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  background: var(--bg-surface-highest);
  border-bottom: 1px solid rgba(66, 70, 86, 0.08);
}
.audit-table td {
  padding: 13px 14px;
  border-bottom: 1px solid rgba(66, 70, 86, 0.04);
  color: var(--on-surface);
}

.operator-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
.op-avatar {
  width: 26px; height: 26px;
  border-radius: 50%; object-fit: cover;
}

.op-type {
  font-size: 10px;
  font-weight: 700;
  padding: 3px 8px;
  border-radius: 4px;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}
.op-auth { background: rgba(180, 197, 255, 0.08); color: var(--primary); }
.op-config { background: rgba(200, 64, 0, 0.08); color: var(--tertiary); }
.op-exec { background: rgba(15, 98, 254, 0.08); color: var(--primary-container); }
.op-auto { background: rgba(57, 153, 174, 0.08); color: #3999AE; }
.op-deny { background: rgba(147, 0, 10, 0.1); color: var(--error); }

.text-muted { color: var(--on-surface-variant); }

.result-tag {
  font-size: 9px;
  font-weight: 800;
  padding: 3px 10px;
  border-radius: 9999px;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}
.res-success { background: rgba(15, 98, 254, 0.12); color: var(--primary-container); }
.res-error { background: rgba(147, 0, 10, 0.15); color: var(--error); }

.detail-link {
  font-size: 10px;
  color: var(--primary-container);
  font-weight: 600;
  text-decoration: none;
}
.detail-link:hover { text-decoration: underline; }

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 12px;
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

.security-notice {
  border-radius: 14px;
  padding: 20px 24px;
  margin-top: 24px;
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.notice-icon {
  font-size: 28px;
  color: var(--primary-container);
  opacity: 0.6;
  flex-shrink: 0;
}

.notice-icon + div h4 {
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 4px;
}

.notice-text {
  font-size: 10.5px;
  color: var(--on-surface-variant);
  line-height: 1.6;
}

.no-data {
  text-align: center; padding: 40px;
  color: var(--on-surface-variant); font-size: 14px;
}
</style>
