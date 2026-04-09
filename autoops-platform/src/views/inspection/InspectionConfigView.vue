<template>
  <div class="inspection-config-page">
    <div class="page-header">
      <!-- <div>
        <h1 class="page-title">{{ t('inspection.configTitle') }}</h1>
        <p class="page-subtitle">{{ t('inspection.configSubtitle') }}</p>
      </div> -->
      <div class="header-actions">
        <button class="action-btn secondary">
          <span class="material-symbols-outlined">save</span>Save Draft
        </button>
        <button class="action-btn primary litho-gradient" @click="$router.push('/inspection/report/RPT-2024-001')">
          <span class="material-symbols-outlined">play_arrow</span>Run Inspection
        </button>
      </div>
    </div>

    <div class="config-layout">
      <main class="config-main">
        <!-- Host Selection -->
        <section class="config-section">
          <div class="step-header">
            <span class="step-num">1</span>
            <h3>{{ t('inspection.hostSelection') }}</h3>
          </div>

          <div class="fuzzy-search-box">
            <span class="material-symbols-outlined search-icon">search</span>
            <input type="text" placeholder="Search nodes by IP, hostname or tag..." />
            <span class="fuzzy-badge font-mono">{{ t('inspection.fuzzySearch') }}</span>
          </div>

          <div class="tag-cloud">
            <span v-for="(tag, idx) in tags" :key="idx"
                  :class="['tag-chip', tag.selected ? 'selected' : '']"
                  @click="tag.selected = !tag.selected">
              {{ tag.name }}
            </span>
          </div>

          <div class="host-table-wrap">
            <table class="host-table">
              <thead>
                <tr>
                  <th><input type="checkbox" /></th>
                  <th>Hostname</th>
                  <th>IP Address</th>
                  <th>Status</th>
                  <th>Last Inspected</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(h, idx) in hostList" :key="idx">
                  <td><input type="checkbox" v-model="h.checked" /></td>
                  <td class="font-mono">{{ h.name }}</td>
                  <td class="font-mono">{{ h.ip }}</td>
                  <td><span :class="['dot', h.statusClass]"></span> {{ h.statusLabel }}</td>
                  <td class="font-mono text-muted">{{ h.lastCheck }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <!-- Execution Settings -->
        <section class="config-section">
          <div class="step-header">
            <span class="step-num">2</span>
            <h3>{{ t('inspection.execSettings') }}</h3>
          </div>

          <div class="settings-grid">
            <div class="setting-item">
              <label>Inspection Type</label>
              <select>
                <option>Full System Health Check</option>
                <option>Security Vulnerability Scan</option>
                <option>Performance Benchmark</option>
              </select>
            </div>
            <div class="setting-item">
              <label>Schedule</label>
              <select>
                <option>Run Now (Immediate)</option>
                <option>Daily at 02:00 UTC</option>
                <option>Weekly on Monday</option>
              </select>
            </div>
            <div class="setting-item">
              <label>Concurrency Level</label>
              <input type="number" value="20" />
            </div>
            <div class="setting-item">
              <label>Timeout per Node (s)</label>
              <input type="number" value="120" />
            </div>
            <div class="setting-item full">
              <label>Notification Channel</label>
              <div class="notif-options">
                <label class="notif-check"><input type="checkbox" checked /> Email Alert</label>
                <label class="notif-check"><input type="checkbox" checked /> Webhook Callback</label>
                <label class="notif-check"><input type="checkbox" /> Slack Notification</label>
              </div>
            </div>
          </div>
        </section>
      </main>

      <aside class="config-sidebar">
        <div class="sidebar-card">
          <h4 class="sidebar-title">Inspection Scope</h4>
          <div class="scope-list">
            <div class="scope-item">
              <span class="scope-label">Selected Nodes</span>
              <span class="scope-val font-mono">24</span>
            </div>
            <div class="scope-item">
              <span class="scope-label">Inspection Type</span>
              <span class="scope-val">Full Health</span>
            </div>
            <div class="scope-item">
              <span class="scope-label">Est. Duration</span>
              <span class="scope-val font-mono">~3m 40s</span>
            </div>
            <div class="scope-item">
              <span class="scope-label">Report Format</span>
              <span class="scope-val">PDF + JSON</span>
            </div>
          </div>
          <div class="checklist-preview">
            <h5>Checks Included:</h5>
            <ul>
              <li>CPU / Memory Utilization</li>
              <li>Disk I/O & Space Analysis</li>
              <li>Network Latency & Packet Loss</li>
              <li>Service Health Status</li>
              <li>Security Patch Status</li>
              <li>Certificate Expiry</li>
            </ul>
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

const tags = ref([
  { name: 'K8S-PROD-01', selected: true },
  { name: 'EDGE-GW-02', selected: false },
  { name: 'DB-CLUSTER-A', selected: true },
  { name: 'MONITORING', selected: false },
  { name: 'STAGING', selected: false },
  { name: 'ALL-NODES', selected: false }
])

const hostList = ref([
  { name: 'k8s-master-01', ip: '192.168.1.100', statusClass: 'dot-green', statusLabel: 'Running', lastCheck: '2024-05-23 02:00', checked: true },
  { name: 'k8s-worker-01', ip: '192.168.1.101', statusClass: 'dot-green', statusLabel: 'Running', lastCheck: '2024-05-23 02:00', checked: true },
  { name: 'k8s-worker-02', ip: '192.168.1.102', statusClass: 'dot-yellow', statusLabel: 'Warning', lastCheck: '2024-05-23 01:58', checked: false },
  { name: 'db-primary-01', ip: '192.168.1.200', statusClass: 'dot-green', statusLabel: 'Running', lastCheck: '2024-05-23 02:00', checked: true },
  { name: 'edge-gw-01', ip: '10.0.0.1', statusClass: 'dot-red', statusLabel: 'Offline', lastCheck: '2024-05-22 14:30', checked: false }
])
</script>

<style scoped>
.inspection-config-page {
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
  font-size: 26px; font-weight: 900; color: #fff;
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
  background: var(--bg-surface-high); color: #fff;
  border: 1px solid rgba(66, 70, 86, 0.1);
}
.action-btn.secondary:hover { background: var(--bg-bright); }
.action-btn.primary { background: linear-gradient(135deg, #0F62FE 0%, #4589FF 100%); color: #fff; box-shadow: 0 4px 15px rgba(15, 98, 254, 0.25); }

.config-layout {
  display: grid;
  grid-template-columns: 1fr 300px;
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
  display: inline-flex; align-items: center; justify-content: center;
  width: 26px; height: 26px; border-radius: 50%;
  background: var(--primary-container); color: white;
  font-size: 13px; font-weight: 800; margin-right: 10px;
}
.step-header h3 {
  display: inline; font-size: 15px; font-weight: 700; color: #fff;
  text-transform: uppercase; letter-spacing: 0.04em;
}

.fuzzy-search-box {
  display: flex;
  align-items: center;
  background: var(--bg-base);
  border: 1px solid var(--outline-variant);
  border-radius: 10px;
  padding: 10px 16px;
  margin-bottom: 16px;
}
.fuzzy-search-box .search-icon { font-size: 18px; color: var(--outline); margin-right: 10px; }
.fuzzy-search-box input {
  flex: 1; background: transparent; border: none; outline: none;
  color: #fff; font-size: 13px;
}
.fuzzy-badge {
  font-size: 9px;
  color: var(--outline);
  padding: 3px 8px;
  background: var(--bg-surface-high);
  border-radius: 4px;
  letter-spacing: 0.08em;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.tag-chip {
  padding: 6px 14px;
  border-radius: 9999px;
  font-size: 11px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s;
  background: var(--bg-surface-high);
  color: var(--on-surface-variant);
  border: 1px solid transparent;
}
.tag-chip:hover { background: var(--bg-bright); }
.tag-chip.selected {
  background: rgba(15, 98, 254, 0.1);
  color: var(--primary-container);
  border-color: rgba(15, 98, 254, 0.2);
}

.host-table-wrap { overflow-x: auto; }

.host-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}
.host-table th {
  text-align: left;
  padding: 10px 14px;
  font-family: var(--font-label);
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  background: var(--bg-surface-highest);
  border-bottom: 1px solid rgba(66, 70, 86, 0.08);
}
.host-table td {
  padding: 12px 14px;
  border-bottom: 1px solid rgba(66, 70, 86, 0.04);
  color: var(--on-surface);
}

.dot {
  display: inline-block;
  width: 7px; height: 7px;
  border-radius: 50%;
  margin-right: 6px;
}
.dot-green { background: var(--primary-container); }
.dot-yellow { background: #FFB800; }
.dot-red { background: var(--error); }
.text-muted { color: var(--on-surface-variant); }

.settings-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.setting-item { display: flex; flex-direction: column; gap: 6px; }
.setting-item.full { grid-column: span 2; }

.setting-item label {
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}
.setting-item select,
.setting-item input[type="number"] {
  background: var(--bg-base);
  border: 1px solid var(--outline-variant);
  border-radius: 8px;
  padding: 9px 12px;
  color: #fff;
  font-size: 13px;
  outline: none;
}
.setting-item select option { background: var(--bg-surface-container); }

.notif-options { display: flex; gap: 20px; flex-wrap: wrap; }

.notif-check {
  display: flex; align-items: center; gap: 6px;
  font-size: 12px; color: var(--on-surface); cursor: pointer;
}
.notif-check input[type="checkbox"] { accent-color: var(--primary-container); }

.config-sidebar { position: relative; }

.sidebar-card {
  position: sticky; top: 106px;
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

.scope-list { display: flex; flex-direction: column; gap: 14px; margin-bottom: 20px; }

.scope-item {
  display: flex; justify-content: space-between; align-items: center;
}
.scope-label { font-size: 11px; color: var(--on-surface-variant); }
.scope-val { font-size: 12px; font-weight: 600; color: #fff; }

.checklist-preview {
  padding-top: 16px;
  border-top: 1px solid rgba(66, 70, 86, 0.08);
}
.checklist-preview h5 {
  font-size: 10px;
  font-weight: 800;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 10px;
}
.checklist-preview ul {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.checklist-preview li {
  font-size: 11px;
  color: var(--on-surface);
  padding-left: 14px;
  position: relative;
}
.checklist-preview li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: var(--primary-container);
  font-size: 10px;
  font-weight: 700;
}
</style>
