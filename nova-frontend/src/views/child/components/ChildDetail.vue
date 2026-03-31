<template>
  <el-dialog
    :model-value="visible"
    title="幼儿详情"
    width="700px"
    @update:model-value="handleClose"
  >
    <el-descriptions v-loading="loading" :column="2" border>
      <el-descriptions-item label="幼儿姓名">
        {{ detailData.childName }}
      </el-descriptions-item>
      <el-descriptions-item label="性别">
        {{ detailData.gender === 1 ? '男' : '女' }}
      </el-descriptions-item>
      <el-descriptions-item label="出生日期">
        {{ detailData.birthDate }}
      </el-descriptions-item>
      <el-descriptions-item label="月龄">
        {{ detailData.age }}个月
      </el-descriptions-item>
      <el-descriptions-item label="所属班级">
        {{ detailData.className }}
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag
          :type="detailData.status === 1 ? 'success' : detailData.status === 2 ? 'warning' : 'info'"
          size="small"
        >
          {{ statusMap[detailData.status] || '未知' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="入园日期">
        {{ detailData.entryDate }}
      </el-descriptions-item>
      <el-descriptions-item label="离园日期">
        {{ detailData.leaveDate || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="家长姓名">
        {{ detailData.parentName }}
      </el-descriptions-item>
      <el-descriptions-item label="联系电话">
        {{ detailData.parentPhone }}
      </el-descriptions-item>
      <el-descriptions-item label="家长关系">
        {{ detailData.parentRelation }}
      </el-descriptions-item>
      <el-descriptions-item label="家庭住址" :span="2">
        {{ detailData.address || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">
        {{ detailData.remark || '-' }}
      </el-descriptions-item>
    </el-descriptions>

    <!-- 余额信息 -->
    <el-divider content-position="left">余额信息</el-divider>
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="balance-card">
          <div class="balance-label">保教费余额</div>
          <div class="balance-value">¥{{ detailData.educationBalance || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="balance-card">
          <div class="balance-label">伙食费余额</div>
          <div class="balance-value">¥{{ detailData.foodBalance || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="balance-card">
          <div class="balance-label">其他费用余额</div>
          <div class="balance-value">¥{{ detailData.otherBalance || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="balance-card total">
          <div class="balance-label">总余额</div>
          <div class="balance-value">¥{{ detailData.totalBalance || 0 }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 合同信息 -->
    <el-divider content-position="left">合同信息</el-divider>
    <el-table :data="contractList" border stripe size="small">
      <el-table-column prop="contractNo" label="合同编号" min-width="120" />
      <el-table-column prop="contractName" label="合同名称" min-width="120" />
      <el-table-column prop="courseType" label="课程类型" width="100" />
      <el-table-column prop="servicePeriod" label="服务周期" min-width="180" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="contractStatusMap[row.status]" size="small">
            {{ contractStatusText[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getChildDetail } from '@/api/child'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  childId: {
    type: Number,
    default: null
  }
})

// Emits
const emit = defineEmits(['update:visible'])

// 状态映射
const statusMap = {
  1: '在园',
  0: '离园',
  2: '休学'
}

// 合同状态映射
const contractStatusMap = {
  1: 'success',
  2: 'warning',
  3: 'info',
  4: 'danger'
}

const contractStatusText = {
  1: '生效中',
  2: '待生效',
  3: '已结束',
  4: '已终止'
}

// 加载状态
const loading = ref(false)

// 详情数据
const detailData = ref({})

// 合同列表
const contractList = ref([])

// 监听childId变化，获取详情
watch(
  () => props.childId,
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
    const res = await getChildDetail(props.childId)
    detailData.value = res.data || {}
    contractList.value = res.data?.contracts || []
  } catch (error) {
    console.error('获取幼儿详情失败:', error)
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
.balance-card {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  text-align: center;

  .balance-label {
    font-size: 14px;
    color: #909399;
    margin-bottom: 8px;
  }

  .balance-value {
    font-size: 20px;
    font-weight: bold;
    color: #409eff;
  }

  &.total {
    background: #ecf5ff;

    .balance-value {
      color: #409eff;
    }
  }
}
</style>
