<template>
  <aside class="sidebar">
    <div class="sidebar-header">
      <div class="logo-icon">
        <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">deployed_code</span>
      </div>
      <div>
        <h1 class="logo-title">AOMP</h1>
        <p class="logo-version">{{ t('common.version') }}</p>
      </div>
    </div>

    <nav class="sidebar-nav">
      <template v-for="item in navItems" :key="item.key">
        <!-- 一级菜单（无子菜单） -->
        <router-link
          v-if="!item.children"
          :to="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
        >
          <span class="material-symbols-outlined nav-icon">{{ item.icon }}</span>
          <span>{{ t(item.label) }}</span>
        </router-link>

        <!-- 一级菜单（有子菜单） -->
        <div
          v-else
          class="nav-group"
          :class="{ expanded: expandedKeys.has(item.key), active: isGroupActive(item) }"
        >
          <div class="nav-group-header" @click="toggleGroup(item.key)">
            <span class="material-symbols-outlined nav-icon">{{ item.icon }}</span>
            <span class="nav-group-label">{{ t(item.label) }}</span>
            <span class="material-symbols-outlined expand-icon">{{ expandedKeys.has(item.key) ? 'expand_less' : 'expand_more' }}</span>
          </div>
          <transition name="slide">
            <div v-if="expandedKeys.has(item.key)" class="nav-submenu">
              <router-link
                v-for="child in item.children"
                :key="child.path"
                :to="child.path"
                class="nav-subitem"
                :class="{ active: isActive(child.path) }"
              >
                <span class="sub-dot"></span>
                {{ t(child.label) }}
              </router-link>
            </div>
          </transition>
        </div>
      </template>
    </nav>

    <div class="sidebar-footer">
      <button class="deploy-btn litho-gradient">
        <span class="material-symbols-outlined">rocket_launch</span>
        {{ t('common.deployNewNode') }}
      </button>
    </div>
  </aside>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute, useRouter } from 'vue-router'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const expandedKeys = ref(new Set())

const navItems = [
  {
    key: 'dashboard',
    path: '/dashboard',
    icon: 'dashboard',
    label: 'nav.dashboard'
  },
  {
    key: 'assets',
    icon: 'inventory_2',
    label: 'nav.assetCenter',
    children: [
      { path: '/assets/list', label: 'nav.hostList' },
      { path: '/assets/groups', label: 'nav.groupManage' },
      { path: '/assets/tags', label: 'nav.tagManage' }
    ]
  },
  {
    key: 'scripts',
    icon: 'terminal',
    label: 'nav.scriptCenter',
    children: [
      { path: '/scripts', label: 'nav.scriptRepo' }
    ]
  },
  {
    key: 'tasks',
    icon: 'play_circle',
    label: 'nav.taskExec',
    children: [
      { path: '/tasks/config', label: 'nav.taskConfig' },
      { path: '/history', label: 'nav.taskHistory' }
    ]
  },
  {
    key: 'inspection',
    icon: 'monitoring',
    label: 'nav.inspection',
    children: [
      { path: '/inspection/config', label: 'nav.inspectionConfig' }
    ]
  },
  {
    key: 'audit',
    icon: 'fact_check',
    label: 'nav.auditCenter',
    children: [
      { path: '/audit', label: 'nav.operationAudit' }
    ]
  },
  {
    key: 'security',
    icon: 'admin_panel_settings',
    label: 'nav.securityPerm',
    children: [
      { path: '/permission/users', label: 'nav.userManage' },
      { path: '/permission/roles', label: 'nav.roleManage' }
    ]
  }
]

function toggleGroup(key) {
  if (expandedKeys.value.has(key)) {
    expandedKeys.value.delete(key)
  } else {
    expandedKeys.value.add(key)
  }
  expandedKeys.value = new Set(expandedKeys.value)
}

function isActive(path) {
  if (path === route.path) return true
  return route.path.startsWith(path + '/') || route.path.startsWith(path)
}

function isGroupActive(item) {
  if (!item.children) return false
  return item.children.some(child => isActive(child.path))
}

watch(() => route.path, (newPath) => {
  for (const item of navItems) {
    if (item.children && item.children.some(c => newPath === c.path || newPath.startsWith(c.path + '/'))) {
      expandedKeys.value.add(item.key)
      expandedKeys.value = new Set(expandedKeys.value)
      break
    }
  }
}, { immediate: true })
</script>

