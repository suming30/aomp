const SCRIPT_KEY = 'aomp_demo_scripts'
let memoryScripts = []

const now = () => new Date().toISOString()

const readScripts = () => {
  if (typeof window === 'undefined' || !window.localStorage) {
    if (!memoryScripts.length) {
      memoryScripts = seedScripts()
    }
    return memoryScripts
  }
  const raw = window.localStorage.getItem(SCRIPT_KEY)
  if (!raw) {
    const seeded = seedScripts()
    window.localStorage.setItem(SCRIPT_KEY, JSON.stringify(seeded))
    return seeded
  }
  try {
    return JSON.parse(raw)
  } catch {
    const seeded = seedScripts()
    window.localStorage.setItem(SCRIPT_KEY, JSON.stringify(seeded))
    return seeded
  }
}

const writeScripts = records => {
  if (typeof window !== 'undefined' && window.localStorage) {
    window.localStorage.setItem(SCRIPT_KEY, JSON.stringify(records))
    return
  }
  memoryScripts = records
}

const nextId = records => records.reduce((max, item) => Math.max(max, Number(item.id) || 0), 4000) + 1

const seedScripts = () => ([
  {
    id: 4001,
    scriptName: 'deploy-k8s-node',
    scriptType: 'bash',
    scope: 'private',
    description: 'K8S 节点部署脚本',
    version: 3,
    auditStatus: 'approved',
    isDraft: false,
    createBy: 'admin',
    createTime: now()
  },
  {
    id: 4002,
    scriptName: 'collect-system-metrics',
    scriptType: 'python',
    scope: 'public',
    description: '采集主机性能指标',
    version: 2,
    auditStatus: 'approved',
    isDraft: false,
    createBy: 'ops.lead',
    createTime: now()
  },
  {
    id: 4003,
    scriptName: 'restart-service',
    scriptType: 'powershell',
    scope: 'private',
    description: 'Windows 服务重启脚本',
    version: 1,
    auditStatus: 'pending',
    isDraft: true,
    createBy: 'qa.owner',
    createTime: now()
  }
])

export const listMockScripts = ({ pageNum = 1, pageSize = 20, keyword = '', scriptType = '', scope = '' } = {}) => {
  const lowerKeyword = keyword.trim().toLowerCase()
  let records = readScripts()
  if (lowerKeyword) {
    records = records.filter(item =>
      [item.scriptName, item.description, item.createBy].join(' ').toLowerCase().includes(lowerKeyword)
    )
  }
  if (scriptType) {
    records = records.filter(item => item.scriptType === scriptType)
  }
  if (scope) {
    records = records.filter(item => item.scope === scope)
  }

  const total = records.length
  const start = (Number(pageNum) - 1) * Number(pageSize)
  return {
    records: records.slice(start, start + Number(pageSize)),
    total
  }
}

export const deleteMockScript = id => {
  const records = readScripts().filter(item => String(item.id) !== String(id))
  writeScripts(records)
}

export const publishMockScript = id => {
  const records = readScripts()
  const idx = records.findIndex(item => String(item.id) === String(id))
  if (idx < 0) {
    throw new Error('Script not found')
  }
  records[idx].isDraft = false
  records[idx].auditStatus = 'approved'
  records[idx].version = Number(records[idx].version || 1) + 1
  writeScripts(records)
}

export const copyMockScript = (id, newName) => {
  const records = readScripts()
  const source = records.find(item => String(item.id) === String(id))
  if (!source) {
    throw new Error('Script not found')
  }
  const copied = {
    ...source,
    id: nextId(records),
    scriptName: newName,
    isDraft: true,
    auditStatus: 'pending',
    version: 1,
    createTime: now()
  }
  records.unshift(copied)
  writeScripts(records)
}

export const resetMockScripts = () => {
  writeScripts(seedScripts())
}
