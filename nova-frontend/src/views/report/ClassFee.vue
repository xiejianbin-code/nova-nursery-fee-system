<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 搜索栏 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="月份">
            <el-date-picker
              v-model="searchForm.month"
              type="month"
              placeholder="请选择月份"
              format="YYYY-MM"
              value-format="YYYY-MM"
              style="width: 150px"
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
            <div class="stat-label">班级总数</div>
            <div class="stat-value">{{ statistics.totalClasses || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">在园幼儿</div>
            <div class="stat-value primary">{{ statistics.totalChildren || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">本月收费</div>
            <div class="stat-value success">¥{{ statistics.totalFee || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">人均收费</div>
            <div class="stat-value warning">¥{{ statistics.avgFee || 0 }}</div>
          </div>
        </el-col>
      </el-row>

      <!-- 表格区域 -->
      <div class="table-area">
        <el-table :data="tableData" v-loading="loading" border stripe show-summary :summary-method="getSummaries">
          <el-table-column prop="className" label="班级名称" min-width="120" />
          <el-table-column prop="teacherName" label="班主任" width="100" />
          <el-table-column prop="childCount" label="在园人数" width="100" align="center" />
          <el-table-column prop="educationFee" label="保教费" min-width="120" align="right">
            <template #default="{ row }">
              ¥{{ row.educationFee }}
            </template>
          </el-table-column>
          <el-table-column prop="foodFee" label="伙食费" min-width="120" align="right">
            <template #default="{ row }">
              ¥{{ row.foodFee }}
            </template>
          </el-table-column>
          <el-table-column prop="otherFee" label="其他费用" min-width="120" align="right">
            <template #default="{ row }">
              ¥{{ row.otherFee }}
            </template>
          </el-table-column>
          <el-table-column prop="totalFee" label="合计" min-width="120" align="right">
            <template #default="{ row }">
              <span class="total-fee">¥{{ row.totalFee }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="avgFee" label="人均收费" min-width="120" align="right">
            <template #default="{ row }">
              ¥{{ row.avgFee }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getClassFeeReport, exportClassFeeReport } from '@/api/report'

// 加载状态
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  month: new Date().toISOString().slice(0, 7)
})

// 统计数据
const statistics = ref({})

// 表格数据
const tableData = ref([])

// 获取报表数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getClassFeeReport(searchForm)
    tableData.value = res.data?.list || []
    statistics.value = res.data?.statistics || {}
  } catch (error) {
    console.error('获取报表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  fetchData()
}

// 导出
const handleExport = async () => {
  try {
    const res = await exportClassFeeReport(searchForm)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `班级收费统计报表_${searchForm.month}.xlsx`
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
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    const values = data.map(item => Number(item[column.property]))
    if (!values.every(value => isNaN(value))) {
      sums[index] = '¥' + values.reduce((prev, curr) => {
        const value = Number(curr)
        if (!isNaN(value)) {
          return prev + curr
        } else {
          return prev
        }
      }, 0).toFixed(2)
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

    &.warning {
      color: #e6a23c;
    }
  }
}

.total-fee {
  color: #409eff;
  font-weight: bold;
}
</style>
