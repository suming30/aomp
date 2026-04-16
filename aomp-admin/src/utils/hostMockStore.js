const STORAGE_KEY = 'aomp_host_demo_records'
const MODE_KEY = 'aomp_host_demo_mode'

let memoryStore = []

const clone = value => JSON.parse(JSON.stringify(value))

const getStorage = () => {
  if (typeof window !== 'undefined' && window.localStorage) {
    return window.localStorage
  }

  return {
    getItem(key) {
      if (key === STORAGE_KEY) {
        return JSON.stringify(memoryStore)
      }
      return null
    },
    setItem(key, value) {
      if (key === STORAGE_KEY) {
        memoryStore = JSON.parse(value)
      }
    }
  }
}

const now = () => new Date().toISOString()

const statusFromIp = ipAddress => {
  const lastChunk = Number(String(ipAddress || '').split('.').pop())
  if (Number.isNaN(lastChunk)) {
    return 'offline'
  }
  return lastChunk % 3 === 0 ? 'offline' : 'online'
}

const createSeedHosts = () => ([
  {
    id: 10001,
    ipAddress: '192.168.10.11',
    sshPort: 22,
    hostname: 'prod-web-01',
    alias: '生产应用节点01',
    osType: 'Ubuntu 22.04',
    cpuCores: 8,
    memoryGb: 16,
    diskGb: 200,
    sshUser: 'root',
    sshAuthType: 'password',
    managerId: 1,
    managerName: 'admin',
    status: 'online',
    remark: 'Nginx + API gateway',
    groupIds: [1],
    groupNames: ['K8S-PROD-01'],
    tagIds: [101, 102],
    tagNames: ['production', 'web'],
    lastCheckTime: now(),
    createTime: now(),
    updateTime: now()
  },
  {
    id: 10002,
    ipAddress: '192.168.10.12',
    sshPort: 22,
    hostname: 'prod-web-02',
    alias: '生产应用节点02',
    osType: 'Ubuntu 22.04',
    cpuCores: 8,
    memoryGb: 16,
    diskGb: 200,
    sshUser: 'root',
    sshAuthType: 'password',
    managerId: 1,
    managerName: 'admin',
    status: 'offline',
    remark: 'Blue deployment node',
    groupIds: [1],
    groupNames: ['K8S-PROD-01'],
    tagIds: [101, 102],
    tagNames: ['production', 'web'],
    lastCheckTime: now(),
    createTime: now(),
    updateTime: now()
  },
  {
    id: 10003,
    ipAddress: '10.0.20.21',
    sshPort: 22,
    hostname: 'edge-gw-01',
    alias: '边缘网关01',
    osType: 'CentOS 7.9',
    cpuCores: 4,
    memoryGb: 8,
    diskGb: 120,
    sshUser: 'ops',
    sshAuthType: 'key',
    managerId: 2,
    managerName: 'ops.lead',
    status: 'online',
    remark: 'South IDC gateway',
    groupIds: [2],
    groupNames: ['EDGE-GW-02'],
    tagIds: [103],
    tagNames: ['edge'],
    lastCheckTime: now(),
    createTime: now(),
    updateTime: now()
  },
  {
    id: 10004,
    ipAddress: '10.0.30.31',
    sshPort: 22,
    hostname: 'db-primary-01',
    alias: '主数据库',
    osType: 'Rocky Linux 9',
    cpuCores: 16,
    memoryGb: 64,
    diskGb: 1024,
    sshUser: 'dba',
    sshAuthType: 'password',
    managerId: 3,
    managerName: 'dba.team',
    status: 'online',
    remark: 'Primary MySQL node',
    groupIds: [3],
    groupNames: ['DB-CLUSTER-A'],
    tagIds: [101, 104],
    tagNames: ['production', 'database'],
    lastCheckTime: now(),
    createTime: now(),
    updateTime: now()
  },
  {
    id: 10005,
    ipAddress: '10.0.40.41',
    sshPort: 2222,
    hostname: 'staging-app-01',
    alias: '预发应用01',
    osType: 'Debian 12',
    cpuCores: 4,
    memoryGb: 8,
    diskGb: 100,
    sshUser: 'deploy',
    sshAuthType: 'password',
    managerId: 4,
    managerName: 'qa.owner',
    status: 'offline',
    remark: 'Staging verification node',
    groupIds: [4],
    groupNames: ['STAGING'],
    tagIds: [105],
    tagNames: ['staging'],
    lastCheckTime: now(),
    createTime: now(),
    updateTime: now()
  }
])

