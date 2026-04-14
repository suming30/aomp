import { reactive } from 'vue'
import { login as loginApi, logout as logoutApi, getCurrentUser } from '../api/auth'
import router from '../router'

const state = reactive({
  user: null,
  token: localStorage.getItem('autoops_token') || '',
  permissions: [],
  roles: [],
  isAuthenticated: !!localStorage.getItem('autoops_token')
})

async function login(username, password) {
  try {
    const res = await loginApi({ username, password })
    state.token = res.data.token
    state.user = res.data.user
    state.permissions = res.data.permissions || []
    state.roles = res.data.roles || []
    state.isAuthenticated = true
    
    localStorage.setItem('autoops_token', res.data.token)
    localStorage.setItem('autoops_auth', 'true')
    localStorage.setItem('autoops_user', JSON.stringify(res.data.user))
    
    return res
  } catch (error) {
    throw error
  }
}

async function logout() {
  try {
    await logoutApi()
  } catch (e) {
    console.error('Logout API error:', e)
  } finally {
    clearAuth()
    router.push('/login')
  }
}

function clearAuth() {
  state.token = ''
  state.user = null
  state.permissions = []
  state.roles = []
  state.isAuthenticated = false
  
  localStorage.removeItem('autoops_token')
  localStorage.removeItem('autoops_auth')
  localStorage.removeItem('autoops_user')
}

async function fetchUserInfo() {
  try {
    const res = await getCurrentUser()
    state.user = res.data
    state.permissions = res.data.permissions || []
    state.roles = res.data.roles || []
    return res.data
  } catch (error) {
    clearAuth()
    throw error
  }
}

function hasPermission(permission) {
  if (!permission) return true
  if (state.permissions.includes('*')) return true
  return state.permissions.includes(permission)
}

function hasAnyPermission(permissions) {
  if (!permissions || permissions.length === 0) return true
  if (state.permissions.includes('*')) return true
  return permissions.some(p => state.permissions.includes(p))
}

function hasAllPermissions(permissions) {
  if (!permissions || permissions.length === 0) return true
  if (state.permissions.includes('*')) return true
  return permissions.every(p => state.permissions.includes(p))
}

function hasRole(role) {
  return state.roles.includes(role)
}

const isAdmin = () => {
  if (!state.user) return false
  const roles = state.roles || []
  return roles.includes('admin') || state.user.userAccount === 'admin'
}

function initFromStorage() {
  const savedUser = localStorage.getItem('autoops_user')
  if (savedUser) {
    try {
      state.user = JSON.parse(savedUser)
    } catch (e) {
      console.error('Parse user error:', e)
    }
  }
}

initFromStorage()

export const useUserStore = () => {
  return {
    state,
    login,
    logout,
    clearAuth,
    fetchUserInfo,
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
    hasRole,
    isAdmin
  }
}
