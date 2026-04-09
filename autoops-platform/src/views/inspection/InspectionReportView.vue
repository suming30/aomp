<template>
  <div class="report-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">{{ t('inspection.reportDetailTitle') }}</h1>
        <p class="page-subtitle font-mono">{{ t('inspection.reportNo') }} RPT-2024-001 | {{ t('inspection.genTime') }} 2024-05-24 03:00:12 UTC+8</p>
      </div>
      <div class="header-actions">
        <button class="action-btn secondary">
          <span class="material-symbols-outlined">share</span>
          {{ t('inspection.shareReport') }}
        </button>
        <button class="action-btn primary">
          <span class="material-symbols-outlined">picture_as_pdf</span>
          {{ t('inspection.exportPdf') }}
        </button>
      </div>
    </div>

    <!-- Health Score Card -->
    <div class="health-score-section glass-card">
      <div class="score-display">
        <svg viewBox="0 0 120 120" width="140" height="140">
          <circle cx="60" cy="60" r="50" fill="none" stroke="#2A2A2A" stroke-width="10"/>
          <circle cx="60" cy="60" r="50" fill="none" stroke="#0F62FE" stroke-width="10"
                  stroke-dasharray="283 314" stroke-linecap="round"
                  transform="rotate(-90 60 60)" style="transition: stroke-dasharray 1s;"/>
        </svg>
        <div class="score-center">
          <span class="score-num font-mono">90.1</span>
          <span class="score-label">{{ t('inspection.healthScore') }}</span>
          <span class="score-status good">{{ t('inspection.good') }}</span>
        </div>
      </div>
      <div class="score-info">
        <p class="score-desc">{{ t('inspection.healthImprovement', { v: '5.3' }) }}</p>
        <div class="improvement-tags">
          <span class="imp-tag up">↑ CPU Optimization +8%</span>
          <span class="imp-tag up">↑ Memory Efficiency +4%</span>
          <span class="imp-tag down">↓ Disk I/O Latency -12%</span>
        </div>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="stats-grid">
      <div class="stat-card glass-card">
        <span class="stat-icon material-symbols-outlined">dns</span>
        <p class="stat-value font-mono">148</p>
        <p class="stat-label">{{ t('inspection.totalHosts') }}</p>
      </div>
      <div class="stat-card glass-card">
        <span class="stat-icon material-symbols-outlined green-icon">check_circle</span>
        <p class="stat-value font-mono success-text">142</p>
        <p class="stat-label">{{ t('inspection.activeNodes') }}</p>
      </div>
      <div class="stat-card glass-card">
        <span class="stat-icon material-symbols-outlined yellow-icon">warning</span>
        <p class="stat-value font-mono warn-text">04</p>
        <p class="stat-label">{{ t('inspection.warningHosts') }}</p>
      </div>
      <div class="stat-card glass-card">
        <span class="stat-icon material-symbols-outlined red-icon">error</span>
        <p class="stat-value font-mono error-text">02</p>
        <p class="stat-label">{{ t('inspection.failedHosts') }}</p>
      </div>
    </div>

    <!-- Detailed Report Table -->
    <section class="report-table-section glass-card">
      <h3>Per-Node Inspection Results</h3>
      <table class="report-table">
        <thead>
          <tr>
            <th>Hostname</th>
            <th>IP Address</th>
            <th>CPU Usage</th>
            <th>Memory Usage</th>
            <th>Disk Usage</th>
            <th>Status</th>
            <th>Details</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, idx) in reportData" :key="idx" :class="{ 'row-warn': row.status === 'WARNING', 'row-error': row.status === 'CRITICAL' }">
            <td class="font-mono">{{ row.name }}</td>
            <td class="font-mono text-muted">{{ row.ip }}</td>
            <td class="font-mono">{{ row.cpu }}%</td>
            <td class="font-mono">{{ row.mem }}%</td>
            <td class="font-mono">{{ row.disk }}%</td>
            <td><span :class="['status-tag', row.statusClass]">{{ row.status }}</span></td>
            <td><a href="#" class="detail-link">View →</a></td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

