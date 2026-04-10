import request from './request'

export function getTaskList(params) {
  return request({
    url: '/tasks',
    method: 'get',
    params
  })
}

export function getTaskById(id) {
  return request({
    url: `/tasks/${id}`,
    method: 'get'
  })
}

export function createTask(data) {
  return request({
    url: '/tasks',
    method: 'post',
    data
  })
}

export function executeTask(id, data) {
  return request({
    url: `/tasks/${id}/execute`,
    method: 'post',
    data
  })
}

export function pauseTask(id) {
  return request({
    url: `/tasks/${id}/pause`,
    method: 'post'
  })
}

export function resumeTask(id) {
  return request({
    url: `/tasks/${id}/resume`,
    method: 'post'
  })
}

export function terminateTask(id) {
  return request({
    url: `/tasks/${id}/terminate`,
    method: 'post'
  })
}

export function retryTask(id) {
  return request({
    url: `/tasks/${id}/retry`,
    method: 'post'
  })
}

export function getTaskLogs(id, params) {
  return request({
    url: `/tasks/${id}/logs`,
    method: 'get',
    params
  })
}

export function getTaskHostLogs(taskId, hostId) {
  return request({
    url: `/tasks/${taskId}/hosts/${hostId}/logs`,
    method: 'get'
  })
}

export function exportTaskLogs(id) {
  return request({
    url: `/tasks/${id}/logs/export`,
    method: 'get',
    responseType: 'blob'
  })
}
