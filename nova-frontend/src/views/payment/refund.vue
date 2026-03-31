<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 搜索栏 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="幼儿姓名">
            <el-select v-model="searchForm.childId" placeholder="请选择幼儿" filterable clearable style="width: 200px">
              <el-option
                v-for="item in childOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="账户类型">
            <el-select v-model="searchForm.accountType" placeholder="请选择账户类型" clearable style="width: 150px">
              <el-option label="保教费" value="EDUCATION" />
              <el-option label="伙食费" value="MEAL" />
              <el-option label="其他费用" value="OTHER" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
              <el-option label="待处理" value="PENDING" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已取消" value="CANCELLED" />
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
          <el-button type="primary" @click="handleAdd">退费登记</el-button>
        </div>

        <!-- 表格 -->
        <el-table :data="tableData" v-loading="loading" border stripe>
          <el-table-column prop="refundNo" label="退费编号" width="150" />
          <el-table-column prop="childName" label="幼儿姓名" width="100" />
          <el-table-column prop="accountType" label="账户类型" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="accountTypeMap[row.accountType]" size="small">
                {{ accountTypeText[row.accountType] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="feeItem" label="费用项目" min-width="120" />
          <el-table-column prop="amount" label="退费金额" width="100" align="right">
            <template #default="{ row }">
              ¥{{ row.amount?.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="退费原因" min-width="150" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="statusMap[row.status]" size="small">
                {{ statusText[row.status] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
        </el-table>

        <!-- 分页 -->
        <div class="pagination-area">
          <Pagination
            v-model:page="pagination.pageNum"
            v-model:limit="pagination.pageSize"
            :total="pagination.total"
            @pagination="handlePagination"
          />
        </div>
      </div>
    </div>

    <!-- 退费登记弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="退费登记"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="幼儿" prop="childId">
          <el-select v-model="form.childId" placeholder="请选择幼儿" filterable style="width: 100%">
            <el-option
              v-for="item in childOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="账户类型" prop="accountType">
          <el-select v-model="form.accountType" placeholder="请选择账户类型" style="width: 100%">
            <el-option label="保教费" value="EDUCATION" />
            <el-option label="伙食费" value="MEAL" />
            <el-option label="其他费用" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="费用项目" prop="feeItem">
          <el-input v-model="form.feeItem" placeholder="请输入费用项目名称" />
        </el-form-item>
        <el-form-item label="退费金额" prop="amount">
          <el-input-number v-model="form.amount" :precision="2" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="退费原因" prop="reason">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入退费原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import Pagination from '@/components/common/Pagination.vue'
import { getRefundPage, registerRefund } from '@/api/refund'
import { getChildOptions } from '@/api/child'

const accountTypeMap = {
  'EDUCATION': 'primary',
  'MEAL': 'success',
  'OTHER': 'info'
}

const accountTypeText = {
  'EDUCATION': '保教费',
  'MEAL': '伙食费',
  'OTHER': '其他费用'
}

const statusMap = {
  'PENDING': 'warning',
  'COMPLETED': 'success',
  'CANCELLED': 'info'
}

const statusText = {
  'PENDING': '待处理',
  'COMPLETED': '已完成',
  'CANCELLED': '已取消'
}

const loading = ref(false)
const tableData = ref([])
const childOptions = ref([])

const searchForm = reactive({
  childId: null,
  accountType: null,
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const form = reactive({
  childId: null,
  accountType: '',
  feeItem: '',
  amount: 0,
  reason: ''
})

const rules = {
  childId: [{ required: true, message: '请选择幼儿', trigger: 'change' }],
  accountType: [{ required: true, message: '请选择账户类型', trigger: 'change' }],
  feeItem: [{ required: true, message: '请输入费用项目名称', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入退费金额', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入退费原因', trigger: 'blur' }]
}

const fetchChildOptions = async () => {
  try {
    const res = await getChildOptions({ status: 1 })
    childOptions.value = res.data || []
  } catch (error) {
    console.error('获取幼儿列表失败:', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.pageNum,
      size: pagination.pageSize,
      childId: searchForm.childId,
      accountType: searchForm.accountType,
      status: searchForm.status
    }
    const res = await getRefundPage(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取退费列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.childId = null
  searchForm.accountType = null
  searchForm.status = null
  handleSearch()
}

const handlePagination = () => {
  fetchData()
}

const handleAdd = () => {
  Object.assign(form, {
    childId: null,
    accountType: '',
    feeItem: '',
    amount: 0,
    reason: ''
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await registerRefund(form)
        ElMessage.success('退费登记成功')
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error('提交失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchChildOptions()
  fetchData()
})
</script>

<style lang="scss" scoped>
.table-toolbar {
  margin-bottom: 16px;
}
</style>
