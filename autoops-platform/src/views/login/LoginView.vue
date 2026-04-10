<template>
  <div class="login-page">
    <div class="bg-effects">
      <div class="glow glow-1"></div>
      <div class="glow glow-2"></div>
    </div>

    <nav class="lang-nav">
      <div class="lang-btn" @click.stop="toggleLangMenu">
        <span class="material-symbols-outlined">language</span>
        <span>{{ locale === 'zh' ? '简体中文' : 'English' }}</span>
        <span class="material-symbols-outlined">expand_more</span>
        <div v-if="showLang" class="lang-dropdown">
          <div :class="{ sel: locale === 'zh' }" @click="switchLang('zh')">简体中文</div>
          <div :class="{ sel: locale === 'en' }" @click="switchLang('en')">English</div>
        </div>
      </div>
    </nav>

    <main class="login-main">
      <div class="brand">
        <div class="brand-icon">
          <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">deployed_code</span>
        </div>
        <h1 class="brand-title">{{ t('login.title') }}</h1>
        <p class="brand-subtitle">{{ t('login.subtitle') }}</p>
      </div>

      <div class="login-card glass-card">
        <h2 class="card-title">{{ t('login.formTitle') }}</h2>

        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label class="form-label">{{ t('login.username') }}</label>
            <div class="input-wrapper">
              <span class="material-symbols-outlined input-icon">person</span>
              <input
                v-model="form.username"
                type="text"
                class="form-input"
                :placeholder="t('login.usernamePlaceholder')"
              />
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">{{ t('login.password') }}</label>
            <div class="input-wrapper">
              <span class="material-symbols-outlined input-icon">lock</span>
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                class="form-input"
                :placeholder="t('login.passwordPlaceholder')"
              />
              <span class="material-symbols-outlined input-icon-right" @click="showPassword = !showPassword">
                {{ showPassword ? 'visibility_off' : 'visibility' }}
              </span>
            </div>
          </div>

          <div class="form-row">
            <label class="checkbox-label">
              <input type="checkbox" v-model="form.remember" />
              <span>{{ t('login.rememberMe') }}</span>
            </label>
            <a href="#" class="forgot-link">{{ t('login.forgotPassword') }}</a>
          </div>

          <button type="submit" class="submit-btn litho-gradient">
            {{ t('login.submit') }}
          </button>
        </form>

        <div class="divider-line"></div>

        <div class="other-methods">
          <span class="methods-label">{{ t('login.otherMethods') }}</span>
          <div class="method-buttons">
            <button class="method-btn">
              <span class="material-symbols-outlined">fingerprint</span>
            </button>
            <button class="method-btn">
              <span class="material-symbols-outlined">qr_code_scanner</span>
            </button>
          </div>
        </div>
      </div>

      <div class="footer-info">
        <p class="footer-text font-mono">SECURE CONNECTION: AES-256 BIT ENCRYPTION</p>
        <div class="status-indicator">
          <div class="status-dot"></div>
          <span class="font-mono status-text">NODE STATUS: OPTIMAL</span>
        </div>
      </div>
    </main>

    <footer class="page-footer">
      <p>{{ t('login.footer') }}</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'
import { useUserStore } from '../../stores/user'

const { t, locale } = useI18n()
const router = useRouter()
const userStore = useUserStore()

const showLang = ref(false)
const showPassword = ref(false)
const loading = ref(false)
const form = reactive({ username: '', password: '', remember: false })

function toggleLangMenu() {
  showLang.value = !showLang.value
}

function switchLang(lang) {
  locale.value = lang
  localStorage.setItem('autoops_lang', lang)
  showLang.value = false
}

async function handleLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning(locale.value === 'zh' ? '请输入用户名和密码' : 'Please enter username and password')
    return
  }
  
  loading.value = true
  const loadingInstance = ElLoading.service({ 
    lock: true, 
    text: locale.value === 'zh' ? '登录中...' : 'Logging in...',
    background: 'rgba(0, 0, 0, 0.7)'
  })
  
  try {
    await userStore.login(form.username, form.password)
    ElMessage.success(locale.value === 'zh' ? '登录成功' : 'Login successful')
    router.push('/dashboard')
  } catch (error) {
    console.error('Login failed:', error)
  } finally {
    loading.value = false
    loadingInstance.close()
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background-color: var(--bg-base);
  background-image:
    radial-gradient(circle at 2px 2px, rgba(255,255,255,0.03) 1px, transparent 0);
  background-size: 40px 40px;
}

.bg-effects {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  pointer-events: none;
  overflow: hidden;
  z-index: 0;
}

.glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
}
.glow-1 {
  top: -10%; left: -10%;
  width: 40%; height: 40%;
  background-color: var(--primary-container);
  opacity: 0.15;
}
.glow-2 {
  bottom: -10%; right: -10%;
  width: 30%; height: 30%;
  background-color: var(--secondary-container);
  opacity: 0.12;
}

