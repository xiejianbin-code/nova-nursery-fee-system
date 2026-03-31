<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 搜索栏 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="幼儿姓名">
            <el-input
              v-model="searchForm.childName"
              placeholder="请输入幼儿姓名"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
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
          <el-table-column prop="className" label="班级" min-width="100" />
          <el-table-column prop="educationBalance" label="保教费余额" width="120" align="right">
            <template #default="{ row }">
              <span class="balance">¥{{ row.educationBalance || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="foodBalance" label="伙食费余额" width="120" align="right">
            <template #default="{ row }">
              <span class="balance">¥{{ row.foodBalance || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="otherBalance" label="其他费用余额" width="120" align="right">
            <template #default="{ row }">
              <span class="balance">¥{{ row.otherBalance || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="totalBalance" label="总余额" width="120" align="right">
            <template #default="{ row }">
              <span class="balance total">¥{{ row.totalBalance || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleViewRecords(row)">变动记录</el-button>
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

    <!-- 余额变动记录弹窗 -->
    <BalanceRecord
      v-model:visible="recordVisible"
      :child-id="currentChildId"
      :child-name="currentChildName"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import Pagination from '@/components/common/Pagination.vue'
import BalanceRecord from './components/BalanceRecord.vue'
import { getBalanceList } from '@/api/balance'
import { getClassOptions } from '@/api/class'

// 加载状态
const loading = ref(false)

// 表格数据
const tableData = ref([])

// 班级选项
const classOptions = ref([])

// 搜索表单
const searchForm = reactive({
  childName: '',
  classId: null
})

// 分页信息
const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

// 弹窗相关
const recordVisible = ref(false)
const currentChildId = ref(null)
const currentChildName = ref('')

// 获取班级选项
const fetchClassOptions = async () => {
  try {
    const res = await getClassOptions()
    classOptions.value = res.data || []
  } catch (error) {
    console.error('获取班级选项失败:', error)
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
    const res = await getBalanceList(params)
    tableData.value = res.data?.list || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取余额列表失败:', error)
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
  searchForm.childName = ''
  searchForm.classId = null
  handleSearch()
}

// 分页变化
const handlePagination = () => {
  fetchData()
}

// 查看变动记录
const handleViewRecords = (row) => {
  currentChildId.value = row.childId
  currentChildName.value = row.childName
  recordVisible.value = true
}

// 初始化
onMounted(() => {
  fetchClassOptions()
  fetchData()
})
</script>

<style lang="scss" scoped>
.balance {
  color: #67c23a;
  font-weight: 500;

  &.total {
    color: #409eff;
    font-weight: bold;
  }
}
</style>
