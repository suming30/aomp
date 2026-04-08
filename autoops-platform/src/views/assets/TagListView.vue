<template>
  <div class="tag-manage-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">{{ t('tagManage.title') }}</h1>
        <p class="page-subtitle">{{ t('tagManage.subtitle') }}</p>
      </div>
      <button class="action-btn primary litho-gradient" @click="showCreateDialog = true">
        <span class="material-symbols-outlined">add</span>
        {{ t('tagManage.createTag') }}
      </button>
    </div>

    <!-- Stats -->
    <div class="stats-row glass-card">
      <div class="stat" v-for="(s, idx) in stats" :key="idx">
        <span class="stat-num font-mono">{{ s.val }}</span>
        <span class="stat-lbl">{{ s.lbl }}</span>
      </div>
    </div>

    <!-- Tag Grid -->
    <div class="tag-grid glass-card">
      <div v-for="(tag, idx) in tags" :key="idx" :class="['tag-card', { selected: tag.selected }]">
        <div class="tag-color-bar" :style="{ background: tag.color }"></div>
        <div class="tag-body">
          <div class="tag-top">
            <span class="tag-name" :style="{ color: tag.color }">{{ tag.name }}</span>
            <span class="tag-count font-mono">{{ tag.hostCount }} {{ t('tagManage.hosts') }}</span>
          </div>
          <p class="tag-desc">{{ tag.desc }}</p>
          <div class="tag-meta">
            <span class="meta-item"><span class="material-symbols-outlined" style="font-size:13px;">person</span> {{ tag.creator }}</span>
            <span class="meta-item font-mono text-sm">{{ tag.createdAt }}</span>
          </div>
          <div class="tag-actions">
            <button class="tag-btn" @click="handleEdit(tag)"><span class="material-symbols-outlined">edit</span></button>
            <button class="tag-btn danger" @click="handleDelete(tag)"><span class="material-symbols-outlined">delete</span></button>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Dialog -->
    <div v-if="showCreateDialog" class="dialog-overlay" @click.self="showCreateDialog = false">
      <div class="dialog glass-card">
        <div class="dialog-header">
          <h3>{{ t('tagManage.createTag') }}</h3>
          <button class="close-btn" @click="showCreateDialog = false"><span class="material-symbols-outlined">close</span></button>
        </div>
        <div class="dialog-body">
          <div class="form-field">
            <label>{{ t('tagManage.formName') }}</label>
            <input type="text" placeholder="" />
          </div>
          <div class="form-field">
            <label>{{ t('tagManage.formColor') }}</label>
            <div class="color-picker">
              <button
                v-for="(c, cidx) in colorOptions" :key="cidx"
                :class="['color-dot', { active: selectedColor === c }]"
                :style="{ background: c }"
                @click="selectedColor = c"
              ></button>
              <input type="color" v-model="selectedColor" class="color-input" />
            </div>
          </div>
          <div class="form-field full">
            <label>{{ t('tagManage.formDesc') }}</label>
            <textarea rows="2" placeholder=""></textarea>
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
const selectedColor = ref('#0F62FE')

const stats = [
  { val: '12', lbl: t('tagManage.statTotal') },
  { val: '148', lbl: t('tagManage.statTaggedHosts') },
  { val: '3.2', lbl: t('tagManage.statAvgPerHost') }
]

const colorOptions = ['#0F62FE', '#C84000', '#FFB800', '#24A148', '#8A3FFC', '#009D9A', '#EE5386', '#491D8B']

const tags = [
  { name: '生产环境', desc: '生产环境核心业务服务器', color: '#C84000', hostCount: 48, creator: 'zhang_san', createdAt: '2025-01-15' },
  { name: '测试环境', desc: '测试/预发布环境服务器', color: '#24A148', hostCount: 22, creator: 'li_si', createdAt: '2025-02-20' },
  { name: 'K8S集群', desc: 'Kubernetes 集群节点', color: '#0F62FE', hostCount: 35, creator: 'zhang_san', createdAt: '2025-03-10' },
  { name: '数据库', desc: 'MySQL/PostgreSQL/Redis 数据库服务', color: '#8A3FFC', hostCount: 14, creator: 'li_si', createdAt: '2025-04-05' },
  { name: '北京机房', desc: '北京 IDC 机房物理机', color: '#009D9A', hostCount: 56, creator: 'admin', createdAt: '2025-01-08' },
  { name: '上海机房', desc: '上海 IDC 机房物理机', color: '#FFB800', hostCount: 32, creator: 'admin', createdAt: '2025-02-18' },
  { name: 'CentOS7', desc: '操作系统为 CentOS 7 的主机', color: '#491D8B', hostCount: 68, creator: 'system', createdAt: '2025-06-01' },
  { name: '高可用', desc: 'HA 高可用架构关键节点', color: '#EE5386', hostCount: 16, creator: 'zhang_san', createdAt: '2025-07-22' },
  { name: '监控采集', desc: 'Prometheus/Grafana 监控采集点', color: '#0F62FE', hostCount: 8, creator: 'li_si', createdAt: '2025-09-10' }
]

