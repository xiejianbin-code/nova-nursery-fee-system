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
          <el-form-item label="欠费金额">
            <el-select v-model="searchForm.amountRange" placeholder="请选择" clearable>
              <el-option label="100元以下" value="0-100" />
              <el-option label="100-500元" value="100-500" />
              <el-option label="500-1000元" value="500-1000" />
              <el-option label="1000元以上" value="1000-" />
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
            <div class="stat-label">欠费人数</div>
            <div class="stat-value danger">{{ statistics.totalCount || 0 }}人</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">欠费总额</div>
            <div class="stat-value danger">¥{{ statistics.totalAmount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">最大欠费</div>
            <div class="stat-value warning">¥{{ statistics.maxAmount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">平均欠费</div>
            <div class="stat-value warning">¥{{ statistics.avgAmount || 0 }}</div>
          </div>
        </el-col>
      </el-row>

      <!-- 表格区域 -->
      <div class="table-area">
        <el-table :data="tableData" v-loading="loading" border stripe>
          <el-table-column prop="childName" label="幼儿姓名" min-width="100" />
          <el-table-column prop="className" label="班级" min-width="100" />
          <el-table-column prop="parentName" label="家长姓名" width="100" />
          <el-table-column prop="parentPhone" label="联系电话" min-width="120" />
          <el-table-column prop="arrearsAmount" label="欠费金额" min-width="120" align="right">
            <template #default="{ row }">
              <span class="arrears">¥{{ row.arrearsAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="arrearsMonths" label="欠费月数" width="100" align="center">
            <template #default="{ row }">
              {{ row.arrearsMonths }}个月
            </template>
          </el-table-column>
          <el-table-column prop="lastPaymentTime" label="最后缴费时间" width="180" />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
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
import { getArrearsReport, exportArrearsReport } from '@/api/report'
import { getClassOptions } from '@/api/class'

// 加载状态
const loading = ref(false)

// 班级选项
const classOptions = ref([])

// 搜索表单
const searchForm = reactive({
  classId: null,
  amountRange: ''
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
    const res = await getArrearsReport(params)
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
    const res = await exportArrearsReport(searchForm)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '欠费名单报表.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
  }
}

// 查看详情
const handleDetail = (row) => {
  ElMessage.info(`查看${row.childName}的欠费详情`)
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

    &.danger {
      color: #f56c6c;
    }

    &.warning {
      color: #e6a23c;
    }
  }
}

.arrears {
  color: #f56c6c;
  font-weight: bold;
}

.pagination-area {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
