import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

const tabs = ref([])
const maxTabs = 15

const titleKeyMap = {
  'Dashboard': 'nav.dashboard',
  'Assets': 'nav.assetCenter',
  'Host List': 'nav.hostList',
  'Group Management': 'nav.groupManage',
  'Tag Management': 'nav.tagManage',
  'Asset Detail': 'nav.hostList',
  'Scripts': 'nav.scriptCenter',
  'Script Editor': 'nav.scriptRepo',
  'Task Config': 'nav.taskConfig',
  'Task Log': 'nav.taskHistory',
  'Task Result': 'nav.taskHistory',
  'Inspection Config': 'nav.inspectionConfig',
  'Inspection Report': 'nav.inspectionConfig',
  'History': 'nav.taskHistory',
  'Permission': 'nav.securityPerm',
  'User Management': 'nav.userManage',
  'Role Management': 'nav.roleManage',
  'Audit Log': 'nav.operationAudit',
  'Settings': 'settings.title'
}

export function useTabs() {
  const route = useRoute()
  const router = useRouter()
  const { t } = useI18n()

  const activePath = computed(() => route.path)

  const hasClosableTabs = computed(() => tabs.value.some(t => t.closable))

  function getTabTitle(r) {
    const titleKey = r.meta?.title
    if (titleKey && titleKeyMap[titleKey]) {
      return t(titleKeyMap[titleKey])
    }
    if (r.meta?.title) return r.meta.title
    if (r.name) return String(r.name)
    const segments = r.path.split('/').filter(Boolean)
    return segments[segments.length - 1] || 'Page'
  }

  function addTab(targetRoute) {
    const r = targetRoute || route
    if (r.meta?.hidden) return
    const path = r.path
    if (!path || path === '/') return
    const exists = tabs.value.find(t => t.path === path)
    if (!exists) {
      tabs.value.push({
        path,
        title: getTabTitle(r),
        icon: r.meta?.icon || '',
        closable: path !== '/dashboard'
      })
      if (tabs.value.length > maxTabs) {
        tabs.value.shift()
      }
    }
  }

  function closeTab(path) {
    const idx = tabs.value.findIndex(t => t.path === path)
    if (idx === -1) return
    const wasActive = activePath.value === path
    tabs.value.splice(idx, 1)
    if (wasActive && tabs.value.length > 0) {
      const nextIdx = Math.min(idx, tabs.value.length - 1)
      router.push(tabs.value[nextIdx].path)
    }
  }

  function closeOtherTabs(keepPath) {
    const target = keepPath || activePath.value
    tabs.value = tabs.value.filter(t => t.path === target || t.path === '/dashboard')
  }

  function closeAllTabs() {
    tabs.value = tabs.value.filter(t => t.path === '/dashboard')
    router.push('/dashboard')
  }

  function goToTab(path) {
    router.push(path)
  }

  watch(() => route.path, (newPath) => {
    addTab()
  }, { immediate: true })

  return {
    tabs,
    activePath,
    hasClosableTabs,
    addTab,
    closeTab,
    closeOtherTabs,
    closeAllTabs,
    goToTab
  }
}
