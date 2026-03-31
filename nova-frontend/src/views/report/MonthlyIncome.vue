<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 搜索栏 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="年份">
            <el-date-picker
              v-model="searchForm.year"
              type="year"
              placeholder="请选择年份"
              format="YYYY"
              value-format="YYYY"
              style="width: 120px"
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
            <div class="stat-label">年度总收入</div>
            <div class="stat-value">¥{{ statistics.totalIncome || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">保教费收入</div>
            <div class="stat-value primary">¥{{ statistics.educationIncome || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">伙食费收入</div>
            <div class="stat-value success">¥{{ statistics.foodIncome || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">其他收入</div>
            <div class="stat-value warning">¥{{ statistics.otherIncome || 0 }}</div>
          </div>
        </el-col>
      </el-row>

      <!-- 图表区域 -->
      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <div class="chart-container">
            <div class="chart-title">月度收入趋势</div>
            <div ref="lineChartRef" class="chart"></div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="chart-container">
            <div class="chart-title">收入构成</div>
            <div ref="pieChartRef" class="chart"></div>
          </div>
        </el-col>
      </el-row>

      <!-- 表格区域 -->
      <div class="table-area">
        <el-table :data="tableData" v-loading="loading" border stripe show-summary :summary-method="getSummaries">
          <el-table-column prop="month" label="月份" width="100" />
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
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getMonthlyIncomeReport, exportMonthlyIncomeReport, getIncomeTrend, getFeeComposition } from '@/api/report'
import * as echarts from 'echarts'

// 加载状态
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  year: new Date().getFullYear().toString()
})

// 统计数据
const statistics = ref({})

// 表格数据
const tableData = ref([])

// 图表引用
const lineChartRef = ref(null)
const pieChartRef = ref(null)
let lineChart = null
let pieChart = null

// 获取报表数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getMonthlyIncomeReport(searchForm)
    tableData.value = res.data?.list || []
    statistics.value = res.data?.statistics || {}

    // 获取图表数据
    await fetchChartData()
  } catch (error) {
    console.error('获取报表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取图表数据
const fetchChartData = async () => {
  try {
    // 获取趋势数据
    const trendRes = await getIncomeTrend(searchForm)
    initLineChart(trendRes.data || [])

    // 获取构成数据
    const compositionRes = await getFeeComposition(searchForm)
    initPieChart(compositionRes.data || [])
  } catch (error) {
    console.error('获取图表数据失败:', error)
  }
}

// 初始化折线图
const initLineChart = (data) => {
  if (!lineChartRef.value) return

  if (!lineChart) {
    lineChart = echarts.init(lineChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['保教费', '伙食费', '其他费用']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.month + '月')
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '保教费',
        type: 'line',
        data: data.map(item => item.educationFee)
      },
      {
        name: '伙食费',
        type: 'line',
        data: data.map(item => item.foodFee)
      },
      {
        name: '其他费用',
        type: 'line',
        data: data.map(item => item.otherFee)
      }
    ]
  }

  lineChart.setOption(option)
}

// 初始化饼图
const initPieChart = (data) => {
  if (!pieChartRef.value) return

  if (!pieChart) {
    pieChart = echarts.init(pieChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: ¥{c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        type: 'pie',
        radius: '50%',
        data: [
          { value: data.educationFee || 0, name: '保教费' },
          { value: data.foodFee || 0, name: '伙食费' },
          { value: data.otherFee || 0, name: '其他费用' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }

  pieChart.setOption(option)
}

// 搜索
const handleSearch = () => {
  fetchData()
}

// 导出
const handleExport = async () => {
  try {
    const res = await exportMonthlyIncomeReport(searchForm)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `月度收入汇总报表_${searchForm.year}.xlsx`
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

// 窗口大小变化时重新渲染图表
const handleResize = () => {
  lineChart?.resize()
  pieChart?.resize()
}

// 初始化
onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

// 销毁
onUnmounted(() => {
  lineChart?.dispose()
  pieChart?.dispose()
  window.removeEventListener('resize', handleResize)
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

.chart-row {
  margin-bottom: 20px;
}

.chart-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

  .chart-title {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 15px;
    color: #303133;
  }

  .chart {
    height: 300px;
  }
}

.total-fee {
  color: #409eff;
  font-weight: bold;
}
</style>
