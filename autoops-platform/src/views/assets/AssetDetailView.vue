<template>
  <div class="asset-detail-page">
    <div class="detail-header">
      <button class="back-btn" @click="$router.push('/assets')">
        <span class="material-symbols-outlined">arrow_back</span>
      </button>
      <div class="header-info">
        <h1 class="host-id font-mono">{{ hostId }}</h1>
        <span class="running-badge"><span class="dot green"></span> {{ t('assetDetail.running') }}</span>
      </div>
      <div class="header-actions">
        <button class="action-btn"><span class="material-symbols-outlined">edit</span>Edit Config</button>
        <button class="action-btn danger"><span class="material-symbols-outlined">restart_alt</span>Restart Instance</button>
      </div>
    </div>

    <p class="location-text font-mono">{{ t('assetDetail.location') }}</p>

    <div class="spec-cards">
      <div class="spec-card">
        <p class="spec-label">{{ t('assetDetail.cpuArch') }}</p>
        <p class="spec-value font-mono">x86_64 | Intel Xeon Gold 6330T</p>
      </div>
      <div class="spec-card">
        <p class="spec-label">{{ t('assetDetail.memory') }}</p>
        <p class="spec-value font-mono">64 GB DDR4 ECC @3200MHz</p>
      </div>
      <div class="spec-card">
        <p class="spec-label">{{ t('assetDetail.storage') }}</p>
        <p class="spec-value font-mono">1 TB NVMe SSD (RAID-1)</p>
      </div>
      <div class="spec-card">
        <p class="spec-label">{{ t('assetDetail.operatingSystem') }}</p>
        <p class="spec-value font-mono">CentOS Stream 9 (Kernel 5.14)</p>
      </div>
    </div>

    <section class="monitor-section">
      <h3 class="section-title">{{ t('assetDetail.connectionMonitor') }}</h3>
      <div class="monitor-grid">
        <div class="monitor-card">
          <span class="monitor-icon material-symbols-outlined">speed</span>
          <div>
            <p class="monitor-value font-mono">0.002ms</p>
            <p class="monitor-label">{{ t('assetDetail.latency', { v: '0.002' }) }}</p>
          </div>
        </div>
        <div class="monitor-card">
          <span class="monitor-icon material-symbols-outlined">network_check</span>
          <div>
            <p class="monitor-value font-mono">0.00%</p>
            <p class="monitor-label">{{ t('assetDetail.packetLoss', { v: '0.00' }) }}</p>
          </div>
        </div>
        <div class="monitor-card">
          <span class="monitor-icon material-symbols-outlined">memory</span>
          <div>
            <p class="monitor-value font-mono">42.3%</p>
            <p class="monitor-label">Memory Usage</p>
          </div>
        </div>
        <div class="monitor-card">
          <span class="monitor-icon material-symbols-outlined">storage</span>
          <div>
            <p class="monitor-value font-mono">68.1%</p>
            <p class="monitor-label">Disk Usage (/dev/sda1)</p>
          </div>
        </div>
      </div>
    </section>

    <section class="ops-log-section">
      <div class="log-header-row">
        <h3 class="section-title">{{ t('assetDetail.recentOperationsLog') }}</h3>
        <a href="#" class="view-all-link">{{ t('assetDetail.viewFullHistory') }} →</a>
      </div>
      <div class="log-table-wrap">
        <table class="log-table">
          <thead>
            <tr>
              <th>{{ t('assetDetail.operationType') }}</th>
              <th>{{ t('assetDetail.executor') }}</th>
              <th>{{ t('assetDetail.timestamp') }}</th>
              <th>{{ t('assetDetail.status') }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, idx) in opsLog" :key="idx">
              <td><span class="font-mono log-op">{{ row.op }}</span></td>
              <td>{{ row.executor }}</td>
              <td class="font-mono text-muted">{{ row.time }}</td>
              <td><span :class="['status-tag', row.status === 'SUCCESS' ? 'success' : 'warning']">{{ row.status }}</span></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'

const route = useRoute()
const { t } = useI18n()
const hostId = route.params.id || 'HOST-2024-001'

