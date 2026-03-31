<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 搜索栏 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="配置键">
            <el-input
              v-model="searchForm.configKey"
              placeholder="请输入配置键"
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
          <el-button type="primary" @click="handleAdd">新增配置</el-button>
        </div>

        <!-- 表格 -->
        <el-table :data="tableData" v-loading="loading" border stripe>
          <el-table-column prop="configKey" label="配置键" min-width="180" />
          <el-table-column prop="configValue" label="配置值" min-width="150" />
          <el-table-column prop="configDesc" label="配置说明" min-width="200" />
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

    <!-- 配置表单弹窗 -->
    <el-dialog
      v-model="showForm"
      :title="formTitle"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="配置键" prop="configKey">
          <el-input
            v-model="formData.configKey"
            placeholder="请输入配置键"
            maxlength="100"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input
            v-model="formData.configValue"
            placeholder="请输入配置值"
            maxlength="500"
          />
        </el-form-item>
        <el-form-item label="配置说明" prop="configDesc">
          <el-input
            v-model="formData.configDesc"
            type="textarea"
            :rows="3"
            placeholder="请输入配置说明"
            maxlength="200"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showForm = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Pagination from '@/components/common/Pagination.vue'
import { getPage, getById, create, update, deleteById, updateStatus } from '@/api/systemConfig'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const showForm = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  configKey: '',
  status: null
})

const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

const formData = reactive({
  id: null,
  configKey: '',
  configValue: '',
  configDesc: '',
  status: 1
})

const formRules = {
  configKey: [
    { required: true, message: '请输入配置键', trigger: 'blur' }
  ],
  configValue: [
    { required: true, message: '请输入配置值', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const formTitle = computed(() => {
  return formData.id ? '编辑配置' : '新增配置'
})

const isEdit = computed(() => {
  return !!formData.id
})

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.page,
      size: pagination.limit,
      ...searchForm
    }
    const res = await getPage(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  searchForm.configKey = ''
  searchForm.status = null
  handleSearch()
}

const handlePagination = () => {
  fetchData()
}

const resetForm = () => {
  formData.id = null
  formData.configKey = ''
  formData.configValue = ''
  formData.configDesc = ''
  formData.status = 1
  formRef.value?.resetFields()
}

const handleAdd = () => {
  resetForm()
  showForm.value = true
}

const handleEdit = async (row) => {
  try {
    const res = await getById(row.id)
    const data = res.data
    formData.id = data.id
    formData.configKey = data.configKey
    formData.configValue = data.configValue
    formData.configDesc = data.configDesc
    formData.status = data.status
    showForm.value = true
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (formData.id) {
      await update(formData)
      ElMessage.success('修改成功')
    } else {
      await create(formData)
      ElMessage.success('新增成功')
    }
    showForm.value = false
    fetchData()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

const handleStatusChange = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '停用'
  
  try {
    await ElMessageBox.confirm(`确定要${statusText}该配置吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateStatus(row.id, newStatus)
    ElMessage.success(`${statusText}成功`)
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该配置吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteById(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.page-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 100px);
}

.table-page {
  background: #fff;
  border-radius: 4px;
}

.search-area {
  padding: 20px 20px 0;
  border-bottom: 1px solid #ebeef5;
}

.table-area {
  padding: 20px;
}

.table-toolbar {
  margin-bottom: 16px;
}

.pagination-area {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
