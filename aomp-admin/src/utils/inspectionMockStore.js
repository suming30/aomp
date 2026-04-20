const TASK_KEY = 'aomp_demo_inspection_tasks'
const RESULT_KEY = 'aomp_demo_inspection_results'
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

const taskSeed = () => ([
  {
    id: 7001,
    taskName: '每日健康巡检',
    status: 'finished',
    totalHosts: 20,
    normalHosts: 17,
    warningHosts: 2,
    errorHosts: 1,
    startTime: now(),
    endTime: now(),
    createBy: 'system'
  }
])

const resultSeed = () => ([
  {
    id: 7101,
    inspectionTaskId: 7001,
    hostId: 10001,
    hostName: 'prod-web-01',
    hostIp: '192.168.10.11',
    itemId: 1,
    status: 'normal',
    actualValue: '42',
    thresholdValue: '80',
    detail: 'CPU 使用率正常',
    createTime: now()
  },
  {
    id: 7102,
    inspectionTaskId: 7001,
    hostId: 10002,
    hostName: 'prod-web-02',
    hostIp: '192.168.10.12',
    itemId: 1,
    status: 'warning',
    actualValue: '82',
    thresholdValue: '80',
    detail: 'CPU 使用率偏高',
    createTime: now()
  },
  {
    id: 7103,
    inspectionTaskId: 7001,
    hostId: 10003,
    hostName: 'edge-gw-01',
    hostIp: '10.0.20.21',
    itemId: 2,
    status: 'error',
    actualValue: '95',
    thresholdValue: '90',
    detail: '磁盘使用率严重超限',
    createTime: now()
  }
])

export const getMockInspectionTaskById = id =>
  readJson(TASK_KEY, taskSeed).find(item => String(item.id) === String(id))

export const listMockInspectionResults = taskId =>
  readJson(RESULT_KEY, resultSeed).filter(item => String(item.inspectionTaskId) === String(taskId))

export const resetMockInspection = () => {
  if (typeof window !== 'undefined' && window.localStorage) {
    window.localStorage.setItem(TASK_KEY, JSON.stringify(taskSeed()))
    window.localStorage.setItem(RESULT_KEY, JSON.stringify(resultSeed()))
    return
  }
  memoryStore[TASK_KEY] = taskSeed()
  memoryStore[RESULT_KEY] = resultSeed()
}
