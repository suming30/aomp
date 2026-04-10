import request from './request'

export function getInspectionItems(params) {
  return request({
    url: '/inspections/items',
    method: 'get',
    params
  })
}

export function getInspectionItemList(params) {
  return request({
    url: '/inspections/items',
    method: 'get',
    params
  })
}

export function getInspectionItemById(id) {
  return request({
    url: `/inspections/items/${id}`,
    method: 'get'
  })
}

export function createInspectionItem(data) {
  return request({
    url: '/inspections/items',
    method: 'post',
    data
  })
}

export function updateInspectionItem(id, data) {
  return request({
    url: `/inspections/items/${id}`,
    method: 'put',
    data
  })
}

export function deleteInspectionItem(id) {
  return request({
    url: `/inspections/items/${id}`,
    method: 'delete'
  })
}

export function executeInspection(taskName, data) {
  return request({
    url: '/inspections/execute',
    method: 'post',
    data,
    params: taskName ? { taskName } : {}
  })
}

export function getInspectionTasks(params) {
  return request({
    url: '/inspections/tasks',
    method: 'get',
    params
  })
}

export function getInspectionReportList(params) {
  return request({
    url: '/inspections/reports',
    method: 'get',
    params
  })
}

export function getInspectionReportById(id) {
  return request({
    url: `/inspections/reports/${id}`,
    method: 'get'
  })
}

export function exportInspectionReport(id, format = 'excel') {
  return request({
    url: `/inspections/reports/${id}/export`,
    method: 'get',
    params: { format },
    responseType: 'blob'
  })
}
