<template>
  <div class="task-result-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">{{ t('tasks.resultSummary') }}</h1>
        <p class="page-subtitle font-mono">Task ID: EXEC-2024-001 | Completed at 2024-05-24 14:33:12 UTC+8</p>
      </div>
      <div class="header-actions">
        <button class="action-btn secondary" @click="$router.push('/history')">
          {{ t('tasks.returnTaskList') }}
        </button>
        <button class="action-btn primary">
          <span class="material-symbols-outlined">download</span>
          {{ t('tasks.exportReport') }}
        </button>
        <button class="action-btn warning">
          <span class="material-symbols-outlined">refresh</span>
          {{ t('tasks.retryFailed') }}
        </button>
      </div>
    </div>

    <!-- Summary Cards -->
    <div class="summary-cards">
      <div class="summary-card total-card glass-card">
        <p class="card-label">{{ t('tasks.totalNodes') }}</p>
        <p class="card-value font-mono">12</p>
      </div>
      <div class="summary-card success-card glass-card">
        <p class="card-label">{{ t('tasks.successExec') }}</p>
        <p class="card-value font-mono success-text">10</p>
      </div>
      <div class="summary-card fail-card glass-card">
        <p class="card-label">{{ t('tasks.execFailed') }}</p>
        <p class="card-value font-mono error-text">2</p>
      </div>
      <div class="summary-card time-card glass-card">
        <p class="card-label">Total Duration</p>
        <p class="card-value font-mono">1m 11s</p>
      </div>
    </div>

    <!-- Chart Section -->
    <section class="chart-section glass-card">
      <div class="chart-header">
        <h3>{{ t('tasks.executionResultDist') }}</h3>
        <div class="chart-legend">
          <span><span class="legend-dot success"></span>{{ t('tasks.successLabel') }}</span>
          <span><span class="legend-dot fail"></span>{{ t('tasks.failedLabel') }}</span>
          <span><span class="legend-dot skip"></span>{{ t('tasks.skipped') }}</span>
        </div>
      </div>
      <div class="pie-chart-area">
        <div class="pie-chart">
          <svg viewBox="0 0 100 100" width="180" height="180">
            <circle cx="50" cy="50" r="40" fill="none" stroke="#2A2A2A" stroke-width="20"/>
            <circle cx="50" cy="50" r="40" fill="none" stroke="#0F62FE" stroke-width="20"
                    stroke-dasharray="209.4 251.3" transform="rotate(-90 50 50)"/>
            <circle cx="50" cy="50" r="40" fill="none" stroke="#FFB4AB" stroke-width="20"
                    stroke-dasharray="41.9 251.3" stroke-dashoffset="-209.4" transform="rotate(-90 50 50)"/>
          </svg>
          <div class="pie-center">
            <span class="pie-percent font-mono">83%</span>
            <span class="pie-label">Success Rate</span>
          </div>
        </div>
        <div class="chart-stats">
          <div class="stat-row">
            <span class="stat-label">{{ t('tasks.targets') }}</span>
            <span class="stat-val font-mono">12</span>
          </div>
          <div class="stat-row">
            <span class="stat-label success">{{ t('tasks.successLabel') }}</span>
            <span class="stat-val font-mono">10 (83%)</span>
          </div>
          <div class="stat-row">
            <span class="stat-label error">{{ t('tasks.failedLabel') }}</span>
            <span class="stat-val font-mono">2 (17%)</span>
          </div>
          <div class="stat-row">
            <span class="stat-label muted">{{ t('tasks.skipped') }}</span>
            <span class="stat-val font-mono">0 (0%)</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Detailed Records Table -->
    <section class="records-section glass-card">
      <h3>{{ t('tasks.detailedRecords') }}</h3>
      <table class="data-table">
        <thead>
          <tr>
            <th>{{ t('tasks.hostIdOrIp') }}</th>
            <th>{{ t('tasks.statusCode') }}</th>
            <th>{{ t('tasks.returnCode') }}</th>
            <th>{{ t('tasks.errorDetail') }}</th>
            <th>{{ t('tasks.execTime') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, idx) in recordData" :key="idx" :class="{ 'row-fail': row.status === 'FAILED' }">
            <td class="font-mono id-cell">{{ row.ip }}</td>
            <td><span :class="['status-tag', row.status === 'SUCCESS' ? 'success' : 'error']">{{ row.status }}</span></td>
            <td class="font-mono">{{ row.code }}</td>
            <td class="font-mono text-muted">{{ row.detail }}</td>
            <td class="font-mono">{{ row.duration }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

const recordData = [
  { ip: '192.168.1.100', status: 'SUCCESS', code: '0', detail: '—', duration: '14s' },
  { ip: '192.168.1.101', status: 'SUCCESS', code: '0', detail: '—', duration: '15s' },
  { ip: '192.168.1.102', status: 'SUCCESS', code: '0', detail: '—', duration: '13s' },
  { ip: '192.168.1.103', status: 'SUCCESS', code: '0', detail: '—', duration: '16s' },
  { ip: '192.168.1.104', status: 'SUCCESS', code: '0', detail: '—', duration: '18s' },
  { ip: '192.168.1.105', status: 'SUCCESS', code: '0', detail: '—', duration: '14s' },
  { ip: '192.168.1.106', status: 'SUCCESS', code: '0', detail: '—', duration: '19s' },
  { ip: '192.168.1.107', status: 'SUCCESS', code: '0', detail: '—', duration: '21s' },
  { ip: '192.168.1.108', status: 'SUCCESS', code: '0', detail: '—', duration: '22s' },
  { ip: '192.168.1.109', status: 'SUCCESS', code: '0', detail: '—', duration: '16s' },
  { ip: '192.168.1.110', status: 'FAILED', code: '127', detail: 'Command not found: kubectl', duration: '3s' },
  { ip: '192.168.1.111', status: 'FAILED', code: '1', detail: 'kubeconfig permission denied', duration: '5s' }
]
</script>

<style scoped>
.task-result-page {
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
.action-btn.primary { background: var(--primary-container); color: white; }
.action-btn.primary:hover { opacity: 0.9; }
.action-btn.warning { background: rgba(255, 184, 0, 0.1); color: #FFB800; border: 1px solid rgba(255, 184, 0, 0.2); }

.summary-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-top: 24px;
}

.summary-card {
  border-radius: 14px;
  padding: 24px;
  transition: transform 0.2s;
}
.summary-card:hover { transform: translateY(-2px); }

.card-label {
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.1em;
}
.card-value {
  font-size: 36px;
  font-weight: 900;
  margin-top: 6px;
  letter-spacing: -0.03em;
}
.success-text { color: var(--primary-container); }
.error-text { color: var(--error); }

.chart-section {
  border-radius: 16px;
  padding: 28px;
  margin-top: 24px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}
.chart-header h3 {
  font-size: 14px;
  font-weight: 800;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.chart-legend {
  display: flex;
  gap: 20px;
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
}
.legend-dot {
  display: inline-block;
  width: 8px; height: 8px;
  border-radius: 50%;
  margin-right: 5px;
}
.legend-dot.success { background: var(--primary-container); }
.legend-dot.fail { background: var(--error); }
.legend-dot.skip { background: var(--outline); }

.pie-chart-area {
  display: flex;
  align-items: center;
  gap: 48px;
}

.pie-chart {
  position: relative;
  flex-shrink: 0;
}
.pie-center {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}
.pie-percent {
  display: block;
  font-size: 28px;
  font-weight: 900;
  color: #fff;
}
.pie-label {
  display: block;
  font-size: 9px;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.chart-stats {
  display: flex;
  flex-direction: column;
  gap: 14px;
  flex: 1;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(66, 70, 86, 0.06);
}
.stat-row:last-child { border-bottom: none; }

.stat-label { font-size: 12px; color: var(--on-surface-variant); font-weight: 500; }
.stat-label.success { color: var(--primary-container); }
.stat-label.error { color: var(--error); }
.stat-label.muted { color: var(--outline); }

.stat-val { font-size: 13px; font-weight: 600; color: #fff; }

.records-section {
  border-radius: 16px;
  padding: 28px;
  margin-top: 24px;
}
.records-section h3 {
  font-size: 14px;
  font-weight: 800;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  margin-bottom: 20px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}
.data-table th {
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
.data-table td {
  padding: 14px 16px;
  border-bottom: 1px solid rgba(66, 70, 86, 0.04);
  color: var(--on-surface);
}

.id-cell { color: var(--primary); font-weight: 600; }
.text-muted { color: var(--on-surface-variant); }

.row-fail td:first-child { color: var(--error); }

.status-tag {
  font-size: 10px;
  font-weight: 700;
  padding: 3px 10px;
  border-radius: 9999px;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}
.status-tag.success { background: rgba(15, 98, 254, 0.12); color: var(--primary-container); }
.status-tag.error { background: rgba(147, 0, 10, 0.15); color: var(--error); }
</style>
