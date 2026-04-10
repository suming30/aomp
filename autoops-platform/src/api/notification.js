import request from './request'

export function getNotificationList(params) {
  return request({
    url: '/notifications',
    method: 'get',
    params
  })
}

export function markNotificationRead(id) {
  return request({
    url: `/notifications/${id}/read`,
    method: 'put'
  })
}

export function markAllNotificationsRead() {
  return request({
    url: '/notifications/read-all',
    method: 'put'
  })
}

export function getUnreadCount() {
  return request({
    url: '/notifications/unread-count',
    method: 'get'
  })
}

export function getWebhookList() {
  return request({
    url: '/webhooks',
    method: 'get'
  })
}

export function createWebhook(data) {
  return request({
    url: '/webhooks',
    method: 'post',
    data
  })
}

export function updateWebhook(id, data) {
  return request({
    url: `/webhooks/${id}`,
    method: 'put',
    data
  })
}

export function deleteWebhook(id) {
  return request({
    url: `/webhooks/${id}`,
    method: 'delete'
  })
}

export function testWebhook(id) {
  return request({
    url: `/webhooks/${id}/test`,
    method: 'post'
  })
}
