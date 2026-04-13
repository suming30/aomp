<template>
  <header class="topbar">
    <div class="topbar-left">
      <span class="topbar-breadcrumb">{{ currentBreadcrumb }}</span>
    </div>

    <div class="topbar-right">
      <div class="search-box">
        <span class="material-symbols-outlined search-icon">search</span>
        <input
          type="text"
          class="search-input"
          :placeholder="t('topbar.searchPlaceholder')"
        />
      </div>

      <div class="lang-switcher" @click.stop="toggleLangMenu">
        <span class="material-symbols-outlined">language</span>
        <span class="lang-text">{{ currentLangLabel }}</span>
        <span class="material-symbols-outlined lang-arrow">expand_more</span>
        <div v-if="showLangMenu" class="lang-dropdown">
          <div
            class="lang-option"
            :class="{ selected: locale === 'zh' }"
            @click="switchLang('zh')"
          >
            简体中文
          </div>
          <div
            class="lang-option"
            :class="{ selected: locale === 'en' }"
            @click="switchLang('en')"
          >
            English
          </div>
        </div>
      </div>

      <div class="notification-wrapper" @click.stop="toggleNotificationMenu">
        <span class="topbar-icon notification-icon">
          <span class="material-symbols-outlined">notifications</span>
          <span v-if="unreadCount > 0" class="notification-dot">{{ unreadCount > 99 ? '99+' : unreadCount }}</span>
        </span>
        
        <transition name="dropdown-fade">
          <div v-if="showNotificationMenu" class="notification-dropdown">
            <div class="notification-header">
              <span>{{ locale === 'zh' ? '通知' : 'Notifications' }}</span>
              <a href="#" class="mark-all-read" @click.prevent="handleMarkAllRead">{{ locale === 'zh' ? '全部已读' : 'Mark all read' }}</a>
            </div>
            <div class="notification-list" v-loading="notificationLoading">
              <div v-for="item in notifications" :key="item.id" class="notification-item" :class="{ unread: !item.isRead }" @click="handleNotificationClick(item)">
                <span :class="['notification-type', item.type]">{{ item.type }}</span>
                <div class="notification-content">
                  <div class="notification-title">{{ item.title }}</div>
                  <div class="notification-time">{{ formatTime(item.createdAt) }}</div>
                </div>
              </div>
              <div v-if="notifications.length === 0 && !notificationLoading" class="no-notification">
                {{ locale === 'zh' ? '暂无通知' : 'No notifications' }}
              </div>
            </div>
          </div>
        </transition>
      </div>

      <!-- Avatar Dropdown -->
      <div class="avatar-wrapper" @click.stop="toggleUserMenu">
        <img
          :src="`https://ui-avatars.com/api/?name=${encodeURIComponent(userDisplayName)}&background=0F62FE&color=fff&size=64`"
          class="avatar-img"
          alt="User Avatar"
        />
        <div class="avatar-info">
          <span class="avatar-name">{{ userDisplayName }}</span>
          <span class="avatar-role font-mono">{{ userRoles.length > 0 ? userRoles[0] : t('topbar.roleAdmin') }}</span>
        </div>
        <span class="material-symbols-outlined avatar-arrow">expand_more</span>

        <transition name="dropdown-fade">
          <div v-if="showUserMenu" class="user-dropdown">
            <div class="dropdown-header">
              <img
                :src="`https://ui-avatars.com/api/?name=${encodeURIComponent(userDisplayName)}&background=0F62FE&color=fff&size=128`"
                class="dropdown-avatar"
                alt="Avatar"
              />
              <div class="dropdown-user-info">
                <span class="dropdown-username">{{ userDisplayName }}</span>
                <span class="dropdown-email">{{ userEmail }}</span>
              </div>
            </div>
            <div class="dropdown-divider"></div>
            <router-link to="/settings" class="dropdown-item" @click.stop="showUserMenu = false">
              <span class="material-symbols-outlined dropdown-item-icon">settings</span>
              {{ t('topbar.personalSettings') }}
            </router-link>
            <a href="#" class="dropdown-item logout" @click.stop.prevent="handleLogout">
              <span class="material-symbols-outlined dropdown-item-icon">logout</span>
              {{ t('topbar.logout') }}
            </a>
          </div>
        </transition>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { getNotificationList, markNotificationRead, markAllNotificationsRead, getUnreadCount } from '../../api/notification'

const { t, locale } = useI18n()
const router = useRouter()
const userStore = useUserStore()

const showLangMenu = ref(false)
const showUserMenu = ref(false)
const showNotificationMenu = ref(false)
const notificationLoading = ref(false)
const notifications = ref([])
const unreadCount = ref(0)

const currentBreadcrumb = computed(() => {
  return 'AutoOps Platform'
})

const currentLangLabel = computed(() => {
  return locale.value === 'zh' ? 'ZH-CN' : 'EN-US'
})

