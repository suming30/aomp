<template>
  <div class="task-config-page">
    <div class="page-header">
      <!-- <div>
        <h1 class="page-title">{{ t('tasks.configTitle') }}</h1>
        <p class="page-subtitle">{{ t('tasks.configSubtitle') }}</p>
      </div> -->
      <div class="header-actions">
        <button class="action-btn secondary" @click="handleSaveDraft" :disabled="loading">
          <span class="material-symbols-outlined">save</span>
          {{ t('tasks.saveDraft') }}
        </button>
        <button class="action-btn primary litho-gradient" @click="handleStartExec" :disabled="loading">
          <span class="material-symbols-outlined">play_arrow</span>
          {{ t('tasks.startExec') }}
        </button>
      </div>
    </div>

    <div class="config-layout">
      <div class="config-main">
        <!-- Step 1: Target Selection -->
        <section class="config-section">
          <div class="step-header">
            <span class="step-num">1</span>
            <h3>{{ t('tasks.step1Target') }}</h3>
            <p class="step-desc">{{ t('tasks.step1Desc') }}</p>
          </div>

          <div class="tab-switcher">
            <button :class="['switch-btn', { active: targetMode === 'group' }]" @click="targetMode = 'group'">
              {{ t('tasks.selectByGroup') }}
            </button>
            <button :class="['switch-btn', { active: targetMode === 'manual' }]" @click="targetMode = 'manual'">
              {{ t('tasks.manualInput') }}
            </button>
          </div>

          <div v-if="targetMode === 'group'" class="group-selector">
            <label class="field-label">{{ t('tasks.businessGroup') }}</label>
            <select class="field-select" v-model="formData.groupId">
              <option :value="null">{{ locale === 'zh' ? '选择分组...' : 'Select group...' }}</option>
              <option v-for="g in groups" :key="g.id" :value="g.id">{{ g.name }}</option>
            </select>
            <div class="selected-hosts">
              <div v-for="(h, idx) in selectedHosts" :key="idx" class="host-chip">
                <span class="font-mono">{{ h }}</span>
                <span class="chip-remove" @click="removeHost(h)">×</span>
              </div>
              <div v-if="selectedHosts.length === 0" class="no-hosts-hint">
                {{ locale === 'zh' ? '请选择目标主机' : 'Please select target hosts' }}
              </div>
            </div>
          </div>

          <div v-else class="manual-input-area">
            <textarea class="manual-textarea font-mono" v-model="formData.manualHosts" :placeholder="locale === 'zh' ? '输入主机IP或ID，每行一个...' : 'Enter host IPs or IDs, one per line...'"></textarea>
          </div>
        </section>

        <!-- Step 2: Script Selection -->
        <section class="config-section">
          <div class="step-header">
            <span class="step-num">2</span>
            <h3>{{ locale === 'zh' ? '选择脚本/命令' : 'Select Script / Command' }}</h3>
            <p class="step-desc">{{ locale === 'zh' ? '选择要执行的脚本或输入自定义命令' : 'Choose the script to execute or enter a raw command' }}</p>
          </div>

          <div class="script-select-row">
            <select class="field-select wide" v-model="formData.scriptId" @change="onScriptChange(formData.scriptId)">
              <option :value="null">{{ locale === 'zh' ? '选择脚本...' : 'Select script...' }}</option>
              <option v-for="s in scripts" :key="s.id" :value="s.id">{{ s.name }} (v{{ s.version || '1.0.0' }})</option>
            </select>
          </div>

          <div class="script-preview glass-card">
            <div class="preview-header">
              <span class="material-symbols-outlined">code</span>
              <span>{{ locale === 'zh' ? '脚本预览' : 'Script Preview' }}</span>
            </div>
            <pre class="preview-code font-mono">{{ scriptContent || (locale === 'zh' ? '# 请选择脚本查看内容...' : '# Select a script to preview...') }}</pre>
          </div>
        </section>

        <!-- Step 3: Parameters -->
        <section class="config-section">
          <div class="step-header">
            <span class="step-num">3</span>
            <h3>{{ locale === 'zh' ? '执行参数' : 'Execution Parameters' }}</h3>
            <p class="step-desc">{{ locale === 'zh' ? '配置超时时间、重试策略和环境变量' : 'Configure timeout, retry policy, and environment variables' }}</p>
          </div>

          <div class="params-grid">
            <div class="param-field">
              <label>{{ locale === 'zh' ? '超时时间(秒)' : 'Timeout (seconds)' }}</label>
              <input type="number" v-model="formData.timeout" />
            </div>
            <div class="param-field">
              <label>{{ locale === 'zh' ? '最大重试次数' : 'Max Retries' }}</label>
              <input type="number" v-model="formData.maxRetries" />
            </div>
            <div class="param-field">
              <label>{{ locale === 'zh' ? '并发数' : 'Concurrency' }}</label>
              <input type="number" v-model="formData.concurrency" />
            </div>
            <div class="param-field">
              <label>{{ locale === 'zh' ? '失败策略' : 'Fail Strategy' }}</label>
              <select v-model="formData.failStrategy">
                <option value="continue">{{ locale === 'zh' ? '失败继续' : 'Continue on Failure' }}</option>
                <option value="stop">{{ locale === 'zh' ? '失败停止' : 'Stop on First Error' }}</option>
              </select>
            </div>
          </div>
        </section>
      </div>

      <aside class="config-sidebar">
        <div class="sidebar-card">
          <h4 class="sidebar-title">{{ locale === 'zh' ? '执行摘要' : 'Execution Summary' }}</h4>
          <div class="summary-list">
            <div class="summary-item">
              <span class="summary-label">{{ locale === 'zh' ? '目标主机' : 'Target Nodes' }}</span>
              <span class="summary-value font-mono">{{ formData.hostIds.length || 0 }}</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">{{ locale === 'zh' ? '脚本' : 'Script' }}</span>
              <span class="summary-value font-mono">{{ scripts.find(s => s.id === formData.scriptId)?.name || '-' }}</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">{{ locale === 'zh' ? '超时时间' : 'Timeout' }}</span>
              <span class="summary-value font-mono">{{ formData.timeout }}s</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">{{ locale === 'zh' ? '并发数' : 'Concurrency' }}</span>
              <span class="summary-value font-mono">{{ formData.concurrency }}</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">{{ locale === 'zh' ? '预计时长' : 'Est. Duration' }}</span>
              <span class="summary-value font-mono">~{{ estimatedDuration }}s</span>
            </div>
          </div>
          <div class="risk-alert" v-if="formData.hostIds.length > 0">
            <span class="material-symbols-outlined">warning_amber</span>
            <span>{{ locale === 'zh' ? `此操作将影响 ${formData.hostIds.length} 台生产节点。` : `This operation will affect ${formData.hostIds.length} production nodes.` }}</span>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createTask, executeTask } from '../../api/task'