const reportData = [
  { name: 'k8s-master-01', ip: '192.168.1.100', cpu: 42, mem: 58, disk: 68, status: 'HEALTHY', statusClass: 'success' },
  { name: 'k8s-worker-01', ip: '192.168.1.101', cpu: 35, mem: 44, disk: 55, status: 'HEALTHY', statusClass: 'success' },
  { name: 'k8s-worker-02', ip: '192.168.1.102', cpu: 78, mem: 82, disk: 71, status: 'WARNING', statusClass: 'warn' },
  { name: 'k8s-worker-03', ip: '192.168.1.103', cpu: 29, mem: 38, disk: 45, status: 'HEALTHY', statusClass: 'success' },
  { name: 'db-primary-01', ip: '192.168.1.200', cpu: 55, mem: 67, disk: 89, status: 'WARNING', statusClass: 'warn' },
  { name: 'edge-gw-01', ip: '10.0.0.1', cpu: 92, mem: 95, disk: 97, status: 'CRITICAL', statusClass: 'error' }
]
</script>

<style scoped>
.report-page {
  padding: 32px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-title {
  font-size: 26px; font-weight: 900; color: #fff;
  text-transform: uppercase; letter-spacing: -0.02em;
}
.page-subtitle { font-size: 11px; color: var(--on-surface-variant); margin-top: 4px; }

.header-actions { display: flex; gap: 8px; }

.action-btn {
  padding: 8px 16px; border-radius: 8px; border: none;
  font-family: var(--font-label);
  font-size: 10px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.08em;
  cursor: pointer; display: flex; align-items: center; gap: 5px;
}
.action-btn.secondary { background: var(--bg-surface-high); color: #fff; border: 1px solid rgba(66, 70, 86, 0.1); }
.action-btn.secondary:hover { background: var(--bg-bright); }
.action-btn.primary { background: linear-gradient(135deg, #0F62FE 0%, #4589FF 100%); color: white; }
.action-btn.primary:hover { opacity: 0.9; }

.health-score-section {
  border-radius: 16px;
  padding: 36px;
  margin-top: 24px;
  display: flex;
  align-items: center;
  gap: 48px;
}

.score-display {
  position: relative;
  flex-shrink: 0;
}
.score-center {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}
.score-num {
  display: block;
  font-size: 34px;
  font-weight: 900;
  color: #fff;
}
.score-label {
  display: block;
  font-size: 9px;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-top: 2px;
}
.score-status {
  display: block;
  font-size: 11px;
  font-weight: 700;
  margin-top: 4px;
}
.score-status.good { color: var(--primary-container); }

.score-info { flex: 1; }

.score-desc {
  font-size: 13px;
  color: var(--on-surface);
  line-height: 1.6;
  margin-bottom: 16px;
}

.improvement-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.imp-tag {
  padding: 5px 12px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 600;
  font-family: var(--font-mono);
}
.imp-tag.up { background: rgba(15, 98, 254, 0.08); color: var(--primary-container); }
.imp-tag.down { background: rgba(15, 98, 254, 0.08); color: var(--primary-container); }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-top: 20px;
}

.stat-card {
  border-radius: 14px;
  padding: 22px;
  text-align: center;
  transition: transform 0.2s;
}
.stat-card:hover { transform: translateY(-2px); }

.stat-icon { font-size: 28px; color: var(--on-surface-variant); opacity: 0.6; }
.green-icon { color: var(--primary-container) !important; }
.yellow-icon { color: #FFB800 !important; }
.red-icon { color: var(--error) !important; }

.stat-value {
  font-size: 30px;
  font-weight: 900;
  margin: 10px 0 4px;
  letter-spacing: -0.02em;
}
.success-text { color: var(--primary-container); }
.warn-text { color: #FFB800; }
.error-text { color: var(--error); }

.stat-label {
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.report-table-section {
  border-radius: 16px;
  padding: 28px;
  margin-top: 24px;
}
.report-table-section h3 {
  font-size: 14px;
  font-weight: 800;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  margin-bottom: 20px;
}

.report-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}
.report-table th {
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
.report-table td {
  padding: 13px 16px;
  border-bottom: 1px solid rgba(66, 70, 86, 0.04);
  color: var(--on-surface);
}

.text-muted { color: var(--on-surface-variant); }

.row-warn td:nth-child(6) { color: #FFB800; }
.row-error td:nth-child(6) { color: var(--error); }

.status-tag {
  font-size: 9px;
  font-weight: 700;
  padding: 3px 10px;
  border-radius: 9999px;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}
.status-tag.success { background: rgba(15, 98, 254, 0.12); color: var(--primary-container); }
.status-tag.warn { background: rgba(255, 184, 0, 0.12); color: #FFB800; }
.status-tag.error { background: rgba(147, 0, 10, 0.15); color: var(--error); }

.detail-link {
  font-size: 11px;
  color: var(--primary-container);
  font-weight: 600;
  text-decoration: none;
}
.detail-link:hover { text-decoration: underline; }
</style>
