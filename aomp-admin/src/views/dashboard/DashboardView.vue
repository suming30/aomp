<template>
  <div class="dashboard-page">
    <div class="page-header">
      <div class="header-actions">
        <button class="action-btn secondary">
          <span class="material-symbols-outlined">refresh</span>
          {{ t('dashboard.refreshFreq') }}
        </button>
        <button class="action-btn primary litho-gradient">{{ t('dashboard.exportReport') }}</button>
      </div>
    </div>

    <div class="bento-row-1">
      <div class="metric-card card-inspection">
        <div class="card-content">
          <p class="metric-label">{{ locale === 'zh' ? '巡检概览' : t('dashboard.inspectionOverview') }}</p>
          <div class="metric-value-row">
            <span class="metric-value">{{ formatNumber(dashboardData.inspectionTotal) }}</span>
            <span class="metric-trend up">{{ dashboardData.inspectionWarning > 0 ? `${dashboardData.inspectionWarning} ${locale === 'zh' ? '异常' : 'Warnings'}` : (locale === 'zh' ? '正常' : 'OK') }}</span>
          </div>
          <div class="mini-bar">
            <div class="bar-fill" :style="{ width: `${Math.min(100, (dashboardData.hostOnline / Math.max(1, dashboardData.hostTotal)) * 100)}%` }"></div>
          </div>
        </div>
        <span class="card-bg-icon material-symbols-outlined">fact_check</span>
      </div>

      <div class="metric-card card-tasks">
        <div class="card-content">
          <p class="metric-label">{{ locale === 'zh' ? '待处理任务' : t('dashboard.pendingTasks') }}</p>
          <div class="metric-value-row">
            <span class="metric-value">{{ dashboardData.taskRunning }}</span>
            <span class="status-badge active">{{ locale === 'zh' ? '执行中' : 'ACTIVE' }}</span>
          </div>
          <p class="task-info">{{ locale === 'zh' ? `今日任务: ${dashboardData.taskToday}` : `Today: ${dashboardData.taskToday} tasks` }}</p>
        </div>
        <span class="card-bg-icon material-symbols-outlined">pending_actions</span>
      </div>

      <div class="metric-card card-exceptions">
        <div class="card-content">
          <p class="metric-label">{{ locale === 'zh' ? '异常统计' : t('dashboard.exceptionStatistics') }}</p>
          <div class="metric-value-row">
            <span class="metric-value" :class="{ 'error-text': dashboardData.hostOffline > 0 }">{{ formatNumber(dashboardData.hostOffline) }}</span>
            <span v-if="dashboardData.hostOffline > 0" class="pulse-dot error-bg"></span>
          </div>
          <p class="exception-detail font-mono">{{ locale === 'zh' ? `在线: ${dashboardData.hostOnline} / 离线: ${dashboardData.hostOffline}` : `Online: ${dashboardData.hostOnline} / Offline: ${dashboardData.hostOffline}` }}</p>
        </div>
        <span class="card-bg-icon material-symbols-outlined" :class="{ 'error-color': dashboardData.hostOffline > 0 }">warning</span>
      </div>
    </div>

    <div class="bento-row-2">
      <section class="chart-section">
        <div class="chart-header">
          <div>
            <h3 class="chart-title">{{ t('dashboard.weeklyTaskExecution') }}</h3>
            <p class="chart-subtitle">Aggregated workflow throughput across all clusters</p>
          </div>
          <div class="chart-legend">
            <div class="legend-item"><span class="legend-dot primary-dot"></span>{{ t('dashboard.success') }}</div>
            <div class="legend-item"><span class="legend-dot error-dot"></span>{{ t('dashboard.failed') }}</div>
          </div>
        </div>
        <div class="bar-chart">
          <div v-for="(day, idx) in weekData" :key="idx" class="chart-col">
            <div class="chart-bars">
              <div class="bar-success" :style="{ height: day.success + '%' }"></div>
              <div class="bar-fail" :style="{ height: day.fail + '%' }"></div>
              <div class="bar-track"></div>
            </div>
            <span class="chart-label font-mono">{{ day.label }}</span>
          </div>
        </div>
      </section>

      <section class="host-dist-section">
        <div class="dist-header">
          <h3 class="chart-title">{{ t('dashboard.activeHosts') }}</h3>
          <p class="chart-subtitle">Real-time node distribution by region</p>
        </div>
        <div class="dist-list">
          <div v-for="(item, idx) in hostDistData" :key="idx" class="dist-item">
            <div class="dist-info">
              <span class="dist-region font-mono">{{ item.region }}</span>
              <span class="dist-count">{{ item.count }} Nodes</span>
            </div>
            <div class="dist-bar-wrap">
              <div class="dist-bar" :style="{ width: item.percent + '%' }" :class="item.colorClass"></div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <section class="ops-log-section">
      <div class="log-header">
        <h3 class="chart-title">{{ locale === 'zh' ? '最近操作' : t('dashboard.recentOperations') }}</h3>
        <router-link to="/history" class="view-all-link">{{ locale === 'zh' ? '查看全部' : 'View All' }} →</router-link>
      </div>
      <div class="log-table-wrap" v-loading="loading">
        <table class="log-table">
          <thead>
            <tr>
              <th>{{ locale === 'zh' ? '操作' : 'Operation' }}</th>
              <th>{{ locale === 'zh' ? '目标节点' : 'Target Node' }}</th>
              <th>{{ locale === 'zh' ? '执行者' : 'Executor' }}</th>
              <th>{{ locale === 'zh' ? '时间戳' : 'Timestamp' }}</th>
              <th>{{ locale === 'zh' ? '状态' : 'Status' }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, idx) in (dashboardData.recentTasks || [])" :key="idx">
              <td><span class="font-mono log-op">{{ row.taskName || row.op || '-' }}</span></td>
              <td class="font-mono">{{ row.targetHost || row.node || '-' }}</td>
              <td>{{ row.executor || row.username || '-' }}</td>
              <td class="font-mono text-muted">{{ row.createdAt || row.time || '-' }}</td>
              <td><span :class="['status-tag', getStatusClass(row.status)]">{{ row.status || 'SUCCESS' }}</span></td>
            </tr>
            <tr v-if="(dashboardData.recentTasks || []).length === 0 && !loading">
              <td colspan="5" class="no-data">{{ locale === 'zh' ? '暂无操作记录' : 'No recent operations' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { getDashboard } from '../../api/dashboard'

const { t, locale } = useI18n()

const loading = ref(false)
const dashboardData = ref({
  hostTotal: 0,
  hostOnline: 0,
  hostOffline: 0,
  scriptTotal: 0,
  taskTotal: 0,
  taskRunning: 0,
  taskToday: 0,
  inspectionTotal: 0,
  inspectionWarning: 0,
  userTotal: 0,
  approvalPending: 0,
  recentTasks: [],
  recentAlerts: []
})

const weekData = ref([
  { label: 'MON', success: 70, fail: 10 },
  { label: 'TUE', success: 85, fail: 5 },
  { label: 'WED', success: 60, fail: 15 },
  { label: 'THU', success: 90, fail: 2 },
  { label: 'FRI', success: 75, fail: 8 },
  { label: 'SAT', success: 40, fail: 5 },
  { label: 'SUN', success: 30, fail: 0 }
])

const hostDistData = ref([
  { region: 'AP-East (Hangzhou)', count: 428, percent: 85, colorClass: 'bar-primary' },
  { region: 'US-West (Oregon)', count: 212, percent: 60, colorClass: 'bar-blue' },
  { region: 'EU-Central (Frankfurt)', count: 156, percent: 42, colorClass: 'bar-purple' }
])

let refreshTimer = null

async function fetchDashboardData() {
  loading.value = true
  try {
    const res = await getDashboard()
    if (res.data) {
      dashboardData.value = res.data
    }
  } catch (error) {
    console.error('Failed to fetch dashboard data:', error)
  } finally {
    loading.value = false
  }
}

function formatNumber(num) {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num?.toString() || '0'
}

function getStatusClass(status) {
  const map = {
    'SUCCESS': 'success',
    'SUCCESSFUL': 'success',
    'FAILED': 'error',
    'FAILURE': 'error',
    'RUNNING': 'active',
    'WARNING': 'warning'
  }
  return map[status?.toUpperCase()] || 'success'
}

onMounted(() => {
  fetchDashboardData()
  refreshTimer = setInterval(fetchDashboardData, 30000)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped>
.dashboard-page {
  padding: 32px;
  max-width: 1600px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.page-title {
  font-size: 30px;
  font-weight: 900;
  letter-spacing: -0.02em;
  color: #fff;
  text-transform: uppercase;
}

.page-subtitle {
  font-size: 11px;
  color: var(--on-surface-variant);
  opacity: 0.6;
  margin-top: 4px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.bento-row-1 {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.metric-card {
  background: var(--bg-surface-container-low);
  border-radius: 16px;
  padding: 28px;
  position: relative;
  overflow: hidden;
  transition: transform 0.2s;
}

.metric-card:hover { transform: translateY(-2px); }

.card-content { position: relative; z-index: 2; }

.metric-label {
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.12em;
  margin-bottom: 8px;
}

.metric-value-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 16px;
}

.metric-value {
  font-size: 40px;
  font-weight: 900;
  color: #fff;
  letter-spacing: -0.03em;
}

.metric-trend {
  font-size: 14px;
  font-weight: 700;
  color: var(--primary-container);
}

.metric-trend.up::before { content: '+'; }

.mini-bar {
  display: flex;
  gap: 4px;
  height: 4px;
}

.bar-fill {
  flex: 1;
  border-radius: 2px;
  background: var(--primary-container);
}
.bar-partial { opacity: 0.3; }

.status-badge {
  font-size: 10px;
  font-weight: 700;
  padding: 3px 8px;
  border-radius: 9999px;
  letter-spacing: 0.06em;
}
.status-badge.active {
  background: var(--secondary-container);
  color: var(--on-secondary-container);
}

.task-avatars {
  display: flex;
  margin-bottom: 8px;
  margin-top: 12px;
}

.avatar-img {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--bg-surface-container-low);
  margin-right: -8px;
}

.avatar-more {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: var(--bg-surface-high);
  border: 2px solid var(--bg-surface-container-low);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 9px;
  font-weight: 700;
  color: var(--on-surface-variant);
  margin-left: 4px;
}

.task-info {
  font-size: 10px;
  color: var(--on-surface-variant);
}

.exception-detail {
  font-size: 10px;
  color: var(--on-surface-variant);
  margin-top: 12px;
}

.error-text { color: var(--error) !important; }

.pulse-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  animation: pulse-glow 2s infinite;
}
.error-bg { background: var(--error); }

.card-bg-icon {
  position: absolute;
  right: -16px;
  bottom: -16px;
  font-size: 72px;
  opacity: 0.07;
  pointer-events: none;
}
.error-color { color: var(--error); }

.bento-row-2 {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.chart-section, .host-dist-section {
  background: var(--bg-surface-container);
  border-radius: 16px;
  padding: 28px;
  border: 1px solid rgba(66, 70, 86, 0.1);
}

.chart-header, .dist-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
}

.chart-title {
  font-size: 14px;
  font-weight: 800;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.chart-subtitle {
  font-size: 10px;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  margin-top: 4px;
}

.chart-legend {
  display: flex;
  gap: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface);
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}
.primary-dot { background: var(--primary-container); }
.error-dot { background: var(--error); }

.bar-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 8px;
  height: 220px;
  padding: 0 8px;
}

.chart-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
  justify-content: flex-end;
}

.chart-bars {
  width: 100%;
  max-width: 42px;
  height: 192px;
  display: flex;
  flex-direction: column-reverse;
  background: rgba(53, 53, 52, 0.3);
  border-radius: 4px 4px 0 0;
  overflow: hidden;
}

.bar-success {
  background: var(--primary-container);
  opacity: 0.8;
  transition: opacity 0.2s;
}
.bar-success:hover { opacity: 1; }

.bar-fail {
  background: rgba(255, 180, 171, 0.5);
}

.bar-track { flex: 1; }

.chart-label {
  margin-top: 8px;
  font-size: 9px;
  color: var(--on-surface-variant);
}

.dist-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.dist-item { display: flex; flex-direction: column; gap: 6px; }

.dist-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dist-region {
  font-size: 11px;
  color: #fff;
  font-weight: 500;
}

.dist-count {
  font-size: 11px;
  color: var(--on-surface-variant);
}

.dist-bar-wrap {
  height: 6px;
  background: var(--bg-surface-highest);
  border-radius: 3px;
  overflow: hidden;
}

.dist-bar {
  height: 100%;
  border-radius: 3px;
  transition: width 0.6s ease;
}
.bar-primary { background: var(--primary-container); }
.bar-blue { background: var(--secondary); }
.bar-purple { background: var(--tertiary); }

.ops-log-section {
  background: var(--bg-surface-container);
  border-radius: 16px;
  padding: 28px;
  border: 1px solid rgba(66, 70, 86, 0.1);
}

.log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.view-all-link {
  font-size: 11px;
  color: var(--primary-container);
  font-weight: 700;
  text-decoration: none;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.log-table-wrap { overflow-x: auto; }

.log-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}

.log-table th {
  text-align: left;
  padding: 12px 16px;
  font-family: var(--font-label);
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  background: var(--bg-surface-highest);
  border-bottom: 1px solid rgba(66, 70, 86, 0.08);
}

.log-table td {
  padding: 14px 16px;
  border-bottom: 1px solid rgba(66, 70, 86, 0.05);
  color: var(--on-surface);
}

.log-op { color: var(--primary); font-weight: 600; }
.text-muted { color: var(--on-surface-variant); }

.status-tag {
  font-size: 10px;
  font-weight: 700;
  padding: 3px 10px;
  border-radius: 9999px;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}
.status-tag.success {
  background: rgba(15, 98, 254, 0.12);
  color: var(--primary-container);
}
.status-tag.error {
  background: rgba(147, 0, 10, 0.15);
  color: var(--error);
}

.no-data {
  text-align: center; padding: 40px;
  color: var(--on-surface-variant); font-size: 14px;
}

@media (max-width: 1200px) {
  .bento-row-1 { grid-template-columns: 1fr; }
  .bento-row-2 { grid-template-columns: 1fr; }
}
</style>
