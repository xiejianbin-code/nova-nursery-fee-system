<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 搜索栏 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="幼儿姓名">
            <el-select v-model="searchForm.childId" placeholder="请选择幼儿" filterable clearable style="width: 200px" @change="handleChildChange">
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
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 余额概览 -->
      <div class="balance-overview" v-if="balanceInfo && searchForm.childId">
        <el-row :gutter="20">
          <el-col :span="8" v-for="account in balanceInfo.accounts" :key="account.accountType">
            <el-card shadow="hover" class="balance-card">
              <div class="balance-card-content">
                <div class="balance-card-icon" :class="'type-' + account.accountType.toLowerCase()">
                  <el-icon size="32">
                    <component :is="getAccountIcon(account.accountType)" />
                  </el-icon>
                </div>
                <div class="balance-card-info">
                  <div class="balance-card-title">{{ account.accountTypeName }}</div>
                  <div class="balance-card-amount">¥{{ account.balance?.toFixed(2) || '0.00' }}</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 表格区域 -->
      <div class="table-area">
        <div class="table-title">余额变动记录</div>
        
        <!-- 表格 -->
        <el-table :data="tableData" v-loading="loading" border stripe>
          <el-table-column prop="childName" label="幼儿姓名" width="100" />
          <el-table-column prop="accountType" label="账户类型" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="accountTypeMap[row.accountType]" size="small">
                {{ row.accountTypeName || accountTypeText[row.accountType] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="changeType" label="变动类型" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.changeType === 'INCOME' ? 'success' : 'danger'" size="small">
                {{ row.changeTypeName || (row.changeType === 'INCOME' ? '收入' : '支出') }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="变动金额" width="120" align="right">
            <template #default="{ row }">
              <span :class="row.changeType === 'INCOME' ? 'text-success' : 'text-danger'">
                {{ row.changeType === 'INCOME' ? '+' : '-' }}¥{{ row.amount?.toFixed(2) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="afterBalance" label="变动后余额" width="120" align="right">
            <template #default="{ row }">
              ¥{{ row.afterBalance?.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="relatedNo" label="关联单号" width="150" />
          <el-table-column prop="feeItem" label="费用项目" min-width="120" />
          <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import Pagination from '@/components/common/Pagination.vue'
import { getChildBalance, getBalanceRecords } from '@/api/balance'
import { getChildOptions } from '@/api/child'
import { Wallet, Food, More } from '@element-plus/icons-vue'

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

const getAccountIcon = (type) => {
  const icons = {
    'EDUCATION': Wallet,
    'MEAL': Food,
    'OTHER': More
  }
  return icons[type] || More
}

const loading = ref(false)
const tableData = ref([])
const childOptions = ref([])
const balanceInfo = ref(null)

const searchForm = reactive({
  childId: null,
  accountType: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const fetchChildOptions = async () => {
  try {
    const res = await getChildOptions({ status: 1 })
    childOptions.value = res.data || []
  } catch (error) {
    console.error('获取幼儿列表失败:', error)
  }
}

const fetchBalanceInfo = async () => {
  if (!searchForm.childId) {
    balanceInfo.value = null
    return
  }
  try {
    const res = await getChildBalance(searchForm.childId)
    balanceInfo.value = res.data
  } catch (error) {
    console.error('获取余额信息失败:', error)
    balanceInfo.value = null
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.pageNum,
      size: pagination.pageSize,
      childId: searchForm.childId,
      accountType: searchForm.accountType
    }
    const res = await getBalanceRecords(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取余额记录失败:', error)
  } finally {
    loading.value = false
  }
}

const handleChildChange = () => {
  fetchBalanceInfo()
  handleSearch()
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.childId = null
  searchForm.accountType = null
  balanceInfo.value = null
  handleSearch()
}

const handlePagination = () => {
  fetchData()
}

onMounted(() => {
  fetchChildOptions()
  fetchData()
})
</script>

<style lang="scss" scoped>
.balance-overview {
  margin-bottom: 20px;
}

.balance-card {
  .balance-card-content {
    display: flex;
    align-items: center;
    padding: 10px;
  }

  .balance-card-icon {
    width: 64px;
    height: 64px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    margin-right: 16px;

    &.type-education {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    &.type-meal {
      background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
    }

    &.type-other {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    }
  }

  .balance-card-info {
    flex: 1;
  }

  .balance-card-title {
    font-size: 14px;
    color: #909399;
    margin-bottom: 8px;
  }

  .balance-card-amount {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }
}

.table-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 16px;
  color: #303133;
}

.text-success {
  color: #67c23a;
}

.text-danger {
  color: #f56c6c;
}
</style>
