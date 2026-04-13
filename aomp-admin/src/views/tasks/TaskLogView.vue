<template>
  <div class="task-log-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">{{ t('tasks.realTimeLog') }}</h1>
        <p class="page-subtitle font-mono">Task ID: EXEC-2024-001 | Started at 2024-05-24 14:32:01 UTC+8</p>
      </div>
      <div class="header-actions">
        <button class="action-btn warning">
          <span class="material-symbols-outlined">pause_circle</span>
          {{ t('tasks.pause') }}
        </button>
        <button class="action-btn danger">
          <span class="material-symbols-outlined">stop_circle</span>
          {{ t('tasks.stop') }}
        </button>
        <button class="action-btn primary litho-gradient">
          <span class="material-symbols-outlined">download</span>
          {{ t('tasks.exportLog') }}
        </button>
      </div>
    </div>

    <div class="progress-bar-wrap">
      <div class="progress-info">
        <span class="progress-label">Progress</span>
        <span class="progress-value font-mono">7/12 nodes completed</span>
      </div>
      <div class="progress-track">
        <div class="progress-fill success" style="width: 58%"></div>
        <div class="progress-fill running" style="width: 17%; left: 58%"></div>
      </div>
      <div class="progress-stats">
        <span><span class="stat-dot green"></span> Success: 7</span>
        <span><span class="stat-dot blue"></span> Running: 2</span>
        <span><span class="stat-dot gray"></span> Pending: 3</span>
      </div>
    </div>

    <div class="log-layout">
      <aside class="node-list-panel">
        <h4 class="panel-title">Target Nodes</h4>
        <div class="node-list">
          <div v-for="(node, idx) in nodeList" :key="idx"
               :class="['node-item', `status-${node.status}`]"
               @click="activeNode = idx">
            <span class="node-status-dot"></span>
            <span class="node-ip font-mono">{{ node.ip }}</span>
            <span class="node-time font-mono">{{ node.time }}</span>
          </div>
        </div>
      </aside>

      <main class="log-viewer">
        <div class="log-toolbar">
          <div class="toolbar-left">
            <span class="log-node-label font-mono">{{ activeNodeData.ip }} — {{ activeNodeData.statusLabel }}</span>
          </div>
          <div class="toolbar-right">
            <button class="tool-btn"><span class="material-symbols-outlined">search</span></button>
            <button class="tool-btn"><span class="material-symbols-outlined">content_copy</span></button>
            <button class="tool-btn"><span class="material-symbols-outlined">expand_more</span></button>
          </div>
        </div>

        <div class="log-content font-mono" ref="logContentRef">
          <div v-for="(line, idx) in logLines" :key="idx" :class="['log-line', line.type]">
            <span class="line-timestamp">{{ line.time }}</span>
            <span class="line-level">[{{ line.level }}]</span>
            <span class="line-message">{{ line.msg }}</span>
          </div>
          <div class="log-line prompt"><span class="cursor-blink">▌</span></div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const activeNode = ref(0)
const logContentRef = ref(null)

const nodeList = [
  { ip: '192.168.1.100', status: 'success', time: '14:32:05', statusLabel: 'SUCCESS' },
  { ip: '192.168.1.101', status: 'success', time: '14:32:07', statusLabel: 'SUCCESS' },
  { ip: '192.168.1.102', status: 'success', time: '14:32:09', statusLabel: 'SUCCESS' },
  { ip: '192.168.1.103', status: 'success', time: '14:32:11', statusLabel: 'SUCCESS' },
  { ip: '192.168.1.104', status: 'success', time: '14:32:14', statusLabel: 'SUCCESS' },
  { ip: '192.168.1.105', status: 'success', time: '14:32:16', statusLabel: 'SUCCESS' },
  { ip: '192.168.1.106', status: 'success', time: '14:32:19', statusLabel: 'SUCCESS' },
  { ip: '192.168.1.107', status: 'running', time: 'Running...', statusLabel: 'RUNNING' },
  { ip: '192.168.1.108', status: 'running', time: 'Running...', statusLabel: 'RUNNING' },
  { ip: '192.168.1.109', status: 'pending', time: '--:--:--', statusLabel: 'PENDING' },
  { ip: '192.168.1.110', status: 'pending', time: '--:--:--', statusLabel: 'PENDING' },
  { ip: '192.168.1.111', status: 'pending', time: '--:--:--', statusLabel: 'PENDING' }
]

const activeNodeData = computed(() => nodeList[activeNode.value])

const logLines = [
  { time: '14:32:01', level: 'INFO', msg: 'Connecting to target node...', type: 'info' },
  { time: '14:32:01', level: 'INFO', msg: 'SSH connection established (ed25519)', type: 'info' },
  { time: '14:32:02', level: 'INFO', msg: 'Loading script: deploy_k8s_node.sh v2.4.1', type: 'info' },
  { time: '14:32:03', level: 'INFO', msg: 'Validating environment variables...', type: 'info' },
  { time: '14:32:03', level: 'OK', msg: 'NODE_GROUP=K8S-PROD-01 ✓', type: 'success' },
  { time: '14:32:04', level: 'INFO', msg: 'Executing preflight checks...', type: 'info' },
  { time: '14:32:04', level: 'OK', msg: 'kubectl version: v1.29.0 ✓', type: 'success' },
  { time: '14:32:05', level: 'OK', msg: 'kubeconfig valid ✓', type: 'success' },
  { time: '14:32:05', level: 'INFO', msg: 'Applying manifest: node-config.yaml', type: 'info' },
  { time: '14:32:08', level: 'OK', msg: 'deployment.apps/k8s-worker created', type: 'success' },
  { time: '14:32:09', level: 'OK', msg: 'service/k8s-worker-svc created', type: 'success' },
  { time: '14:32:10', level: 'OK', msg: 'configmap/app-config created', type: 'success' },
  { time: '14:32:11', level: 'INFO', msg: 'Waiting for rollout to complete...', type: 'info' },
  { time: '14:32:15', level: 'OK', msg: 'Rollout completed successfully.', type: 'success' },
  { time: '14:32:15', level: 'INFO', msg: 'Post-deployment health check passed.', type: 'info' },
  { time: '14:32:16', level: 'DONE', msg: 'Execution completed on 192.168.1.100 — Exit code: 0', type: 'success' }
]
</script>

