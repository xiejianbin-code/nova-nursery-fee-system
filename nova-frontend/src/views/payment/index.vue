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
          <el-form-item label="收费类型">
            <el-select v-model="searchForm.paymentType" placeholder="请选择收费类型" clearable style="width: 150px">
              <el-option
                v-for="item in paymentTypes"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="日期范围">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 240px"
            />
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
          <el-button type="primary" @click="handleAdd">收费登记</el-button>
        </div>

        <!-- 表格 -->
        <el-table :data="tableData" v-loading="loading" border stripe>
          <el-table-column prop="receiptNo" label="票据号" min-width="150" />
          <el-table-column prop="childName" label="幼儿姓名" min-width="100" />
          <el-table-column prop="paymentType" label="收费类型" width="100">
            <template #default="{ row }">
              {{ getPaymentTypeText(row.paymentType) }}
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="120" align="right">
            <template #default="{ row }">
              <span class="amount">¥{{ row.amount?.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="paymentMethod" label="收费方式" width="100">
            <template #default="{ row }">
              {{ getPaymentMethodText(row.paymentMethod) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 'VALID' ? 'success' : 'danger'" size="small">
                {{ row.status === 'VALID' ? '有效' : '已作废' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="时间" width="180" />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button
                type="danger"
                link
                @click="handleCancel(row)"
                v-if="row.status === 'VALID'"
              >
                作废
              </el-button>
            </template>
          </el-table-column>
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

    <!-- 收费登记弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="收费登记"
      width="600px"
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
        <el-form-item label="收费类型" prop="paymentType">
          <el-select v-model="form.paymentType" placeholder="请选择收费类型" style="width: 100%">
            <el-option
              v-for="item in paymentTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="费用项目" prop="feeItem">
          <el-input v-model="form.feeItem" placeholder="请输入费用项目" />
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="form.amount" :precision="2" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="收费方式" prop="paymentMethod">
          <el-select v-model="form.paymentMethod" placeholder="请选择收费方式" style="width: 100%">
            <el-option
              v-for="item in paymentMethods"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import Pagination from '@/components/common/Pagination.vue'
import { getPaymentList, registerPayment, cancelPayment } from '@/api/payment'
import { getChildOptions } from '@/api/child'

const paymentTypes = [
  { label: '保教费', value: 'EDUCATION' },
  { label: '伙食费', value: 'MEAL' },
  { label: '其他费用', value: 'OTHER' }
]

const paymentMethods = [
  { label: '现金', value: 'CASH' },
  { label: '微信', value: 'WECHAT' },
  { label: '支付宝', value: 'ALIPAY' },
  { label: '银行转账', value: 'BANK' }
]

const paymentTypeText = {
  'EDUCATION': '保教费',
  'MEAL': '伙食费',
  'OTHER': '其他费用'
}

const paymentMethodText = {
  'CASH': '现金',
  'WECHAT': '微信',
  'ALIPAY': '支付宝',
  'BANK': '银行转账'
}

const getPaymentTypeText = (type) => paymentTypeText[type] || type
const getPaymentMethodText = (method) => paymentMethodText[method] || method

const loading = ref(false)
const tableData = ref([])
const childOptions = ref([])

const searchForm = reactive({
  childId: null,
  paymentType: '',
  dateRange: []
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
  paymentType: '',
  feeItem: '',
  amount: 0,
  paymentMethod: '',
  remark: ''
})

const rules = {
  childId: [{ required: true, message: '请选择幼儿', trigger: 'change' }],
  paymentType: [{ required: true, message: '请选择收费类型', trigger: 'change' }],
  feeItem: [{ required: true, message: '请输入费用项目', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  paymentMethod: [{ required: true, message: '请选择收费方式', trigger: 'change' }]
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
      paymentType: searchForm.paymentType,
      startDate: searchForm.dateRange?.[0] || null,
      endDate: searchForm.dateRange?.[1] || null
    }
    const res = await getPaymentList(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取收费列表失败:', error)
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
  searchForm.paymentType = ''
  searchForm.dateRange = []
  handleSearch()
}

const handlePagination = () => {
  fetchData()
}

const handleAdd = () => {
  Object.assign(form, {
    childId: null,
    paymentType: '',
    feeItem: '',
    amount: 0,
    paymentMethod: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await registerPayment(form)
        ElMessage.success('收费登记成功')
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

const handleCancel = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入作废原因', '作废确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /\S+/,
      inputErrorMessage: '请输入作废原因'
    })
    await cancelPayment(row.id, value)
    ElMessage.success('作废成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('作废失败:', error)
    }
  }
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

.amount {
  color: #f56c6c;
  font-weight: bold;
}
</style>