import { getScriptList } from '../../api/script'
import { getHostList } from '../../api/host'

const { t, locale } = useI18n()
const router = useRouter()

const targetMode = ref('group')
const selectedHosts = ref([])
const selectedHostIds = ref([])
const scripts = ref([])
const groups = ref([])

const formData = reactive({
  name: '',
  scriptId: null,
  hostIds: [],
  groupId: null,
  timeout: 300,
  maxRetries: 3,
  concurrency: 10,
  failStrategy: 'continue',
  execMode: 'parallel',
  params: {}
})

const loading = ref(false)
const scriptContent = ref('')

async function fetchScripts() {
  try {
    const res = await getScriptList({ pageNum: 1, pageSize: 100, status: 'active' })
    scripts.value = res.data.records || []
  } catch (error) {
    console.error('Failed to fetch scripts:', error)
  }
}

async function fetchHosts() {
  try {
    const res = await getHostList({ pageNum: 1, pageSize: 1000 })
    const hosts = res.data.records || []
    selectedHosts.value = hosts.slice(0, 12).map(h => h.ip)
    selectedHostIds.value = hosts.slice(0, 12).map(h => h.id)
    formData.hostIds = selectedHostIds.value
  } catch (error) {
    console.error('Failed to fetch hosts:', error)
  }
}

