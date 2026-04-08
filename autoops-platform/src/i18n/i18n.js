import { createI18n } from 'vue-i18n'
import messages from './index.js'

const i18n = createI18n({
  legacy: false,
  locale: localStorage.getItem('autoops_lang') || 'zh',
  fallbackLocale: 'zh',
  messages
})

export default i18n