<style scoped>
.task-log-page {
  padding: 32px;
  height: calc(100vh - 94px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.page-title {
  font-size: 24px; font-weight: 900; color: #fff;
  text-transform: uppercase; letter-spacing: -0.02em;
}
.page-subtitle { font-size: 11px; color: var(--on-surface-variant); margin-top: 4px; }

.header-actions { display: flex; gap: 8px; }

.progress-bar-wrap {
  background: var(--bg-surface-container-low);
  border-radius: 12px;
  padding: 16px 20px;
  margin-top: 20px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}
.progress-label { font-size: 10px; font-weight: 700; color: var(--on-surface-variant); text-transform: uppercase; }
.progress-value { font-size: 11px; color: #fff; font-weight: 600; }

.progress-track {
  height: 6px;
  background: var(--bg-base);
  border-radius: 3px;
  position: relative;
  overflow: hidden;
}
.progress-fill {
  position: absolute;
  top: 0; height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
}
.progress-fill.success { background: var(--primary-container); }
.progress-fill.running {
  background: linear-gradient(90deg, transparent, var(--primary-container));
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% { opacity: 0.4; }
  50% { opacity: 1; }
  100% { opacity: 0.4; }
}

.progress-stats {
  display: flex;
  gap: 20px;
  margin-top: 8px;
  font-size: 10px;
  color: var(--on-surface-variant);
}
.stat-dot {
  display: inline-block;
  width: 6px; height: 6px;
  border-radius: 50%;
  margin-right: 4px;
}
.stat-dot.green { background: var(--primary-container); }
.stat-dot.blue { background: #FFB800; }
.stat-dot.gray { background: var(--outline); }

.log-layout {
  display: flex;
  flex: 1;
  gap: 0;
  margin-top: 20px;
  min-height: 0;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(66, 70, 86, 0.06);
}

.node-list-panel {
  width: 240px;
  background: var(--bg-surface-container-low);
  border-right: 1px solid rgba(66, 70, 86, 0.08);
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.panel-title {
  font-size: 10px;
  font-weight: 800;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.12em;
  padding: 16px 16px 12px;
}

.node-list { display: flex; flex-direction: column; }

.node-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  cursor: pointer;
  transition: all 0.15s;
  border-left: 3px solid transparent;
}
.node-item:hover { background: var(--bg-surface-high); }
.node-item.status-success { border-left-color: var(--primary-container); }
.node-item.status-running { border-left-color: #FFB800; background: rgba(255, 184, 0, 0.03); }
.node-item.status-pending { border-left-color: var(--outline); }

.node-status-dot {
  width: 7px; height: 7px;
  border-radius: 50%;
  flex-shrink: 0;
}
.status-success .node-status-dot { background: var(--primary-container); }
.status-running .node-status-dot { background: #FFB800; animation: pulse-glow 1.5s infinite; }
.status-pending .node-status-dot { background: var(--outline); }

.node-ip { font-size: 11px; color: #fff; font-weight: 500; flex: 1; }
.node-time { font-size: 9px; color: var(--on-surface-variant); }

.log-viewer {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #0a0a0a;
  min-width: 0;
}

.log-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  background: var(--bg-surface-container);
  border-bottom: 1px solid rgba(66, 70, 86, 0.08);
}

.log-node-label { font-size: 11px; color: var(--primary-container); font-weight: 600; }

.toolbar-right { display: flex; gap: 4px; }

.tool-btn {
  width: 30px; height: 30px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 6px;
  background: transparent;
  border: none;
  cursor: pointer;
  color: var(--on-surface-variant);
  transition: all 0.15s;
}
.tool-btn:hover { background: var(--bg-surface-high); color: #fff; }
.tool-btn .material-symbols-outlined { font-size: 16px; }

.log-content {
  flex: 1;
  padding: 16px 20px;
  overflow-y: auto;
  font-size: 11.5px;
  line-height: 1.75;
}

.log-line { display: flex; gap: 6px; padding: 1px 0; }

.line-timestamp {
  color: var(--outline);
  opacity: 0.5;
  user-select: none;
  min-width: 72px;
}
.line-level {
  font-weight: 700;
  min-width: 52px;
  user-select: none;
}
.line-msg { color: var(--on-surface); word-break: break-all; }

.log-line.info .line-level { color: var(--on-surface-variant); }
.log-line.success .line-level { color: var(--primary-container); }
.log-line.warn .line-level { color: #FFB800; }
.log-line.error .line-level { color: var(--error); }
.log-line.prompt .line-msg { color: var(--primary); }

.cursor-blink {
  animation: blink 1s step-end infinite;
}
@keyframes blink {
  50% { opacity: 0; }
}
</style>
