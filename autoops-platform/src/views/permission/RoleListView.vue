<template>
  <div class="role-manage-page">
    <div class="page-header">
      <!-- <div>
        <h1 class="page-title">{{ t('roleManage.title') }}</h1>
        <p class="page-subtitle">{{ t('roleManage.subtitle') }}</p>
      </div> -->
      <div class="header-actions">
        <button class="action-btn primary litho-gradient" @click="showCreateDialog = true">
          <span class="material-symbols-outlined">add</span>
          {{ t('roleManage.createRole') }}
        </button>
      </div>
    </div>

    <!-- Role Cards Grid -->
    <div class="roles-grid">
      <div v-for="(role, idx) in roles" :key="idx" :class="['role-card glass-card', { 'card-featured': role.featured }]">
        <div class="card-top">
          <div class="role-badge-row">
            <span :class="['role-indicator', role.color]"></span>
            <h3 class="role-name">{{ role.name }}</h3>
            <span v-if="role.builtIn" class="built-in-tag font-mono">BUILT-IN</span>
          </div>
          <p class="role-desc">{{ role.desc }}</p>
        </div>

        <div class="card-stats">
          <div class="stat" v-for="(s, sidx) in role.stats" :key="sidx">
            <span class="stat-num font-mono">{{ s.val }}</span>
            <span class="stat-lbl">{{ s.lbl }}</span>
          </div>
        </div>

        <div class="perm-section">
          <div class="perm-header">
            <span class="perm-title">{{ t('roleManage.permissions') }}</span>
            <button class="edit-perm-btn font-mono" @click="showPermEditor = true">
              {{ t('roleManage.editPerms') }} &rarr;
            </button>
          </div>
          <div class="perm-chips">
            <span v-for="(p, pidx) in role.perms" :key="pidx" class="perm-chip">{{ p }}</span>
          </div>
        </div>

        <div class="card-footer">
          <div class="member-preview">
            <img v-for="(m, midx) in role.members" :key="midx"
                 :src="m" class="member-avatar-sm" />
            <span v-if="role.memberCount > 3" class="more-count font-mono">+{{ role.memberCount - 3 }}</span>
          </div>
          <div class="card-actions">
            <button class="icon-btn" :title="t('common.edit')"><span class="material-symbols-outlined">edit</span></button>
            <button v-if="!role.builtIn" class="icon-btn danger" :title="t('common.delete')"><span class="material-symbols-outlined">delete</span></button>
            <button class="icon-btn" :title="t('roleManage.duplicate')"><span class="material-symbols-outlined">content_copy</span></button>
          </div>
        </div>
      </div>
    </div>

    <!-- Permission Matrix Section -->
    <div class="matrix-section glass-card">
      <div class="section-head">
        <h2>{{ t('roleManage.permMatrixTitle') }}</h2>
        <span class="section-desc">{{ t('roleManage.permMatrixDesc') }}</span>
      </div>
      <div class="matrix-table-wrap">
        <table class="matrix-table">
          <thead>
            <tr>
              <th class="module-col">{{ t('roleManage.matrixModule') }}</th>
              <th v-for="r in roles" :key="r.name" class="role-col">{{ r.name }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(mod, midx) in modules" :key="midx">
              <td class="module-cell">
                <span class="material-symbols-outlined module-icon">{{ mod.icon }}</span>
                {{ mod.label }}
              </td>
              <td v-for="r in roles" :key="r.name + mod.key" class="perm-cell">
                <span
                  v-for="(level, lidx) in permLevels" :key="lidx"
                  :class="['perm-dot', level, { active: getPerm(r, mod.key) >= lidx }]"
                  :title="getPermLabel(lidx)"
                ></span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create Role Dialog -->
    <div v-if="showCreateDialog" class="dialog-overlay" @click.self="showCreateDialog = false">
      <div class="dialog glass-card">
        <div class="dialog-header">
          <h3>{{ t('roleManage.createRole') }}</h3>
          <button class="close-btn" @click="showCreateDialog = false"><span class="material-symbols-outlined">close</span></button>
        </div>
        <div class="dialog-body">
          <div class="form-field">
            <label>{{ t('roleManage.formRoleName') }}</label>
            <input type="text" placeholder="" />
          </div>
          <div class="form-field">
            <label>{{ t('roleManage.formDesc') }}</label>
            <textarea rows="3" placeholder=""></textarea>
          </div>
          <div class="form-field">
            <label>{{ t('roleManage.formBaseRole') }}</label>
            <select><option>无（从零开始）</option><option>基于普通运维</option><option>基于高级运维</option></select>
          </div>
          <div class="form-field full">
            <label>{{ t('roleManage.formCopyFrom') }}</label>
            <div class="copy-options">
              <label v-for="r in roles.filter(r => !r.builtIn)" :key="r.name" class="copy-option">
                <input type="radio" name="base" />
                <span>{{ r.name }}</span>
              </label>
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
const showCreateDialog = ref(false)
const showPermEditor = ref(false)

const roles = [
  {
    name: t('roleManage.roleAdmin'), desc: t('roleManage.roleAdminDesc'),
    color: 'red', builtIn: true, featured: true,
    stats: [{ val: '1', lbl: t('roleManage.statUsers') }, { val: 'ALL', lbl: t('roleManage.statModules') }, { val: 'FULL', lbl: t('roleManage.statLevel') }],
    perms: [t('roleManage.permRead'), t('roleManage.permWrite'), t('roleManage.permDelete'), t('roleManage.permAdmin'), t('roleManage.permAudit')],
    memberCount: 2, members: ['https://ui-avatars.com/api/?name=ZS&background=C84000&color=fff', 'https://ui-avatars.com/api/?name=ZS&background=0F62FE&color=fff'],
    permissions: { assets: 4, scripts: 4, tasks: 4, inspection: 4, audit: 5, security: 5 }
  },
  {
    name: t('roleManage.roleSre'), desc: t('roleManage.roleSreDesc'),
    color: 'blue', builtIn: true, featured: false,
    stats: [{ val: '8', lbl: t('roleManage.statUsers') }, { val: '6/6', lbl: t('roleManage.statModules') }, { val: 'HIGH', lbl: t('roleManage.statLevel') }],
    perms: [t('roleManage.permRead'), t('roleManage.permWrite'), t('roleManage.permDeploy'), t('roleManage.permMonitor')],
    memberCount: 8, members: ['https://ui-avatars.com/api/?name=LS&background=0F62FE&color=fff', 'https://ui-avatars.com/api/?name=ZL&background=0F62FE&color=fff', 'https://ui-avatars.com/api/?name=WJ&background=333&color=fff'],
    permissions: { assets: 3, scripts: 4, tasks: 4, inspection: 3, audit: 2, security: 2 }
  },
  {
    name: t('roleManage.roleOps'), desc: t('roleManage.roleOpsDesc'),
    color: 'yellow', builtIn: true, featured: false,
    stats: [{ val: '12', lbl: t('roleManage.statUsers') }, { val: '4/6', lbl: t('roleManage.statModules') }, { val: 'MID', lbl: t('roleManage.statLevel') }],
    perms: [t('roleManage.permRead'), t('roleManage.permWrite'), t('roleManage.permExecute')],
    memberCount: 12, members: ['https://ui-avatars.com/api/?name=SQ&background=555&color=fff', 'https://ui-avatars.com/api/?name=WJ&background=333&color=fff', 'https://ui-avatars.com/api/?name=XX&background=333&color=fff'],
    permissions: { assets: 2, scripts: 2, tasks: 2, inspection: 2, audit: 1, security: 1 }
  },
  {
    name: t('roleManage.roleDev'), desc: t('roleManage.roleDevDesc'),
    color: 'gray', builtIn: true, featured: false,
    stats: [{ val: '24', lbl: t('roleManage.statUsers') }, { val: '2/6', lbl: t('roleManage.statModules') }, { val: 'LOW', lbl: t('roleManage.statLevel') }],
    perms: [t('roleManage.permRead'), t('roleManage.permView')],
    memberCount: 24, members: ['https://ui-avatars.com/api/?name=WW&background=333&color=fff', 'https://ui-avatars.com/api/?name=ZB&background=333&color=fff', 'https://ui-avatars.com/api/?name=XX&background=555&color=fff'],
    permissions: { assets: 1, scripts: 1, tasks: 0, inspection: 1, audit: 0, security: 0 }
  }
]

const modules = [
  { key: 'assets', label: t('roleManage.modAssets'), icon: 'inventory_2' },
  { key: 'scripts', label: t('roleManage.modScripts'), icon: 'terminal' },
  { key: 'tasks', label: t('roleManage.modTasks'), icon: 'play_circle' },
  { key: 'inspection', label: t('roleManage.modInspection'), icon: 'monitoring' },
  { key: 'audit', label: t('roleManage.modAudit'), icon: 'fact_check' },
  { key: 'security', label: t('roleManage.modSecurity'), icon: 'admin_panel_settings' }
]

const permLevels = ['none', 'read', 'write', 'execute', 'admin']

function getPerm(role, modKey) { return (role.permissions || {})[modKey] || 0 }
function getPermLabel(idx) { return ['', 'Read Only', 'Read + Write', 'Full Execute', 'Admin'][idx] || '' }
</script>

<style scoped>
.role-manage-page { padding: 32px; max-width: 1600px; margin: 0 auto; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title { font-size: 26px; font-weight: 900; color: #fff; letter-spacing: -0.02em; }
.page-subtitle { font-size: 12px; color: var(--on-surface-variant); margin-top: 4px; }

.header-actions { display: flex; gap: 8px; }
.action-btn {
  padding: 9px 20px; border-radius: 8px; border: none;
  font-family: var(--font-label); font-size: 11px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.08em;
  cursor: pointer; display: flex; align-items: center; gap: 6px;
  color: var(--on-primary-container); box-shadow: 0 4px 15px rgba(15,98,254,0.25);
}

/* Role Cards */
.roles-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; margin-top: 24px; }

.role-card {
  border-radius: 16px; padding: 24px;
  transition: all 0.2s ease; display: flex; flex-direction: column; gap: 18px;
}
.role-card:hover { transform: translateY(-2px); box-shadow: 0 12px 40px rgba(0,0,0,0.3); }
.card-featured { border-left: 3px solid var(--tertiary); }

.card-top {}
.role-badge-row { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.role-indicator { width: 10px; height: 10px; border-radius: 50%; }
.role-indicator.red { background: var(--tertiary); box-shadow: 0 0 8px rgba(200,64,0,0.4); }
.role-indicator.blue { background: var(--primary-container); box-shadow: 0 0 8px rgba(15,98,254,0.4); }
.role-indicator.yellow { background: #FFB800; box-shadow: 0 0 8px rgba(255,184,0,0.4); }
.role-indicator.gray { background: var(--outline); }

.role-name { font-size: 15px; font-weight: 800; color: #fff; flex: 1; }
.built-in-tag {
  font-size: 8px; padding: 2px 8px; border-radius: 9999px;
  background: rgba(200,64,0,0.1); color: var(--tertiary);
  text-transform: uppercase; letter-spacing: 0.08em; font-weight: 800;
}

.role-desc { font-size: 11.5px; color: var(--on-surface-variant); line-height: 1.55; }

.card-stats { display: flex; gap: 24px; padding: 14px 0; border-top: 1px solid rgba(66,70,86,0.06); border-bottom: 1px solid rgba(66,70,86,0.06); }
.stat { display: flex; flex-direction: column; gap: 2px; }
.stat-num { font-size: 20px; font-weight: 900; color: #fff; }
.stat-lbl { font-size: 9px; color: var(--on-surface-variant); text-transform: uppercase; letter-spacing: 0.08em; }

.perm-section { display: flex; flex-direction: column; gap: 10px; }
.perm-header { display: flex; justify-content: space-between; align-items: center; }
.perm-title { font-size: 10px; font-weight: 800; color: var(--on-surface-variant); text-transform: uppercase; letter-spacing: 0.1em; }
.edit-perm-btn {
  background: none; border: none; color: var(--primary-container);
  font-size: 10px; cursor: pointer; letter-spacing: 0.04em;
}
.perm-chips { display: flex; flex-wrap: wrap; gap: 6px; }
.perm-chip {
  padding: 4px 10px; border-radius: 5px; font-size: 10px; font-weight: 600;
  background: var(--bg-base); color: var(--on-surface-variant);
}

.card-footer { display: flex; justify-content: space-between; align-items: center; margin-top: auto; }
.member-preview { display: flex; align-items: center; gap: -6px; }
.member-avatar-sm {
  width: 28px; height: 28px; border-radius: 7px;
  border: 2px solid var(--bg-surface-low); object-fit: cover;
  margin-right: -6px;
}
.more-count { font-size: 9px; color: var(--on-surface-variant); margin-left: 6px; }

.card-actions { display: flex; gap: 6px; }
.icon-btn {
  width: 30px; height: 30px; border-radius: 7px; border: none;
  background: var(--bg-surface-high); color: var(--on-surface-variant);
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: all 0.15s;
}
.icon-btn .material-symbols-outlined { font-size: 16px !important; }
.icon-btn:hover:not(.danger) { background: var(--primary-container); color: #fff; }
.icon-btn.danger:hover { background: var(--error); color: #fff; }

/* Matrix */
.matrix-section { margin-top: 32px; border-radius: 16px; padding: 28px; }
.section-head { margin-bottom: 24px; }
.section-head h2 { font-size: 17px; font-weight: 900; color: #fff; text-transform: uppercase; letter-spacing: -0.01em; }
.section-desc { font-size: 11px; color: var(--on-surface-variant); margin-top: 4px; }

.matrix-table-wrap { overflow-x: auto; }
.matrix-table { width: 100%; border-collapse: separate; border-spacing: 0; font-size: 12px; }
.matrix-table th {
  padding: 12px 16px; font-family: var(--font-label); font-size: 9px;
  font-weight: 800; color: var(--on-surface-variant); text-transform: uppercase;
  letter-spacing: 0.1em; background: var(--bg-surface-highest);
  border-bottom: 1px solid rgba(66,70,86,0.1);
}
.module-col { text-align: left; min-width: 160px; }
.role-col { text-align: center; min-width: 120px; }

.matrix-table td { padding: 14px 16px; vertical-align: middle; border-bottom: 1px solid rgba(66,70,86,0.04); }
.module-cell {
  display: flex; align-items: center; gap: 10px; font-weight: 600; color: #fff;
}
.module-icon { font-size: 18px !important; color: var(--primary-container); opacity: 0.6; }

.perm-cell { text-align: center; }
.perm-dot {
  width: 10px; height: 10px; border-radius: 50%; display: inline-block; margin: 0 3px;
  transition: all 0.15s;
}
.perm-dot.none { background: transparent; border: 1px solid rgba(66,70,86,0.15); }
.perm-dot.read { background: rgba(66,70,86,0.3); }
.perm-dot.write { background: rgba(255,184,0,0.5); }
.perm-dot.execute { background: rgba(15,98,254,0.6); }
.perm-dot.admin { background: var(--tertiary); opacity: 0.7; }
.perm-dot.active { transform: scale(1.35); box-shadow: 0 0 6px currentColor; }

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

.dialog-body { padding: 24px; display: flex; flex-direction: column; gap: 16px; }
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

.copy-options { display: flex; gap: 12px; flex-wrap: wrap; }
.copy-option {
  display: flex; align-items: center; gap: 6px; padding: 8px 14px;
  border-radius: 8px; border: 1px solid rgba(66,70,86,0.1); cursor: pointer;
  font-size: 12px; color: var(--on-surface-variant); transition: all 0.15s;
}
.copy-option:hover { border-color: var(--primary-container); color: #fff; }

.dialog-footer {
  display: flex; justify-content: flex-end; gap: 10px;
  padding: 16px 24px; border-top: 1px solid rgba(66,70,86,0.08);
}
.btn-cancel {
  padding: 9px 22px; border-radius: 8px; border: 1px solid rgba(66,70,86,0.15);
  background: transparent; color: var(--on-surface-variant); font-size: 12px;
  font-weight: 600; cursor: pointer;
}
.btn-confirm {
  padding: 9px 28px; border-radius: 8px; border: none;
  color: var(--on-primary-container); font-size: 12px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.06em; cursor: pointer;
  box-shadow: 0 4px 15px rgba(15,98,254,0.25);
}
</style>
