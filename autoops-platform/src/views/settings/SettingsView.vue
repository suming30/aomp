<template>
  <div class="settings-page">
    <div class="page-header-row">
      <div>
        <h1 class="page-title">{{ t('settings.title') }}</h1>
        <p class="page-subtitle">{{ t('settings.subtitle') }}</p>
      </div>
      <button class="save-btn litho-gradient" @click="handleSave">
        <span class="material-symbols-outlined">save</span>
        {{ t('settings.saveChanges') }}
      </button>
    </div>

    <div class="settings-grid">
      <!-- Left Nav -->
      <aside class="settings-sidebar glass-card">
        <div class="user-card">
          <img src="https://ui-avatars.com/api/?name=ZS&background=0F62FE&color=fff&size=128" class="user-avatar" />
          <span class="user-name">zhang_san</span>
          <span class="user-role font-mono">{{ t('topbar.roleAdmin') }}</span>
        </div>
        <nav class="side-nav">
          <button
            v-for="(item, idx) in navItems"
            :key="idx"
            :class="['nav-btn', { active: activeSection === item.id }]"
            @click="activeSection = item.id"
          >
            <span class="material-symbols-outlined nav-icon">{{ item.icon }}</span>
            {{ item.label }}
          </button>
        </nav>
      </aside>

      <!-- Right Content -->
      <main class="settings-content">
        <!-- Profile -->
        <section v-if="activeSection === 'profile'" class="content-section glass-card">
          <div class="sec-head">
            <span class="material-symbols-outlined sec-icon">person</span>
            <h3>{{ t('settings.profile') }}</h3>
          </div>
          <div class="form-layout">
            <div class="form-col">
              <div class="field">
                <label>{{ t('settings.username') }}</label>
                <input type="text" value="zhang_san" />
              </div>
              <div class="field">
                <label>{{ t('settings.email') }}</label>
                <input type="email" value="zhang_san@autoops.io" />
              </div>
            </div>
            <div class="form-col">
              <div class="field">
                <label>Phone</label>
                <input type="text" value="+86 138****8888" />
              </div>
              <div class="field">
                <label>Department</label>
                <input type="text" value="Platform Team" />
              </div>
            </div>
          </div>
          <div class="field field-full">
            <label>Bio</label>
            <textarea rows="2" placeholder="Tell us about yourself..."></textarea>
          </div>
        </section>

        <!-- Appearance -->
        <section v-if="activeSection === 'appearance'" class="content-section glass-card">
          <div class="sec-head">
            <span class="material-symbols-outlined sec-icon">palette</span>
            <h3>{{ t('settings.appearanceTheme') }}</h3>
          </div>
          <div class="theme-grid">
            <div :class="['theme-card', { active: theme === 'dark' }]" @click="theme = 'dark'">
              <div class="theme-preview dark-preview"></div>
              <div class="theme-info">
                <span class="theme-name">{{ t('settings.darkMode') }}</span>
                <span v-if="theme === 'dark'" class="check-dot"></span>
              </div>
            </div>
            <div :class="['theme-card', { active: theme === 'light' }]" @click="theme = 'light'">
              <div class="theme-preview light-preview"></div>
              <div class="theme-info">
                <span class="theme-name">{{ t('settings.lightMode') }}</span>
                <span v-if="theme === 'light'" class="check-dot"></span>
              </div>
            </div>
          </div>
        </section>

        <!-- Language -->
        <section v-if="activeSection === 'language'" class="content-section glass-card">
          <div class="sec-head">
            <span class="material-symbols-outlined sec-icon">language</span>
            <h3>{{ t('settings.languageSettings') }}</h3>
          </div>
          <div class="lang-list">
            <div :class="['lang-card', { selected: locale === 'zh' }]" @click="switchLang('zh')">
              <span class="lang-flag">🇨🇳</span>
              <div class="lang-detail">
                <span class="lang-name">简体中文</span>
                <span class="lang-sub">Simplified Chinese</span>
              </div>
              <span v-if="locale === 'zh'" class="check-dot"></span>
            </div>
            <div :class="['lang-card', { selected: locale === 'en' }]" @click="switchLang('en')">
              <span class="lang-flag">🇺🇸</span>
              <div class="lang-detail">
                <span class="lang-name">English</span>
                <span class="lang-sub">United States English</span>
              </div>
              <span v-if="locale === 'en'" class="check-dot"></span>
            </div>
          </div>
        </section>

        <!-- Security -->
        <section v-if="activeSection === 'security'" class="content-section glass-card">
          <div class="sec-head">
            <span class="material-symbols-outlined sec-icon">security</span>
            <h3>{{ t('settings.securitySettings') }}</h3>
          </div>
          <div class="sec-items">
            <div class="sec-item">
              <div class="sec-left">
                <div class="sec-icon-wrap"><span class="material-symbols-outlined">lock</span></div>
                <div class="sec-text">
                  <p class="sec-label">{{ t('settings.changePassword') }}</p>
                  <p class="sec-desc">Last changed 30 days ago</p>
                </div>
              </div>
              <button class="sec-btn">Update</button>
            </div>
            <div class="sec-item">
              <div class="sec-left">
                <div class="sec-icon-wrap"><span class="material-symbols-outlined">verified_user</span></div>
                <div class="sec-text">
                  <p class="sec-label">{{ t('settings.twoFactorAuth') }}</p>
                  <p class="sec-desc">Using TOTP authenticator app</p>
                </div>
              </div>
              <label class="switch-wrap">
                <input type="checkbox" checked /><span class="switch-track"><span class="switch-thumb"></span></span>
              </label>
            </div>
            <div class="sec-item">
              <div class="sec-left">
                <div class="sec-icon-wrap"><span class="material-symbols-outlined">devices</span></div>
                <div class="sec-text">
                  <p class="sec-label">Active Sessions</p>
                  <p class="sec-desc">3 devices currently logged in</p>
                </div>
              </div>
              <button class="sec-btn danger">Manage</button>
            </div>
          </div>
        </section>

        <!-- Notifications -->
        <section v-if="activeSection === 'notifications'" class="content-section glass-card">
          <div class="sec-head">
            <span class="material-symbols-outlined sec-icon">notifications</span>
            <h3>{{ t('settings.notificationSettings') }}</h3>
          </div>
          <div class="notif-items">
            <div class="notif-item" v-for="(n, idx) in notifs" :key="idx">
              <div class="notif-text">
                <p class="notif-label">{{ n.label }}</p>
                <p class="notif-desc">{{ n.desc }}</p>
              </div>
              <label class="switch-wrap">
                <input type="checkbox" :checked="n.on" /><span class="switch-track"><span class="switch-thumb"></span></span>
              </label>
            </div>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()