const readHosts = () => {
  const raw = getStorage().getItem(STORAGE_KEY)
  if (!raw) {
    const seed = createSeedHosts()
    getStorage().setItem(STORAGE_KEY, JSON.stringify(seed))
    return seed
  }

  try {
    return JSON.parse(raw)
  } catch {
    const seed = createSeedHosts()
    getStorage().setItem(STORAGE_KEY, JSON.stringify(seed))
    return seed
  }
}

const writeHosts = hosts => {
  getStorage().setItem(STORAGE_KEY, JSON.stringify(hosts))
}

const nextId = hosts => hosts.reduce((max, item) => Math.max(max, Number(item.id) || 0), 10000) + 1

const resolveCurrentUser = () => {
  if (typeof window !== 'undefined' && window.localStorage) {
    const raw = window.localStorage.getItem('autoops_user')
    if (raw) {
      try {
        const user = JSON.parse(raw)
        return {
          managerId: user.id || 0,
          managerName: user.username || user.userAccount || 'current.user'
        }
      } catch {
        return {
          managerId: 0,
          managerName: 'current.user'
        }
      }
    }
  }

  return {
    managerId: 0,
    managerName: 'current.user'
  }
}

const normalizeDraft = draft => {
  const currentUser = resolveCurrentUser()
  const osType = draft.osType?.trim() || 'Linux'

  return {
    sshPort: Number(draft.sshPort || 22),
    hostname: draft.hostname?.trim() || '',
    alias: draft.alias?.trim() || '',
    ipAddress: draft.ipAddress?.trim() || '',
    osType,
    cpuCores: draft.cpuCores ? Number(draft.cpuCores) : null,
    memoryGb: draft.memoryGb ? Number(draft.memoryGb) : null,
    diskGb: draft.diskGb ? Number(draft.diskGb) : null,
    sshUser: draft.sshUser?.trim() || 'root',
    sshAuthType: draft.sshAuthType || 'password',
    managerId: draft.managerId ?? currentUser.managerId,
    managerName: draft.managerName || currentUser.managerName,
    remark: draft.remark?.trim() || '',
    groupIds: draft.groupIds || [],
    groupNames: draft.groupNames || [],
    tagIds: draft.tagIds || [],
    tagNames: draft.tagNames || []
  }
}

export const isDemoModeEnabled = () => {
  if (typeof window !== 'undefined' && window.localStorage) {
    const current = window.localStorage.getItem(MODE_KEY)
    if (current === null) {
      window.localStorage.setItem(MODE_KEY, '1')
      return true
    }
    return current === '1'
  }

  return true
}

export const setDemoModeEnabled = enabled => {
  if (typeof window !== 'undefined' && window.localStorage) {
    window.localStorage.setItem(MODE_KEY, enabled ? '1' : '0')
  }
}

export const listMockHosts = ({ pageNum = 1, pageSize = 20, keyword = '', status = '' } = {}) => {
  const normalizedKeyword = keyword.trim().toLowerCase()
  let records = readHosts()

  if (normalizedKeyword) {
    records = records.filter(item => {
      const joined = [
        item.ipAddress,
        item.hostname,
        item.alias,
        item.osType,
        item.managerName
      ].join(' ').toLowerCase()
      return joined.includes(normalizedKeyword)
    })
  }

  if (status) {
    records = records.filter(item => item.status === status)
  }

  const total = records.length
  const start = (Number(pageNum) - 1) * Number(pageSize)
  const pagedRecords = records.slice(start, start + Number(pageSize))

  return {
    records: clone(pagedRecords),
    total,
    pageNum: Number(pageNum),
    pageSize: Number(pageSize)
  }
}

