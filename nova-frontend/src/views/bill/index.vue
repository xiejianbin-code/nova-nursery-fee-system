<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 搜索栏 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="幼儿姓名">
            <el-select v-model="searchForm.childId" placeholder="请选择幼儿" clearable filterable style="width: 150px">
              <el-option
                v-for="item in childOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="班级">
            <el-select v-model="searchForm.classId" placeholder="请选择班级" clearable filterable style="width: 150px">
              <el-option
                v-for="item in classOptions"
                :key="item.id"
                :label="item.className"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="合同">
            <el-select v-model="searchForm.contractId" placeholder="请选择合同" clearable filterable style="width: 200px">
              <el-option
                v-for="item in contractOptions"
                :key="item.id"
                :label="item.contractName"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="账单月份">
            <el-date-picker
              v-model="searchForm.billMonth"
              type="month"
              placeholder="请选择月份"
              format="YYYY-MM"
              value-format="YYYY-MM"
              style="width: 150px"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
              <el-option label="待确认" value="PENDING" />
              <el-option label="已确认" value="CONFIRMED" />
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
          <el-button type="primary" @click="handleGenerate">生成账单</el-button>
        </div>

        <!-- 表格 -->
        <el-table :data="tableData" v-loading="loading" border stripe>
          <el-table-column prop="billNo" label="账单编号" min-width="150" />
          <el-table-column prop="childName" label="幼儿姓名" min-width="100" />
          <el-table-column prop="className" label="班级" min-width="100" />
          <el-table-column prop="billMonth" label="账单月份" width="100" />
          <el-table-column prop="educationFeeReceivable" width="120" align="right">
            <template #header>
              <div class="header-with-tooltip">
                <span>保教费应收</span>
                <el-tooltip
                  content="按幼儿当月实际月龄段，匹配合同内对应年龄段的保教费标准，跨月龄时按实际天数分段核算；最终确认公式：某段保教费应收 = 该段合同约定月保教费标准 ÷ 当月工作日天数(周一到周五) × 该段工作日天数"
                  placement="top"
                  :show-after="500"
                >
                  <el-icon class="question-icon"><QuestionFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <template #default="{ row }">
              <span class="amount">¥{{ row.educationFeeReceivable?.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="mealFeeReceivable" width="120" align="right">
            <template #header>
              <div class="header-with-tooltip">
                <span>伙食费应收</span>
                <el-tooltip
                  content="合同约定月伙食费标准"
                  placement="top"
                  :show-after="500"
                >
                  <el-icon class="question-icon"><QuestionFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <template #default="{ row }">
              <span class="amount">¥{{ row.mealFeeReceivable?.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="otherFeeReceivable" label="其他费用" width="100" align="right">
            <template #default="{ row }">
              <span class="amount">¥{{ row.otherFeeReceivable?.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="educationFeeRefund" width="120" align="right">
            <template #header>
              <div class="header-with-tooltip">
                <span>应退保教费</span>
                <el-tooltip
                  content="事假、病假天数分开单独计算，任一假期天数超过系统可配置的起算天数，退还该假期全部天数的保教费；公式：日保教费单价 = 合同约定月保教费应收金额 ÷ 当月工作日天数(周一到周五) → 应退保教费 =（符合规则的事假天数+符合规则的病假天数）× 日保教费单价"
                  placement="top"
                  :show-after="500"
                >
                  <el-icon class="question-icon"><QuestionFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <template #default="{ row }">
              <span class="refund">¥{{ row.educationFeeRefund?.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="mealFeeRefund" width="120" align="right">
            <template #header>
              <div class="header-with-tooltip">
                <span>应退伙食费</span>
                <el-tooltip
                  content="按当月未到园天数（请假+病假+缺勤）全额退还；公式：日伙食费单价 = 合同约定月伙食费标准 ÷ 当月工作日天数(周一到周五) → 应退伙食费 = 未到园天数 × 日伙食费单价"
                  placement="top"
                  :show-after="500"
                >
                  <el-icon class="question-icon"><QuestionFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <template #default="{ row }">
              <span class="refund">¥{{ row.mealFeeRefund?.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="discountAmount" label="减免金额" width="100" align="right">
            <template #default="{ row }">
              <span class="amount">¥{{ row.discountAmount?.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="actualAmount" width="120" align="right">
            <template #header>
              <div class="header-with-tooltip">
                <span>实际应交</span>
                <el-tooltip
                  content="实际应交金额 = 应收项目总金额（保教费应收+伙食费应收+其他费用） - 应退保教费 - 应退伙食费 - 手动减免金额"
                  placement="top"
                  :show-after="500"
                >
                  <el-icon class="question-icon"><QuestionFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <template #default="{ row }">
              <span class="actual">¥{{ row.actualAmount?.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="dueAmount" width="120" align="right">
            <template #header>
              <div class="header-with-tooltip">
                <span>实际需缴</span>
                <el-tooltip
                  content="实际需缴金额 = 当月实际应交金额 - 保教费余额 - 伙食费余额 - 其他费用余额，如果大于0，说明余额不足"
                  placement="top"
                  :show-after="500"
                >
                  <el-icon class="question-icon"><QuestionFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <template #default="{ row }">
              <span class="actual">¥{{ row.dueAmount?.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="statusMap[row.status]" size="small">
                {{ statusText[row.status] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
              <el-button
                type="success"
                link
                @click="handleConfirm(row)"
                v-if="row.status === 'PENDING'"
              >
                确认
              </el-button>
              <el-button
                type="warning"
                link
                @click="handleRecalculate(row)"
                v-if="row.status === 'PENDING'"
              >
                重算
              </el-button>
              <el-button
                type="info"
                link
                @click="handleDiscount(row)"
                v-if="row.status === 'PENDING' || row.status === 'CONFIRMED'"
              >
                减免
              </el-button>
              <el-button
                type="primary"
                link
                @click="handleOtherFee(row)"
                v-if="row.status === 'PENDING' || row.status === 'CONFIRMED'"
              >
                其他费用
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

    <!-- 详情弹窗 -->
    <BillDetail
      v-model:visible="detailVisible"
      :bill-id="currentBillId"
    />

    <!-- 减免弹窗 -->
    <BillDiscount
      v-model:visible="discountVisible"
      :bill-data="currentBill"
      @success="handleFormSuccess"
    />

    <!-- 其他费用弹窗 -->
    <BillOtherFee
      v-model:visible="otherFeeVisible"
      :bill-data="currentBill"
      @success="handleFormSuccess"
    />

    <!-- 生成账单弹窗 -->
    <el-dialog
      v-model="generateVisible"
      title="生成账单"
      width="400px"
    >
      <el-form :model="generateForm" label-width="80px">
        <el-form-item label="账单月份">
          <el-date-picker
            v-model="generateForm.billMonth"
            type="month"
            placeholder="请选择月份"
            format="YYYY-MM"
            value-format="YYYY-MM"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="generateForm.classId" placeholder="全部班级" clearable style="width: 100%">
            <el-option
              v-for="item in classOptions"
              :key="item.id"
              :label="item.className"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateVisible = false">取消</el-button>
        <el-button type="primary" :loading="generateLoading" @click="submitGenerate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { QuestionFilled } from '@element-plus/icons-vue'
import Pagination from '@/components/common/Pagination.vue'
import BillDetail from './components/BillDetail.vue'
import BillDiscount from './components/BillDiscount.vue'
import BillOtherFee from './components/BillOtherFee.vue'
import { getBillList, confirmBill, recalculateBill, batchGenerateBill } from '@/api/bill'
import { getClassOptions } from '@/api/class'
import { getChildOptions } from '@/api/child'
import { getContractOptions } from '@/api/contract'

const statusMap = {
  'PENDING': 'warning',
  'CONFIRMED': 'success'
}

const statusText = {
  'PENDING': '待确认',
  'CONFIRMED': '已确认'
}

const loading = ref(false)
const tableData = ref([])
const classOptions = ref([])
const childOptions = ref([])
const contractOptions = ref([])

const searchForm = reactive({
  childId: null,
  classId: null,
  contractId: null,
  billMonth: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

const detailVisible = ref(false)
const discountVisible = ref(false)
const otherFeeVisible = ref(false)
const generateVisible = ref(false)
const generateLoading = ref(false)
const currentBillId = ref(null)
const currentBill = ref(null)

const generateForm = reactive({
  billMonth: '',
  classId: null
})

const fetchClassOptions = async () => {
  try {
    const res = await getClassOptions()
    classOptions.value = res.data || []
  } catch (error) {
    console.error('获取班级选项失败:', error)
  }
}

const fetchChildOptions = async () => {
  try {
    const res = await getChildOptions()
    childOptions.value = res.data || []
  } catch (error) {
    console.error('获取幼儿选项失败:', error)
  }
}

const fetchContractOptions = async () => {
  try {
    const res = await getContractOptions()
    contractOptions.value = res.data || []
  } catch (error) {
    console.error('获取合同选项失败:', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      page: pagination.page,
      limit: pagination.limit
    }
    const res = await getBillList(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取账单列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
    fetchData()
}

const handleReset = () => {
  searchForm.childId = null
  searchForm.classId = null
  searchForm.contractId = null
  searchForm.billMonth = ''
  searchForm.status = ''
  handleSearch()
}

const handlePagination = () => {
    fetchData()
}

const handleGenerate = () => {
    generateForm.billMonth = ''
    generateForm.classId = null
    generateVisible.value = true
}

const submitGenerate = async () => {
    if (!generateForm.billMonth) {
        ElMessage.warning('请选择账单月份')
        return
    }

    generateLoading.value = true
    try {
        await batchGenerateBill(generateForm)
        ElMessage.success('账单生成成功')
        generateVisible.value = false
        fetchData()
    } catch (error) {
        console.error('生成失败:', error)
    } finally {
        generateLoading.value = false
    }
}

const handleDetail = (row) => {
    currentBillId.value = row.id
    detailVisible.value = true
}

const handleConfirm = async (row) => {
    try {
        await ElMessageBox.confirm(`确定要确认账单"${row.billNo}"吗?`, '提示', {
        type: 'warning'
      })
      await confirmBill(row.id)
      ElMessage.success('确认成功')
      fetchData()
    } catch (error) {
        if (error !== 'cancel') {
        console.error('确认失败:', error)
      }
    }
}

const handleRecalculate = async (row) => {
    try {
        await ElMessageBox.confirm(`确定要重新计算账单"${row.billNo}"吗?`, '提示', {
        type: 'warning'
      })
      await recalculateBill(row.id)
      ElMessage.success('重算成功')
      fetchData()
    } catch (error) {
        if (error !== 'cancel') {
        console.error('重算失败:', error)
      }
    }
}

const handleDiscount = (row) => {
    currentBill.value = { ...row }
    discountVisible.value = true
}

const handleOtherFee = (row) => {
    currentBill.value = { ...row }
    otherFeeVisible.value = true
}

const handleFormSuccess = () => {
    fetchData()
}

onMounted(() => {
  fetchClassOptions()
  fetchChildOptions()
  fetchContractOptions()
  fetchData()
})
</script>

<style lang="scss" scoped>
.table-toolbar {
  margin-bottom: 16px;
}

.amount {
  color: #f56c6c;
  font-weight: 500;
}

.refund {
  color: #67c23a;
  font-weight: 500;
}

.actual {
  color: #409eff;
  font-weight: bold;
}

.header-with-tooltip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.question-icon {
  font-size: 14px;
  color: #909399;
  cursor: help;
}

.question-icon:hover {
  color: #409eff;
}
</style>
