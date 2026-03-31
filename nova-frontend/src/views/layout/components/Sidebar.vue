<template>
  <div class="sidebar-container">
    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapse"
      :unique-opened="true"
      :collapse-transition="false"
      router
      class="sidebar-menu"
    >
      <template v-for="menu in menus" :key="menu.id || menu.path || menu.routePath">
        <sidebar-item
          :item="menu"
          :base-path="menu.path || menu.routePath"
        />
      </template>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import SidebarItem from './SidebarItem.vue'

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  },
  menus: {
    type: Array,
    default: () => []
  }
})

const route = useRoute()

const activeMenu = computed(() => route.path)
</script>

<style lang="scss" scoped>
.sidebar-container {
  height: calc(100% - 50px);
  overflow-y: auto;
  overflow-x: hidden;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.2);
    border-radius: 3px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }
}

.sidebar-menu {
  border-right: none;
  background: transparent;

  &:not(.el-menu--collapse) {
    width: 210px;
  }

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    color: #a0a0a0;
    height: 48px;
    line-height: 48px;

    &:hover {
      background-color: rgba(255, 255, 255, 0.05);
      color: #fff;
    }
  }

  :deep(.el-menu-item.is-active) {
    background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
    color: #fff;
  }

  :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
    color: #fff;
  }

  :deep(.el-sub-menu .el-menu-item) {
    padding-left: 50px !important;
    min-width: auto;

    &:hover {
      background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
      color: #fff;
    }
  }
}
</style>