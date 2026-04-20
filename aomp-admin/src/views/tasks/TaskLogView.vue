<template>
  <div class="task-log-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">{{ isZh ? '实时日志' : 'Real-time Log' }}</h1>
        <p class="page-subtitle font-mono">
          {{ isZh ? '任务ID' : 'Task ID' }}: {{ taskId }}
          |
          {{ isZh ? '状态' : 'Status' }}: {{ taskInfo.status || '-' }}
        </p>
      </div>
      <div class="header-actions">
        <button class="action-btn warning" @click="pauseCurrentTask">{{ isZh ? '暂停' : 'Pause' }}</button>
        <button class="action-btn danger" @click="terminateCurrentTask">{{ isZh ? '终止' : 'Stop' }}</button>
      </div>
    </div>

    <div class="progress-bar-wrap">
      <div class="progress-info">
        <span>{{ isZh ? '进度' : 'Progress' }}</span>
        <span class="font-mono">{{ progressSummary }}</span>
      </div>
      <div class="progress-track">
        <div class="progress-fill success" :style="{ width: `${successPercent}%` }"></div>
        <div class="progress-fill running" :style="{ width: `${runningPercent}%`, left: `${successPercent}%` }"></div>
      </div>
    </div>

    <div class="log-layout">
      <aside class="host-list">
        <h3>{{ isZh ? '目标主机' : 'Target Hosts' }}</h3>
        <button
          v-for="item in hostSummary"
          :key="item.hostId"
          :class="['host-item', { active: selectedHostId === item.hostId }]"
          @click="selectHost(item.hostId)"
        >
          <span class="font-mono">{{ item.hostIp }}</span>
          <span :class="['tag', item.status]">{{ item.statusLabel }}</span>
        </button>
      </aside>
      <main class="log-content">
        <div class="toolbar">
          <span class="font-mono">{{ isZh ? '筛选主机' : 'Host Filter' }}: {{ selectedHostLabel }}</span>
          <button class="tool-btn" @click="reloadLogs">{{ isZh ? '刷新' : 'Refresh' }}</button>
        </div>
        <div v-loading="loading" class="log-lines">
          <div v-for="line in logLines" :key="line.id" :class="['log-line', line.logTypeClass]">
            <span class="line-time">{{ formatDate(line.createTime) }}</span>
            <span class="line-level">[{{ line.logType }}]</span>
            <span class="line-msg">{{ line.content }}</span>
          </div>
          <div v-if="logLines.length === 0 && !loading" class="empty-line">
            {{ isZh ? '暂无日志数据' : 'No log lines' }}
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { getTaskById, getTaskLogs, pauseTask, terminateTask } from '../../api/task'
import { getMockTaskById, getMockTaskLogs, resetMockTasks } from '../../utils/taskMockStore'

const { locale } = useI18n()
const isZh = computed(() => locale.value === 'zh')
const route = useRoute()

const taskId = computed(() => route.params.id)
const loading = ref(false)
const useFallback = ref(false)

const taskInfo = ref({
  totalHosts: 0,
  successHosts: 0,
  runningHosts: 0,
  failHosts: 0,
  status: ''
})
const logLines = ref([])
const hostSummary = ref([])
const selectedHostId = ref(null)

const totalHosts = computed(() => Number(taskInfo.value.totalHosts || 0))
const successPercent = computed(() => totalHosts.value ? Math.round((Number(taskInfo.value.successHosts || 0) / totalHosts.value) * 100) : 0)
const runningPercent = computed(() => totalHosts.value ? Math.round((Number(taskInfo.value.runningHosts || 0) / totalHosts.value) * 100) : 0)
const progressSummary = computed(() =>
  `${taskInfo.value.successHosts || 0}/${taskInfo.value.totalHosts || 0} ${isZh.value ? '已完成' : 'completed'}`
)

const selectedHostLabel = computed(() => {
  if (!selectedHostId.value) {
    return isZh.value ? '全部主机' : 'All Hosts'
  }
  const host = hostSummary.value.find(item => item.hostId === selectedHostId.value)
  return host?.hostIp || String(selectedHostId.value)
})

const formatDate = value => (value ? String(value).replace('T', ' ').slice(0, 19) : '-')

const mapLogTypeClass = type => {
  const lower = String(type || '').toLowerCase()
  if (lower.includes('error')) return 'error'
  if (lower.includes('success')) return 'success'
  return 'info'
}

const mapTaskInfo = task => ({
  totalHosts: task?.totalHosts || 0,
  successHosts: task?.successHosts || 0,
  runningHosts: task?.runningHosts || 0,
  failHosts: task?.failHosts || 0,
  status: task?.status || '-'
})

const buildHostSummary = records => {
  const grouped = new Map()
  records.forEach(item => {
    const hostId = item.hostId || 0
    if (!grouped.has(hostId)) {
      grouped.set(hostId, {
        hostId,
        hostIp: item.hostIp || `HOST-${hostId}`,
        status: 'info',
        statusLabel: 'RUNNING'
      })
    }
    if (String(item.logType || '').toLowerCase().includes('error')) {
      grouped.get(hostId).status = 'error'
      grouped.get(hostId).statusLabel = 'FAILED'
    }
    if (String(item.logType || '').toLowerCase().includes('success')) {
      grouped.get(hostId).status = 'success'
      grouped.get(hostId).statusLabel = 'SUCCESS'
    }
  })
  hostSummary.value = [...grouped.values()]
}

