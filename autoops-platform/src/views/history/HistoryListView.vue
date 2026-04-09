<template>
  <div class="history-page">
    <div class="page-header">
      <!-- <div>
        <h1 class="page-title">{{ t('history.title') }}</h1>
        <p class="page-subtitle">{{ t('history.monitoringNodes') }}</p>
      </div> -->
      <div class="header-actions">
        <button class="action-btn secondary">
          <span class="material-symbols-outlined">filter_list</span>
          {{ t('history.advancedFilter') }}
        </button>
        <button class="action-btn primary">
          <span class="material-symbols-outlined">download</span>
          Export CSV
        </button>
      </div>
    </div>

    <!-- Stats Row -->
    <div class="stats-row">
      <div class="stat-item glass-card">
        <p class="stat-label">{{ t('history.totalExecs') }}</p>
        <p class="stat-value font-mono">12,483</p>
      </div>
      <div class="stat-item glass-card">
        <p class="stat-label">{{ t('history.successRate') }}</p>
        <p class="stat-value font-mono success">96.7%</p>
      </div>
      <div class="stat-item glass-card">
        <p class="stat-label">{{ t('history.avgDuration') }}</p>
        <p class="stat-value font-mono">42.3s</p>
      </div>
      <div class="stat-item glass-card">
        <p class="stat-label">{{ t('history.activeTasks') }}</p>
        <p class="stat-value font-mono active">3</p>
      </div>
    </div>

    <!-- Date Range Filter -->
    <div class="filter-bar">
      <div class="date-range">
        <label>{{ t('history.dateRange') }}</label>
        <input type="date" value="2024-05-17" />
        <span class="range-sep">—</span>
        <input type="date" value="2024-05-24" />
      </div>
      <div class="search-box">
        <span class="material-symbols-outlined">search</span>
        <input placeholder="Search by task ID or keyword..." />
      </div>
    </div>

    <!-- Task History Table -->
    <div class="table-section glass-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>Task ID</th>
            <th>Type</th>
            <th>Targets</th>
            <th>Started At</th>
            <th>Duration</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, idx) in taskHistory" :key="idx">
            <td><span class="task-id font-mono">{{ row.id }}</span></td>
            <td>{{ row.type }}</td>
            <td class="font-mono">{{ row.targets }}</td>
            <td class="font-mono text-muted">{{ row.startedAt }}</td>
            <td class="font-mono">{{ row.duration }}</td>
            <td><span :class="['status-tag', row.statusClass]">{{ row.status }}</span></td>
            <td>
              <div class="row-actions">
                <a href="#" @click.prevent="$router.push(`/tasks/result/${row.id}`)">Detail</a>
                <a href="#" @click.prevent="$router.push(`/tasks/log/${row.id}`)">Log</a>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="pagination">
      <span class="page-info font-mono">Showing 1-15 of 12,483 records</span>
      <div class="page-nav">
        <button class="page-btn" disabled>← Prev</button>
        <button class="page-btn active">1</button>
        <button class="page-btn">2</button>
        <button class="page-btn">3</button>
        <button class="page-btn">...</button>
        <button class="page-btn">833</button>
        <button class="page-btn">Next →</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

const taskHistory = [
  { id: 'EXEC-2024-001', type: 'Script Deploy', targets: '12 nodes', startedAt: '2024-05-24 14:32', duration: '1m 11s', status: 'Completed', statusClass: 'success' },
  { id: 'EXEC-2024-002', type: 'Health Check', targets: '148 nodes', startedAt: '2024-05-24 02:00', duration: '3m 40s', status: 'Completed', statusClass: 'success' },
  { id: 'EXEC-2024-003', type: 'Cert Renewal', targets: '8 nodes', startedAt: '2024-05-23 18:45', duration: '2m 05s', status: 'Partial Fail', statusClass: 'warn' },
  { id: 'EXEC-2024-004', type: 'Config Update', targets: '24 nodes', startedAt: '2024-05-23 15:20', duration: '45s', status: 'Running', statusClass: 'running' },
  { id: 'EXEC-2024-005', type: 'Disk Cleanup', targets: '6 nodes', startedAt: '2024-05-23 11:10', duration: '38s', status: 'Completed', statusClass: 'success' },
  { id: 'EXEC-2024-006', type: 'Security Scan', targets: 'All (148)', startedAt: '2024-05-22 02:00', duration: '12m 30s', status: 'Failed', statusClass: 'error' }
]
</script>

<style scoped>
.history-page {
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
.page-subtitle { font-size: 12px; color: var(--on-surface-variant); margin-top: 4px; }

.header-actions { display: flex; gap: 8px; }

.action-btn {
  padding: 8px 16px; border-radius: 8px; border: none;
  font-family: var(--font-label);
  font-size: 10px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.08em;
  cursor: pointer; display: flex; align-items: center; gap: 5px;
}
.action-btn.secondary { background: var(--bg-surface-high); color: #fff; border: 1px solid rgba(66, 70, 86, 0.1); }
.action-btn.primary { background: linear-gradient(135deg, #0F62FE 0%, #4589FF 100%); color: white; }

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-top: 24px;
}

.stat-item {
  border-radius: 14px;
  padding: 22px;
  transition: transform 0.2s;
}
.stat-item:hover { transform: translateY(-2px); }

.stat-label {
  font-size: 10px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.1em;
}
.stat-value {
  font-size: 28px;
  font-weight: 900;
  margin-top: 6px;
  letter-spacing: -0.02em;
}
.stat-value.success { color: var(--primary-container); }
.stat-value.active { color: #FFB800; }

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  gap: 20px;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 10px;
}
.date-range label {
  font-size: 11px;
  font-weight: 600;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}
.date-range input[type="date"] {
  background: var(--bg-surface-low);
  border: 1px solid rgba(66, 70, 86, 0.1);
  border-radius: 8px;
  padding: 8px 12px;
  color: #fff;
  font-size: 12px;
  outline: none;
  font-family: var(--font-body);
}
.range-sep { color: var(--outline); }

.search-box {
  display: flex;
  align-items: center;
  background: var(--bg-surface-low);
  border-radius: 9999px;
  padding: 8px 16px;
  min-width: 280px;
  border: 1px solid rgba(66, 70, 86, 0.08);
}
.search-box .material-symbols-outlined { font-size: 16px; color: var(--outline); margin-right: 8px; }
.search-box input {
  background: transparent; border: none; outline: none;
  color: #fff; font-size: 12px; flex: 1;
}
.search-box input::placeholder { color: var(--outline); }

.table-section {
  border-radius: 16px;
  padding: 24px;
  margin-top: 20px;
  overflow-x: auto;
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

.task-id { color: var(--primary); font-weight: 600; }
.text-muted { color: var(--on-surface-variant); }

.row-actions { display: flex; gap: 12px; }
.row-actions a {
  font-size: 11px;
  color: var(--primary-container);
  text-decoration: none;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}
.row-actions a:hover { text-decoration: underline; }

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
.status-tag.running { background: rgba(57, 153, 174, 0.12); color: #3999AE; }
.status-tag.error { background: rgba(147, 0, 10, 0.15); color: var(--error); }

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 12px;
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
</style>
