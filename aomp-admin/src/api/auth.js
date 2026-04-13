import request from './request'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

export function refreshToken() {
  return request({
    url: '/auth/refresh',
    method: 'post'
  })
}

export function changePassword(data) {
  return request({
    url: '/auth/password',
    method: 'put',
    data
  })
}

export function getCurrentUser() {
  return request({
    url: '/users/me',
    method: 'get'
  })
}
