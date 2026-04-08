<template>
  <div class="group-manage-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">{{ t('groupManage.title') }}</h1>
        <p class="page-subtitle">{{ t('groupManage.subtitle') }}</p>
      </div>
      <div class="header-actions">
        <button class="action-btn secondary">
          <span class="material-symbols-outlined">refresh</span>
          {{ t('groupManage.syncDynamic') }}
        </button>
        <button class="action-btn primary litho-gradient" @click="showCreateDialog = true">
          <span class="material-symbols-outlined">add</span>
          {{ t('groupManage.createGroup') }}
        </button>
      </div>
    </div>

    <!-- Stats -->
    <div class="stats-row glass-card">
      <div class="stat" v-for="(s, idx) in stats" :key="idx">
        <span class="stat-num font-mono">{{ s.val }}</span>
        <span class="stat-lbl">{{ s.lbl }}</span>
      </div>
    </div>

    <!-- Filter & Search -->
    <div class="filter-bar glass-card">
      <div class="filter-left">
        <div class="search-box">
          <span class="material-symbols-outlined">search</span>
          <input :placeholder="t('groupManage.searchPlaceholder')" v-model="searchKey" />
        </div>
        <select class="filter-select" v-model="filterType">
          <option value="">{{ t('groupManage.allTypes') }}</option>
          <option value="static">{{ t('groupManage.staticGroup') }}</option>
          <option value="dynamic">{{ t('groupManage.dynamicGroup') }}</option>
        </select>
      </div>
      <span class="result-count font-mono">{{ groups.length }} {{ t('groupManage.totalGroups') }}</span>
    </div>

    <!-- Group List Cards -->
    <div class="group-list">
      <div v-for="(g, idx) in groups" :key="idx" class="group-card glass-card">
        <div class="card-left">
          <div :class="['type-badge', g.type]">
            <span class="material-symbols-outlined" style="font-size:14px;">{{ g.type === 'static' ? 'folder' : 'auto_awesome' }}</span>
            {{ g.typeLabel }}
          </div>
          <div class="group-info">
            <h3 class="group-name">{{ g.name }}</h3>
            <p class="group-desc">{{ g.desc }}</p>
          </div>
        </div>
        <div class="card-center">
          <div class="info-block">
            <span class="info-label font-mono">{{ t('groupManage.hostCount') }}</span>
            <span class="info-value font-mono">{{ g.hostCount }}</span>
          </div>
          <div class="info-block">
            <span class="info-label font-mono">{{ t('groupManage.creator') }}</span>
            <span class="info-value">{{ g.creator }}</span>
          </div>
          <div class="info-block">
            <span class="info-label font-mono">{{ t('groupManage.updatedAt') }}</span>
            <span class="info-value font-mono text-sm">{{ g.updatedAt }}</span>
          </div>
        </div>
        <div class="card-right">
          <div class="host-avatars">
            <img v-for="(a, aidx) in g.hostAvatars.slice(0,4)" :key="aidx" :src="a" class="host-avatar-sm" />
            <span v-if="g.hostCount > 4" class="more-count font-mono">+{{ g.hostCount - 4 }}</span>
          </div>
          <div class="card-actions">
            <button class="icon-btn" :title="t('common.view')"><span class="material-symbols-outlined">visibility</span></button>
            <button class="icon-btn" :title="t('common.edit')"><span class="material-symbols-outlined">edit</span></button>
            <button class="icon-btn danger" :title="t('common.delete')"><span class="material-symbols-outlined">delete</span></button>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Dialog -->
    <div v-if="showCreateDialog" class="dialog-overlay" @click.self="showCreateDialog = false">
      <div class="dialog glass-card">
        <div class="dialog-header">
          <h3>{{ t('groupManage.createGroup') }}</h3>
          <button class="close-btn" @click="showCreateDialog = false"><span class="material-symbols-outlined">close</span></button>
        </div>
        <div class="dialog-body">
          <div class="form-field">
            <label>{{ t('groupManage.formName') }}</label>
            <input type="text" placeholder="" />
          </div>
          <div class="form-field">
            <label>{{ t('groupManage.formType') }}</label>
            <select><option>静态组</option><option>动态组</option></select>
          </div>
          <div class="form-field">
            <label>{{ t('groupManage.formDesc') }}</label>
            <textarea rows="2" placeholder=""></textarea>
          </div>
          <div class="form-field full">
            <label>{{ t('groupManage.formSelectHosts') }}</label>
            <div class="host-select-area">
              <span class="placeholder-text">点击选择主机...</span>
              <button class="btn-small"><span class="material-symbols-outlined">add</span> 选择主机</button>
            </div>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="showCreateDialog = false">{{ t('common.cancel') }}</button>
          <button class="btn-confirm litho-gradient">{{ t('common.confirm') }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const searchKey = ref('')
const filterType = ref('')
const showCreateDialog = ref(false)

const stats = [
  { val: '8', lbl: t('groupManage.statTotal') },
  { val: '5', lbl: t('groupManage.statStatic') },
  { val: '3', lbl: t('groupManage.statDynamic') }
]

const groups = [
  { name: 'K8S-PROD-01', desc: '生产环境 K8S 节点组', type: 'static', typeLabel: '静态组', hostCount: 24, creator: 'zhang_san', updatedAt: '2026-04-08', hostAvatars: Array(5).fill('https://ui-avatars.com/api/?name=H&background=0F62FE&color=fff&size=64') },
  { name: 'DB-Master-Slave', desc: '数据库主从集群', type: 'static', typeLabel: '静态组', hostCount: 6, creator: 'li_si', updatedAt: '2026-04-07', hostAvatars: Array(5).fill('https://ui-avatars.com/api/?name=D&background=C84000&color=fff&size=64') },
  { name: 'Test-Env-All', desc: '测试环境全量（动态匹配）', type: 'dynamic', typeLabel: '动态组', hostCount: 18, creator: 'admin', updatedAt: '2026-04-06', hostAvatars: Array(5).fill('https://ui-avatars.com/api/?name=T&background=FFB800&color=000&size=64') },
  { name: 'Beijing-IDC-A', desc: '北京机房 A 区服务器', type: 'static', typeLabel: '静态组', hostCount: 42, creator: 'zhang_san', updatedAt: '2026-04-05', hostAvatars: Array(5).fill('https://ui-avatars.com/api/?name=B&background=0F62FE&color=fff&size=64') },
  { name: 'CentOS-All', desc: '所有 CentOS 操作系统主机（自动）', type: 'dynamic', typeLabel: '动态组', hostCount: 86, creator: 'system', updatedAt: '2026-04-08', hostAvatars: Array(5).fill('https://ui-avatars.com/api/?name=C&background=333&color=fff&size=64') }
]
</script>

<style scoped>
.group-manage-page { padding: 28px; max-width: 1400px; margin: 0 auto; }
.page-title { font-size: 26px; font-weight: 900; color: #fff; letter-spacing: -0.02em; }
.page-subtitle { font-size: 12px; color: var(--on-surface-variant); margin-top: 4px; }

.header-actions { display: flex; gap: 8px; }
.action-btn {
  padding: 9px 18px; border-radius: 8px; border: none;
  font-family: var(--font-label); font-size: 11px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.08em;
  cursor: pointer; display: flex; align-items: center; gap: 6px;
}
.action-btn.secondary { background: var(--bg-surface-high); color: #fff; border: 1px solid rgba(66,70,86,0.1); }
.action-btn.primary { color: var(--on-primary-container); box-shadow: 0 4px 15px rgba(15,98,254,0.25); }

.stats-row {
  display: flex; gap: 40px; padding: 18px 24px; margin-top: 20px; align-items: center;
}
.stat { display: flex; flex-direction: column; gap: 2px; }
.stat-num { font-size: 28px; font-weight: 900; color: #fff; }
.stat-lbl { font-size: 10px; color: var(--on-surface-variant); text-transform: uppercase; letter-spacing: 0.08em; }

.filter-bar { display: flex; justify-content: space-between; align-items: center; padding: 14px 20px; margin-top: 16px; }
.filter-left { display: flex; gap: 12px; align-items: center; }
.search-box {
  display: flex; align-items: center; background: var(--bg-base);
  border-radius: 9999px; padding: 7px 14px; gap: 8px;
  border: 1px solid rgba(66,70,86,0.1); min-width: 240px;
}
.search-box .material-symbols-outlined { font-size: 16px; color: var(--outline); }
.search-box input { background: transparent; border: none; outline: none; color: #fff; font-size: 12px; flex: 1; }
.filter-select {
  background: var(--bg-base); border: 1px solid rgba(66,70,86,0.1);
  border-radius: 8px; padding: 8px 12px; color: #fff; font-size: 12px; outline: none;
}
.filter-select option { background: var(--bg-surface-container); }
.result-count { font-size: 11px; color: var(--on-surface-variant); }

.group-list { margin-top: 16px; display: flex; flex-direction: column; gap: 10px; }

.group-card {
  display: grid; grid-template-columns: 320px 1fr auto; gap: 20px;
  align-items: center; padding: 18px 22px; border-radius: 12px;
  transition: all 0.15s ease;
}
.group-card:hover { box-shadow: 0 8px 30px rgba(0,0,0,0.25); }

.card-left { display: flex; flex-direction: column; gap: 10px; }
.type-badge {
  display: inline-flex; align-items: center; gap: 6px; width: fit-content;
  padding: 4px 10px; border-radius: 6px; font-size: 10px; font-weight: 700;
  letter-spacing: 0.05em; text-transform: uppercase;
}
.type-badge.static { background: rgba(15,98,254,0.1); color: var(--primary-container); }
.type-badge.dynamic { background: rgba(255,184,0,0.1); color: #FFB800; }
.group-name { font-size: 15px; font-weight: 800; color: #fff; }
.group-desc { font-size: 11px; color: var(--on-surface-variant); line-height: 1.45; }

.card-center { display: flex; gap: 32px; }
.info-block { display: flex; flex-direction: column; gap: 2px; }
.info-label { font-size: 9px; color: var(--outline); text-transform: uppercase; letter-spacing: 0.1em; }
.info-value { font-size: 12px; color: #fff; font-weight: 600; }
.text-sm { font-size: 11px !important; }

.card-right { display: flex; flex-direction: column; align-items: flex-end; gap: 10px; }
.host-avatars { display: flex; align-items: center; }
.host-avatar-sm {
  width: 26px; height: 26px; border-radius: 6px;
  border: 2px solid var(--bg-surface-low); object-fit: cover;
  margin-right: -6px;
}
.more-count { font-size: 9px; color: var(--on-surface-variant); margin-left: 8px; }
.card-actions { display: flex; gap: 6px; }
.icon-btn {
  width: 30px; height: 30px; border-radius: 7px; border: none;
  background: var(--bg-surface-high); color: var(--on-surface-variant);
  cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.15s;
}
.icon-btn .material-symbols-outlined { font-size: 16px !important; }
.icon-btn:hover:not(.danger) { background: var(--primary-container); color: #fff; }
.icon-btn.danger:hover { background: var(--error); color: #fff; }

/* Dialog */
.dialog-overlay {
  position: fixed; inset: 0; z-index: 500;
  background: rgba(0,0,0,0.65); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center;
}
.dialog { width: 520px; max-height: 85vh; overflow-y: auto; border-radius: 16px; }
.dialog-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 24px 16px; border-bottom: 1px solid rgba(66,70,86,0.08);
}
.dialog-header h3 { font-size: 16px; font-weight: 800; color: #fff; text-transform: uppercase; letter-spacing: 0.03em; }
.close-btn {
  width: 32px; height: 32px; border-radius: 8px; border: none;
  background: var(--bg-surface-high); color: var(--on-surface-variant);
  cursor: pointer; display: flex; align-items: center; justify-content: center;
}
.close-btn .material-symbols-outlined { font-size: 18px !important; }
.dialog-body { padding: 24px; display: flex; flex-direction: column; gap: 14px; }
.form-field { display: flex; flex-direction: column; gap: 6px; }
.form-field.full { grid-column: span 2; }
.form-field label {
  font-size: 10px; font-weight: 700; color: var(--on-surface-variant);
  text-transform: uppercase; letter-spacing: 0.08em;
}
.form-field input,
.form-field select,
.form-field textarea {
  background: var(--bg-base); border: 1px solid var(--outline-variant);
  border-radius: 8px; padding: 10px 12px; color: #fff; font-size: 13px;
  outline: none; font-family: var(--font-body);
}
.form-field select option { background: var(--bg-surface-container); }
.form-field textarea { resize: vertical; }

.host-select-area {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px; border: 1px dashed var(--outline-variant); border-radius: 8px;
  background: var(--bg-base);
}
.placeholder-text { font-size: 12px; color: var(--outline); }
.btn-small {
  padding: 6px 14px; border-radius: 6px; border: none;
  background: var(--primary-container); color: #fff; font-size: 11px;
  font-weight: 600; cursor: pointer; display: flex; align-items: center; gap: 4px;
}

.dialog-footer {
  display: flex; justify-content: flex-end; gap: 10px;
  padding: 16px 24px; border-top: 1px solid rgba(66,70,86,0.08);
}
.btn-cancel {
  padding: 9px 22px; border-radius: 8px; border: 1px solid rgba(66,70,86,0.15);
  background: transparent; color: var(--on-surface-variant); font-size: 12px; font-weight: 600; cursor: pointer;
}
.btn-confirm {
  padding: 9px 28px; border-radius: 8px; border: none;
  color: var(--on-primary-container); font-size: 12px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.06em; cursor: pointer;
  box-shadow: 0 4px 15px rgba(15,98,254,0.25);
}
</style>
