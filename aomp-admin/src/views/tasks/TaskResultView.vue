<template>
  <div class="task-result-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">{{ isZh ? '任务执行结果' : 'Task Result Summary' }}</h1>
        <p class="page-subtitle font-mono">
          {{ isZh ? '任务ID' : 'Task ID' }}: {{ taskId }} |
          {{ isZh ? '完成时间' : 'Completed At' }}: {{ formatDate(taskInfo.endTime || taskInfo.createTime) }}
        </p>
      </div>
      <div class="header-actions">
        <button class="action-btn secondary" @click="$router.push('/history')">{{ isZh ? '返回任务列表' : 'Back to Tasks' }}</button>
      </div>
    </div>

    <div class="summary-cards">
      <div class="summary-card glass-card">
        <p class="card-label">{{ isZh ? '目标主机' : 'Total Hosts' }}</p>
        <p class="card-value font-mono">{{ taskInfo.totalHosts || 0 }}</p>
      </div>
      <div class="summary-card glass-card">
        <p class="card-label">{{ isZh ? '成功' : 'Success' }}</p>
        <p class="card-value font-mono success-text">{{ taskInfo.successHosts || 0 }}</p>
      </div>
      <div class="summary-card glass-card">
        <p class="card-label">{{ isZh ? '失败' : 'Failed' }}</p>
        <p class="card-value font-mono error-text">{{ taskInfo.failHosts || 0 }}</p>
      </div>
      <div class="summary-card glass-card">
        <p class="card-label">{{ isZh ? '成功率' : 'Success Rate' }}</p>
        <p class="card-value font-mono">{{ successRate }}%</p>
      </div>
    </div>

    <section class="records-section glass-card">
      <h3>{{ isZh ? '执行明细' : 'Execution Details' }}</h3>
      <table class="data-table">
        <thead>
          <tr>
            <th>{{ isZh ? '主机' : 'Host' }}</th>
            <th>{{ isZh ? '日志级别' : 'Log Type' }}</th>
            <th>{{ isZh ? '日志内容' : 'Detail' }}</th>
            <th>{{ isZh ? '时间' : 'Time' }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in records" :key="item.id" :class="{ 'row-fail': item.logTypeClass === 'error' }">
            <td class="font-mono">{{ item.hostIp }}</td>
            <td>
              <span :class="['status-tag', item.logTypeClass]">{{ item.logType }}</span>
            </td>
            <td class="font-mono text-muted">{{ item.content }}</td>
            <td class="font-mono">{{ formatDate(item.createTime) }}</td>
          </tr>
          <tr v-if="records.length === 0 && !loading">
            <td colspan="4" class="no-data">{{ isZh ? '暂无执行明细' : 'No execution details' }}</td>
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
import { getTaskById, getTaskLogs } from '../../api/task'
import { getMockTaskById, getMockTaskLogs, resetMockTasks } from '../../utils/taskMockStore'

const route = useRoute()
const taskId = computed(() => route.params.id)
const { locale } = useI18n()
const isZh = computed(() => locale.value === 'zh')

const loading = ref(false)
const useFallback = ref(false)
const taskInfo = ref({})
const records = ref([])

const successRate = computed(() => {
  const total = Number(taskInfo.value.totalHosts || 0)
  if (!total) return 0
  return Math.round((Number(taskInfo.value.successHosts || 0) / total) * 100)
})

const formatDate = value => (value ? String(value).replace('T', ' ').slice(0, 19) : '-')
const mapLogTypeClass = type => {
  const lower = String(type || '').toLowerCase()
  if (lower.includes('error')) return 'error'
  if (lower.includes('success')) return 'success'
  return 'info'
}

const loadFallback = () => {
  useFallback.value = true
  taskInfo.value = getMockTaskById(taskId.value) || {}
  records.value = getMockTaskLogs({
    taskId: taskId.value,
    pageNum: 1,
    pageSize: 300
  }).records.map(item => ({
    ...item,
    hostIp: item.hostIp || `HOST-${item.hostId}`,
    logTypeClass: mapLogTypeClass(item.logType)
  }))
}

const loadTaskResult = async () => {
  loading.value = true
  try {
    if (useFallback.value) {
      loadFallback()
      return
    }
    const [taskRes, logRes] = await Promise.all([
      getTaskById(taskId.value),
      getTaskLogs(taskId.value, { pageNum: 1, pageSize: 300 })
    ])
    taskInfo.value = taskRes.data || {}
    records.value = (logRes.data.records || []).map(item => ({
      ...item,
      hostIp: `HOST-${item.hostId}`,
      logTypeClass: mapLogTypeClass(item.logType)
    }))
  } catch (error) {
    console.error('Failed to load task result:', error)
    resetMockTasks()
    loadFallback()
    ElMessage.warning(isZh.value ? '任务结果接口异常，已切换演示数据。' : 'Task result API unavailable, switched to demo data.')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadTaskResult()
})
</script>

<style scoped>
.task-result-page { padding: 32px; max-width: 1400px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-title { font-size: 26px; color: #fff; font-weight: 900; }
.page-subtitle { margin-top: 6px; color: var(--on-surface-variant); font-size: 11px; }
.header-actions { display: flex; gap: 8px; }
.summary-cards { margin-top: 20px; display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.summary-card { border-radius: 12px; padding: 18px; }
.card-label { color: var(--on-surface-variant); font-size: 10px; text-transform: uppercase; }
.card-value { margin-top: 8px; font-size: 30px; font-weight: 900; color: #fff; }
.success-text { color: var(--primary-container); }
.error-text { color: var(--error); }
.records-section { border-radius: 14px; margin-top: 20px; padding: 20px; }
.records-section h3 { color: #fff; font-size: 14px; margin-bottom: 14px; text-transform: uppercase; }
.data-table { width: 100%; border-collapse: collapse; font-size: 12px; }
.data-table th { text-align: left; padding: 10px 12px; color: var(--on-surface-variant); font-size: 10px; text-transform: uppercase; border-bottom: 1px solid rgba(66,70,86,0.1); }
.data-table td { padding: 12px; border-bottom: 1px solid rgba(66,70,86,0.04); color: var(--on-surface); }
.text-muted { color: var(--on-surface-variant); }
.status-tag { font-size: 10px; border-radius: 9999px; padding: 2px 10px; font-weight: 700; }
.status-tag.success { color: var(--primary-container); background: rgba(15,98,254,0.12); }
.status-tag.error { color: var(--error); background: rgba(147,0,10,0.15); }
.status-tag.info { color: var(--on-surface-variant); background: rgba(120,120,120,0.2); }
.row-fail td:first-child { color: var(--error); }
.no-data { text-align: center; color: var(--on-surface-variant); padding: 36px; }
</style>
