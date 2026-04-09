<template>
  <div class="scripts-page">
    <div class="page-header">
      <!-- <div>
        <h1 class="page-title">{{ t('scripts.title') }}</h1>
        <p class="page-subtitle font-mono">{{ t('scripts.path') }} — {{ t('scripts.subtitle') }}</p>
      </div> -->
      <div class="header-actions">
        <button class="action-btn-primary" @click="$router.push('/scripts/editor/new')">
          <span class="material-symbols-outlined">add</span>
          {{ t('scripts.createScript') }}
        </button>
      </div>
    </div>

    <div class="toolbar">
      <div class="search-area">
        <span class="material-symbols-outlined search-icon">search</span>
        <input type="text" :placeholder="t('scripts.searchPlaceholder')" />
      </div>
      <div class="filter-group">
        <select class="filter-select">
          <option>{{ t('scripts.allTypes') }}</option>
          <option>Bash/Shell</option>
          <option>Python</option>
          <option>PowerShell</option>
        </select>
        <label class="private-toggle">
          <input type="checkbox" />
          {{ t('scripts.privateOnly') }}
        </label>
      </div>
    </div>

    <div class="script-list">
      <div v-for="(item, idx) in scriptData" :key="idx" class="script-card" @click="$router.push(`/scripts/editor/${item.id}`)">
        <div class="card-left">
          <span :class="['type-icon', item.typeClass]">{{ item.typeIcon }}</span>
          <div>
            <h3 class="script-name">{{ item.name }}</h3>
            <p class="script-meta">
              <span class="font-mono">v{{ item.version }}</span>
              ·
              <span>{{ item.author }}</span>
              ·
              <span>{{ item.date }}</span>
            </p>
          </div>
        </div>
        <div class="card-right">
          <span :class="['status-badge', item.statusClass]">{{ item.statusLabel }}</span>
          <span class="perm-badge">{{ item.permission }}</span>
          <span class="card-actions">
            <span class="material-symbols-outlined">more_vert</span>
          </span>
        </div>
      </div>
    </div>

    <div class="pagination">
      <span class="page-info font-mono">Showing 1-8 of 64 scripts</span>
      <div class="page-nav">
        <button class="page-btn active">1</button>
        <button class="page-btn">2</button>
        <button class="page-btn">3</button>
        <button class="page-btn">Next →</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

const scriptData = [
  { id: 'scr-001', name: 'deploy_k8s_node.sh', version: '2.4.1', author: 'ops_team', date: '2024-05-20', typeIcon: '#!', typeClass: 'type-bash', statusClass: 'badge-active', statusLabel: 'ACTIVE', permission: 'PUBLIC' },
  { id: 'scr-002', name: 'health_check.py', version: '1.8.0', author: 'monitoring', date: '2024-05-18', typeIcon: 'PY', typeClass: 'type-python', statusClass: 'badge-active', statusLabel: 'ACTIVE', permission: 'PRIVATE' },
  { id: 'scr-003', name: 'cert_renewal.sh', version: '3.0.2', author: 'security', date: '2024-05-15', typeIcon: '#!', typeClass: 'type-bash', statusClass: 'badge-draft', statusLabel: 'DRAFT', permission: 'PRIVATE' },
  { id: 'scr-004', name: 'disk_cleanup.ps1', version: '1.2.0', author: 'windows_ops', date: '2024-05-12', typeIcon: 'PS', typeClass: 'type-powershell', statusClass: 'badge-active', statusLabel: 'ACTIVE', permission: 'PUBLIC' },
  { id: 'scr-005', name: 'log_rotate.sh', version: '1.5.3', author: 'ops_team', date: '2024-05-10', typeIcon: '#!', typeClass: 'type-bash', statusClass: 'badge-deprecated', statusLabel: 'DEPRECATED', permission: 'PUBLIC' },
  { id: 'scr-006', name: 'backup_db.py', version: '2.1.0', author: 'dba_team', date: '2024-05-08', typeIcon: 'PY', typeClass: 'type-python', statusClass: 'badge-active', statusLabel: 'ACTIVE', permission: 'PRIVATE' }
]
</script>

<style scoped>
.scripts-page {
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
  font-size: 11px;
  color: var(--on-surface-variant);
  margin-top: 4px;
}

.action-btn-primary {
  padding: 10px 20px;
  background: linear-gradient(135deg, #0F62FE 0%, #4589FF 100%);
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

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 8px;
}

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
  min-width: 320px;
}
.search-icon { font-size: 16px; color: var(--outline); }
.search-area input {
  background: transparent; border: none; outline: none;
  color: #fff; padding-left: 10px; font-size: 13px; flex: 1;
}
.search-area input::placeholder { color: var(--outline); }

.filter-group { display: flex; gap: 12px; align-items: center; }

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

.private-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: var(--on-surface-variant);
  cursor: pointer;
  font-family: var(--font-label);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}
.private-toggle input[type="checkbox"] { accent-color: var(--primary-container); }

.script-list {
  margin-top: 24px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.script-card {
  background: var(--bg-surface-container-low);
  border-radius: 12px;
  padding: 18px 22px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: all 0.15s;
  border: 1px solid rgba(66, 70, 86, 0.04);
}
.script-card:hover {
  background: var(--bg-surface-container);
  border-color: rgba(180, 197, 255, 0.12);
}

.card-left { display: flex; align-items: center; gap: 14px; }

.type-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 800;
  font-family: var(--font-mono);
}
.type-bash { background: rgba(15, 98, 254, 0.1); color: var(--primary-container); }
.type-python { background: rgba(200, 64, 0, 0.1); color: var(--tertiary); }
.type-powershell { background: rgba(57, 153, 174, 0.1); color: #3999AE; }

.script-name {
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 2px;
}

.script-meta {
  font-size: 11px;
  color: var(--on-surface-variant);
}

.card-right { display: flex; align-items: center; gap: 12px; }

.status-badge {
  font-size: 9px;
  font-weight: 800;
  padding: 4px 10px;
  border-radius: 9999px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}
.badge-active { background: rgba(15, 98, 254, 0.1); color: var(--primary-container); }
.badge-draft { background: rgba(53, 53, 52, 0.6); color: var(--on-surface-variant); }
.badge-deprecated { background: rgba(147, 0, 10, 0.1); color: var(--error); }

.perm-badge {
  font-size: 10px;
  font-weight: 600;
  color: var(--outline);
  text-transform: uppercase;
  letter-spacing: 0.06em;
  padding: 3px 8px;
  background: var(--bg-surface-high);
  border-radius: 4px;
}

.card-actions {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  cursor: pointer;
  color: var(--on-surface-variant);
  transition: all 0.15s;
}
.card-actions:hover { background: var(--bg-surface-high); color: #fff; }

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding-top: 16px;
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
</style>
