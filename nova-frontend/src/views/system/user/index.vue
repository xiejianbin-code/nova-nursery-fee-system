<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 搜索栏 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="用户名">
            <el-input
              v-model="searchForm.username"
              placeholder="请输入用户名"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="真实姓名">
            <el-input
              v-model="searchForm.realName"
              placeholder="请输入真实姓名"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="启用" :value="1" />
              <el-option label="停用" :value="0" />
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
        <!-- 工具栏 -->
        <div class="table-toolbar">
          <el-button type="primary" @click="handleAdd">新增用户</el-button>
        </div>

        <!-- 表格 -->
        <el-table :data="tableData" v-loading="loading" border stripe>
          <el-table-column prop="username" label="用户名" min-width="120" />
          <el-table-column prop="realName" label="真实姓名" min-width="120" />
          <el-table-column prop="phone" label="手机号" min-width="130" />
          <el-table-column prop="roleNames" label="角色" min-width="150">
            <template #default="{ row }">
              <template v-if="row.roleNames && row.roleNames.length">
                <el-tag
                  v-for="(name, index) in row.roleNames"
                  :key="index"
                  size="small"
                  class="role-tag"
                >
                  {{ name }}
                </el-tag>
              </template>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 1 ? '启用' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button
                :type="row.status === 1 ? 'warning' : 'success'"
                link
                @click="handleStatusChange(row)"
              >
                {{ row.status === 1 ? '停用' : '启用' }}
              </el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <!-- 用户表单弹窗 -->
    <UserForm
      v-model="showForm"
      :user-id="currentUserId"
      @success="handleFormSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Pagination from '@/components/common/Pagination.vue'
import UserForm from './components/UserForm.vue'
import { getPage, deleteById, updateStatus } from '@/api/user'

// 加载状态
const loading = ref(false)

// 表格数据
const tableData = ref([])

// 搜索表单
const searchForm = reactive({
  username: '',
  realName: '',
  status: null
})

// 分页数据
const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

// 弹窗显示状态
const showForm = ref(false)

// 当前用户ID
const currentUserId = ref(null)

// 获取表格数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      page: pagination.page,
      limit: pagination.limit
    }
    const res = await getPage(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
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
  searchForm.username = ''
  searchForm.realName = ''
  searchForm.status = null
  handleSearch()
}

// 分页变化
const handlePagination = () => {
  fetchData()
}

// 新增用户
const handleAdd = () => {
  currentUserId.value = null
  showForm.value = true
}

// 编辑用户
const handleEdit = (row) => {
  currentUserId.value = row.id
  showForm.value = true
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除用户【${row.username}】吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await deleteById(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error('删除用户失败:', error)
    }
  }).catch(() => {})
}

// 更新用户状态
const handleStatusChange = (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '停用'
  ElMessageBox.confirm(`确定要${statusText}用户【${row.username}】吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await updateStatus(row.id, newStatus)
      ElMessage.success(`${statusText}成功`)
      fetchData()
    } catch (error) {
      console.error('更新状态失败:', error)
    }
  }).catch(() => {})
}

// 表单提交成功
const handleFormSuccess = () => {
  fetchData()
}

// 初始化
onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.table-toolbar {
  margin-bottom: 16px;
}

.role-tag {
  margin-right: 4px;
  margin-bottom: 4px;
}
</style>
