import request from './request'

export function getScriptList(params) {
  return request({
    url: '/scripts',
    method: 'get',
    params
  })
}

export function getScriptById(id) {
  return request({
    url: `/scripts/${id}`,
    method: 'get'
  })
}

export function createScript(data) {
  return request({
    url: '/scripts',
    method: 'post',
    data
  })
}

export function updateScript(id, data) {
  return request({
    url: `/scripts/${id}`,
    method: 'put',
    data
  })
}

export function deleteScript(id) {
  return request({
    url: `/scripts/${id}`,
    method: 'delete'
  })
}

export function getScriptVersions(id, params) {
  return request({
    url: `/scripts/${id}/versions`,
    method: 'get',
    params
  })
}

export function getScriptVersionDetail(scriptId, versionId) {
  return request({
    url: `/scripts/${scriptId}/versions/${versionId}`,
    method: 'get'
  })
}

export function rollbackScriptVersion(scriptId, versionId) {
  return request({
    url: `/scripts/${scriptId}/versions/${versionId}/rollback`,
    method: 'post'
  })
}

export function scanDangerousCommands(id) {
  return request({
    url: `/scripts/${id}/scan`,
    method: 'post'
  })
}

export function submitScriptAudit(id) {
  return request({
    url: `/scripts/${id}/audit`,
    method: 'post'
  })
}
