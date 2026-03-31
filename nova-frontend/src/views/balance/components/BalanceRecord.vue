<template>
  <el-dialog
    :model-value="visible"
    :title="`${childName} - 余额变动记录`"
    width="800px"
    @update:model-value="handleClose"
  >
    <el-table :data="recordList" v-loading="loading" border stripe max-height="500">
      <el-table-column prop="changeType" label="变动类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.changeType === '收入' ? 'success' : 'danger'" size="small">
            {{ row.changeType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="feeType" label="费用类型" width="100" />
      <el-table-column prop="amount" label="变动金额" width="120" align="right">
        <template #default="{ row }">
          <span :class="row.changeType === '收入' ? 'income' : 'expense'">
            {{ row.changeType === '收入' ? '+' : '-' }}¥{{ row.amount }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="balanceBefore" label="变动前余额" width="120" align="right">
        <template #default="{ row }">
          ¥{{ row.balanceBefore }}
        </template>
      </el-table-column>
      <el-table-column prop="balanceAfter" label="变动后余额" width="120" align="right">
        <template #default="{ row }">
          ¥{{ row.balanceAfter }}
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="150" />
      <el-table-column prop="createTime" label="时间" width="180" />
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.limit"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchRecords"
        @current-change="fetchRecords"
      />
    </div>

    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { getBalanceRecords } from '@/api/balance'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  childId: {
    type: Number,
    default: null
  },
  childName: {
    type: String,
    default: ''
  }
})

// Emits
const emit = defineEmits(['update:visible'])

// 加载状态
const loading = ref(false)

// 记录列表
const recordList = ref([])

// 分页信息
const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

// 监听childId变化，获取记录
watch(
  () => props.childId,
  (newVal) => {
    if (newVal && props.visible) {
      pagination.page = 1
      fetchRecords()
    }
  },
  { immediate: true }
)

// 获取变动记录
const fetchRecords = async () => {
  if (!props.childId) return

  loading.value = true
  try {
    const res = await getBalanceRecords({
      childId: props.childId,
      page: pagination.page,
      limit: pagination.limit
    })
    recordList.value = res.data?.list || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取变动记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false)
}
</script>

<style lang="scss" scoped>
.income {
  color: #67c23a;
  font-weight: 500;
}

.expense {
  color: #f56c6c;
  font-weight: 500;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