const activeSection = ref('profile')
const theme = ref('dark')

const navItems = [
  { id: 'profile', icon: 'person', label: t('settings.profile') },
  { id: 'appearance', icon: 'palette', label: t('settings.appearanceTheme') },
  { id: 'language', icon: 'language', label: t('settings.languageSettings') },
  { id: 'security', icon: 'security', label: t('settings.securitySettings') },
  { id: 'notifications', icon: 'notifications', label: t('settings.notificationSettings') }
]

const notifs = [
  { label: 'Task Completion Alerts', desc: 'Get notified when tasks finish executing', on: true },
  { label: 'System Health Warnings', desc: 'Receive alerts when host health drops below threshold', on: true },
  { label: 'Security Alerts', desc: 'Critical security events and access denials', on: true },
  { label: 'Weekly Digest Email', desc: 'Summary of operations every Monday', on: false }
]

function switchLang(lang) {
  locale.value = lang
  localStorage.setItem('autoops_lang', lang)
}
function handleSave() {}
</script>

<style scoped>
.settings-page {
  padding: 24px 32px;
  max-width: 1440px;
  margin: 0 auto;
}

.page-header-row {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 24px;
}
.page-title {
  font-size: 24px; font-weight: 900; color: #fff;
  letter-spacing: -0.02em;
}
.page-subtitle { font-size: 11.5px; color: var(--on-surface-variant); margin-top: 3px; }

.save-btn {
  display: flex; align-items: center; gap: 6px;
  padding: 9px 24px; border-radius: 8px; border: none;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-container) 100%);
  color: #fff; font-family: var(--font-label); font-size: 11px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.08em; cursor: pointer;
  box-shadow: 0 4px 15px rgba(15,98,254,0.25);
}
.save-btn .material-symbols-outlined { font-size: 16px !important; }

/* Grid Layout */
.settings-grid {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 24px;
  align-items: start;
}

