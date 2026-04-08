<template>
  <div class="assets-page">
    <div class="page-header">
      <!-- <div>
        <h1 class="page-title">{{ t('assets.title') }}</h1>
        <p class="page-subtitle">{{ t('assets.subtitle') }}</p>
      </div> -->
      <div class="header-actions">
        <button class="action-btn-primary" @click="showAddDialog = true">
          <span class="material-symbols-outlined">add</span>
          {{ t('assets.addHost') }}
        </button>
      </div>
    </div>

    <div class="toolbar">
      <div class="search-area">
        <span class="material-symbols-outlined search-icon">search</span>
        <input type="text" :placeholder="t('assets.searchPlaceholder')" />
      </div>
      <div class="filter-group">
        <select class="filter-select">
          <option>{{ t('assets.allStatus') }}</option>
          <option>Running</option>
          <option>High Load</option>
          <option>Offline</option>
        </select>
        <select class="filter-select">
          <option>{{ t('assets.allGroup') }}</option>
          <option>K8S-PROD-01</option>
          <option>EDGE-GW-02</option>
        </select>
        <button class="filter-btn">
          <span class="material-symbols-outlined">tune</span>
        </button>
      </div>
    </div>

    <div class="table-section">
      <table class="data-table">
        <thead>
          <tr>
            <th><input type="checkbox" /></th>
            <th>{{ t('assets.hostId') }}</th>
            <th>{{ t('assets.ipAddress') }}</th>
            <th>{{ t('assets.os') }}</th>
            <th>{{ t('assets.status') }}</th>
            <th>{{ t('assets.owner') }}</th>
            <th>{{ t('assets.actions') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, idx) in hostData" :key="idx">
            <td><input type="checkbox" /></td>
            <td><span class="font-mono id-cell">{{ item.id }}</span></td>
            <td class="font-mono">{{ item.ip }}</td>
            <td>{{ item.os }}</td>
            <td><span :class="['status-dot', item.statusClass]"></span> {{ item.statusLabel }}</td>
            <td>{{ item.owner }}</td>
            <td>
              <div class="action-links">
                <a href="#" @click.prevent="$router.push(`/assets/${item.id}`)">{{ t('assets.terminalConnect') }}</a>
                <a href="#">{{ t('assets.updateConfig') }}</a>
                <a href="#" class="danger">{{ t('assets.restartInstance') }}</a>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination">
      <span class="page-info font-mono">Showing 1-10 of 2,842 hosts</span>
      <div class="page-nav">
        <button class="page-btn" disabled>← Prev</button>
        <button class="page-btn active">1</button>
        <button class="page-btn">2</button>
        <button class="page-btn">3</button>
        <button class="page-btn">...</button>
        <button class="page-btn">285</button>
        <button class="page-btn">Next →</button>
      </div>
    </div>

    <!-- Add Host Dialog -->
    <el-dialog
      v-model="showAddDialog"
      title="新增主机 / Batch Import"
      width="640px"
      :close-on-click-modal="false"
      class="add-host-dialog"
    >
      <el-tabs v-model="activeTab">
        <el-tab-pane label="Single Add" name="single">
          <div class="form-grid">
            <div class="form-field">
              <label>Host ID *</label>
              <input placeholder="e.g. HOST-2024-001" />
            </div>
            <div class="form-field">
              <label>IP Address *</label>
              <input placeholder="192.168.1.100" />
            </div>
            <div class="form-field">
              <label>Operating System</label>
              <input placeholder="CentOS 7.9 x86_64" />
            </div>
            <div class="form-field">
              <label>Business Group</label>
              <select>
                <option>K8S-PROD-01</option>
                <option>EDGE-GW-02</option>
              </select>
            </div>
            <div class="form-field full-width">
              <label>Description</label>
              <textarea rows="3" placeholder="Enter description..."></textarea>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="Batch Import (CSV)" name="batch">
          <div class="upload-zone">
            <span class="material-symbols-outlined upload-icon">cloud_upload</span>
            <p>Drag & drop CSV file here or click to browse</p>
            <p class="hint">Supports .csv format with columns: host_id, ip, os, group</p>
          </div>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <button class="btn-cancel" @click="showAddDialog = false">Cancel</button>
        <button class="btn-submit litho-gradient">Submit</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

const showAddDialog = ref(false)
const activeTab = ref('single')

const hostData = [
  { id: 'HOST-2024-001', ip: '192.168.1.100', os: 'CentOS 7.9 x86_64', statusClass: 'dot-running', statusLabel: 'Running', owner: 'zhang_san' },
  { id: 'HOST-2024-002', ip: '192.168.1.101', os: 'Ubuntu 22.04 LTS', statusClass: 'dot-highload', statusLabel: 'High Load', owner: 'li_si' },
  { id: 'HOST-2024-003', ip: '192.168.1.102', os: 'Debian 11', statusClass: 'dot-offline', statusLabel: 'Offline', owner: 'wang_wu' },
  { id: 'HOST-2024-004', ip: '192.168.1.103', os: 'RHEL 8.6', statusClass: 'dot-running', statusLabel: 'Running', owner: 'zhao_liu' },
  { id: 'HOST-2024-005', ip: '192.168.1.104', os: 'Alinux 3', statusClass: 'dot-running', statusLabel: 'Running', owner: 'sun_qi' }
]
</script>

<style scoped>
.assets-page {
  padding: 32px;
  max-width: 1600px;
  margin: 0 auto;
}

.page-title {
  font-size: 30px;
  font-weight: 900;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: -0.02em;
}
.page-subtitle {
  font-size: 12px;
  color: var(--on-surface-variant);
  margin-top: 4px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.action-btn-primary {
  padding: 10px 20px;
  background: var(--primary-container);
  border-radius: 8px;
  border: none;
  color: white;
  font-family: var(--font-label);
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: opacity 0.2s;
}
.action-btn-primary:hover { opacity: 0.9; }

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  gap: 16px;
}

.search-area {
  display: flex;
  align-items: center;
  background: var(--bg-surface-low);
  border-radius: 9999px;
  padding: 8px 16px;
  border: 1px solid rgba(66, 70, 86, 0.08);
  min-width: 300px;
}
.search-icon { font-size: 16px; color: var(--outline); }
.search-area input {
  background: transparent;
  border: none;
  outline: none;
  color: #fff;
  padding-left: 10px;
  font-size: 13px;
  flex: 1;
}
.search-area input::placeholder { color: var(--outline); }

.filter-group {
  display: flex;
  gap: 8px;
  align-items: center;
}

.filter-select {
  background: var(--bg-surface-low);
  border: 1px solid rgba(66, 70, 86, 0.08);
  border-radius: 8px;
  padding: 8px 14px;
  color: #fff;
  font-size: 12px;
  outline: none;
  cursor: pointer;
}
.filter-select option { background: var(--bg-surface-container); }

.filter-btn {
  padding: 8px 14px;
  background: var(--bg-surface-low);
  border: 1px solid rgba(66, 70, 86, 0.08);
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  color: var(--on-surface-variant);
  transition: background 0.2s;
}
.filter-btn:hover { background: var(--bg-surface-high); }

.table-section {
  margin-top: 20px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(66, 70, 86, 0.06);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}
.data-table th {
  text-align: left;
  padding: 14px 18px;
  font-family: var(--font-label);
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  background: var(--bg-surface-highest);
  border-bottom: 1px solid rgba(66, 70, 86, 0.08);
}
.data-table td {
  padding: 15px 18px;
  border-bottom: 1px solid rgba(66, 70, 86, 0.04);
  color: var(--on-surface);
}

.id-cell { color: var(--primary); font-weight: 600; }

.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 8px;
}
.dot-running { background: var(--primary-container); }
.dot-highload { background: #FFB800; }
.dot-offline { background: var(--error); }

.action-links {
  display: flex;
  gap: 12px;
}
.action-links a {
  font-size: 11px;
  color: var(--primary-container);
  text-decoration: none;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
.action-links a.danger { color: var(--error); }
.action-links a:hover { text-decoration: underline; }

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 12px 0;
}
.page-info { font-size: 11px; color: var(--on-surface-variant); }
.page-nav { display: flex; gap: 4px; }
.page-btn {
  padding: 6px 12px;
  background: transparent;
  border: 1px solid rgba(66, 70, 86, 0.1);
  border-radius: 6px;
  color: var(--on-surface-variant);
  font-size: 11px;
  cursor: pointer;
  transition: all 0.15s;
}
.page-btn:hover:not(:disabled) { background: var(--bg-surface-high); color: #fff; }
.page-btn.active { background: var(--primary-container); color: white; border-color: transparent; }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }

/* Dialog styles */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.form-field { display: flex; flex-direction: column; gap: 6px; }
.form-field.full-width { grid-column: span 2; }
.form-field label {
  font-size: 11px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}
.form-field input,
.form-field select,
.form-field textarea {
  background: var(--bg-base);
  border: 1px solid var(--outline-variant);
  border-radius: 8px;
  padding: 10px 12px;
  color: #fff;
  font-size: 13px;
  outline: none;
  font-family: var(--font-body);
}
.form-field textarea { resize: vertical; }
.form-field input:focus,
.form-field select:focus,
.form-field textarea:focus {
  border-color: var(--primary-container);
}

.upload-zone {
  border: 2px dashed var(--outline-variant);
  border-radius: 12px;
  padding: 48px;
  text-align: center;
  cursor: pointer;
  transition: border-color 0.2s;
}
.upload-zone:hover { border-color: var(--primary-container); }
.upload-icon { font-size: 40px; color: var(--outline); }
.upload-zone p { margin-top: 12px; color: var(--on-surface-variant); font-size: 13px; }
.upload-zone .hint { font-size: 11px; color: var(--outline); margin-top: 8px; }

.btn-cancel {
  padding: 10px 24px;
  background: transparent;
  border: 1px solid var(--outline-variant);
  border-radius: 8px;
  color: var(--on-surface-variant);
  font-weight: 600;
  cursor: pointer;
  font-size: 13px;
}
.btn-submit {
  padding: 10px 28px;
  border: none;
  border-radius: 8px;
  color: var(--on-primary-container);
  font-weight: 700;
  font-size: 13px;
  cursor: pointer;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}
</style>
