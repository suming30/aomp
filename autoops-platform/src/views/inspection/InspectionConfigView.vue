<template>
  <div class="inspection-config-page">
    <div class="page-header">
      <!-- <div>
        <h1 class="page-title">{{ t('inspection.configTitle') }}</h1>
        <p class="page-subtitle">{{ t('inspection.configSubtitle') }}</p>
      </div> -->
      <div class="header-actions">
        <button class="action-btn secondary" :disabled="loading">
          <span class="material-symbols-outlined">save</span>{{ locale === 'zh' ? '保存草稿' : 'Save Draft' }}
        </button>
        <button class="action-btn primary litho-gradient" @click="handleRunInspection" :disabled="loading">
          <span class="material-symbols-outlined">play_arrow</span>{{ locale === 'zh' ? '执行巡检' : 'Run Inspection' }}
        </button>
      </div>
    </div>

    <div class="config-layout">
      <main class="config-main">
        <!-- Host Selection -->
        <section class="config-section">
          <div class="step-header">
            <span class="step-num">1</span>
            <h3>{{ t('inspection.hostSelection') }}</h3>
          </div>

          <div class="fuzzy-search-box">
            <span class="material-symbols-outlined search-icon">search</span>
            <input type="text" placeholder="Search nodes by IP, hostname or tag..." />
            <span class="fuzzy-badge font-mono">{{ t('inspection.fuzzySearch') }}</span>
          </div>

          <div class="tag-cloud">
            <span v-for="(tag, idx) in tags" :key="idx"
                  :class="['tag-chip', tag.selected ? 'selected' : '']"
                  @click="toggleTag(tag)">
              {{ tag.name }}
            </span>
          </div>

          <div class="host-table-wrap" v-loading="loading">
            <table class="host-table">
              <thead>
                <tr>
                  <th><input type="checkbox" /></th>
                  <th>{{ locale === 'zh' ? '主机名' : 'Hostname' }}</th>
                  <th>{{ locale === 'zh' ? 'IP地址' : 'IP Address' }}</th>
                  <th>{{ locale === 'zh' ? '状态' : 'Status' }}</th>
                  <th>{{ locale === 'zh' ? '上次巡检' : 'Last Inspected' }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(h, idx) in hostList" :key="idx">
                  <td><input type="checkbox" v-model="h.checked" /></td>
                  <td class="font-mono">{{ h.name }}</td>
                  <td class="font-mono">{{ h.ip }}</td>
                  <td><span :class="['dot', h.statusClass]"></span> {{ h.statusLabel }}</td>
                  <td class="font-mono text-muted">{{ h.lastCheck }}</td>
                </tr>
                <tr v-if="hostList.length === 0 && !loading">
                  <td colspan="5" class="no-data">{{ locale === 'zh' ? '暂无主机数据' : 'No host data' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <!-- Execution Settings -->
        <section class="config-section">
          <div class="step-header">
            <span class="step-num">2</span>
            <h3>{{ locale === 'zh' ? '执行设置' : t('inspection.execSettings') }}</h3>
          </div>

          <div class="settings-grid">
            <div class="setting-item">
              <label>{{ locale === 'zh' ? '巡检类型' : 'Inspection Type' }}</label>
              <select v-model="formData.inspectionType">
                <option value="full">{{ locale === 'zh' ? '完整系统健康检查' : 'Full System Health Check' }}</option>
                <option value="security">{{ locale === 'zh' ? '安全漏洞扫描' : 'Security Vulnerability Scan' }}</option>
                <option value="performance">{{ locale === 'zh' ? '性能基准测试' : 'Performance Benchmark' }}</option>
              </select>
            </div>
            <div class="setting-item">
              <label>{{ locale === 'zh' ? '调度方式' : 'Schedule' }}</label>
              <select v-model="formData.schedule">
                <option value="immediate">{{ locale === 'zh' ? '立即执行' : 'Run Now (Immediate)' }}</option>
                <option value="daily">{{ locale === 'zh' ? '每天 02:00 UTC' : 'Daily at 02:00 UTC' }}</option>
                <option value="weekly">{{ locale === 'zh' ? '每周一' : 'Weekly on Monday' }}</option>
              </select>
            </div>
            <div class="setting-item">
              <label>{{ locale === 'zh' ? '并发数' : 'Concurrency Level' }}</label>
              <input type="number" v-model="formData.concurrency" />
            </div>
            <div class="setting-item">
              <label>{{ locale === 'zh' ? '单节点超时(秒)' : 'Timeout per Node (s)' }}</label>
              <input type="number" v-model="formData.timeout" />
            </div>
            <div class="setting-item full">
              <label>{{ locale === 'zh' ? '通知渠道' : 'Notification Channel' }}</label>
              <div class="notif-options">
                <label class="notif-check"><input type="checkbox" v-model="formData.notifications.email" /> {{ locale === 'zh' ? '邮件通知' : 'Email Alert' }}</label>
                <label class="notif-check"><input type="checkbox" v-model="formData.notifications.webhook" /> Webhook</label>
                <label class="notif-check"><input type="checkbox" v-model="formData.notifications.slack" /> Slack</label>
              </div>
            </div>
          </div>
        </section>
      </main>

      <aside class="config-sidebar">
        <div class="sidebar-card">
          <h4 class="sidebar-title">{{ locale === 'zh' ? '巡检范围' : 'Inspection Scope' }}</h4>
          <div class="scope-list">
            <div class="scope-item">
              <span class="scope-label">{{ locale === 'zh' ? '已选主机' : 'Selected Nodes' }}</span>
              <span class="scope-val font-mono">{{ selectedCount }}</span>
            </div>
            <div class="scope-item">
              <span class="scope-label">{{ locale === 'zh' ? '巡检类型' : 'Inspection Type' }}</span>
              <span class="scope-val">{{ formData.inspectionType === 'full' ? (locale === 'zh' ? '完整健康检查' : 'Full Health') : formData.inspectionType }}</span>
            </div>
            <div class="scope-item">
              <span class="scope-label">{{ locale === 'zh' ? '预计时长' : 'Est. Duration' }}</span>
              <span class="scope-val font-mono">~{{ estimatedDuration }}s</span>
            </div>
            <div class="scope-item">
              <span class="scope-label">{{ locale === 'zh' ? '报告格式' : 'Report Format' }}</span>
              <span class="scope-val">PDF + JSON</span>
            </div>
          </div>
          <div class="checklist-preview">
            <h5>{{ locale === 'zh' ? '检查项目:' : 'Checks Included:' }}</h5>
            <ul>
              <li>{{ locale === 'zh' ? 'CPU/内存使用率' : 'CPU / Memory Utilization' }}</li>
              <li>{{ locale === 'zh' ? '磁盘I/O和空间分析' : 'Disk I/O & Space Analysis' }}</li>
              <li>{{ locale === 'zh' ? '网络延迟和丢包率' : 'Network Latency & Packet Loss' }}</li>
              <li>{{ locale === 'zh' ? '服务健康状态' : 'Service Health Status' }}</li>
              <li>{{ locale === 'zh' ? '安全补丁状态' : 'Security Patch Status' }}</li>
              <li>{{ locale === 'zh' ? '证书过期检查' : 'Certificate Expiry' }}</li>
            </ul>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getInspectionItems, createInspectionItem, executeInspection, getInspectionTasks } from '../../api/inspection'
import { getHostList } from '../../api/host'

const { t, locale } = useI18n()
const router = useRouter()

const tags = ref([
  { name: 'K8S-PROD-01', selected: true },
  { name: 'EDGE-GW-02', selected: false },
  { name: 'DB-CLUSTER-A', selected: true },
  { name: 'MONITORING', selected: false },
  { name: 'STAGING', selected: false },
  { name: 'ALL-NODES', selected: false }
])

const hostList = ref([])
const inspectionItems = ref([])
const loading = ref(false)

const formData = reactive({
  hostIds: [],
  itemIds: [],
  taskName: '',
  inspectionType: 'full',
  schedule: 'immediate',
  concurrency: 20,
  timeout: 120,
  notifications: {
    email: true,
    webhook: true,
    slack: false
  }
})

async function fetchHosts() {
  try {
    const res = await getHostList({ pageNum: 1, pageSize: 100 })
    hostList.value = (res.data.records || []).map(h => ({
      id: h.id,
      name: h.hostname,
      ip: h.ip,
      statusClass: getStatusClass(h.status),
      statusLabel: getStatusLabel(h.status),
      lastCheck: h.lastInspectionTime || '-',
      checked: false
    }))
  } catch (error) {
    console.error('Failed to fetch hosts:', error)
  }
}

async function fetchInspectionItems() {
  try {
    const res = await getInspectionItems({ pageNum: 1, pageSize: 100 })
    inspectionItems.value = res.data.records || []
    formData.itemIds = inspectionItems.value.filter(i => i.isDefault).map(i => i.id)
  } catch (error) {
    console.error('Failed to fetch inspection items:', error)
  }
}

function getStatusClass(status) {
  const map = {
    'online': 'dot-green',
    'offline': 'dot-red',
    'high_load': 'dot-yellow',
    'warning': 'dot-yellow'
  }
  return map[status] || 'dot-green'
}

function getStatusLabel(status) {
  const map = {
    'online': 'Running',
    'offline': 'Offline',
    'high_load': 'Warning',
    'warning': 'Warning'
  }
  return map[status] || 'Running'
}

const selectedHosts = computed(() => hostList.value.filter(h => h.checked))
const selectedCount = computed(() => selectedHosts.value.length)

function toggleTag(tag) {
  tag.selected = !tag.selected
}

async function handleRunInspection() {
  if (selectedCount.value === 0) {
    ElMessage.warning(locale.value === 'zh' ? '请选择至少一台主机' : 'Please select at least one host')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      locale.value === 'zh' 
        ? `即将对 ${selectedCount.value} 台主机执行巡检，是否继续？` 
        : `About to run inspection on ${selectedCount.value} hosts. Continue?`,
      locale.value === 'zh' ? '确认执行' : 'Confirm Execution',
      { type: 'info' }
    )
    
    loading.value = true
    formData.hostIds = selectedHosts.value.map(h => h.id)
    
    const res = await executeInspection(formData.taskName || undefined, {
      hostIds: formData.hostIds,
      itemIds: formData.itemIds
    })
    
    ElMessage.success(locale.value === 'zh' ? '巡检任务已启动' : 'Inspection task started')
    router.push(`/inspection/report/${res.data?.id}`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to run inspection:', error)
    }
  } finally {
    loading.value = false
  }
}

const estimatedDuration = computed(() => {
  const hostCount = selectedCount.value || 1
  const batchCount = Math.ceil(hostCount / formData.concurrency)
  return Math.ceil(batchCount * (formData.timeout / 60))
})

onMounted(() => {
  fetchHosts()
  fetchInspectionItems()
})
</script>

<style scoped>
.inspection-config-page {
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
  font-size: 26px; font-weight: 900; color: #fff;
  text-transform: uppercase; letter-spacing: -0.02em;
}
.page-subtitle { font-size: 12px; color: var(--on-surface-variant); margin-top: 6px; }

.header-actions { display: flex; gap: 10px; }

.action-btn {
  padding: 10px 20px; border-radius: 8px; border: none;
  font-family: var(--font-label);
  font-size: 11px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.08em;
  cursor: pointer; display: flex; align-items: center; gap: 6px;
}
.action-btn.secondary {
  background: var(--bg-surface-high); color: #fff;
  border: 1px solid rgba(66, 70, 86, 0.1);
}
.action-btn.secondary:hover { background: var(--bg-bright); }
.action-btn.primary { background: linear-gradient(135deg, #0F62FE 0%, #4589FF 100%); color: #fff; box-shadow: 0 4px 15px rgba(15, 98, 254, 0.25); }

.config-layout {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 24px;
  margin-top: 28px;
}

.config-section {
  background: var(--bg-surface-container-low);
  border-radius: 16px;
  padding: 28px;
  margin-bottom: 20px;
  border: 1px solid rgba(66, 70, 86, 0.06);
}

.step-header { margin-bottom: 20px; }

.step-num {
  display: inline-flex; align-items: center; justify-content: center;
  width: 26px; height: 26px; border-radius: 50%;
  background: var(--primary-container); color: white;
  font-size: 13px; font-weight: 800; margin-right: 10px;
}
.step-header h3 {
  display: inline; font-size: 15px; font-weight: 700; color: #fff;
  text-transform: uppercase; letter-spacing: 0.04em;
}

.fuzzy-search-box {
  display: flex;
  align-items: center;
  background: var(--bg-base);
  border: 1px solid var(--outline-variant);
  border-radius: 10px;
  padding: 10px 16px;
  margin-bottom: 16px;
}
.fuzzy-search-box .search-icon { font-size: 18px; color: var(--outline); margin-right: 10px; }
.fuzzy-search-box input {
  flex: 1; background: transparent; border: none; outline: none;
  color: #fff; font-size: 13px;
}
.fuzzy-badge {
  font-size: 9px;
  color: var(--outline);
  padding: 3px 8px;
  background: var(--bg-surface-high);
  border-radius: 4px;
  letter-spacing: 0.08em;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.tag-chip {
  padding: 6px 14px;
  border-radius: 9999px;
  font-size: 11px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s;
  background: var(--bg-surface-high);
  color: var(--on-surface-variant);
  border: 1px solid transparent;
}
.tag-chip:hover { background: var(--bg-bright); }
.tag-chip.selected {
  background: rgba(15, 98, 254, 0.1);
  color: var(--primary-container);
  border-color: rgba(15, 98, 254, 0.2);
}

.host-table-wrap { overflow-x: auto; }

.host-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}
.host-table th {
  text-align: left;
  padding: 10px 14px;
  font-family: var(--font-label);
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  background: var(--bg-surface-highest);
  border-bottom: 1px solid rgba(66, 70, 86, 0.08);
}
.host-table td {
  padding: 12px 14px;
  border-bottom: 1px solid rgba(66, 70, 86, 0.04);
  color: var(--on-surface);
}

.dot {
  display: inline-block;
  width: 7px; height: 7px;
  border-radius: 50%;
  margin-right: 6px;
}
.dot-green { background: var(--primary-container); }
.dot-yellow { background: #FFB800; }
.dot-red { background: var(--error); }
.text-muted { color: var(--on-surface-variant); }

.settings-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.setting-item { display: flex; flex-direction: column; gap: 6px; }
.setting-item.full { grid-column: span 2; }

.setting-item label {
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}
.setting-item select,
.setting-item input[type="number"] {
  background: var(--bg-base);
  border: 1px solid var(--outline-variant);
  border-radius: 8px;
  padding: 9px 12px;
  color: #fff;
  font-size: 13px;
  outline: none;
}
.setting-item select option { background: var(--bg-surface-container); }

.notif-options { display: flex; gap: 20px; flex-wrap: wrap; }

.notif-check {
  display: flex; align-items: center; gap: 6px;
  font-size: 12px; color: var(--on-surface); cursor: pointer;
}
.notif-check input[type="checkbox"] { accent-color: var(--primary-container); }

.config-sidebar { position: relative; }

.sidebar-card {
  position: sticky; top: 106px;
  background: var(--bg-surface-container);
  border-radius: 16px;
  padding: 24px;
  border: 1px solid rgba(66, 70, 86, 0.08);
}

.sidebar-title {
  font-size: 12px;
  font-weight: 800;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin-bottom: 20px;
}

.scope-list { display: flex; flex-direction: column; gap: 14px; margin-bottom: 20px; }

.scope-item {
  display: flex; justify-content: space-between; align-items: center;
}
.scope-label { font-size: 11px; color: var(--on-surface-variant); }
.scope-val { font-size: 12px; font-weight: 600; color: #fff; }

.checklist-preview {
  padding-top: 16px;
  border-top: 1px solid rgba(66, 70, 86, 0.08);
}
.checklist-preview h5 {
  font-size: 10px;
  font-weight: 800;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 10px;
}
.checklist-preview ul {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.checklist-preview li {
  font-size: 11px;
  color: var(--on-surface);
  padding-left: 14px;
  position: relative;
}
.checklist-preview li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: var(--primary-container);
  font-size: 10px;
  font-weight: 700;
}

.no-data {
  text-align: center; padding: 40px;
  color: var(--on-surface-variant); font-size: 14px;
}
</style>
