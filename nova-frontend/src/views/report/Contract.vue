<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 搜索栏 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="合同状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="生效中" :value="1" />
              <el-option label="待生效" :value="2" />
              <el-option label="已结束" :value="3" />
              <el-option label="已终止" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="课程类型">
            <el-select v-model="searchForm.courseType" placeholder="请选择课程类型" clearable>
              <el-option
                v-for="item in courseTypes"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button type="success" @click="handleExport">导出</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stat-cards">
        <el-col :span="4">
          <div class="stat-card">
            <div class="stat-label">合同总数</div>
            <div class="stat-value">{{ statistics.totalCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card">
            <div class="stat-label">生效中</div>
            <div class="stat-value success">{{ statistics.activeCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card">
            <div class="stat-label">待生效</div>
            <div class="stat-value warning">{{ statistics.pendingCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card">
            <div class="stat-label">已结束</div>
            <div class="stat-value info">{{ statistics.endedCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card">
            <div class="stat-label">已终止</div>
            <div class="stat-value danger">{{ statistics.terminatedCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card">
            <div class="stat-label">合同总金额</div>
            <div class="stat-value primary">¥{{ statistics.totalAmount || 0 }}</div>
          </div>
        </el-col>
      </el-row>

      <!-- 表格区域 -->
      <div class="table-area">
        <el-table :data="tableData" v-loading="loading" border stripe show-summary :summary-method="getSummaries">
          <el-table-column prop="contractNo" label="合同编号" min-width="120" />
          <el-table-column prop="childName" label="幼儿姓名" min-width="100" />
          <el-table-column prop="contractName" label="合同名称" min-width="120" />
          <el-table-column prop="courseType" label="课程类型" width="100" />
          <el-table-column prop="amount" label="合同金额" min-width="120" align="right">
            <template #default="{ row }">
              ¥{{ row.amount }}
            </template>
          </el-table-column>
          <el-table-column prop="startDate" label="开始日期" width="120" />
          <el-table-column prop="endDate" label="结束日期" width="120" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="statusMap[row.status]" size="small">
                {{ statusText[row.status] }}
              </el-tag>
            </template>
          </el-table-column>
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
import { getContractReport, exportContractReport } from '@/api/report'
import { getCourseTypes } from '@/api/contract'

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

// 课程类型
const courseTypes = ref([])

// 搜索表单
const searchForm = reactive({
  status: null,
  courseType: ''
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

// 获取课程类型
const fetchCourseTypes = async () => {
  try {
    const res = await getCourseTypes()
    courseTypes.value = res.data || []
  } catch (error) {
    console.error('获取课程类型失败:', error)
  }
}

// 获取报表数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      page: pagination.page,
      limit: pagination.limit
    }
    const res = await getContractReport(params)
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
    const res = await exportContractReport(searchForm)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '合同管理统计报表.xlsx'
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
    if (column.property === 'amount') {
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
      }
    } else {
      sums[index] = ''
    }
  })
  return sums
}

// 初始化
onMounted(() => {
  fetchCourseTypes()
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

    &.danger {
      color: #f56c6c;
    }

    &.info {
      color: #909399;
    }
  }
}

.pagination-area {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
