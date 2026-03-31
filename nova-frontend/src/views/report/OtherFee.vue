<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 搜索栏 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="费用类型">
            <el-select v-model="searchForm.feeType" placeholder="请选择费用类型" clearable>
              <el-option label="教材费" value="教材费" />
              <el-option label="活动费" value="活动费" />
              <el-option label="保险费" value="保险费" />
              <el-option label="校服费" value="校服费" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="日期范围">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 240px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button type="success" @click="handleExport">导出</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stat-cards">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">总收入</div>
            <div class="stat-value success">¥{{ statistics.totalIncome || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">总支出</div>
            <div class="stat-value danger">¥{{ statistics.totalExpense || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">净收入</div>
            <div class="stat-value primary">¥{{ statistics.netIncome || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">记录数</div>
            <div class="stat-value">{{ statistics.recordCount || 0 }}</div>
          </div>
        </el-col>
      </el-row>

      <!-- 表格区域 -->
      <div class="table-area">
        <el-table :data="tableData" v-loading="loading" border stripe show-summary :summary-method="getSummaries">
          <el-table-column prop="feeType" label="费用类型" width="100" />
          <el-table-column prop="feeName" label="费用名称" min-width="150" />
          <el-table-column prop="childName" label="幼儿姓名" width="100" />
          <el-table-column prop="changeType" label="收支类型" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.changeType === '收入' ? 'success' : 'danger'" size="small">
                {{ row.changeType }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" min-width="120" align="right">
            <template #default="{ row }">
              <span :class="row.changeType === '收入' ? 'income' : 'expense'">
                {{ row.changeType === '收入' ? '+' : '-' }}¥{{ row.amount }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="时间" width="180" />
          <el-table-column prop="remark" label="备注" min-width="150" />
        </el-table>

        <!-- 分页 -->
        <div class="pagination-area">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.limit"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="fetchData"
            @current-change="fetchData"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOtherFeeReport, exportOtherFeeReport } from '@/api/report'

// 加载状态
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  feeType: '',
  dateRange: []
})

// 统计数据
const statistics = ref({})

// 表格数据
const tableData = ref([])

// 分页信息
const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

// 获取报表数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      feeType: searchForm.feeType,
      startDate: searchForm.dateRange?.[0] || '',
      endDate: searchForm.dateRange?.[1] || '',
      page: pagination.page,
      limit: pagination.limit
    }
    const res = await getOtherFeeReport(params)
    tableData.value = res.data?.list || []
    pagination.total = res.data?.total || 0
    statistics.value = res.data?.statistics || {}
  } catch (error) {
    console.error('获取报表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

// 导出
const handleExport = async () => {
  try {
    const res = await exportOtherFeeReport(searchForm)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '其他费用收支明细报表.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
  }
}

// 合计行
const getSummaries = (param) => {
  const { columns, data } = param
  const sums = []
  let totalIncome = 0
  let totalExpense = 0

  data.forEach(item => {
    if (item.changeType === '收入') {
      totalIncome += Number(item.amount)
    } else {
      totalExpense += Number(item.amount)
    }
  })

  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    if (column.property === 'amount') {
      sums[index] = `收入: ¥${totalIncome.toFixed(2)} / 支出: ¥${totalExpense.toFixed(2)}`
    } else {
      sums[index] = ''
    }
  })
  return sums
}

// 初始化
onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

  .stat-label {
    font-size: 14px;
    color: #909399;
    margin-bottom: 10px;
  }

  .stat-value {
    font-size: 24px;
    font-weight: bold;
    color: #303133;

    &.primary {
      color: #409eff;
    }

    &.success {
      color: #67c23a;
    }

    &.danger {
      color: #f56c6c;
    }
  }
}

.income {
  color: #67c23a;
  font-weight: 500;
}

.expense {
  color: #f56c6c;
  font-weight: 500;
}

.pagination-area {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
