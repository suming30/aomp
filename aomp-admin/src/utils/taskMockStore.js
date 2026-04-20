const TASK_KEY = 'aomp_demo_tasks'
const LOG_KEY = 'aomp_demo_task_logs'
const memoryStore = {}

const now = () => new Date().toISOString()

const readJson = (key, fallback) => {
  if (typeof window === 'undefined' || !window.localStorage) {
    if (!memoryStore[key]) {
      memoryStore[key] = fallback()
    }
    return memoryStore[key]
  }
  const raw = window.localStorage.getItem(key)
  if (!raw) {
    const seeded = fallback()
    window.localStorage.setItem(key, JSON.stringify(seeded))
    return seeded
  }
  try {
    return JSON.parse(raw)
  } catch {
    const seeded = fallback()
    window.localStorage.setItem(key, JSON.stringify(seeded))
    return seeded
  }
}

const seedTasks = () => ([
  {
    id: 5001,
    taskName: 'K8S 集群变更发布',
    scriptName: 'deploy-k8s-node',
    status: 'running',
    totalHosts: 12,
    successHosts: 7,
    failHosts: 1,
    runningHosts: 2,
    startTime: now(),
    endTime: null,
    createBy: 'admin',
    createTime: now()
  },
  {
    id: 5002,
    taskName: '数据库备份巡检',
    scriptName: 'backup-check',
    status: 'success',
    totalHosts: 8,
    successHosts: 8,
    failHosts: 0,
    runningHosts: 0,
    startTime: now(),
    endTime: now(),
    createBy: 'dba.team',
    createTime: now()
  }
])

const seedLogs = () => ([
  {
    id: 6001,
    taskId: 5001,
    hostId: 10001,
    hostIp: '192.168.10.11',
    logType: 'INFO',
    content: 'Connecting to host 192.168.10.11',
    createTime: now()
  },
  {
    id: 6002,
    taskId: 5001,
    hostId: 10001,
    hostIp: '192.168.10.11',
    logType: 'SUCCESS',
    content: 'Deployment completed with exit code 0',
    createTime: now()
  },
  {
    id: 6003,
    taskId: 5001,
    hostId: 10002,
    hostIp: '192.168.10.12',
    logType: 'ERROR',
    content: 'kubeconfig permission denied',
    createTime: now()
  }
])

export const listMockTasks = ({ pageNum = 1, pageSize = 20 } = {}) => {
  const records = readJson(TASK_KEY, seedTasks)
  const start = (Number(pageNum) - 1) * Number(pageSize)
  return {
    records: records.slice(start, start + Number(pageSize)),
    total: records.length
  }
}

export const getMockTaskById = id => readJson(TASK_KEY, seedTasks).find(item => String(item.id) === String(id))

export const getMockTaskLogs = ({ taskId, hostId = null, pageNum = 1, pageSize = 50 }) => {
  let records = readJson(LOG_KEY, seedLogs).filter(item => String(item.taskId) === String(taskId))
  if (hostId) {
    records = records.filter(item => String(item.hostId) === String(hostId))
  }
  const start = (Number(pageNum) - 1) * Number(pageSize)
  return {
    records: records.slice(start, start + Number(pageSize)),
    total: records.length
  }
}

export const resetMockTasks = () => {
  if (typeof window !== 'undefined' && window.localStorage) {
    window.localStorage.setItem(TASK_KEY, JSON.stringify(seedTasks()))
    window.localStorage.setItem(LOG_KEY, JSON.stringify(seedLogs()))
    return
  }
  memoryStore[TASK_KEY] = seedTasks()
  memoryStore[LOG_KEY] = seedLogs()
}
