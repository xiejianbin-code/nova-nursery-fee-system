<template>
  <el-sub-menu
    v-if="item.children && item.children.length > 0"
    :index="resolvePath(item.path || item.routePath)"
    :popper-class="'sidebar-popper'"
  >
    <template #title>
      <el-icon v-if="item.icon">
        <component :is="item.icon" />
      </el-icon>
      <span>{{ item.menuName || item.title }}</span>
    </template>
    <sidebar-item
      v-for="child in item.children"
      :key="child.id || child.path"
      :item="child"
      :base-path="resolvePath(item.path || item.routePath)"
    />
  </el-sub-menu>

  <el-menu-item
    v-else
    :index="resolvePath(item.path || item.routePath)"
    v-show="item.hidden !== 1"
  >
    <el-icon v-if="item.icon">
      <component :is="item.icon" />
    </el-icon>
    <template #title>
      <span>{{ item.menuName || item.title }}</span>
    </template>
  </el-menu-item>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  basePath: {
    type: String,
    default: ''
  }
})

const resolvePath = (path) => {
  if (!path) {
    return props.basePath || '/'
  }
  if (path.startsWith('/')) {
    return path
  }
  if (props.basePath.endsWith('/')) {
    return props.basePath + path
  }
  return props.basePath + '/' + path
}
</script>