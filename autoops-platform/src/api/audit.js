import request from './request'

export function getAuditLogList(params) {
  return request({
    url: '/audit-logs',
    method: 'get',
    params
  })
}

export function getAuditLogById(id) {
  return request({
    url: `/audit-logs/${id}`,
    method: 'get'
  })
}

export function exportAuditLogs(params) {
  return request({
    url: '/audit-logs/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
