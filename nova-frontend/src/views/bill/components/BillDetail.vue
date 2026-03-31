<template>
  <el-dialog
    :model-value="visible"
    title="账单详情"
    width="900px"
    @update:model-value="handleClose"
  >
    <div v-loading="loading">
      <el-descriptions title="基本信息" :column="3" border>
        <el-descriptions-item label="账单编号">{{ detailData.billNo }}</el-descriptions-item>
        <el-descriptions-item label="账单月份">{{ detailData.billMonth }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusMap[detailData.status]" size="small">
            {{ statusText[detailData.status] || detailData.statusDesc }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="幼儿姓名">{{ detailData.childName }}</el-descriptions-item>
        <el-descriptions-item label="幼儿性别">
          {{ detailData.childGender === 'MALE' ? '男' : detailData.childGender === 'FEMALE' ? '女' : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ detailData.childBirthDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ detailData.className || '-' }}</el-descriptions-item>
        <el-descriptions-item label="合同" :span="2">
          <el-link type="primary" @click="goToContract" v-if="detailData.contractId">
            {{ detailData.contractNo }} - {{ detailData.contractName }}
          </el-link>
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>

      <el-descriptions title="应收明细" :column="3" border class="mt-16">
        <el-descriptions-item label="保教费应收">
          <span class="amount">¥{{ detailData.educationFeeReceivable?.toFixed(2) || '0.00' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="伙食费应收">
          <span class="amount">¥{{ detailData.mealFeeReceivable?.toFixed(2) || '0.00' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="其他费用应收">
          <span class="amount">¥{{ detailData.otherFeeReceivable?.toFixed(2) || '0.00' }}</span>
        </el-descriptions-item>
      </el-descriptions>

      <el-descriptions title="应退明细" :column="2" border class="mt-16">
        <el-descriptions-item label="保教费应退">
          <span class="refund">¥{{ detailData.educationFeeRefund?.toFixed(2) || '0.00' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="伙食费应退">
          <span class="refund">¥{{ detailData.mealFeeRefund?.toFixed(2) || '0.00' }}</span>
        </el-descriptions-item>
      </el-descriptions>

      <el-descriptions title="费用汇总" :column="3" border class="mt-16">
        <el-descriptions-item label="减免金额">
          <span class="discount">¥{{ detailData.discountAmount?.toFixed(2) || '0.00' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="实际应交">
          <span class="actual">¥{{ detailData.actualAmount?.toFixed(2) || '0.00' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="实际需缴">
          <span class="due">¥{{ detailData.dueAmount?.toFixed(2) || '0.00' }}</span>
        </el-descriptions-item>
      </el-descriptions>

      <el-descriptions title="确认信息" :column="2" border class="mt-16">
        <el-descriptions-item label="确认时间">{{ detailData.confirmTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="确认人">{{ detailData.confirmUserName || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-descriptions title="其他信息" :column="2" border class="mt-16">
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updateTime || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">账单明细</el-divider>
      <el-table :data="detailData.items || []" border stripe size="small" max-height="300">
        <el-table-column prop="itemTypeDesc" label="费用类型" width="120" />
        <el-table-column prop="feeItem" label="费用名称" min-width="150" />
        <el-table-column prop="amount" label="金额" width="120" align="right">
          <template #default="{ row }">
            <span :class="getRowClass(row.itemType)">
              ¥{{ row.amount?.toFixed(2) || '0.00' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="说明" min-width="150" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>
    </div>

    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getBillDetail } from '@/api/bill'

const router = useRouter()

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  billId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['update:visible'])

const statusMap = {
  'PENDING': 'warning',
  'CONFIRMED': 'success',
  '1': 'warning',
  '2': 'success'
}

const statusText = {
  'PENDING': '待确认',
  'CONFIRMED': '已确认',
  '1': '待确认',
  '2': '已确认'
}

const loading = ref(false)
const detailData = ref({})

watch(
  () => props.visible,
  (newVal) => {
    if (newVal && props.billId) {
      fetchDetail()
    }
  }
)

const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await getBillDetail(props.billId)
    detailData.value = res.data || {}
  } catch (error) {
    console.error('获取账单详情失败:', error)
  } finally {
    loading.value = false
  }
}

const getRowClass = (itemType) => {
  if (itemType === 'EDUCATION_REFUND' || itemType === 'MEAL_REFUND') {
    return 'refund'
  }
  if (itemType === 'DISCOUNT') {
    return 'discount'
  }
  return 'amount'
}

const goToContract = () => {
  if (detailData.value.contractId) {
    emit('update:visible', false)
    router.push({
      path: `/contract/detail/${detailData.value.contractId}`,
      query: { from: 'bill', billId: props.billId }
    })
  }
}

const handleClose = () => {
  emit('update:visible', false)
}
</script>

<style lang="scss" scoped>
.mt-16 {
  margin-top: 16px;
}

.amount {
  color: #f56c6c;
  font-weight: 500;
}

.refund {
  color: #67c23a;
  font-weight: 500;
}

.discount {
  color: #e6a23c;
  font-weight: 500;
}

.actual {
  color: #409eff;
  font-weight: bold;
}

.due {
  color: #409eff;
  font-weight: bold;
  font-size: 16px;
}
</style>
