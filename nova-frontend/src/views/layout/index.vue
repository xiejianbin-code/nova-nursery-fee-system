<template>
  <div class="layout-container">
    <el-container>
      <el-aside :width="isCollapse ? '64px' : '210px'" class="layout-aside">
        <div class="logo-container" @click="goHome">
          <div class="logo-icon">
            <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M24 4L42 14V34L24 44L6 34V14L24 4Z" stroke="currentColor" stroke-width="2" fill="none"/>
              <path d="M24 4V44" stroke="currentColor" stroke-width="2"/>
              <path d="M6 14L42 34" stroke="currentColor" stroke-width="2"/>
              <path d="M42 14L6 34" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <span v-show="!isCollapse" class="logo-text">Nova</span>
        </div>
        <Sidebar
          :is-collapse="isCollapse"
          :menus="menuList"
        />
      </el-aside>

      <el-container class="layout-main">
        <el-header class="layout-header">
          <Navbar
            :is-collapse="isCollapse"
            @toggle-collapse="toggleCollapse"
          />
        </el-header>

        <el-main class="layout-content">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'
import useMenuStore from '@/store/modules/menu'

const route = useRoute()
const router = useRouter()
const menuStore = useMenuStore()

const isCollapse = ref(false)

const menuList = computed(() => menuStore.menus)

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const goHome = () => {
  router.push('/dashboard')
}

onMounted(async () => {
  if (!menuStore.isRoutesLoaded) {
    await menuStore.generateRoutes()
  }
})
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
}

.layout-aside {
  background: #1d1e1f;
  transition: width 0.3s;
  overflow: hidden;

  .logo-container {
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    padding: 0 16px;
    background: #18191a;
    cursor: pointer;

    .logo-icon {
      width: 32px;
      height: 32px;
      color: #667eea;

      svg {
        width: 100%;
        height: 100%;
      }
    }

    .logo-text {
      font-size: 18px;
      font-weight: 600;
      color: #fff;
      white-space: nowrap;
    }
  }
}

.layout-main {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.layout-header {
  height: 50px;
  padding: 0;
}

.layout-content {
  flex: 1;
  overflow: auto;
  background: #f5f7fa;
  padding: 16px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