const currentUser = computed(() => userStore.state.user || {})
const userDisplayName = computed(() => currentUser.value.displayName || currentUser.value.username || 'User')
const userEmail = computed(() => currentUser.value.email || 'user@autoops.io')
const userRoles = computed(() => currentUser.value.roles || [])

function toggleLangMenu() {
  showLangMenu.value = !showLangMenu.value
  showUserMenu.value = false
  showNotificationMenu.value = false
}

function toggleUserMenu() {
  showUserMenu.value = !showUserMenu.value
  showLangMenu.value = false
  showNotificationMenu.value = false
}

function toggleNotificationMenu() {
  showNotificationMenu.value = !showNotificationMenu.value
  showLangMenu.value = false
  showUserMenu.value = false
  if (showNotificationMenu.value) {
    fetchNotifications()
  }
}

function switchLang(lang) {
  locale.value = lang
  localStorage.setItem('autoops_lang', lang)
  showLangMenu.value = false
}

async function handleLogout() {
  showUserMenu.value = false
  await userStore.logout()
}

async function fetchNotifications() {
  notificationLoading.value = true
  try {
    const res = await getNotificationList({ pageNum: 1, pageSize: 10 })
    notifications.value = res.data.records || []
  } catch (error) {
    console.error('Failed to fetch notifications:', error)
  } finally {
    notificationLoading.value = false
  }
}

async function fetchUnreadCount() {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data || 0
  } catch (error) {
    console.error('Failed to fetch unread count:', error)
  }
}

async function handleNotificationClick(item) {
  if (!item.isRead) {
    try {
      await markNotificationRead(item.id)
      item.isRead = true
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch (error) {
      console.error('Failed to mark notification read:', error)
    }
  }
  if (item.link) {
    router.push(item.link)
    showNotificationMenu.value = false
  }
}

async function handleMarkAllRead() {
  try {
    await markAllNotificationsRead()
    notifications.value.forEach(n => n.isRead = true)
    unreadCount.value = 0
  } catch (error) {
    console.error('Failed to mark all read:', error)
  }
}

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return locale.value === 'zh' ? '刚刚' : 'Just now'
  if (minutes < 60) return locale.value === 'zh' ? `${minutes}分钟前` : `${minutes}m ago`
  if (hours < 24) return locale.value === 'zh' ? `${hours}小时前` : `${hours}h ago`
  return locale.value === 'zh' ? `${days}天前` : `${days}d ago`
}

function handleClickOutside(e) {
  const target = e.target
  if (!target.closest('.lang-switcher') && !target.closest('.avatar-wrapper') && !target.closest('.notification-wrapper')) {
    showLangMenu.value = false
    showUserMenu.value = false
    showNotificationMenu.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  fetchUnreadCount()
  setInterval(fetchUnreadCount, 60000)
})
onUnmounted(() => document.removeEventListener('click', handleClickOutside))
</script>

<style scoped>
.topbar {
  position: fixed;
  top: 0;
  right: 0;
  left: var(--sidebar-width);
  height: var(--topbar-height);
  z-index: 50;
  background-color: rgba(19, 19, 19, 0.85);
  backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  box-shadow: 0 4px 30px rgba(229, 226, 225, 0.05);
}

.topbar-left {
  display: flex;
  align-items: center;
}

.topbar-breadcrumb {
  font-family: var(--font-headline);
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 0.15em;
  color: var(--on-surface-variant);
  cursor: pointer;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-box {
  display: flex;
  align-items: center;
  background-color: var(--bg-surface-container-low);
  border-radius: 9999px;
  padding: 6px 14px;
  gap: 8px;
  border: 1px solid rgba(66, 70, 86, 0.1);
}

.search-icon { font-size: 14px; color: var(--outline); }

.search-input {
  background: transparent;
  border: none;
  outline: none;
  color: var(--on-surface);
  font-family: var(--font-headline);
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  width: 180px;
}
.search-input::placeholder { color: var(--outline); opacity: 0.5; }

.lang-switcher {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 8px;
  background-color: var(--bg-surface-container-low);
  border: 1px solid rgba(66, 70, 86, 0.1);
  position: relative;
  transition: background-color 0.2s;
}
.lang-switcher:hover { background-color: var(--bg-surface-high); }
.lang-switcher .material-symbols-outlined { font-size: 16px; color: var(--primary-container); }

.lang-text {
  font-family: var(--font-headline);
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--on-surface-variant);
}

.lang-arrow { font-size: 14px !important; color: var(--on-surface-variant) !important; }

.lang-dropdown {
  position: absolute;
  top: calc(100% + 6px);
  right: 0;
  background-color: var(--bg-surface-container);
  border: 1px solid var(--outline-variant);
  border-radius: 8px;
  min-width: 140px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
  z-index: 100;
  overflow: hidden;
}