function onScriptChange(scriptId) {
  const script = scripts.value.find(s => s.id === scriptId)
  if (script) {
    scriptContent.value = script.content || '# Script content will be loaded...'
  }
}

function removeHost(ip) {
  const idx = selectedHosts.value.indexOf(ip)
  if (idx > -1) {
    selectedHosts.value.splice(idx, 1)
    selectedHostIds.value.splice(idx, 1)
    formData.hostIds = selectedHostIds.value
  }
}

async function handleSaveDraft() {
  if (!formData.name) {
    formData.name = `Task-${Date.now()}`
  }
  
  try {
    const res = await createTask({
      ...formData,
      status: 'draft'
    })
    ElMessage.success(locale.value === 'zh' ? '草稿保存成功' : 'Draft saved successfully')
  } catch (error) {
    console.error('Failed to save draft:', error)
  }
}

async function handleStartExec() {
  if (!formData.scriptId) {
    ElMessage.warning(locale.value === 'zh' ? '请选择脚本' : 'Please select a script')
    return
  }
  
  if (formData.hostIds.length === 0) {
    ElMessage.warning(locale.value === 'zh' ? '请选择目标主机' : 'Please select target hosts')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      locale.value === 'zh' 
        ? `即将在 ${formData.hostIds.length} 台主机上执行任务，是否继续？` 
        : `About to execute task on ${formData.hostIds.length} hosts. Continue?`,
      locale.value === 'zh' ? '确认执行' : 'Confirm Execution',
      { type: 'warning' }
    )
    
    loading.value = true
    let taskId = formData.id
    
    if (!taskId) {
      const createRes = await createTask(formData)
      taskId = createRes.data?.id
    }
    
    if (taskId) {
      await executeTask(taskId)
      ElMessage.success(locale.value === 'zh' ? '任务已开始执行' : 'Task execution started')
      router.push(`/tasks/log/${taskId}`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to start execution:', error)
    }
  } finally {
    loading.value = false
  }
}

const estimatedDuration = computed(() => {
  const hostCount = formData.hostIds.length || 1
  const batchCount = Math.ceil(hostCount / formData.concurrency)
  return Math.ceil(batchCount * (formData.timeout / 60))
})

onMounted(() => {
  fetchScripts()
  fetchHosts()
})
</script>

<style scoped>
.task-config-page {
  padding: 32px;
  max-width: 1600px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 28px; font-weight: 900; color: #fff;
  text-transform: uppercase; letter-spacing: -0.02em;
}
.page-subtitle { font-size: 12px; color: var(--on-surface-variant); margin-top: 6px; }

.header-actions { display: flex; gap: 10px; }

.action-btn {
  padding: 10px 20px; border-radius: 8px; border: none;
  font-family: var(--font-label);
  font-size: 11px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.08em;
  cursor: pointer; display: flex; align-items: center; gap: 6px;
}
.action-btn.secondary {
  background: var(--bg-surface-high);
  color: #fff; border: 1px solid rgba(66, 70, 86, 0.1);
}
.action-btn.secondary:hover { background: var(--bg-bright); }
.action-btn.primary {
  background: linear-gradient(135deg, #0F62FE 0%, #4589FF 100%);
  color: #fff;
  box-shadow: 0 4px 15px rgba(15, 98, 254, 0.25);
}

.config-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
  margin-top: 28px;
}

