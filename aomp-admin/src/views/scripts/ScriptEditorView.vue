<template>
  <div class="editor-page">
    <div class="editor-header">
      <div class="header-left">
        <button class="back-btn" @click="$router.push('/scripts')">
          <span class="material-symbols-outlined">arrow_back</span>
        </button>
        <input v-model="fileName" class="file-name-input" placeholder="untitled.sh" />
        <span class="save-status font-mono">{{ saveStatus }}</span>
      </div>
      <div class="header-right">
        <button class="editor-btn secondary" @click="$router.push('/scripts')">
          {{ t('scripts.cancel') }}
        </button>
        <button class="editor-btn primary" @click="handleSave">
          <span class="material-symbols-outlined">save</span>
          {{ t('scripts.save') }}
        </button>
        <button class="action-btn primary litho-gradient" @click="handleDeploy">
          <span class="material-symbols-outlined">rocket_launch</span>
          {{ t('scripts.deployRun') }}
        </button>
      </div>
    </div>

    <div class="editor-body">
      <aside class="sidebar-panel">
        <div class="panel-section">
          <h4 class="panel-title">Script Info</h4>
          <div class="info-row">
            <span class="info-label">Type</span>
            <select class="info-value select-sm">
              <option>Bash / Shell</option>
              <option>Python</option>
              <option>PowerShell</option>
            </select>
          </div>
          <div class="info-row">
            <span class="info-label">Version</span>
            <span class="info-value font-mono">v1.0.0</span>
          </div>
          <div class="info-row">
            <span class="info-label">Permission</span>
            <select class="info-value select-sm">
              <option>PUBLIC</option>
              <option>PRIVATE</option>
            </select>
          </div>
          <div class="info-row full">
            <span class="info-label">Description</span>
            <textarea rows="3" class="info-textarea" placeholder="Enter description..."></textarea>
          </div>
        </div>

        <div class="panel-section">
          <h4 class="panel-title">Variables</h4>
          <div class="var-list">
            <div v-for="(v, idx) in variables" :key="idx" class="var-item">
              <span class="var-key font-mono">\${{ v.key }}</span>
              <input class="var-val" :value="v.value" />
            </div>
            <button class="add-var-btn"><span class="material-symbols-outlined">add</span>Add Variable</button>
          </div>
        </div>
      </aside>

      <main class="code-editor-main">
        <div class="editor-toolbar">
          <div class="tool-tabs">
            <span class="tool-tab active">main.sh</span>
            <span class="tool-tab">config.yaml</span>
            <span class="tool-tab add-tab">+</span>
          </div>
          <div class="tool-actions">
            <span class="tool-icon"><span class="material-symbols-outlined">format_align_left</span></span>
            <span class="tool-icon"><span class="material-symbols-outlined">search</span></span>
            <span class="tool-icon"><span class="material-symbols-outlined">more_horiz</span></span>
          </div>
        </div>

        <div class="code-area">
          <div class="line-numbers">
            <span v-for="i in lineCount" :key="i" class="line-no">{{ i }}</span>
          </div>
          <pre class="code-pre font-mono"><code v-text="codeContent"></code></pre>
        </div>

        <div class="console-output">
          <div class="console-header">
            <span class="console-dot green"></span>
            <span class="console-title font-mono">Console Output</span>
            <span class="console-clear">Clear</span>
          </div>
          <div class="console-body font-mono">
            <div class="console-line info">$ bash main.sh --dry-run</div>
            <div class="console-line success">[INFO] Initializing script engine v2.4.0...</div>
            <div class="console-line info">[INFO] Loading configuration from config.yaml...</div>
            <div class="console-line success">[OK] Syntax check passed.</div>
            <div class="console-line warn">[WARN] Variable $TARGET_HOST is not set, using default.</div>
            <div class="console-line info">[INFO] Dry-run completed successfully.</div>
            <div class="console-line prompt">$ _</div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const fileName = ref('deploy_k8s_node.sh')
const saveStatus = ref(t('scripts.draftSaved'))
const lineCount = 48