.lang-option {
  padding: 10px 16px;
  font-size: 13px;
  cursor: pointer;
  transition: background-color 0.15s;
  color: var(--on-surface-variant);
}
.lang-option:hover { background-color: var(--bg-surface-high); color: #fff; }
.lang-option.selected { color: var(--primary-container); font-weight: 600; background-color: rgba(15, 98, 254, 0.08); }

.notification-icon {
  cursor: pointer;
  color: var(--on-surface-variant);
  transition: color 0.2s;
  position: relative;
  display: flex;
  align-items: center;
}
.notification-icon:hover { color: #fff; }
.notification-icon .material-symbols-outlined { font-size: 22px; }
.notification-dot {
  position: absolute;
  top: -2px; right: -4px;
  min-width: 16px; height: 16px;
  padding: 0 4px;
  background-color: var(--error);
  border-radius: 8px;
  font-size: 9px;
  font-weight: 700;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 0 2px var(--bg-base);
}

.notification-wrapper {
  position: relative;
}

.notification-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: -40px;
  width: 320px;
  background-color: var(--bg-surface-container);
  border: 1px solid var(--outline-variant);
  border-radius: 14px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.55);
  z-index: 200;
  overflow: hidden;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(66, 70, 86, 0.1);
  font-size: 13px;
  font-weight: 700;
  color: #fff;
}

.mark-all-read {
  font-size: 11px;
  color: var(--primary-container);
  text-decoration: none;
}

.mark-all-read:hover {
  text-decoration: underline;
}

.notification-list {
  max-height: 300px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.15s;
  border-bottom: 1px solid rgba(66, 70, 86, 0.05);
}

.notification-item:hover {
  background-color: var(--bg-surface-high);
}

.notification-item.unread {
  background-color: rgba(15, 98, 254, 0.04);
}

.notification-type {
  font-size: 9px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 4px;
  text-transform: uppercase;
  height: fit-content;
}

.notification-type.task { background: rgba(15, 98, 254, 0.12); color: var(--primary-container); }
.notification-type.inspection { background: rgba(57, 153, 174, 0.12); color: #3999AE; }
.notification-type.security { background: rgba(147, 0, 10, 0.12); color: var(--error); }
.notification-type.system { background: rgba(180, 197, 255, 0.08); color: var(--primary); }

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-size: 12px;
  color: var(--on-surface);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-time {
  font-size: 10px;
  color: var(--on-surface-variant);
  margin-top: 4px;
}

.no-notification {
  text-align: center;
  padding: 40px 20px;
  color: var(--on-surface-variant);
  font-size: 13px;
}

/* ===== Avatar Dropdown ===== */
.avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 5px 12px 5px 5px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  border: 1px solid transparent;
}
.avatar-wrapper:hover {
  background-color: var(--bg-surface-high);
  border-color: rgba(66, 70, 86, 0.1);
}

.avatar-img {
  width: 34px; height: 34px;
  border-radius: 8px;
  object-fit: cover;
  border: 2px solid transparent;
  transition: border-color 0.2s;
}
.avatar-wrapper:hover .avatar-img { border-color: var(--primary-container); }

.avatar-info {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.avatar-name {
  font-size: 12px;
  font-weight: 700;
  color: #fff;
}

.avatar-role {
  font-size: 9px;
  color: var(--primary-container);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.avatar-arrow {
  font-size: 18px !important;
  color: var(--on-surface-variant) !important;
  opacity: 0.5;
  transition: transform 0.2s;
}

/* User Dropdown Menu */
.user-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: -4px;
  width: 260px;
  background-color: var(--bg-surface-container);
  border: 1px solid var(--outline-variant);
  border-radius: 14px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.55);
  z-index: 200;
  overflow: hidden;
}

.dropdown-header {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(15, 98, 254, 0.08) 0%, transparent 100%);
}

.dropdown-avatar {
  width: 48px; height: 48px;
  border-radius: 12px;
  object-fit: cover;
  border: 2px solid var(--primary-container);
}

.dropdown-user-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.dropdown-username {
  font-size: 14px;
  font-weight: 700;
  color: #fff;
}

.dropdown-email {
  font-size: 11px;
  color: var(--on-surface-variant);
}

.dropdown-divider {
  height: 1px;
  background: rgba(66, 70, 86, 0.1);
  margin: 0 16px;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  font-size: 13px;
  color: var(--on-surface);
  text-decoration: none;
  transition: all 0.15s ease;
  cursor: pointer;
}
.dropdown-item:hover {
  background-color: var(--bg-surface-high);
  color: #fff;
}
.dropdown-item.logout {
  color: var(--error);
}
.dropdown-item.logout:hover {
  background-color: rgba(255, 180, 171, 0.08);
}

.dropdown-item-icon {
  font-size: 18px !important;
  color: inherit;
  opacity: 0.7;
}

/* Dropdown animation */
.dropdown-fade-enter-active,
.dropdown-fade-leave-active {
  transition: all 0.18s ease;
}
.dropdown-fade-enter-from,
.dropdown-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.96);
}
</style>