.config-section {
  background: var(--bg-surface-container-low);
  border-radius: 16px;
  padding: 28px;
  margin-bottom: 20px;
  border: 1px solid rgba(66, 70, 86, 0.06);
}

.step-header { margin-bottom: 20px; }

.step-num {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px; height: 26px;
  border-radius: 50%;
  background: var(--primary-container);
  color: white;
  font-size: 13px;
  font-weight: 800;
  margin-right: 10px;
}

.step-header h3 {
  display: inline;
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.step-desc {
  font-size: 11px;
  color: var(--on-surface-variant);
  margin-top: 6px;
  margin-left: 36px;
}

.tab-switcher {
  display: flex;
  gap: 4px;
  margin-bottom: 16px;
  background: var(--bg-base);
  padding: 4px;
  border-radius: 10px;
  width: fit-content;
}

.switch-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 7px;
  background: transparent;
  color: var(--on-surface-variant);
  font-family: var(--font-label);
  font-size: 11px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}
.switch-btn.active {
  background: var(--primary-container);
  color: white;
}

.group-selector { display: flex; flex-direction: column; gap: 12px; }

.field-label {
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.field-select {
  background: var(--bg-base);
  border: 1px solid var(--outline-variant);
  border-radius: 8px;
  padding: 10px 14px;
  color: #fff;
  font-size: 13px;
  outline: none;
  cursor: pointer;
}
.field-select option { background: var(--bg-surface-container); }
.field-select.wide { width: 100%; max-width: 480px; }

.selected-hosts {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.host-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 5px 10px;
  background: var(--bg-surface-high);
  border-radius: 6px;
  font-size: 11px;
  color: #fff;
}
.chip-remove {
  cursor: pointer;
  opacity: 0.5;
  font-weight: 700;
  transition: opacity 0.15s;
}
.chip-remove:hover { opacity: 1; }

.manual-textarea {
  width: 100%;
  min-height: 120px;
  background: var(--bg-base);
  border: 1px solid var(--outline-variant);
  border-radius: 10px;
  padding: 16px;
  color: #fff;
  font-size: 12.5px;
  resize: vertical;
  outline: none;
}

.script-select-row { margin-bottom: 16px; }

.script-preview {
  border-radius: 10px;
  padding: 16px;
  overflow-x: auto;
}

.preview-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 10px;
}
.preview-header .material-symbols-outlined { font-size: 14px; }

.preview-code {
  margin: 0;
  font-size: 11.5px;
  line-height: 1.65;
  color: var(--on-surface-variant);
  white-space: pre-wrap;
}

.params-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.param-field { display: flex; flex-direction: column; gap: 6px; }
.param-field label {
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}
.param-field input,
.param-field select {
  background: var(--bg-base);
  border: 1px solid var(--outline-variant);
  border-radius: 8px;
  padding: 9px 12px;
  color: #fff;
  font-size: 13px;
  outline: none;
}
.param-field select option { background: var(--bg-surface-container); }

.config-sidebar { position: relative; }

.sidebar-card {
  position: sticky;
  top: 106px;
  background: var(--bg-surface-container);
  border-radius: 16px;
  padding: 24px;
  border: 1px solid rgba(66, 70, 86, 0.08);
}

.sidebar-title {
  font-size: 12px;
  font-weight: 800;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin-bottom: 20px;
}

.summary-list { display: flex; flex-direction: column; gap: 14px; margin-bottom: 20px; }

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.summary-label { font-size: 11px; color: var(--on-surface-variant); }
.summary-value { font-size: 12px; font-weight: 600; color: #fff; }

.risk-alert {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px;
  background: rgba(255, 184, 0, 0.06);
  border: 1px solid rgba(255, 184, 0, 0.15);
  border-radius: 8px;
  font-size: 11px;
  color: #FFB800;
  line-height: 1.5;
}
.risk-alert .material-symbols-outlined { font-size: 16px; flex-shrink: 0; }
</style>
