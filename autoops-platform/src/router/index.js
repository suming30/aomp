import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/LoginView.vue'),
    meta: { title: 'Login', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('../components/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/DashboardView.vue'),
        meta: { title: 'Dashboard', icon: 'dashboard' }
      },
      {
        path: 'assets',
        name: 'Assets',
        redirect: '/assets/list',
        meta: { title: 'Assets', icon: 'inventory_2' },
        children: [
          {
            path: 'list',
            name: 'AssetList',
            component: () => import('../views/assets/AssetListView.vue'),
            meta: { title: 'Host List', icon: 'dns' }
          },
          {
            path: 'groups',
            name: 'GroupList',
            component: () => import('../views/assets/GroupListView.vue'),
            meta: { title: 'Group Management', icon: 'folder' }
          },
          {
            path: 'tags',
            name: 'TagList',
            component: () => import('../views/assets/TagListView.vue'),
            meta: { title: 'Tag Management', icon: 'label' }
          }
        ]
      },
      {
        path: 'assets/:id',
        name: 'AssetDetail',
        component: () => import('../views/assets/AssetDetailView.vue'),
        meta: { title: 'Asset Detail', icon: 'inventory_2', hidden: true }
      },
      {
        path: 'scripts',
        name: 'Scripts',
        component: () => import('../views/scripts/ScriptListView.vue'),
        meta: { title: 'Scripts', icon: 'terminal' }
      },
      {
        path: 'scripts/editor/:id?',
        name: 'ScriptEditor',
        component: () => import('../views/scripts/ScriptEditorView.vue'),
        meta: { title: 'Script Editor', icon: 'terminal', hidden: true }
      },
      {
        path: 'tasks/config',
        name: 'TaskConfig',
        component: () => import('../views/tasks/TaskConfigView.vue'),
        meta: { title: 'Task Config', icon: 'account_tree' }
      },
      {
        path: 'tasks/log/:id',
        name: 'TaskLog',
        component: () => import('../views/tasks/TaskLogView.vue'),
        meta: { title: 'Task Log', icon: 'account_tree', hidden: true }
      },
      {
        path: 'tasks/result/:id',
        name: 'TaskResult',
        component: () => import('../views/tasks/TaskResultView.vue'),
        meta: { title: 'Task Result', icon: 'account_tree', hidden: true }
      },
      {
        path: 'inspection/config',
        name: 'InspectionConfig',
        component: () => import('../views/inspection/InspectionConfigView.vue'),
        meta: { title: 'Inspection Config', icon: 'analytics' }
      },
      {
        path: 'inspection/report/:id',
        name: 'InspectionReport',
        component: () => import('../views/inspection/InspectionReportView.vue'),
        meta: { title: 'Inspection Report', icon: 'analytics', hidden: true }
      },
      {
        path: 'history',
        name: 'History',
        component: () => import('../views/history/HistoryListView.vue'),
        meta: { title: 'History', icon: 'history' }
      },
      {
        path: 'permission',
        name: 'Permission',
        redirect: '/permission/users',
        meta: { title: 'Permission', icon: 'admin_panel_settings' },
        children: [
          {
            path: 'users',
            name: 'UserList',
            component: () => import('../views/permission/UserListView.vue'),
            meta: { title: 'User Management', icon: 'people' }
          },
          {
            path: 'roles',
            name: 'RoleList',
            component: () => import('../views/permission/RoleListView.vue'),
            meta: { title: 'Role Management', icon: 'shield' }
          }
        ]
      },
      {
        path: 'audit',
        name: 'Audit',
        component: () => import('../views/audit/AuditLogView.vue'),
        meta: { title: 'Audit Log', icon: 'description' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('../views/settings/SettingsView.vue'),
        meta: { title: 'Settings', icon: 'settings' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('autoops_auth') === 'true'
  if (to.meta.requiresAuth === false || isAuthenticated) {
    next()
  } else {
    next('/login')
  }
})

export default router
