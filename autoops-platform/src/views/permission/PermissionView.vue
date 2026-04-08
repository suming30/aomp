<template>
  <div class="permission-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">{{ t('permission.title') }}</h1>
      </div>
      <button class="action-btn primary litho-gradient">
        <span class="material-symbols-outlined">add</span>
        {{ t('permission.createRole') }}
      </button>
    </div>

    <el-tabs v-model="activeTab" class="permission-tabs">
      <el-tab-pane :label="t('permission.userManagement')" name="users">
        <div class="toolbar-row">
          <div class="search-area">
            <span class="material-symbols-outlined search-icon">search</span>
            <input placeholder="Search users..." />
          </div>
          <button class="add-user-btn"><span class="material-symbols-outlined">person_add</span>Add User</button>
        </div>

        <table class="data-table">
          <thead>
            <tr>
              <th>{{ t('permission.userName') }}</th>
              <th>{{ t('permission.email') }}</th>
              <th>{{ t('permission.role') }}</th>
              <th>{{ t('permission.department') }}</th>
              <th>{{ t('permission.lastLogin') }}</th>
              <th>{{ t('permission.userActions') }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(u, idx) in users" :key="idx">
              <td>
                <div class="user-cell">
                  <img :src="u.avatar" class="user-avatar" />
                  <span class="user-name">{{ u.name }}</span>
                </div>
              </td>
              <td class="text-muted">{{ u.email }}</td>
              <td><span :class="['role-badge', u.roleClass]">{{ u.role }}</span></td>
              <td>{{ u.dept }}</td>
              <td class="font-mono text-muted">{{ u.lastLogin }}</td>
              <td>
                <div class="row-actions">
                  <a href="#">{{ t('permission.editUser') }}</a>
                  <a href="#" class="danger">{{ t('permission.deleteUser') }}</a>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </el-tab-pane>

      <el-tab-pane :label="t('permission.roleManagement')" name="roles">
        <div class="roles-grid">
          <div v-for="(r, idx) in roles" :key="idx" class="role-card glass-card">
            <div class="role-header">
              <span :class="['role-dot', r.dotColor]"></span>
              <h4 class="role-name">{{ r.name }}</h4>
              <span class="role-active-badge">{{ t('permission.active') }}</span>
            </div>
            <p class="role-desc">{{ r.desc }}</p>
            <div class="role-perms">
              <span v-for="(perm, pidx) in r.perms" :key="pidx" class="perm-chip">{{ perm }}</span>
            </div>
            <div class="role-footer">
              <span class="member-count font-mono">{{ r.members }} members</span>
              <button class="edit-role-btn">Edit Role</button>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const activeTab = ref('users')

const users = [
  { name: 'zhang_san', email: 'zhang_san@autoops.io', role: 'Super Admin', roleClass: 'role-super', dept: 'Platform Team', lastLogin: '2024-05-24 14:20', avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAc0vS318_7OXcTC2YSdOa7Eq1rsXwYBC_afOGAzgIbZHM6oovFaso4CW94bOfOdQ3XjCbno95XgRapqkLDNgEiuivbWtb5vdKaBza78J1RjOrgpVua3KQZh-yTYvU1Krn3GcrkpqIIfbclkcd79GZKp062J18c-y16-SF16pkVLjMcCS_6H4uZDz5iMntpDUyJeYSq6Q2ARBVMeIgbucEkoBIBZthw4_lMoK86BDSwuCJbBsaAQHtQs3xQeJvk12EjUv_Su4rIbJW7' },
  { name: 'li_si', email: 'li_si@autoops.io', role: 'SRE', roleClass: 'role-sre', dept: 'SRE Team', lastLogin: '2024-05-24 13:45', avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBdJ9It8bcAmloKGW8_QVDC-w-Qpxe8JEt6DXVTqYIiyoEIQYJTtGzMpz-qGaR4mj0IG_0Qmo_t3mLoC_4XOcGqi66iMRBjiG15aDjpOdxgmMA7XcIiCSvIFvhhTTHiyJ3YV2-ZJnLUJYzUViTNeRPZnFYbfDhswhmpIcKWUaeKsC9em6C1xRbtrzqZZJkE2PwvjYqGPLtF9oSNMkIgJJtUDKR4Ep95ZIn8GRIqo_2O1CpPPtcgvARY8xotAmEiPY7AZTWzu2zRqa4c' },
  { name: 'wang_wu', email: 'wang_wu@autoops.io', role: 'Viewer', roleClass: 'role-viewer', dept: 'Dev Team', lastLogin: '2024-05-23 09:12', avatar: 'https://ui-avatars.com/api/?name=WW&background=333&color=fff' },
  { name: 'zhao_liu', email: 'zhao_liu@autoops.io', role: 'SRE', roleClass: 'role-sre', dept: 'SRE Team', lastLogin: '2024-05-24 11:30', avatar: 'https://ui-avatars.com/api/?name=ZL&background=333&color=fff' },
  { name: 'sun_qi', email: 'sun_qi@autoops.io', role: 'Viewer', roleClass: 'role-viewer', dept: 'QA Team', lastLogin: '2024-05-22 16:00', avatar: 'https://ui-avatars.com/api/?name=SQ&background=333&color=fff' }
]

const roles = [
  { name: t('permission.superAdmin'), desc: 'Full system access with all administrative privileges.', dotColor: 'dot-red', perms: ['Read', 'Write', 'Delete', 'Admin', 'Audit'], members: 2 },
  { name: t('permission.sre'), desc: 'Site Reliability Engineering access for deployment and monitoring.', dotColor: 'dot-blue', perms: ['Read', 'Write', 'Deploy', 'Monitor'], members: 8 },
  { name: t('permission.viewer'), desc: 'Read-only access to view dashboards and reports.', dotColor: 'dot-gray', perms: ['Read'], members: 24 }
]
</script>

<style scoped>
.permission-page {
  padding: 32px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-title {
  font-size: 28px; font-weight: 900; color: #fff;
  text-transform: uppercase; letter-spacing: -0.02em;
}

.header-actions { display: flex; gap: 8px; }

.action-btn {
  padding: 10px 20px; border-radius: 8px; border: none;
  font-family: var(--font-label);
  font-size: 11px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.08em;
  cursor: pointer; display: flex; align-items: center; gap: 6px;
  color: var(--on-primary-container);
  box-shadow: 0 4px 15px rgba(15, 98, 254, 0.25);
}

.permission-tabs { margin-top: 24px; }

.toolbar-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-area {
  display: flex;
  align-items: center;
  background: var(--bg-surface-low);
  border-radius: 9999px;
  padding: 8px 16px;
  min-width: 260px;
  border: 1px solid rgba(66, 70, 86, 0.08);
}
.search-icon { font-size: 16px; color: var(--outline); }
.search-area input {
  background: transparent; border: none; outline: none;
  color: #fff; padding-left: 10px; font-size: 12px; flex: 1;
}

.add-user-btn {
  padding: 8px 16px;
  background: var(--bg-surface-high);
  border: 1px solid rgba(66, 70, 86, 0.1);
  border-radius: 8px;
  color: #fff;
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
.add-user-btn:hover { background: var(--bg-bright); }

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

.user-cell { display: flex; align-items: center; gap: 10px; }

.user-avatar {
  width: 30px; height: 30px;
  border-radius: 50%;
  object-fit: cover;
}
.user-name { font-weight: 600; }

.text-muted { color: var(--on-surface-variant); }

.role-badge {
  font-size: 10px;
  font-weight: 700;
  padding: 3px 10px;
  border-radius: 9999px;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}
.role-super { background: rgba(200, 64, 0, 0.12); color: var(--tertiary); }
.role-sre { background: rgba(15, 98, 254, 0.12); color: var(--primary-container); }
.role-viewer { background: rgba(53, 53, 52, 0.6); color: var(--on-surface-variant); }

.row-actions { display: flex; gap: 12px; }
.row-actions a {
  font-size: 11px;
  color: var(--primary-container);
  text-decoration: none;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}
.row-actions a.danger { color: var(--error); }
.row-actions a:hover { text-decoration: underline; }

.roles-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 8px;
}

.role-card {
  border-radius: 14px;
  padding: 24px;
  transition: transform 0.2s;
}
.role-card:hover { transform: translateY(-2px); }

.role-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.role-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
}
.dot-red { background: var(--tertiary); }
.dot-blue { background: var(--primary-container); }
.dot-gray { background: var(--outline); }

.role-name {
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  flex: 1;
}

.role-active-badge {
  font-size: 8px;
  font-weight: 800;
  padding: 2px 8px;
  border-radius: 9999px;
  background: rgba(15, 98, 254, 0.1);
  color: var(--primary-container);
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.role-desc {
  font-size: 11px;
  color: var(--on-surface-variant);
  line-height: 1.5;
  margin-bottom: 14px;
}

.role-perms {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 16px;
}

.perm-chip {
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 600;
  background: var(--bg-surface-high);
  color: var(--on-surface-variant);
}

.role-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 14px;
  border-top: 1px solid rgba(66, 70, 86, 0.08);
}

.member-count { font-size: 11px; color: var(--on-surface-variant); }

.edit-role-btn {
  padding: 5px 14px;
  background: transparent;
  border: 1px solid var(--outline-variant);
  border-radius: 6px;
  color: var(--on-surface-variant);
  font-size: 10px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s;
}
.edit-role-btn:hover { border-color: var(--primary-container); color: var(--primary-container); }
</style>