const loadFallback = () => {
  useFallback.value = true
  const task = getMockTaskById(taskId.value)
  taskInfo.value = mapTaskInfo(task || {})
  const logRes = getMockTaskLogs({
    taskId: taskId.value,
    hostId: selectedHostId.value,
    pageNum: 1,
    pageSize: 200
  })
  logLines.value = logRes.records.map(item => ({
    ...item,
    logTypeClass: mapLogTypeClass(item.logType)
  }))
  buildHostSummary(logRes.records)
}

const reloadLogs = async () => {
  loading.value = true
  try {
    if (useFallback.value) {
      loadFallback()
      return
    }
    const [taskRes, logRes] = await Promise.all([
      getTaskById(taskId.value),
      getTaskLogs(taskId.value, {
        hostId: selectedHostId.value,
        pageNum: 1,
        pageSize: 200
      })
    ])
    taskInfo.value = mapTaskInfo(taskRes.data || {})
    const records = logRes.data.records || []
    logLines.value = records.map(item => ({
      ...item,
      hostIp: `HOST-${item.hostId}`,
      logTypeClass: mapLogTypeClass(item.logType)
    }))
    buildHostSummary(records.map(item => ({ ...item, hostIp: `HOST-${item.hostId}` })))
  } catch (error) {
    console.error('Failed to load task logs:', error)
    resetMockTasks()
    loadFallback()
    ElMessage.warning(isZh.value ? '任务日志接口异常，已切换演示数据。' : 'Task log API unavailable, switched to demo data.')
  } finally {
    loading.value = false
  }
}

const selectHost = hostId => {
  selectedHostId.value = selectedHostId.value === hostId ? null : hostId
  reloadLogs()
}

const pauseCurrentTask = async () => {
  if (useFallback.value) {
    ElMessage.info(isZh.value ? '演示模式下不执行真实暂停。' : 'Demo mode: pause is simulated.')
    return
  }
  await pauseTask(taskId.value)
  ElMessage.success(isZh.value ? '任务已暂停。' : 'Task paused.')
  reloadLogs()
}

const terminateCurrentTask = async () => {
  if (useFallback.value) {
    ElMessage.info(isZh.value ? '演示模式下不执行真实终止。' : 'Demo mode: terminate is simulated.')
    return
  }
  await terminateTask(taskId.value)
  ElMessage.success(isZh.value ? '任务已终止。' : 'Task terminated.')
  reloadLogs()
}

onMounted(() => {
  reloadLogs()
})
</script>

<style scoped>
.task-log-page { padding: 32px; height: calc(100vh - 94px); display: flex; flex-direction: column; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-title { font-size: 24px; color: #fff; font-weight: 900; }
.page-subtitle { margin-top: 6px; color: var(--on-surface-variant); font-size: 11px; }
.header-actions { display: flex; gap: 8px; }
.action-btn { border: none; border-radius: 8px; padding: 9px 14px; color: #fff; font-size: 12px; font-weight: 700; cursor: pointer; }
.action-btn.warning { background: #6a4f00; }
.action-btn.danger { background: #7a1a1a; }
.progress-bar-wrap { margin-top: 16px; background: var(--bg-surface-container-low); padding: 12px 16px; border-radius: 10px; }
.progress-info { display: flex; justify-content: space-between; font-size: 12px; color: var(--on-surface-variant); }
.progress-track { margin-top: 8px; height: 6px; background: var(--bg-base); border-radius: 9999px; position: relative; overflow: hidden; }
.progress-fill { position: absolute; top: 0; height: 100%; }
.progress-fill.success { background: var(--primary-container); }
.progress-fill.running { background: #FFB800; }
.log-layout { margin-top: 16px; display: grid; grid-template-columns: 280px 1fr; gap: 12px; min-height: 0; flex: 1; }
.host-list { background: var(--bg-surface-container-low); border-radius: 10px; padding: 12px; overflow-y: auto; }
.host-list h3 { color: #fff; font-size: 12px; margin-bottom: 8px; }
.host-item { width: 100%; border: none; border-radius: 8px; background: var(--bg-base); color: #fff; padding: 10px 12px; margin-bottom: 8px; cursor: pointer; display: flex; justify-content: space-between; align-items: center; }
.host-item.active { outline: 1px solid var(--primary-container); }
.tag { font-size: 10px; padding: 2px 6px; border-radius: 9999px; }
.tag.success { background: rgba(15,98,254,0.12); color: var(--primary-container); }
.tag.error { background: rgba(147,0,10,0.15); color: var(--error); }
.tag.info { background: rgba(120,120,120,0.2); color: var(--on-surface-variant); }
.log-content { background: #0a0a0a; border-radius: 10px; display: flex; flex-direction: column; min-height: 0; }
.toolbar { display: flex; justify-content: space-between; align-items: center; padding: 10px 12px; border-bottom: 1px solid rgba(66,70,86,0.08); color: var(--on-surface-variant); }
.tool-btn { background: transparent; border: 1px solid rgba(66,70,86,0.1); color: var(--on-surface-variant); border-radius: 6px; padding: 5px 10px; cursor: pointer; }
.log-lines { overflow-y: auto; padding: 12px; font-size: 12px; }
.log-line { display: grid; grid-template-columns: 170px 90px 1fr; gap: 8px; margin-bottom: 4px; }
.line-time { color: var(--outline); }
.line-level { font-weight: 700; }
.log-line.info .line-level { color: var(--on-surface-variant); }
.log-line.success .line-level { color: var(--primary-container); }
.log-line.error .line-level { color: var(--error); }
.line-msg { color: #ddd; word-break: break-word; }
.empty-line { color: var(--on-surface-variant); padding: 18px 0; }
</style>
