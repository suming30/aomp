import request from './request'

export function getTempAuthList(params) {
  return request({
    url: '/temp-auths',
    method: 'get',
    params
  })
}

export function createTempAuth(data) {
  return request({
    url: '/temp-auths',
    method: 'post',
    data
  })
}

export function revokeTempAuth(id) {
  return request({
    url: `/temp-auths/${id}/revoke`,
    method: 'post'
  })
}
