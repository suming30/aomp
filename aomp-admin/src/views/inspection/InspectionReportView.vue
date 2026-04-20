<template>
  <div class="report-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">{{ isZh ? '巡检报告详情' : 'Inspection Report' }}</h1>
        <p class="page-subtitle font-mono">
          {{ isZh ? '任务ID' : 'Task ID' }}: {{ reportId }}
          |
          {{ isZh ? '生成时间' : 'Generated At' }}: {{ formatDate(taskInfo.endTime || taskInfo.createTime) }}
        </p>
      </div>
      <div class="header-actions">
        <button class="action-btn secondary" @click="reloadReport">{{ isZh ? '刷新' : 'Refresh' }}</button>
      </div>
    </div>

    <div class="health-score-section glass-card">
      <div class="score-display">
        <div class="score-center">
          <span class="score-num font-mono">{{ healthScore }}</span>
          <span class="score-label">{{ isZh ? '健康评分' : 'Health Score' }}</span>
          <span class="score-status good">{{ healthScore >= 85 ? (isZh ? '良好' : 'Good') : (isZh ? '关注' : 'Needs Attention') }}</span>
        </div>
      </div>
      <div class="score-info">
        <p class="score-desc">
          {{ isZh ? '根据巡检结果自动计算评分，异常越少得分越高。' : 'Score is calculated from inspection results; fewer anomalies means a higher score.' }}
        </p>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card glass-card">
        <p class="stat-value font-mono">{{ taskInfo.totalHosts || 0 }}</p>
        <p class="stat-label">{{ isZh ? '总主机数' : 'Total Hosts' }}</p>
      </div>
      <div class="stat-card glass-card">
        <p class="stat-value font-mono success-text">{{ taskInfo.normalHosts || 0 }}</p>
        <p class="stat-label">{{ isZh ? '正常' : 'Normal' }}</p>
      </div>
      <div class="stat-card glass-card">
        <p class="stat-value font-mono warn-text">{{ taskInfo.warningHosts || 0 }}</p>
        <p class="stat-label">{{ isZh ? '告警' : 'Warning' }}</p>
      </div>
      <div class="stat-card glass-card">
        <p class="stat-value font-mono error-text">{{ taskInfo.errorHosts || 0 }}</p>
        <p class="stat-label">{{ isZh ? '异常' : 'Error' }}</p>
      </div>
    </div>

    <section class="report-table-section glass-card">
      <h3>{{ isZh ? '巡检明细' : 'Per-Node Inspection Details' }}</h3>
      <table class="report-table">
        <thead>
          <tr>
            <th>{{ isZh ? '主机' : 'Host' }}</th>
            <th>{{ isZh ? '状态' : 'Status' }}</th>
            <th>{{ isZh ? '实际值' : 'Actual' }}</th>
            <th>{{ isZh ? '阈值' : 'Threshold' }}</th>
            <th>{{ isZh ? '详情' : 'Detail' }}</th>
            <th>{{ isZh ? '时间' : 'Time' }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in resultRows" :key="item.id">
            <td class="font-mono">{{ item.hostLabel }}</td>
            <td>
              <span :class="['status-tag', item.statusClass]">{{ item.statusLabel }}</span>
            </td>
            <td class="font-mono">{{ item.actualValue || '-' }}</td>
            <td class="font-mono">{{ item.thresholdValue || '-' }}</td>
            <td>{{ item.detail || '-' }}</td>
            <td class="font-mono">{{ formatDate(item.createTime) }}</td>
          </tr>
          <tr v-if="resultRows.length === 0 && !loading">
            <td colspan="6" class="no-data">{{ isZh ? '暂无巡检明细' : 'No inspection records' }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { getInspectionTaskById, getInspectionTaskResults } from '../../api/inspection'
import { getMockInspectionTaskById, listMockInspectionResults, resetMockInspection } from '../../utils/inspectionMockStore'

const route = useRoute()
const reportId = computed(() => route.params.id)
const { locale } = useI18n()
const isZh = computed(() => locale.value === 'zh')

const loading = ref(false)
const useFallback = ref(false)
const taskInfo = ref({})
const resultRows = ref([])

const healthScore = computed(() => {
  const total = Number(taskInfo.value.totalHosts || 0)
  if (!total) return 0
  const warning = Number(taskInfo.value.warningHosts || 0)
  const error = Number(taskInfo.value.errorHosts || 0)
  const score = 100 - Math.round((warning * 1.5 + error * 4) / total * 100)
  return Math.max(0, score)
})

const formatDate = value => (value ? String(value).replace('T', ' ').slice(0, 19) : '-')

const toStatus = status => {
  const lower = String(status || '').toLowerCase()
  if (lower === 'normal') return { statusLabel: isZh.value ? '正常' : 'NORMAL', statusClass: 'success' }
  if (lower === 'warning') return { statusLabel: isZh.value ? '告警' : 'WARNING', statusClass: 'warn' }
  return { statusLabel: isZh.value ? '异常' : 'ERROR', statusClass: 'error' }
}

const mapRows = rows =>
  rows.map(item => ({
    ...item,
    hostLabel: item.hostName ? `${item.hostName} (${item.hostIp || `HOST-${item.hostId}`})` : (item.hostIp || `HOST-${item.hostId}`),
    ...toStatus(item.status)
  }))

const loadFallback = () => {
  useFallback.value = true
  taskInfo.value = getMockInspectionTaskById(reportId.value) || {}
  resultRows.value = mapRows(listMockInspectionResults(reportId.value))
}

const reloadReport = async () => {
  loading.value = true
  try {
    if (useFallback.value) {
      loadFallback()
      return
    }
    const [taskRes, resultRes] = await Promise.all([
      getInspectionTaskById(reportId.value),
      getInspectionTaskResults(reportId.value)
    ])
    taskInfo.value = taskRes.data || {}
    resultRows.value = mapRows(resultRes.data || [])
  } catch (error) {
    console.error('Failed to load inspection report:', error)
    resetMockInspection()
    loadFallback()
    ElMessage.warning(isZh.value ? '巡检报告接口异常，已切换演示数据。' : 'Inspection report API unavailable, switched to demo data.')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  reloadReport()
})
</script>

<style scoped>
.report-page { padding: 32px; max-width: 1400px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-title { font-size: 26px; color: #fff; font-weight: 900; }
.page-subtitle { margin-top: 6px; color: var(--on-surface-variant); font-size: 11px; }
.health-score-section { margin-top: 20px; border-radius: 14px; padding: 20px; display: flex; align-items: center; gap: 24px; }
.score-display { min-width: 160px; text-align: center; }
.score-num { color: #fff; font-size: 40px; font-weight: 900; }
.score-label { color: var(--on-surface-variant); display: block; font-size: 11px; margin-top: 4px; }
.score-status { display: block; margin-top: 6px; font-size: 12px; font-weight: 700; color: var(--primary-container); }
.score-desc { color: var(--on-surface); font-size: 13px; line-height: 1.5; }
.stats-grid { margin-top: 16px; display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.stat-card { border-radius: 12px; padding: 16px; text-align: center; }
.stat-value { font-size: 32px; color: #fff; font-weight: 900; }
.stat-label { margin-top: 4px; color: var(--on-surface-variant); font-size: 10px; text-transform: uppercase; }
.success-text { color: var(--primary-container); }
.warn-text { color: #FFB800; }
.error-text { color: var(--error); }
.report-table-section { border-radius: 14px; margin-top: 20px; padding: 20px; }
.report-table-section h3 { color: #fff; font-size: 14px; margin-bottom: 12px; text-transform: uppercase; }
.report-table { width: 100%; border-collapse: collapse; font-size: 12px; }
.report-table th { text-align: left; color: var(--on-surface-variant); font-size: 10px; border-bottom: 1px solid rgba(66,70,86,0.1); padding: 10px 12px; text-transform: uppercase; }
.report-table td { padding: 12px; color: var(--on-surface); border-bottom: 1px solid rgba(66,70,86,0.05); }
.status-tag { font-size: 10px; border-radius: 9999px; padding: 2px 10px; font-weight: 700; }
.status-tag.success { color: var(--primary-container); background: rgba(15,98,254,0.12); }
.status-tag.warn { color: #FFB800; background: rgba(255,184,0,0.12); }
.status-tag.error { color: var(--error); background: rgba(147,0,10,0.15); }
.no-data { text-align: center; color: var(--on-surface-variant); padding: 36px; }
</style>
