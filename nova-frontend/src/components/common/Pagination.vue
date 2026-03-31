<template>
  <div class="pagination-container" v-if="total > 0">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="pageSizes"
      :total="total"
      :background="background"
      :layout="layout"
      :small="small"
      :disabled="disabled"
      :hide-on-single-page="hideOnSinglePage"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
    <div v-if="showTotal" class="pagination-total">
      共 {{ total }} 条记录，第 {{ currentPage }} / {{ totalPages }} 页
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

// 定义Props
const props = defineProps({
  // 当前页码
  page: {
    type: Number,
    default: 1
  },
  // 每页条数
  limit: {
    type: Number,
    default: 10
  },
  // 总条数
  total: {
    type: Number,
    default: 0
  },
  // 每页条数选项
  pageSizes: {
    type: Array,
    default: () => [10, 20, 30, 50, 100]
  },
  // 是否使用背景色
  background: {
    type: Boolean,
    default: true
  },
  // 布局
  layout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper'
  },
  // 是否使用小型分页
  small: {
    type: Boolean,
    default: false
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 只有一页时是否隐藏
  hideOnSinglePage: {
    type: Boolean,
    default: false
  },
  // 是否显示总条数信息
  showTotal: {
    type: Boolean,
    default: true
  }
})

// 定义Emits
const emit = defineEmits(['update:page', 'update:limit', 'pagination'])

// 当前页码
const currentPage = computed({
  get() {
    return props.page
  },
  set(val) {
    emit('update:page', val)
  }
})

// 每页条数
const pageSize = computed({
  get() {
    return props.limit
  },
  set(val) {
    emit('update:limit', val)
  }
})

// 总页数
const totalPages = computed(() => {
  return Math.ceil(props.total / props.limit)
})

// 每页条数改变
const handleSizeChange = (val) => {
  emit('pagination', { page: currentPage.value, limit: val })
}

// 当前页码改变
const handleCurrentChange = (val) => {
  emit('pagination', { page: val, limit: pageSize.value })
}
</script>

<style lang="scss" scoped>
.pagination-container {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 16px 0;
  gap: 16px;

  .pagination-total {
    color: #606266;
    font-size: 14px;
    white-space: nowrap;
  }
}

:deep(.el-pagination) {
  .el-pagination__total,
  .el-pagination__sizes,
  .el-pagination__jump {
    margin-right: 8px;
  }
}
</style>