<style scoped>
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  width: var(--sidebar-width);
  height: 100vh;
  background-color: var(--bg-surface-low);
  display: flex;
  flex-direction: column;
  padding: 24px 0;
  z-index: 60;
  font-family: var(--font-body);
  font-size: 14px;
  letter-spacing: -0.01em;
}

.sidebar-header {
  padding: 0 20px;
  margin-bottom: 28px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-container) 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  flex-shrink: 0;
}

.logo-title {
  font-size: 18px;
  font-weight: 900;
  color: #fff;
  letter-spacing: -0.03em;
  line-height: 1;
}

.logo-version {
  font-size: 9px;
  color: var(--on-surface-variant);
  opacity: 0.45;
  text-transform: uppercase;
  letter-spacing: 0.15em;
  margin-top: 2px;
}

.sidebar-nav {
  flex: 1;
  padding: 0 10px;
  display: flex;
  flex-direction: column;
  gap: 2px;
  overflow-y: auto;
  overflow-x: hidden;
}
.sidebar-nav::-webkit-scrollbar { width: 3px; }
.sidebar-nav::-webkit-scrollbar-track { background: transparent; }
.sidebar-nav::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.08); border-radius: 3px; }

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 9px 14px;
  border-radius: 8px;
  color: var(--on-surface-variant);
  opacity: 0.7;
  text-decoration: none;
  transition: all 0.2s ease;
  cursor: pointer;
  font-size: 13px;
  white-space: nowrap;
}

.nav-item:hover {
  background-color: var(--bg-surface-high);
  opacity: 1;
}

.nav-item.active {
  color: #fff;
  font-weight: 700;
  background-color: var(--bg-surface-high);
  border-right: 3px solid var(--primary-container);
  border-radius: 4px 8px 8px 4px;
}

.nav-item.active .nav-icon {
  color: var(--primary-container);
}

/* ===== Group Menu ===== */
.nav-group {
  border-radius: 8px;
  transition: all 0.15s ease;
}

.nav-group-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 14px;
  border-radius: 8px;
  cursor: pointer;
  color: var(--on-surface-variant);
  opacity: 0.75;
  transition: all 0.2s ease;
  user-select: none;
}

.nav-group-header:hover {
  background-color: var(--bg-surface-high);
  opacity: 1;
  color: #fff;
}

.nav-group.active .nav-group-header {
  color: #fff;
  opacity: 1;
}

.nav-group.active .nav-group-header .nav-icon {
  color: var(--primary-container);
}

.nav-group-label {
  flex: 1;
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.expand-icon {
  font-size: 18px !important;
  transition: transform 0.2s ease;
  opacity: 0.5;
}

.nav-group.expanded .expand-icon {
  transform: rotate(180deg);
}

/* Submenu */
.nav-submenu {
  padding: 2px 0 6px 0;
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.nav-subitem {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 7px 14px 7px 38px;
  border-radius: 6px;
  color: var(--on-surface-variant);
  opacity: 0.65;
  text-decoration: none;
  transition: all 0.15s ease;
  cursor: pointer;
  font-size: 12.5px;
  white-space: nowrap;
}

.nav-subitem:hover {
  background-color: var(--bg-surface-high);
  opacity: 1;
  color: #fff;
}

.nav-subitem.active {
  color: var(--primary-container);
  font-weight: 700;
  opacity: 1;
  background-color: rgba(15, 98, 254, 0.06);
}

.sub-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: currentColor;
  opacity: 0.4;
  flex-shrink: 0;
  transition: opacity 0.15s;
}

.nav-subitem.active .sub-dot {
  opacity: 1;
  box-shadow: 0 0 6px var(--primary-container);
}

/* Slide animation */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.2s ease;
  overflow: hidden;
}
.slide-enter-from,
.slide-leave-to {
  opacity: 0;
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
}
.slide-enter-to,
.slide-leave-from {
  opacity: 1;
  max-height: 300px;
}

.nav-icon {
  font-size: 19px;
  transition: color 0.2s;
  flex-shrink: 0;
}

.sidebar-footer {
  padding: 0 14px;
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid rgba(66, 70, 86, 0.08);
}

.deploy-btn {
  width: 100%;
  padding: 11px;
  border: none;
  border-radius: 10px;
  color: var(--on-primary-container);
  font-weight: 700;
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s ease;
  box-shadow: 0 4px 20px rgba(15, 98, 254, 0.2);
}

.deploy-btn:hover {
  filter: brightness(1.1);
}

.deploy-btn:active {
  transform: scale(0.97);
}
</style>