export const getMockHostById = id => {
  const host = readHosts().find(item => String(item.id) === String(id))
  return host ? clone(host) : null
}

export const createMockHost = payload => {
  const hosts = readHosts()
  const normalized = normalizeDraft(payload)

  if (hosts.some(item => item.ipAddress === normalized.ipAddress)) {
    throw new Error('该 IP 地址已存在')
  }

  const timestamp = now()
  const created = {
    id: nextId(hosts),
    ...normalized,
    status: statusFromIp(normalized.ipAddress),
    lastCheckTime: timestamp,
    createTime: timestamp,
    updateTime: timestamp
  }

  hosts.unshift(created)
  writeHosts(hosts)
  return clone(created)
}

export const updateMockHost = (id, payload) => {
  const hosts = readHosts()
  const index = hosts.findIndex(item => String(item.id) === String(id))
  if (index === -1) {
    throw new Error('主机不存在')
  }

  const current = hosts[index]
  const normalized = normalizeDraft({
    ...current,
    ...payload,
    ipAddress: current.ipAddress
  })

  hosts[index] = {
    ...current,
    ...normalized,
    status: statusFromIp(current.ipAddress),
    updateTime: now()
  }

  writeHosts(hosts)
  return clone(hosts[index])
}

export const deleteMockHost = id => {
  const hosts = readHosts().filter(item => String(item.id) !== String(id))
  writeHosts(hosts)
}

export const checkMockHostConnectivity = id => {
  const hosts = readHosts()
  const target = hosts.find(item => String(item.id) === String(id))
  if (!target) {
    throw new Error('主机不存在')
  }

  target.status = statusFromIp(target.ipAddress)
  target.lastCheckTime = now()
  target.updateTime = now()
  writeHosts(hosts)

  return clone(target)
}

export const batchCheckMockHosts = ids => {
  ids.forEach(id => {
    checkMockHostConnectivity(id)
  })
}

export const importMockHosts = (items = []) => {
  const hosts = readHosts()
  let currentId = nextId(hosts)
  const imported = items.map(item => {
    const normalized = normalizeDraft(item)
    const timestamp = now()
    return {
      id: currentId++,
      ...normalized,
      status: statusFromIp(normalized.ipAddress),
      lastCheckTime: timestamp,
      createTime: timestamp,
      updateTime: timestamp
    }
  })

  writeHosts([...imported, ...hosts])
  return imported.length
}

export const resetMockHosts = () => {
  writeHosts(createSeedHosts())
}

export const createImportedDemoHosts = () => ([
  {
    hostname: 'demo-import-01',
    alias: '演示导入节点01',
    ipAddress: '172.16.10.51',
    osType: 'Alibaba Cloud Linux 3',
    sshPort: 22,
    sshUser: 'root',
    sshAuthType: 'password',
    cpuCores: 4,
    memoryGb: 8,
    diskGb: 100,
    remark: 'Imported demo host',
    groupIds: [5],
    groupNames: ['DEMO-IMPORT']
  },
  {
    hostname: 'demo-import-02',
    alias: '演示导入节点02',
    ipAddress: '172.16.10.52',
    osType: 'Ubuntu 24.04',
    sshPort: 22,
    sshUser: 'ubuntu',
    sshAuthType: 'key',
    cpuCores: 8,
    memoryGb: 16,
    diskGb: 160,
    remark: 'Imported demo host',
    groupIds: [5],
    groupNames: ['DEMO-IMPORT']
  },
  {
    hostname: 'demo-import-03',
    alias: '演示导入节点03',
    ipAddress: '172.16.10.53',
    osType: 'CentOS Stream 9',
    sshPort: 22,
    sshUser: 'ops',
    sshAuthType: 'password',
    cpuCores: 4,
    memoryGb: 8,
    diskGb: 100,
    remark: 'Imported demo host',
    groupIds: [5],
    groupNames: ['DEMO-IMPORT']
  }
])
