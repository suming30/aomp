const GROUP_KEY = 'aomp_demo_groups'
const TAG_KEY = 'aomp_demo_tags'
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

const writeJson = (key, value) => {
  if (typeof window !== 'undefined' && window.localStorage) {
    window.localStorage.setItem(key, JSON.stringify(value))
    return
  }
  memoryStore[key] = value
}

const nextId = list => list.reduce((max, item) => Math.max(max, Number(item.id) || 0), 1000) + 1

const groupSeed = () => ([
  {
    id: 2001,
    groupName: 'K8S-PROD-01',
    groupType: 'static',
    description: '生产环境 K8S 节点组',
    hostCount: 24,
    createBy: 'admin',
    updateTime: now()
  },
  {
    id: 2002,
    groupName: 'EDGE-GW-02',
    groupType: 'static',
    description: '边缘网关节点集合',
    hostCount: 12,
    createBy: 'ops.lead',
    updateTime: now()
  },
  {
    id: 2003,
    groupName: 'STAGING-AUTO',
    groupType: 'dynamic',
    description: '测试环境动态分组',
    hostCount: 18,
    createBy: 'system',
    updateTime: now()
  }
])

const tagSeed = () => ([
  {
    id: 3001,
    tagName: 'production',
    color: '#C84000',
    description: '生产环境主机',
    hostCount: 48,
    createBy: 'admin',
    createTime: now()
  },
  {
    id: 3002,
    tagName: 'k8s',
    color: '#0F62FE',
    description: 'Kubernetes 节点',
    hostCount: 35,
    createBy: 'ops.lead',
    createTime: now()
  },
  {
    id: 3003,
    tagName: 'database',
    color: '#8A3FFC',
    description: '数据库节点',
    hostCount: 14,
    createBy: 'dba.team',
    createTime: now()
  }
])

export const listMockGroups = ({ keyword = '', groupType = '' } = {}) => {
  const lowerKeyword = keyword.trim().toLowerCase()
  let records = readJson(GROUP_KEY, groupSeed)
  if (lowerKeyword) {
    records = records.filter(item =>
      [item.groupName, item.description, item.createBy].join(' ').toLowerCase().includes(lowerKeyword)
    )
  }
  if (groupType) {
    records = records.filter(item => item.groupType === groupType)
  }
  return records
}

export const createMockGroup = payload => {
  const groups = readJson(GROUP_KEY, groupSeed)
  const created = {
    id: nextId(groups),
    groupName: payload.groupName,
    groupType: payload.groupType || 'static',
    description: payload.description || '',
    hostCount: Number(payload.hostCount || 0),
    createBy: payload.createBy || 'current.user',
    updateTime: now()
  }
  groups.unshift(created)
  writeJson(GROUP_KEY, groups)
  return created
}

export const updateMockGroup = (id, payload) => {
  const groups = readJson(GROUP_KEY, groupSeed)
  const index = groups.findIndex(item => String(item.id) === String(id))
  if (index < 0) {
    throw new Error('Group not found')
  }
  groups[index] = {
    ...groups[index],
    ...payload,
    updateTime: now()
  }
  writeJson(GROUP_KEY, groups)
  return groups[index]
}

export const deleteMockGroup = id => {
  const groups = readJson(GROUP_KEY, groupSeed).filter(item => String(item.id) !== String(id))
  writeJson(GROUP_KEY, groups)
}

export const listMockTags = ({ keyword = '' } = {}) => {
  const lowerKeyword = keyword.trim().toLowerCase()
  let records = readJson(TAG_KEY, tagSeed)
  if (lowerKeyword) {
    records = records.filter(item =>
      [item.tagName, item.description, item.createBy].join(' ').toLowerCase().includes(lowerKeyword)
    )
  }
  return records
}

export const createMockTag = payload => {
  const tags = readJson(TAG_KEY, tagSeed)
  const created = {
    id: nextId(tags),
    tagName: payload.tagName,
    color: payload.color || '#0F62FE',
    description: payload.description || '',
    hostCount: Number(payload.hostCount || 0),
    createBy: payload.createBy || 'current.user',
    createTime: now()
  }
  tags.unshift(created)
  writeJson(TAG_KEY, tags)
  return created
}

export const updateMockTag = (id, payload) => {
  const tags = readJson(TAG_KEY, tagSeed)
  const index = tags.findIndex(item => String(item.id) === String(id))
  if (index < 0) {
    throw new Error('Tag not found')
  }
  tags[index] = {
    ...tags[index],
    ...payload
  }
  writeJson(TAG_KEY, tags)
  return tags[index]
}

export const deleteMockTag = id => {
  const tags = readJson(TAG_KEY, tagSeed).filter(item => String(item.id) !== String(id))
  writeJson(TAG_KEY, tags)
}

export const importMockGroups = () => {
  writeJson(GROUP_KEY, groupSeed())
}

export const importMockTags = () => {
  writeJson(TAG_KEY, tagSeed())
}
