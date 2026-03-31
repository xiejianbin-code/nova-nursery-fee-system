<template>
  <div class="page-container">
    <div class="detail-page">
      <el-card v-loading="loading">
        <template #header>
          <div class="card-header">
            <span class="title">合同详情</span>
            <el-button type="primary" @click="goBack">返回</el-button>
          </div>
        </template>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="合同编号">{{ detailData.contractNo }}</el-descriptions-item>
          <el-descriptions-item label="合同名称">{{ detailData.contractName }}</el-descriptions-item>
          <el-descriptions-item label="幼儿姓名">{{ detailData.childName }}</el-descriptions-item>
          <el-descriptions-item label="课程类型">
            <el-tag :type="detailData.courseType === 'MONTHLY' ? 'primary' : 'success'" size="small">
              {{ detailData.courseType === 'MONTHLY' ? '按月' : '按学期' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="开始日期">{{ detailData.startDate }}</el-descriptions-item>
          <el-descriptions-item label="结束日期">{{ detailData.endDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="合同状态">
            <el-tag :type="statusMap[detailData.status]" size="small">
              {{ statusText[detailData.status] }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
          <el-descriptions-item label="附件" :span="2">
            <el-link v-if="detailData.attachmentUrl" :href="detailData.attachmentUrl" target="_blank" type="primary">
              查看附件
            </el-link>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">年龄段收费标准</el-divider>
        <el-table :data="detailData.feeItems || []" border stripe size="small">
          <el-table-column prop="ageRange" label="年龄段" width="150" />
          <el-table-column prop="educationFee" label="保教费" width="120" align="right">
            <template #default="{ row }">
              ¥{{ row.educationFee?.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="mealFee" label="伙食费" width="120" align="right">
            <template #default="{ row }">
              ¥{{ row.mealFee?.toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>

        <el-divider content-position="left">其他费用</el-divider>
        <el-table :data="detailData.otherFees || []" border stripe size="small">
          <el-table-column prop="feeName" label="费用名称" min-width="150" />
          <el-table-column prop="feeStandard" label="金额" width="120" align="right">
            <template #default="{ row }">
              ¥{{ row.feeStandard?.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="chargeCycle" label="收取周期" width="120">
            <template #default="{ row }">
              {{ getChargeCycleText(row.chargeCycle) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                {{ row.status === 1 ? '启用' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>

        <el-divider content-position="left" v-if="detailData.changeLogs && detailData.changeLogs.length > 0">变更记录</el-divider>
        <el-table :data="detailData.changeLogs || []" border stripe size="small" v-if="detailData.changeLogs && detailData.changeLogs.length > 0">
          <el-table-column prop="changeType" label="变更类型" width="120">
            <template #default="{ row }">
              {{ getChangeTypeText(row.changeType) }}
            </template>
          </el-table-column>
          <el-table-column prop="changeTime" label="变更时间" width="180" />
          <el-table-column prop="changeReason" label="变更原因" min-width="200" />
          <el-table-column prop="operatorName" label="操作人" width="120" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getContractDetail } from '@/api/contract'

const route = useRoute()
const router = useRouter()

const statusMap = {
  1: 'success',
  2: 'info',
  3: 'warning',
  4: 'danger'
}

const statusText = {
  1: '生效中',
  2: '已到期',
  3: '已变更',
  4: '已终止'
}

const chargeCycleText = {
  'ONE_TIME': '一次性',
  'ONCE': '一次性',
  'MONTHLY': '按月',
  'QUARTERLY': '按季度',
  'SEMESTER': '按学期',
  'YEARLY': '按年'
}

const changeTypeText = {
  'RENEW': '续签',
  'CHANGE': '变更',
  'TERMINATE': '终止'
}

const loading = ref(false)
const detailData = ref({})

const getChargeCycleText = (cycle) => {
  return chargeCycleText[cycle] || cycle
}

const getChangeTypeText = (type) => {
  return changeTypeText[type] || type
}

const fetchDetail = async () => {
  const id = route.params.id
  if (!id) return

  loading.value = true
  try {
    const res = await getContractDetail(id)
    detailData.value = res.data || {}
  } catch (error) {
    console.error('获取合同详情失败:', error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  const from = route.query.from
  const billId = route.query.billId
  if (from === 'bill' && billId) {
    router.push('/bill/list')
  } else {
    router.push('/contract/list')
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style lang="scss" scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .title {
    font-size: 18px;
    font-weight: bold;
  }
}
</style>
