<template>
  <div class="task-config-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">{{ t('tasks.configTitle') }}</h1>
        <p class="page-subtitle">{{ t('tasks.configSubtitle') }}</p>
      </div>
      <div class="header-actions">
        <button class="action-btn secondary">
          <span class="material-symbols-outlined">save</span>
          {{ t('tasks.saveDraft') }}
        </button>
        <button class="action-btn primary litho-gradient" @click="$router.push('/tasks/log/EXEC-2024-001')">
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
            <select class="field-select">
              <option>K8S-PROD-01</option>
              <option>EDGE-GW-02</option>
              <option>DB-CLUSTER-A</option>
            </select>
            <div class="selected-hosts">
              <div v-for="(h, idx) in selectedHosts" :key="idx" class="host-chip">
                <span class="font-mono">{{ h }}</span>
                <span class="chip-remove">×</span>
              </div>
            </div>
          </div>

          <div v-else class="manual-input-area">
            <textarea class="manual-textarea font-mono" placeholder="Enter host IPs or IDs, one per line..."></textarea>
          </div>
        </section>

        <!-- Step 2: Script Selection -->
        <section class="config-section">
          <div class="step-header">
            <span class="step-num">2</span>
            <h3>Select Script / Command</h3>
            <p class="step-desc">Choose the script to execute or enter a raw command</p>
          </div>

          <div class="script-select-row">
            <select class="field-select wide">
              <option>deploy_k8s_node.sh (v2.4.1)</option>
              <option>health_check.py (v1.8.0)</option>
              <option>cert_renewal.sh (v3.0.2)</option>
              <option>Custom Command...</option>
            </select>
          </div>

          <div class="script-preview glass-card">
            <div class="preview-header">
              <span class="material-symbols-outlined">code</span>
              <span>Script Preview</span>
            </div>
            <pre class="preview-code font-mono">#!/bin/bash
set -euo pipefail
readonly NODE_GROUP="${NODE_GROUP:-K8S-PROD-01}"
log_info "Deploying to node group: ${NODE_GROUP}"
kubectl apply -f manifests/node-config.yaml
log_info "Deployment complete."</pre>
          </div>
        </section>

        <!-- Step 3: Parameters -->
        <section class="config-section">
          <div class="step-header">
            <span class="step-num">3</span>
            <h3>Execution Parameters</h3>
            <p class="step-desc">Configure timeout, retry policy, and environment variables</p>
          </div>

          <div class="params-grid">
            <div class="param-field">
              <label>Timeout (seconds)</label>
              <input type="number" value="300" />
            </div>
            <div class="param-field">
              <label>Max Retries</label>
              <input type="number" value="3" />
            </div>
            <div class="param-field">
              <label>Concurrency</label>
              <input type="number" value="10" />
            </div>
            <div class="param-field">
              <label>Fail Strategy</label>
              <select>
                <option>Continue on Failure</option>
                <option>Stop on First Error</option>
              </select>
            </div>
          </div>
        </section>
      </div>

      <aside class="config-sidebar">
        <div class="sidebar-card">
          <h4 class="sidebar-title">Execution Summary</h4>
          <div class="summary-list">
            <div class="summary-item">
              <span class="summary-label">Target Nodes</span>
              <span class="summary-value font-mono">12</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">Script</span>
              <span class="summary-value font-mono">deploy_k8s_node.sh</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">Timeout</span>
              <span class="summary-value font-mono">300s</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">Concurrency</span>
              <span class="summary-value font-mono">10</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">Est. Duration</span>
              <span class="summary-value font-mono">~45s</span>
            </div>
          </div>
          <div class="risk-alert">
            <span class="material-symbols-outlined">warning_amber</span>
            <span>This operation will affect 12 production nodes.</span>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const targetMode = ref('group')
const selectedHosts = ['192.168.1.100', '192.168.1.101', '192.168.1.102']
</script>

<style scoped>
.task-config-page {
  padding: 32px;
  max-width: 1400px;
  margin: 0 auto;
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
  color: var(--on-primary-container);
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
