<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 搜索栏 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="预警类型">
            <el-select v-model="searchForm.alertType" placeholder="请选择预警类型" clearable style="width: 200px">
              <el-option
                v-for="item in alertTypes"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="处理状态">
            <el-select v-model="searchForm.handleStatus" placeholder="请选择处理状态" clearable style="width: 150px">
              <el-option label="待处理" value="PENDING" />
              <el-option label="已处理" value="HANDLED" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格区域 -->
      <div class="table-area">
        <!-- 表格 -->
        <el-table :data="tableData" v-loading="loading" border stripe>
          <el-table-column prop="childName" label="幼儿姓名" min-width="100" />
          <el-table-column prop="alertType" label="预警类型" width="140">
            <template #default="{ row }">
              <el-tag :type="alertTypeMap[row.alertType]" size="small">
                {{ row.alertTypeName || row.alertType }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="alertContent" label="预警内容" min-width="200" />
          <el-table-column prop="triggerTime" label="触发时间" width="180" />
          <el-table-column prop="handleStatus" label="处理状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="statusMap[row.handleStatus]" size="small">
                {{ row.handleStatusName || statusText[row.handleStatus] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="handleTime" label="处理时间" width="180" />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button
                type="primary"
                link
                @click="handleAlert(row)"
                v-if="row.handleStatus === 'PENDING'"
              >
                处理
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-area">
          <Pagination
            v-model:page="pagination.page"
            v-model:limit="pagination.limit"
            :total="pagination.total"
            @pagination="handlePagination"
          />
        </div>
      </div>
    </div>

    <!-- 预警处理弹窗 -->
    <AlertHandle
      v-model:visible="handleVisible"
      :alert-data="currentAlert"
      @success="handleFormSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import Pagination from '@/components/common/Pagination.vue'
import AlertHandle from './components/AlertHandle.vue'
import { getAlertList, getAlertTypes } from '@/api/alert'

// 预警类型映射
const alertTypeMap = {
  'CONTRACT_EXPIRE': 'warning',
  'EDUCATION_FEE_EXPIRE': 'warning',
  'MEAL_FEE_EXPIRE': 'warning',
  'OVERDUE': 'danger',
  'EDUCATION_FEE_LOW': 'danger',
  'MEAL_FEE_LOW': 'danger',
  'OTHER_FEE_LOW': 'danger'
}

// 状态映射
const statusMap = {
  'PENDING': 'danger',
  'HANDLED': 'success'
}

const statusText = {
  'PENDING': '待处理',
  'HANDLED': '已处理'
}

// 加载状态
const loading = ref(false)

// 表格数据
const tableData = ref([])

// 预警类型
const alertTypes = ref([])

// 搜索表单
const searchForm = reactive({
  alertType: '',
  handleStatus: ''
})

// 分页信息
const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

// 弹窗相关
const handleVisible = ref(false)
const currentAlert = ref(null)

// 获取预警类型
const fetchAlertTypes = async () => {
  try {
    const res = await getAlertTypes()
    alertTypes.value = res.data || []
  } catch (error) {
    console.error('获取预警类型失败:', error)
  }
}

// 获取列表数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      page: pagination.page,
      limit: pagination.limit
    }
    const res = await getAlertList(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取预警列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.alertType = ''
  searchForm.handleStatus = null
  handleSearch()
}

// 分页变化
const handlePagination = () => {
  fetchData()
}

// 处理预警
const handleAlert = (row) => {
  currentAlert.value = { ...row }
  handleVisible.value = true
}

// 表单提交成功
const handleFormSuccess = () => {
  fetchData()
}

// 初始化
onMounted(() => {
  fetchAlertTypes()
  fetchData()
})
</script>

<style lang="scss" scoped>
</style>
