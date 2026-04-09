<template>
  <div class="audit-page">
    <div class="page-header">
      <!-- <div>
        <h1 class="page-title">{{ t('audit.title') }}</h1>
        <p class="page-subtitle">{{ t('audit.subtitle') }}</p>
      </div> -->
      <div class="header-actions">
        <button class="action-btn secondary">
          <span class="material-symbols-outlined">filter_list</span>
          {{ t('audit.advancedFilter') }}
        </button>
        <button class="action-btn primary">
          <span class="material-symbols-outlined">download</span>
          {{ t('audit.exportAuditReport') }}
        </button>
      </div>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <div class="filter-group">
        <select class="filter-select">
          <option>All Operations</option>
          <option>Login / Logout</option>
          <option>Config Change</option>
          <option>Script Execution</option>
          <option>User Management</option>
        </select>
        <select class="filter-select">
          <option>All Results</option>
          <option>Success</option>
          <option>Failed</option>
          <option>Denied</option>
        </select>
        <input type="date" value="2024-05-17" class="date-input" />
        <span class="range-sep">—</span>
        <input type="date" value="2024-05-24" class="date-input" />
      </div>
      <div class="search-area">
        <span class="material-symbols-outlined search-icon">search</span>
        <input placeholder="Search by operator or resource..." />
      </div>
    </div>

    <!-- Audit Table -->
    <div class="table-section glass-card">
      <table class="audit-table">
        <thead>
          <tr>
            <th>{{ t('audit.operator') }}</th>
            <th>{{ t('audit.operationType') }}</th>
            <th>{{ t('audit.targetResource') }}</th>
            <th>{{ t('audit.timestamp') }}</th>
            <th>{{ t('audit.result') }}</th>
            <th>{{ t('audit.sourceIp') }}</th>
            <th>{{ t('audit.details') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, idx) in auditData" :key="idx">
            <td>
              <div class="operator-cell">
                <img :src="row.avatar" class="op-avatar" />
                <span>{{ row.operator }}</span>
              </div>
            </td>
            <td><span :class="['op-type', row.opClass]">{{ row.operation }}</span></td>
            <td class="font-mono text-muted">{{ row.resource }}</td>
            <td class="font-mono text-muted">{{ row.time }}</td>
            <td><span :class="['result-tag', row.resultClass]">{{ row.result }}</span></td>
            <td class="font-mono text-muted">{{ row.ip }}</td>
            <td><a href="#" class="detail-link">View →</a></td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="pagination">
      <span class="page-info font-mono">Showing 1-15 of 48,291 records</span>
      <div class="page-nav">
        <button class="page-btn" disabled>← Prev</button>
        <button class="page-btn active">1</button>
        <button class="page-btn">2</button>
        <button class="page-btn">3</button>
        <button class="page-btn">...</button>
        <button class="page-btn">3220</button>
        <button class="page-btn">Next →</button>
      </div>
    </div>

    <!-- Security Notice -->
    <div class="security-notice glass-card">
      <span class="material-symbols-outlined notice-icon">shield</span>
      <div>
        <h4>Compliance Information</h4>
        <p class="font-mono notice-text">All audit logs are encrypted at rest (AES-256) and retained for 365 days per SOX compliance. Tamper-evident logging enabled via blockchain hash chain.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

const auditData = [
  { operator: 'zhang_san', avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAc0vS318_7OXcTC2YSdOa7Eq1rsXwYBC_afOGAzgIbZHM6oovFaso4CW94bOfOdQ3XjCbno95XgRapqkLDNgEiuivbWtb5vdKaBza78J1RjOrgpVua3KQZh-yTYvU1Krn3GcrkpqIIfbclkcd79GZKp062J18c-y16-SF16pkVLjMcCS_6H4uZDz5iMntpDUyJeYSq6Q2ARBVMeIgbucEkoBIBZthw4_lMoK86BDSwuCJbBsaAQHtQs3xQeJvk12EjUv_Su4rIbJW7', operation: 'LOGIN', opClass: 'op-auth', resource: '/login', time: '2024-05-24 14:20:01', result: 'SUCCESS', resultClass: 'res-success', ip: '10.0.5.42' },
  { operator: 'li_si', avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBdJ9It8bcAmloKGW8_QVDC-w-Qpxe8JEt6DXVTqYIiyoEIQYJTtGzMpz-qGaR4mj0IG_0Qmo_t3mLoC_4XOcGqi66iMRBjiG15aDjpOdxgmMA7XcIiCSvIFvhhTTHiyJ3YV2-ZJnLUJYzUViTNeRPZnFYbfDhswhmpIcKWUaeKsC9em6C1xRbtrzqZZJkE2PwvjYqGPLtF9oSNMkIgJJtUDKR4Ep95ZIn8GRIqo_2O1CpPPtcgvARY8xotAmEiPY7AZTWzu2zRqa4c', operation: 'CONFIG_UPDATE', opClass: 'op-config', resource: 'HOST-2024-001', time: '2024-05-24 14:32:05', result: 'SUCCESS', resultClass: 'res-success', ip: '10.0.5.43' },
  { operator: 'wang_wu', avatar: 'https://ui-avatars.com/api/?name=WW&background=333&color=fff', operation: 'SCRIPT_EXEC', opClass: 'op-exec', resource: 'deploy_k8s_node.sh', time: '2024-05-24 14:32:10', result: 'SUCCESS', resultClass: 'res-success', ip: '10.0.5.44' },
  { operator: 'system', avatar: 'https://ui-avatars.com/api/?name=SYS&background=333&color=fff', operation: 'AUTO_DEPLOY', opClass: 'op-auto', resource: 'cert_renewal.sh', time: '2024-05-24 02:00:00', result: 'SUCCESS', resultClass: 'res-success', ip: '127.0.0.1' },
  { operator: 'unknown', avatar: 'https://ui-avatars.com/api/?name=??&background=333&color=fff', operation: 'ACCESS_DENIED', opClass: 'op-deny', resource: '/admin/settings', time: '2024-05-23 23:45:12', result: 'DENIED', resultClass: 'res-error', ip: '203.0.113.50' }
]
</script>

<style scoped>
.audit-page {
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

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  gap: 20px;
}

.filter-group { display: flex; align-items: center; gap: 8px; }

.filter-select {
  background: var(--bg-surface-low);
  border: 1px solid rgba(66, 70, 86, 0.08);
  border-radius: 8px;
  padding: 8px 12px;
  color: #fff;
  font-size: 11px;
  outline: none;
  cursor: pointer;
}
.filter-select option { background: var(--bg-surface-container); }

.date-input {
  background: var(--bg-surface-low);
  border: 1px solid rgba(66, 70, 86, 0.08);
  border-radius: 8px;
  padding: 8px 12px;
  color: #fff;
  font-size: 11px;
  outline: none;
  font-family: var(--font-body);
}
.range-sep { color: var(--outline); }

.search-area {
  display: flex;
  align-items: center;
  background: var(--bg-surface-low);
  border-radius: 9999px;
  padding: 8px 16px;
  min-width: 260px;
  border: 1px solid rgba(66, 70, 86, 0.08);
}
.search-icon { font-size: 16px; color: var(--outline); margin-right: 8px; }
.search-area input {
  background: transparent; border: none; outline: none;
  color: #fff; font-size: 12px; flex: 1;
}
.search-area input::placeholder { color: var(--outline); }

.table-section {
  border-radius: 16px;
  padding: 24px;
  margin-top: 20px;
  overflow-x: auto;
}

.audit-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 11.5px;
}
.audit-table th {
  text-align: left;
  padding: 11px 14px;
  font-family: var(--font-label);
  font-size: 9px;
  font-weight: 700;
  color: var(--on-surface-variant);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  background: var(--bg-surface-highest);
  border-bottom: 1px solid rgba(66, 70, 86, 0.08);
}
.audit-table td {
  padding: 13px 14px;
  border-bottom: 1px solid rgba(66, 70, 86, 0.04);
  color: var(--on-surface);
}

.operator-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
.op-avatar {
  width: 26px; height: 26px;
  border-radius: 50%; object-fit: cover;
}

.op-type {
  font-size: 10px;
  font-weight: 700;
  padding: 3px 8px;
  border-radius: 4px;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}
.op-auth { background: rgba(180, 197, 255, 0.08); color: var(--primary); }
.op-config { background: rgba(200, 64, 0, 0.08); color: var(--tertiary); }
.op-exec { background: rgba(15, 98, 254, 0.08); color: var(--primary-container); }
.op-auto { background: rgba(57, 153, 174, 0.08); color: #3999AE; }
.op-deny { background: rgba(147, 0, 10, 0.1); color: var(--error); }

.text-muted { color: var(--on-surface-variant); }

.result-tag {
  font-size: 9px;
  font-weight: 800;
  padding: 3px 10px;
  border-radius: 9999px;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}
.res-success { background: rgba(15, 98, 254, 0.12); color: var(--primary-container); }
.res-error { background: rgba(147, 0, 10, 0.15); color: var(--error); }

.detail-link {
  font-size: 10px;
  color: var(--primary-container);
  font-weight: 600;
  text-decoration: none;
}
.detail-link:hover { text-decoration: underline; }

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

.security-notice {
  border-radius: 14px;
  padding: 20px 24px;
  margin-top: 24px;
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.notice-icon {
  font-size: 28px;
  color: var(--primary-container);
  opacity: 0.6;
  flex-shrink: 0;
}

.notice-icon + div h4 {
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 4px;
}

.notice-text {
  font-size: 10.5px;
  color: var(--on-surface-variant);
  line-height: 1.6;
}
</style>
