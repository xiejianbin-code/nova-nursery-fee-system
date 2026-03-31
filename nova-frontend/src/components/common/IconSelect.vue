<template>
  <el-popover
    v-model:visible="visible"
    placement="bottom-start"
    :width="400"
    trigger="click"
  >
    <template #reference>
      <div class="icon-select-trigger">
        <el-icon v-if="modelValue" :size="18">
          <component :is="modelValue" />
        </el-icon>
        <span v-else class="placeholder">请选择图标</span>
        <el-icon class="arrow-icon" :size="14">
          <ArrowDown />
        </el-icon>
      </div>
    </template>
    <div class="icon-select-content">
      <el-input
        v-model="searchText"
        placeholder="搜索图标"
        clearable
        style="margin-bottom: 12px"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <div class="icon-grid">
        <div
          v-for="icon in filteredIcons"
          :key="icon"
          class="icon-item"
          :class="{ active: modelValue === icon }"
          @click="handleSelect(icon)"
        >
          <el-icon :size="20">
            <component :is="icon" />
          </el-icon>
          <span class="icon-name">{{ icon }}</span>
        </div>
      </div>
    </div>
  </el-popover>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const visible = ref(false)
const searchText = ref('')

const iconList = Object.keys(ElementPlusIconsVue)

const filteredIcons = computed(() => {
  if (!searchText.value) {
    return iconList
  }
  return iconList.filter(icon => 
    icon.toLowerCase().includes(searchText.value.toLowerCase())
  )
})

const handleSelect = (icon) => {
  emit('update:modelValue', icon)
  visible.value = false
}

watch(visible, (val) => {
  if (!val) {
    searchText.value = ''
  }
})
</script>

<style lang="scss" scoped>
.icon-select-trigger {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px;
  height: 32px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  transition: border-color 0.3s;

  &:hover {
    border-color: #409eff;
  }

  .placeholder {
    color: #a8abb2;
    font-size: 14px;
  }

  .arrow-icon {
    color: #a8abb2;
    margin-left: 8px;
  }
}

.icon-select-content {
  max-height: 400px;
  overflow-y: auto;

  .icon-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 8px;
  }

  .icon-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 8px;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      border-color: #409eff;
      background-color: #ecf5ff;
    }

    &.active {
      border-color: #409eff;
      background-color: #ecf5ff;
      color: #409eff;
    }

    .icon-name {
      font-size: 10px;
      color: #909399;
      margin-top: 4px;
      text-align: center;
      word-break: break-all;
      line-height: 1.2;
    }
  }
}
</style>