const opsLog = [
  { op: 'CONFIG_UPDATE_V3', executor: 'Admin (zhang_san)', time: '2024-05-24 14:32:01', status: 'SUCCESS' },
  { op: 'HEALTH_CHECK', executor: 'Monitor-Agent-B', time: '2024-05-24 14:30:00', status: 'SUCCESS' },
  { op: 'CERT_RENEWAL', executor: 'System (Auto)', time: '2024-05-24 14:15:22', status: 'SUCCESS' },
  { op: 'DISK_ALERT', executor: 'AlertSystem', time: '2024-05-24 13:58:44', status: 'WARNING' }
]
</script>

<style scoped>
.asset-detail-page {
  padding: 32px;
  max-width: 1400px;
  margin: 0 auto;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 8px;
}

.back-btn {
  width: 40px;
  height: 40px;
  border-radius: 10px;
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
.back-btn .material-symbols-outlined { font-size: 20px; }

.header-info { display: flex; align-items: center; gap: 12px; }

.host-id {
  font-size: 26px;
  font-weight: 900;
  color: #fff;
  letter-spacing: -0.02em;
}

.running-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  font-weight: 700;
  color: var(--primary-container);
  text-transform: uppercase;
  letter-spacing: 0.06em;
  padding: 4px 12px;
  background: rgba(15, 98, 254, 0.1);
  border-radius: 9999px;
}
.running-badge .dot.green {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--primary-container);
  animation: pulse-glow 2s infinite;
}

.header-actions { margin-left: auto; display: flex; gap: 8px; }

.action-btn {
  padding: 8px 16px;
  background: var(--bg-surface-high);
  border: 1px solid rgba(66, 70, 86, 0.1);
  border-radius: 8px;
  color: var(--on-surface);
  font-family: var(--font-label);
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
}
.action-btn:hover { background: var(--bg-bright); }
.action-btn.danger { color: var(--error); border-color: rgba(255,180,171,0.15); }
.action-btn.danger:hover { background: rgba(147, 0, 10, 0.1); }

.location-text {
  font-size: 11px;
  color: var(--on-surface-variant);
  margin-bottom: 24px;
  padding-left: 56px;
}

.spec-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

.spec-card {
  background: var(--bg-surface-container);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid rgba(66, 70, 86, 0.08);
}

.spec-label {
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 8px;
}

.spec-value {
  font-size: 12px;
  color: #fff;
  line-height: 1.5;
}

.section-title {
  font-size: 14px;
  font-weight: 800;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.monitor-section {
  background: var(--bg-surface-container);
  border-radius: 16px;
  padding: 28px;
  margin-bottom: 24px;
  border: 1px solid rgba(66, 70, 86, 0.08);
}

.monitor-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-top: 20px;
}

.monitor-card {
  background: var(--bg-surface-low);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
}

.monitor-icon {
  font-size: 28px;
  color: var(--primary-container);
  opacity: 0.7;
}

.monitor-value {
  font-size: 22px;
  font-weight: 800;
  color: #fff;
}

.monitor-label {
  font-size: 10px;
  color: var(--on-surface-variant);
  margin-top: 2px;
}

.ops-log-section {
  background: var(--bg-surface-container);
  border-radius: 16px;
  padding: 28px;
  border: 1px solid rgba(66, 70, 86, 0.08);
}

.log-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.view-all-link {
  font-size: 11px;
  color: var(--primary-container);
  font-weight: 700;
  text-decoration: none;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.log-table-wrap { overflow-x: auto; }

.log-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}
.log-table th {
  text-align: left;
  padding: 12px 16px;
  font-family: var(--font-label);
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  background: var(--bg-surface-highest);
  border-bottom: 1px solid rgba(66, 70, 86, 0.08);
}
.log-table td {
  padding: 14px 16px;
  border-bottom: 1px solid rgba(66, 70, 86, 0.04);
  color: var(--on-surface);
}

.log-op { color: var(--primary); font-weight: 600; }
.text-muted { color: var(--on-surface-variant); }

.status-tag {
  font-size: 10px;
  font-weight: 700;
  padding: 3px 10px;
  border-radius: 9999px;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}
.status-tag.success {
  background: rgba(15, 98, 254, 0.12);
  color: var(--primary-container);
}
.status-tag.warning {
  background: rgba(255, 184, 0, 0.12);
  color: #FFB800;
}
</style>