/* Sidebar */
.settings-sidebar {
  border-radius: 14px; padding: 20px;
  position: sticky; top: 106px;
  display: flex; flex-direction: column; gap: 20px;
}
.user-card {
  display: flex; flex-direction: column; align-items: center; gap: 8px;
  padding-bottom: 16px; border-bottom: 1px solid rgba(66,70,86,0.08);
}
.user-avatar {
  width: 56px; height: 56px; border-radius: 50%;
  border: 2px solid var(--primary-container);
}
.user-name { font-size: 13px; font-weight: 700; color: #fff; }
.user-role { font-size: 9px; color: var(--primary-container); opacity: 0.7; }

.side-nav { display: flex; flex-direction: column; gap: 3px; }
.nav-btn {
  display: flex; align-items: center; gap: 10px;
  padding: 9px 12px; border-radius: 8px; border: none;
  background: transparent; color: var(--on-surface-variant);
  cursor: pointer; font-size: 12.5px; font-weight: 500;
  transition: all 0.15s; width: 100%; text-align: left;
}
.nav-btn:hover { background: rgba(255,255,255,0.04); color: #fff; }
.nav-btn.active {
  background: rgba(15,98,254,0.08); color: var(--primary-container);
  font-weight: 700;
}
.nav-btn .nav-icon { font-size: 17px !important; }

/* Content */
.settings-content { display: flex; flex-direction: column; gap: 16px; }

.content-section {
  border-radius: 14px; padding: 22px 26px;
}
.sec-head {
  display: flex; align-items: center; gap: 10px;
  margin-bottom: 20px; padding-bottom: 14px;
  border-bottom: 1px solid rgba(66,70,86,0.07);
}
.sec-head h3 {
  font-size: 13px; font-weight: 800; color: #fff;
  text-transform: uppercase; letter-spacing: 0.04em;
}
.sec-icon { font-size: 20px !important; color: var(--primary-container); opacity: 0.7; }

/* Form */
.form-layout { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.form-col { display: flex; flex-direction: column; gap: 14px; }
.field { display: flex; flex-direction: column; gap: 5px; }
.field-full { margin-top: 4px; }
.field label {
  font-size: 9.5px; font-weight: 700; color: var(--on-surface-variant);
  text-transform: uppercase; letter-spacing: 0.1em;
}
.field input,
.field textarea {
  background: var(--bg-base); border: 1px solid var(--outline-variant);
  border-radius: 8px; padding: 9px 12px; color: #fff; font-size: 13px;
  outline: none; font-family: var(--font-body);
}
.field textarea { resize: vertical; }

/* Theme */
.theme-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.theme-card {
  padding: 18px; border-radius: 11px; border: 2px solid transparent;
  cursor: pointer; transition: all 0.18s; background: var(--bg-base);
}
.theme-card:hover { border-color: var(--outline-variant); }
.theme-card.active { border-color: var(--primary-container); background: rgba(15,98,254,0.03); }
.theme-preview {
  height: 72px; border-radius: 8px; margin-bottom: 10px;
}
.dark-preview { background: linear-gradient(135deg, #131313, #201F1F); border: 1px solid rgba(66,70,86,0.2); }
.light-preview { background: linear-gradient(135deg, #f5f5f5, #e8e8e8); border: 1px solid rgba(0,0,0,0.1); }
.theme-info { display: flex; justify-content: space-between; align-items: center; }
.theme-name { font-size: 12px; font-weight: 600; color: #fff; }
.check-dot {
  width: 18px; height: 18px; border-radius: 50%;
  background: var(--primary-container); display: inline-block;
  box-shadow: 0 0 8px rgba(15,98,254,0.4);
}

/* Lang */
.lang-list { display: flex; flex-direction: column; gap: 10px; }
.lang-card {
  display: flex; align-items: center; gap: 14px;
  padding: 13px 16px; border-radius: 10px;
  border: 1px solid rgba(66,70,86,0.06); cursor: pointer;
  transition: all 0.15s; background: var(--bg-base);
}
.lang-card:hover { border-color: var(--outline-variant); }
.lang-card.selected { border-color: var(--primary-container); background: rgba(15,98,254,0.03); }
.lang-flag { font-size: 22px; }
.lang-detail { flex: 1; display: flex; flex-direction: column; }
.lang-name { font-size: 12.5px; font-weight: 600; color: #fff; }
.lang-sub { font-size: 10px; color: var(--on-surface-variant); }

/* Security Items */
.sec-items { display: flex; flex-direction: column; gap: 10px; }
.sec-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 14px 16px; border-radius: 10px;
  background: var(--bg-base); border: 1px solid rgba(66,70,86,0.05);
}
.sec-left { display: flex; align-items: center; gap: 12px; }
.sec-icon-wrap {
  width: 36px; height: 36px; border-radius: 8px;
  background: rgba(15,98,254,0.08); display: flex;
  align-items: center; justify-content: center;
}
.sec-icon-wrap .material-symbols-outlined { font-size: 18px !important; color: var(--primary-container); }
.sec-label { font-size: 12.5px; font-weight: 600; color: #fff; }
.sec-desc { font-size: 10px; color: var(--on-surface-variant); margin-top: 2px; }

.sec-btn {
  padding: 6px 16px; border-radius: 6px; border: 1px solid rgba(66,70,86,0.1);
  background: var(--bg-surface-high); color: var(--on-surface-variant);
  font-size: 11px; font-weight: 600; cursor: pointer; transition: all 0.15s;
}
.sec-btn:hover { background: var(--bg-bright); color: #fff; }
.sec-btn.danger { color: var(--error); border-color: rgba(255,180,171,0.12); }

/* Toggle Switch */
.switch-wrap { cursor: pointer; position: relative; }
.switch-wrap input { display: none; }
.switch-track {
  display: block; width: 40px; height: 22px;
  background: var(--bg-surface-highest); border-radius: 11px;
  transition: 0.2s; position: relative;
}
.switch-thumb {
  position: absolute; width: 17px; height: 17px;
  background: #fff; border-radius: 50%; top: 2.5px; left: 2.5px;
  transition: 0.2s; box-shadow: 0 1px 3px rgba(0,0,0,0.2);
}
.switch-wrap input:checked + .switch-track { background: var(--primary-container); }
.switch-wrap input:checked + .switch-thumb { transform: translateX(18px); }

/* Notif */
.notif-items { display: flex; flex-direction: column; gap: 0; }
.notif-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 13px 0; border-bottom: 1px solid rgba(66,70,86,0.05);
}
.notif-item:last-child { border-bottom: none; }
.notif-label { font-size: 12.5px; font-weight: 600; color: #fff; }
.notif-desc { font-size: 10px; color: var(--on-surface-variant); margin-top: 2px; }
</style>