function handleEdit(tag) {}
function handleDelete(tag) {}
</script>

<style scoped>
.tag-manage-page { padding: 28px; max-width: 1400px; margin: 0 auto; }
.page-title { font-size: 26px; font-weight: 900; color: #fff; letter-spacing: -0.02em; }
.page-subtitle { font-size: 12px; color: var(--on-surface-variant); margin-top: 4px; }

.action-btn {
  padding: 9px 20px; border-radius: 8px; border: none;
  font-family: var(--font-label); font-size: 11px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.08em;
  cursor: pointer; display: flex; align-items: center; gap: 6px;
  color: var(--on-primary-container); box-shadow: 0 4px 15px rgba(15,98,254,0.25);
}

.stats-row {
  display: flex; gap: 40px; padding: 18px 24px; margin-top: 20px; align-items: center;
}
.stat { display: flex; flex-direction: column; gap: 2px; }
.stat-num { font-size: 28px; font-weight: 900; color: #fff; }
.stat-lbl { font-size: 10px; color: var(--on-surface-variant); text-transform: uppercase; letter-spacing: 0.08em; }

.tag-grid {
  margin-top: 16px; padding: 24px;
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px;
}

.tag-card {
  display: flex; border-radius: 12px; overflow: hidden;
  border: 1px solid transparent; transition: all 0.2s ease;
  background: var(--bg-base);
}
.tag-card:hover { border-color: var(--outline-variant); transform: translateY(-2px); box-shadow: 0 8px 28px rgba(0,0,0,0.2); }
.tag-card.selected { border-color: var(--primary-container); }

.tag-color-bar { width: 4px; flex-shrink: 0; }
.tag-body { flex: 1; padding: 16px 18px; display: flex; flex-direction: column; gap: 10px; }

.tag-top { display: flex; justify-content: space-between; align-items: center; }
.tag-name { font-size: 14px; font-weight: 800; }
.tag-count { font-size: 10px; color: var(--on-surface-variant); opacity: 0.7; }

.tag-desc { font-size: 11px; color: var(--on-surface-variant); line-height: 1.45; }

.tag-meta { display: flex; gap: 16px; align-items: center; }
.meta-item {
  display: flex; align-items: center; gap: 4px; font-size: 10px;
  color: var(--outline);
}
.meta-item .material-symbols-outlined { font-size: 13px !important; opacity: 0.6; }
.text-sm { font-size: 10px !important; }

.tag-actions { display: flex; justify-content: flex-end; gap: 6px; margin-top: auto; }
.tag-btn {
  width: 26px; height: 26px; border-radius: 6px; border: none;
  background: var(--bg-surface-high); color: var(--on-surface-variant);
  cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.15s;
}
.tag-btn .material-symbols-outlined { font-size: 14px !important; }
.tag-btn:hover:not(.danger) { background: var(--primary-container); color: #fff; }
.tag-btn.danger:hover { background: var(--error); color: #fff; }

/* Dialog */
.dialog-overlay {
  position: fixed; inset: 0; z-index: 500;
  background: rgba(0,0,0,0.65); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center;
}
.dialog { width: 480px; border-radius: 16px; }
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
.form-field textarea {
  background: var(--bg-base); border: 1px solid var(--outline-variant);
  border-radius: 8px; padding: 10px 12px; color: #fff; font-size: 13px;
  outline: none; font-family: var(--font-body);
}
.form-field textarea { resize: vertical; }

.color-picker { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
.color-dot {
  width: 28px; height: 28px; border-radius: 50%; cursor: pointer;
  border: 2px solid transparent; transition: all 0.15s;
}
.color-dot:hover { transform: scale(1.15); }
.color-dot.active { border-color: #fff; box-shadow: 0 0 8px currentColor; }
.color-input { width: 28px; height: 28px; border: none; border-radius: 50%; cursor: pointer; padding: 0; }

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