const codeContent = '#!/bin/bash\n' +
'set -euo pipefail\n' +
'\n' +
'# ============================================================\n' +
'# AutoOps - K8S Node Deployment Script\n' +
'# Version: 2.4.1 | Author: ops_team\n' +
'# ============================================================\n' +
'\n' +
'readonly SCRIPT_NAME="$(basename "$0")"\n' +
'readonly LOG_DIR="/var/log/autoops"\n' +
'readonly TIMESTAMP=$(date +%Y%m%d_%H%M%S)\n' +
'\n' +
'# --- Configuration ---\n' +
'NODE_GROUP="${NODE_GROUP:-K8S-PROD-01}"\n' +
'KUBE_CONFIG="${KUBE_CONFIG:-/etc/kubernetes/admin.conf}"\n' +
'DEPLOY_TIMEOUT="${DEPLOY_TIMEOUT:-300}"\n' +
'\n' +
'# --- Logging ---\n' +
'log_info()  { echo "[INFO]  $(date +%Y-%m-%d-%H:%M:%S) $*"; }\n' +
'log_error() { echo "[ERROR] $(date +%Y-%m-%d-%H:%M:%S) $*" >&2; }\n' +
'log_warn()  { echo "[WARN]  $(date +%Y-%m-%d-%H:%M:%S) $*"; }\n' +
'\n' +
'# --- Pre-flight Checks ---\n' +
'preflight_check() {\n' +
'    log_info "Running preflight checks..."\n' +
'    \n' +
'    if [[ ! -f "$KUBE_CONFIG" ]]; then\n' +
'        log_error "Kubeconfig not found at ${KUBE_CONFIG}"\n' +
'        exit 1\n' +
'    fi\n' +
'    \n' +
'    if ! command -v kubectl &>/dev/null; then\n' +
'        log_error "kubectl is not installed"\n' +
'        exit 1\n' +
'    fi\n' +
'    \n' +
'    log_info "Preflight checks passed."\n' +
'}\n' +
'\n' +
'# --- Main Deployment ---\n' +
'deploy_node() {\n' +
'    local node_ip="${1:?Node IP is required}"\n' +
'    \n' +
'    log_info "Deploying to node: ${node_ip} [Group: ${NODE_GROUP}]"\n' +
'    \n' +
'    kubectl --kubeconfig "$KUBE_CONFIG" \\\\\n' +
'        apply -f manifests/node-config.yaml \\\\\n' +
'        --timeout="${DEPLOY_TIMEOUT}s"\n' +
'    \n' +
'    if [[ $? -eq 0 ]]; then\n' +
'        log_info "Deployment successful on ${node_ip}"\n' +
'    else\n' +
'        log_error "Deployment FAILED on ${node_ip}"\n' +
'        exit 1\n' +
'    fi\n' +
'}\n' +
'\n' +
'# --- Entry Point ---\n' +
'main() {\n' +
'    preflight_check\n' +
'    \n' +
'    for target in "${TARGET_HOSTS[@]}"; do\n' +
'        deploy_node "$target"\n' +
'    done\n' +
'    \n' +
'    log_info "All nodes deployed successfully."\n' +
'    exit 0\n' +
'}\n' +
'\n' +
'main "$@"'

const variables = [
  { key: 'NODE_GROUP', value: 'K8S-PROD-01' },
  { key: 'TARGET_HOSTS', value: '192.168.1.100,192.168.1.101' },
  { key: 'DEPLOY_TIMEOUT', value: '300' }
]

function handleSave() {
  saveStatus.value = 'Saved ✓'
}
function handleDeploy() {}
</script>

<style scoped>
.editor-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 94px);
  overflow: hidden;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 32px;
  background: var(--bg-surface-low);
  border-bottom: 1px solid rgba(66, 70, 86, 0.1);
}

.header-left { display: flex; align-items: center; gap: 12px; }

