<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 搜索栏 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="班级">
            <el-select v-model="searchForm.classId" placeholder="请选择班级" clearable>
              <el-option
                v-for="item in classOptions"
                :key="item.id"
                :label="item.className"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="余额范围">
            <el-select v-model="searchForm.balanceRange" placeholder="请选择" clearable>
              <el-option label="余额不足100元" value="0-100" />
              <el-option label="余额100-500元" value="100-500" />
              <el-option label="余额500-1000元" value="500-1000" />
              <el-option label="余额1000元以上" value="1000-" />
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
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">幼儿总数</div>
            <div class="stat-value">{{ statistics.totalCount || 0 }}人</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">总余额</div>
            <div class="stat-value success">¥{{ statistics.totalBalance || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">保教费余额</div>
            <div class="stat-value primary">¥{{ statistics.educationBalance || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">伙食费余额</div>
            <div class="stat-value warning">¥{{ statistics.foodBalance || 0 }}</div>
          </div>
        </el-col>
      </el-row>

      <!-- 表格区域 -->
      <div class="table-area">
        <el-table :data="tableData" v-loading="loading" border stripe show-summary :summary-method="getSummaries">
          <el-table-column prop="childName" label="幼儿姓名" min-width="100" />
          <el-table-column prop="className" label="班级" min-width="100" />
          <el-table-column prop="educationBalance" label="保教费余额" min-width="120" align="right">
            <template #default="{ row }">
              <span class="balance">¥{{ row.educationBalance }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="foodBalance" label="伙食费余额" min-width="120" align="right">
            <template #default="{ row }">
              <span class="balance">¥{{ row.foodBalance }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="otherBalance" label="其他费用余额" min-width="120" align="right">
            <template #default="{ row }">
              <span class="balance">¥{{ row.otherBalance }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="totalBalance" label="总余额" min-width="120" align="right">
            <template #default="{ row }">
              <span class="total-balance">¥{{ row.totalBalance }}</span>
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
import { getBalanceReport, exportBalanceReport } from '@/api/report'
import { getClassOptions } from '@/api/class'

// 加载状态
const loading = ref(false)

// 班级选项
const classOptions = ref([])

// 搜索表单
const searchForm = reactive({
  classId: null,
  balanceRange: ''
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

// 获取班级选项
const fetchClassOptions = async () => {
  try {
    const res = await getClassOptions()
    classOptions.value = res.data || []
  } catch (error) {
    console.error('获取班级选项失败:', error)
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
    const res = await getBalanceReport(params)
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
    const res = await exportBalanceReport(searchForm)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '幼儿余额统计报表.xlsx'
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
  fetchClassOptions()
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

.balance {
  color: #67c23a;
  font-weight: 500;
}

.total-balance {
  color: #409eff;
  font-weight: bold;
}

.pagination-area {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
