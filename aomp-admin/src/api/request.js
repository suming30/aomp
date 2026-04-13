import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('autoops_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    const lang = localStorage.getItem('autoops_lang') || 'zh'
    config.headers['Accept-Language'] = lang === 'zh' ? 'zh-CN' : 'en-US'
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200 || res.code === 0) {
      return res
    }
    ElMessage.error(res.message || 'Request failed')
    return Promise.reject(new Error(res.message || 'Request failed'))
  },
  error => {
    const status = error.response?.status
    const message = error.response?.data?.message || error.message
    
    if (status === 401) {
      localStorage.removeItem('autoops_token')
      localStorage.removeItem('autoops_auth')
      localStorage.removeItem('autoops_user')
      router.push('/login')
      ElMessage.error('Login expired, please login again')
    } else if (status === 403) {
      ElMessage.error('No permission to access')
    } else if (status === 404) {
      ElMessage.error('Resource not found')
    } else if (status === 500) {
      ElMessage.error('Server error')
    } else {
      ElMessage.error(message || 'Network error')
    }
    
    return Promise.reject(error)
  }
)

export default request
