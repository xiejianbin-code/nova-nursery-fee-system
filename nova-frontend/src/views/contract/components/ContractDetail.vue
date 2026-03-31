<template>
  <el-dialog
    :model-value="visible"
    title="合同详情"
    width="700px"
    @update:model-value="handleClose"
  >
    <el-descriptions v-loading="loading" :column="2" border>
      <el-descriptions-item label="合同编号">
        {{ detailData.contractNo }}
      </el-descriptions-item>
      <el-descriptions-item label="合同名称">
        {{ detailData.contractName }}
      </el-descriptions-item>
      <el-descriptions-item label="幼儿姓名">
        {{ detailData.childName }}
      </el-descriptions-item>
      <el-descriptions-item label="课程类型">
        {{ detailData.courseType }}
      </el-descriptions-item>
      <el-descriptions-item label="合同金额">
        ¥{{ detailData.amount }}
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="statusMap[detailData.status]" size="small">
          {{ statusText[detailData.status] }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="开始日期">
        {{ detailData.startDate }}
      </el-descriptions-item>
      <el-descriptions-item label="结束日期">
        {{ detailData.endDate }}
      </el-descriptions-item>
      <el-descriptions-item label="保教费">
        ¥{{ detailData.educationFee }}
      </el-descriptions-item>
      <el-descriptions-item label="伙食费">
        ¥{{ detailData.foodFee }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ detailData.createTime }}
      </el-descriptions-item>
      <el-descriptions-item label="创建人">
        {{ detailData.creator }}
      </el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">
        {{ detailData.remark || '-' }}
      </el-descriptions-item>
    </el-descriptions>

    <!-- 变更记录 -->
    <el-divider content-position="left">变更记录</el-divider>
    <el-table :data="changeRecords" border stripe size="small">
      <el-table-column prop="changeType" label="变更类型" width="100" />
      <el-table-column prop="changeContent" label="变更内容" min-width="200" />
      <el-table-column prop="changeTime" label="变更时间" width="180" />
      <el-table-column prop="operator" label="操作人" width="100" />
    </el-table>

    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getContractDetail, getContractChangeRecords } from '@/api/contract'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  contractId: {
    type: Number,
    default: null
  }
})

// Emits
const emit = defineEmits(['update:visible'])

// 状态映射
const statusMap = {
  1: 'success',
  2: 'warning',
  3: 'info',
  4: 'danger'
}

const statusText = {
  1: '生效中',
  2: '待生效',
  3: '已结束',
  4: '已终止'
}

// 加载状态
const loading = ref(false)

// 详情数据
const detailData = ref({})

// 变更记录
const changeRecords = ref([])

// 监听contractId变化，获取详情
watch(
  () => props.contractId,
  (newVal) => {
    if (newVal && props.visible) {
      fetchDetail()
    }
  },
  { immediate: true }
)

// 获取详情
const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await getContractDetail(props.contractId)
    detailData.value = res.data || {}

    // 获取变更记录
    const recordsRes = await getContractChangeRecords(props.contractId)
    changeRecords.value = recordsRes.data || []
  } catch (error) {
    console.error('获取合同详情失败:', error)
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
</style>
