import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const tabs = ref([])
const maxTabs = 15

export function useTabs() {
  const route = useRoute()
  const router = useRouter()

  const activePath = computed(() => route.path)

  function getTabTitle(r) {
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
    tabs.value = []
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
    addTab,
    closeTab,
    closeOtherTabs,
    closeAllTabs,
    goToTab
  }
}
