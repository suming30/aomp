import request from './request'

export function getHostList(params) {
  return request({
    url: '/hosts',
    method: 'get',
    params
  })
}

export function getHostById(id) {
  return request({
    url: `/hosts/${id}`,
    method: 'get'
  })
}

export function createHost(data) {
  return request({
    url: '/hosts',
    method: 'post',
    data
  })
}

export function updateHost(id, data) {
  return request({
    url: `/hosts/${id}`,
    method: 'put',
    data
  })
}

export function deleteHost(id) {
  return request({
    url: `/hosts/${id}`,
    method: 'delete'
  })
}

export function checkHostConnectivity(id) {
  return request({
    url: `/hosts/${id}/check`,
    method: 'post'
  })
}

export function batchCheckConnectivity(ids) {
  return request({
    url: '/hosts/batch-check',
    method: 'post',
    data: ids
  })
}

export function importHosts(data) {
  return request({
    url: '/hosts/import',
    method: 'post',
    data
  })
}

export function exportHosts(params) {
  return request({
    url: '/hosts/export',
    method: 'get',
    params
  })
}

export function getHostsByGroup(groupId) {
  return request({
    url: `/hosts/group/${groupId}`,
    method: 'get'
  })
}

export function getHostsByTag(tagId) {
  return request({
    url: `/hosts/tag/${tagId}`,
    method: 'get'
  })
}

export function getHostGroupList(params) {
  return request({
    url: '/host-groups',
    method: 'get',
    params
  })
}

export function createHostGroup(data) {
  return request({
    url: '/host-groups',
    method: 'post',
    data
  })
}

export function updateHostGroup(id, data) {
  return request({
    url: `/host-groups/${id}`,
    method: 'put',
    data
  })
}

export function deleteHostGroup(id) {
  return request({
    url: `/host-groups/${id}`,
    method: 'delete'
  })
}

export function getTagList(params) {
  return request({
    url: '/tags',
    method: 'get',
    params
  })
}

export function createTag(data) {
  return request({
    url: '/tags',
    method: 'post',
    data
  })
}

export function updateTag(id, data) {
  return request({
    url: `/tags/${id}`,
    method: 'put',
    data
  })
}

export function deleteTag(id) {
  return request({
    url: `/tags/${id}`,
    method: 'delete'
  })
}