.back-btn {
  width: 36px; height: 36px;
  border-radius: 8px;
  background: var(--bg-surface-high);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--on-surface-variant);
  transition: all 0.2s;
}
.back-btn:hover { background: var(--bg-bright); color: #fff; }
.back-btn .material-symbols-outlined { font-size: 18px; }

.file-name-input {
  background: transparent;
  border: none;
  outline: none;
  color: #fff;
  font-family: var(--font-mono);
  font-size: 16px;
  font-weight: 700;
  min-width: 220px;
}

.save-status {
  font-size: 10px;
  color: var(--on-surface-variant);
  opacity: 0.5;
}

.header-right { display: flex; gap: 8px; }

.editor-btn {
  padding: 8px 18px;
  border-radius: 8px;
  border: none;
  font-family: var(--font-label);
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
}
.editor-btn.secondary {
  background: transparent;
  color: var(--on-surface-variant);
  border: 1px solid rgba(66, 70, 86, 0.1);
}
.editor-btn.secondary:hover { background: var(--bg-surface-high); }
.editor-btn.primary {
  background: var(--bg-surface-high);
  color: #fff;
  border: 1px solid rgba(66, 70, 86, 0.1);
}
.editor-btn.primary:hover { background: var(--bg-bright); }

.editor-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.sidebar-panel {
  width: 280px;
  background: var(--bg-surface-low);
  border-right: 1px solid rgba(66, 70, 86, 0.08);
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.panel-section { display: flex; flex-direction: column; gap: 12px; }

.panel-title {
  font-size: 10px;
  font-weight: 800;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.12em;
}

.info-row { display: flex; flex-direction: column; gap: 4px; }
.info-row.full { grid-column: span 2; }

.info-label {
  font-size: 10px;
  font-weight: 600;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.info-value {
  font-size: 12px;
  color: #fff;
}

.select-sm {
  background: var(--bg-base);
  border: 1px solid var(--outline-variant);
  border-radius: 6px;
  padding: 6px 8px;
  color: #fff;
  font-size: 12px;
  outline: none;
}
.select-sm option { background: var(--bg-surface-container); }

.info-textarea {
  background: var(--bg-base);
  border: 1px solid var(--outline-variant);
  border-radius: 6px;
  padding: 8px;
  color: #fff;
  font-size: 12px;
  resize: vertical;
  outline: none;
  font-family: var(--font-body);
}

.var-list { display: flex; flex-direction: column; gap: 8px; }

.var-item {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--bg-base);
  border-radius: 6px;
  padding: 8px 10px;
}

.var-key {
  font-size: 10px;
  color: var(--primary-container);
  font-weight: 600;
  white-space: nowrap;
  min-width: 100px;
}

.var-val {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: #fff;
  font-size: 11px;
  font-family: var(--font-mono);
}

.add-var-btn {
  padding: 8px;
  background: transparent;
  border: 1px dashed var(--outline-variant);
  border-radius: 6px;
  color: var(--on-surface-variant);
  font-size: 11px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  transition: all 0.15s;
}
.add-var-btn:hover { border-color: var(--primary-container); color: var(--primary-container); }

.code-editor-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  background: var(--bg-surface-container);
  border-bottom: 1px solid rgba(66, 70, 86, 0.08);
}

.tool-tabs { display: flex; gap: 2px; }

.tool-tab {
  padding: 6px 16px;
  font-size: 11px;
  color: var(--on-surface-variant);
  cursor: pointer;
  border-radius: 6px 6px 0 0;
  font-family: var(--font-mono);
  transition: all 0.15s;
}
.tool-tab.active {
  background: var(--bg-surface-low);
  color: #fff;
  font-weight: 600;
}
.tool-tab.add-tab { font-size: 16px; }
.tool-tab:hover:not(.active) { background: var(--bg-surface-high); }

.tool-actions { display: flex; gap: 4px; }

.tool-icon {
  width: 28px; height: 28px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 4px;
  cursor: pointer;
  color: var(--on-surface-variant);
  transition: all 0.15s;
}
.tool-icon:hover { background: var(--bg-surface-high); color: #fff; }
.tool-icon .material-symbols-outlined { font-size: 16px; }

.code-area {
  flex: 1;
  display: flex;
  overflow: auto;
  background: var(--bg-base);
}

.line-numbers {
  padding: 16px 0;
  text-align: right;
  user-select: none;
  min-width: 50px;
  background: var(--bg-base);
  border-right: 1px solid rgba(66, 70, 86, 0.06);
}

.line-no {
  display: block;
  padding: 0 12px;
  font-size: 12px;
  line-height: 1.65;
  color: var(--outline);
  opacity: 0.35;
}

.code-pre {
  flex: 1;
  padding: 16px 20px;
  margin: 0;
  overflow-x: auto;
  font-size: 12.5px;
  line-height: 1.65;
  color: #E5E2E1;
  tab-size: 2;
  white-space: pre;
}

.console-output {
  border-top: 1px solid rgba(66, 70, 86, 0.1);
  max-height: 200px;
  display: flex;
  flex-direction: column;
}

.console-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: var(--bg-surface-container);
  border-bottom: 1px solid rgba(66, 70, 86, 0.06);
}

.console-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
}
.console-dot.green { background: var(--primary-container); }

.console-title {
  font-size: 10px;
  color: var(--on-surface-variant);
  flex: 1;
}

.console-clear {
  font-size: 10px;
  color: var(--outline);
  cursor: pointer;
}

.console-body {
  flex: 1;
  padding: 12px 16px;
  overflow-y: auto;
  background: #0a0a0a;
  font-size: 11.5px;
  line-height: 1.7;
}

.console-line {
  padding: 1px 0;
}
.console-line.info { color: var(--on-surface-variant); }
.console-line.success { color: var(--primary-container); }
.console-line.warn { color: #FFB800; }
.console-line.error { color: var(--error); }
.console-line.prompt { color: var(--primary); }
</style>