.lang-nav {
  position: fixed;
  top: 32px;
  right: 32px;
  z-index: 50;
  display: flex;
  gap: 16px;
}

.lang-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 8px;
  background-color: var(--bg-surface-container-low);
  cursor: pointer;
  color: var(--on-surface);
  font-size: 12px;
  transition: background-color 0.2s;
  position: relative;
  font-family: var(--font-label);
}

.lang-btn:hover {
  background-color: var(--bg-surface-high);
}

.lang-btn .material-symbols-outlined:first-child {
  font-size: 16px;
  color: var(--primary-container);
}

.lang-dropdown {
  position: absolute;
  top: calc(100% + 6px);
  right: 0;
  background: var(--bg-surface-container);
  border: 1px solid var(--outline-variant);
  border-radius: 8px;
  min-width: 130px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.5);
  z-index: 100;
  overflow: hidden;
}

.lang-dropdown div {
  padding: 10px 16px;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.15s;
  color: var(--on-surface-variant);
}

.lang-dropdown div:hover { background: var(--bg-surface-high); color: #fff; }
.lang-dropdown div.sel { color: var(--primary-container); font-weight: 600; }

.login-main {
  width: 100%;
  max-width: 448px;
  padding: 24px;
  z-index: 10;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.brand {
  text-align: center;
  margin-bottom: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.brand-icon {
  width: 44px;
  height: 44px;
  background-color: var(--primary-container);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 14px;
  color: white;
  font-size: 24px;
}

.brand-title {
  font-size: 24px;
  font-weight: 900;
  letter-spacing: -0.02em;
  color: #fff;
}

.brand-subtitle {
  font-family: var(--font-label);
  font-size: 14px;
  color: var(--on-surface-variant);
  margin-top: 6px;
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

.login-card {
  width: 100%;
  padding: 36px;
  border-radius: 16px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.5);
  border: 1px solid rgba(66, 70, 86, 0.1);
}

.card-title {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 28px;
  padding-left: 16px;
  border-left: 4px solid var(--primary-container);
  letter-spacing: -0.01em;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-family: var(--font-label);
  font-size: 11px;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-weight: 500;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 12px;
  font-size: 18px;
  color: var(--outline);
  pointer-events: none;
}

.input-icon-right {
  position: absolute;
  right: 12px;
  font-size: 18px;
  color: var(--outline);
  cursor: pointer;
  transition: color 0.2s;
}

.input-icon-right:hover { color: #fff; }

.form-input {
  width: 100%;
  background-color: var(--bg-surface-lowest);
  border: none;
  border-radius: 10px;
  padding: 13px 12px 13px 42px;
  font-size: 14px;
  color: #fff;
  outline: none;
  transition: box-shadow 0.2s;
  font-family: var(--font-body);
}

.form-input:focus {
  box-shadow: 0 0 0 1px var(--primary-container);
}

.form-input::placeholder {
  color: var(--outline);
  opacity: 0.7;
}

.form-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 12px;
  color: var(--on-surface-variant);
  transition: color 0.2s;
}

.checkbox-label input[type="checkbox"] {
  width: 16px;
  height: 16px;
  accent-color: var(--primary-container);
  border-radius: 4px;
}

.checkbox-label:hover { color: #fff; }

.forgot-link {
  font-size: 12px;
  color: var(--primary);
  text-decoration: none;
  font-weight: 500;
}

.forgot-link:hover { text-decoration: underline; }

.submit-btn {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 10px;
  color: var(--on-primary-container);
  font-weight: 700;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 4px 15px rgba(15, 98, 254, 0.3);
  font-family: var(--font-label);
}

.submit-btn:hover { opacity: 0.9; }
.submit-btn:active { transform: scale(0.98); }

.divider-line {
  height: 1px;
  background-color: rgba(66, 70, 86, 0.1);
  margin: 28px 0;
}

.other-methods {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.methods-label {
  font-family: var(--font-label);
  font-size: 10px;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.15em;
}

.method-buttons {
  display: flex;
  gap: 16px;
}

.method-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background-color: var(--bg-surface-high);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--on-surface-variant);
  transition: all 0.2s;
}

.method-btn:hover {
  background-color: var(--bg-bright);
  color: #fff;
}

.footer-info {
  margin-top: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  opacity: 0.35;
}

.footer-text {
  font-size: 10px;
  letter-spacing: 0.05em;
  color: var(--on-surface-variant);
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: var(--primary-container);
  animation: pulse-glow 2s infinite;
}

.status-text {
  font-size: 10px;
  color: var(--on-surface-variant);
}

.page-footer {
  position: fixed;
  bottom: 24px;
  width: 100%;
  text-align: center;
}

.page-footer p {
  font-family: var(--font-label);
  font-size: 10px;
  color: var(--on-surface-variant);
  letter-spacing: 0.12em;
  text-transform: uppercase;
}
</style>
