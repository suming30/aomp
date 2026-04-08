<template>
  <div class="main-layout">
    <SidebarNav />
    <TopNavBar />
    <TabBar />
    <main class="main-content" :style="{ marginTop: tabBarHeight + 'px' }">
      <router-view v-slot="{ Component, route: r }">
        <transition name="fade" mode="out-in">
          <component :is="Component" :key="r.path" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import SidebarNav from './SidebarNav.vue'
import TopNavBar from './TopNavBar.vue'
import TabBar from './TabBar.vue'

const route = useRoute()
const tabBarHeight = ref(94)

watch(() => route.path, () => {
  window.scrollTo(0, 0)
})
</script>

<style scoped>
.main-layout {
  width: 100%;
  height: 100vh;
}

.main-content {
  margin-left: var(--sidebar-width);
  height: calc(100vh - 94px);
  overflow-y: auto;
  overflow-x: hidden;
  background-color: var(--bg-surface);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
