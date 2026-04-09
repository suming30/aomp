<template>
  <div class="tab-bar" v-if="tabs.length > 0">
    <div class="tab-list" ref="tabListRef">
      <div
        v-for="tab in tabs"
        :key="tab.path"
        :class="['tab-item', { active: activePath === tab.path }]"
        @click="goToTab(tab.path)"
      >
        <span v-if="tab.icon" class="material-symbols-outlined tab-icon">{{ tab.icon }}</span>
        <span class="tab-title">{{ tab.title }}</span>
        <span
          v-if="tab.closable"
          class="tab-close"
          @click.stop="closeTab(tab.path)"
          title="Close Tab"
        >
          <span class="material-symbols-outlined" style="font-size: 13px;">close</span>
        </span>
      </div>
    </div>

    <div class="tab-actions" v-if="hasClosableTabs">
      <button class="tab-action-btn" @click="closeAllTabs()" title="Close All">
        <span class="material-symbols-outlined" style="font-size: 15px;">close_all</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { useTabs } from '../../composables/useTabs.js'

const {
  tabs,
  activePath,
  hasClosableTabs,
  closeTab,
  closeOtherTabs,
  closeAllTabs,
  goToTab
} = useTabs()
</script>

<style scoped>
.tab-bar {
  position: fixed;
  top: var(--topbar-height);
  left: var(--sidebar-width);
  right: 0;
  height: 40px;
  background-color: var(--bg-surface-low);
  border-bottom: 1px solid rgba(66, 70, 86, 0.1);
  z-index: 40;
  display: flex;
  align-items: center;
  padding: 0 12px;
  gap: 8px;
}

.tab-list {
  display: flex;
  gap: 3px;
  height: 100%;
  align-items: center;
  flex: 1;
  overflow-x: auto;
  overflow-y: hidden;
}
.tab-list::-webkit-scrollbar { height: 0; }

.tab-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 14px;
  height: 32px;
  border-radius: 7px 7px 2px 2px;
  font-size: 11.5px;
  font-weight: 500;
  color: var(--on-surface-variant);
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.18s ease;
  background: transparent;
  border: 1px solid transparent;
  border-bottom: none;
  user-select: none;
  flex-shrink: 0;
}

.tab-item:hover {
  background-color: rgba(255,255,255,0.04);
  color: #fff;
}

.tab-item.active {
  background-color: var(--bg-surface);
  color: var(--primary-container);
  font-weight: 700;
  border-color: rgba(66,70,86,0.1);
  box-shadow: 0 -2px 10px rgba(0,0,0,0.06);
}

.tab-icon {
  font-size: 14px !important;
  opacity: 0.7;
}
.tab-item.active .tab-icon {
  opacity: 1;
}

.tab-title {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  letter-spacing: 0.01em;
}

.tab-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 17px;
  height: 17px;
  border-radius: 4px;
  opacity: 0;
  transition: all 0.15s ease;
  color: inherit;
  margin-left: 2px;
  flex-shrink: 0;
}

.tab-item:hover .tab-close {
  opacity: 0.5;
}

.tab-close:hover {
  opacity: 1 !important;
  background-color: rgba(255,180,171,0.15) !important;
  color: var(--error) !important;
  border-radius: 4px;
}

/* Right side actions */
.tab-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
  padding-left: 8px;
  border-left: 1px solid rgba(66,70,86,0.08);
}

.tab-action-btn {
  height: 28px;
  padding: 0 12px;
  border-radius: 6px;
  border: none;
  background: var(--bg-surface-high);
  color: var(--on-surface-variant);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  transition: all 0.15s ease;
  font-family: var(--font-label);
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.tab-action-btn:hover {
  background: linear-gradient(135deg, #0F62FE 0%, #4589FF 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(15, 98, 254, 0.25);
}

.tab-action-btn .material-symbols-outlined {
  font-size: 16px !important;
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 20;
}
</style>
