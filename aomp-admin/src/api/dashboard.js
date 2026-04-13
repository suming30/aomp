import request from './request'

export function getDashboard() {
  return request({
    url: '/dashboard',
    method: 'get'
  })
}

export function getDashboardStats() {
  return request({
    url: '/dashboard/stats',
    method: 'get'
  })
}

export function getWeeklyTaskTrend() {
  return request({
    url: '/dashboard/weekly-trend',
    method: 'get'
  })
}

export function getHostDistribution() {
  return request({
    url: '/dashboard/host-distribution',
    method: 'get'
  })
}

export function getRecentOperations(params) {
  return request({
    url: '/dashboard/recent-operations',
    method: 'get',
    params
  })
}

export function getPendingTasks() {
  return request({
    url: '/dashboard/pending-tasks',
    method: 'get'
  })
}

export function getExceptionStats() {
  return request({
    url: '/dashboard/exception-stats',
    method: 'get'
  })
}
