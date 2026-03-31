<template>
  <div class="page-container">
    <div class="table-page">
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="操作人">
            <el-input
              v-model="searchForm.username"
              placeholder="请输入操作人"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="操作模块">
            <el-input
              v-model="searchForm.module"
              placeholder="请输入操作模块"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="操作类型">
            <el-select v-model="searchForm.operationType" placeholder="请选择" clearable>
              <el-option label="新增" value="INSERT" />
              <el-option label="修改" value="UPDATE" />
              <el-option label="删除" value="DELETE" />
              <el-option label="查询" value="SELECT" />
              <el-option label="登录" value="LOGIN" />
              <el-option label="登出" value="LOGOUT" />
              <el-option label="导出" value="EXPORT" />
              <el-option label="其他" value="OTHER" />
            </el-select>
          </el-form-item>
          <el-form-item label="操作状态">
            <el-select v-model="searchForm.operationStatus" placeholder="请选择" clearable>
              <el-option label="成功" value="成功" />
              <el-option label="失败" value="失败" />
            </el-select>
          </el-form-item>
          <el-form-item label="操作时间">
            <el-date-picker
              v-model="searchForm.timeRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD HH:mm:ss"
              :shortcuts="dateShortcuts"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-area">
        <div class="table-toolbar">
          <el-button type="danger" @click="handleClearLogs" :disabled="selectedIds.length === 0">
            批量删除
          </el-button>
          <el-button type="warning" @click="handleClearOldLogs">
            清理历史日志
          </el-button>
        </div>

        <el-table 
          :data="tableData" 
          v-loading="loading" 
          border 
          stripe
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column prop="operator" label="操作人" width="100" />
          <el-table-column prop="operationIp" label="操作IP" width="130" />
          <el-table-column prop="operationModule" label="操作模块" width="100" />
          <el-table-column prop="operationName" label="操作名称" width="120" />
          <el-table-column prop="operationType" label="操作类型" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="getTypeTagType(row.operationType)" size="small">
                {{ getTypeText(row.operationType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="requestUrl" label="请求URL" min-width="150" show-overflow-tooltip />
          <el-table-column prop="operationStatus" label="状态" width="70" align="center">
            <template #default="{ row }">
              <el-tag :type="row.operationStatus === '成功' ? 'success' : 'danger'" size="small">
                {{ row.operationStatus }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="costTime" label="耗时(ms)" width="90" align="center" />
          <el-table-column prop="createTime" label="操作时间" width="160" />
          <el-table-column label="操作" width="80" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>

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

    <el-dialog
      v-model="showDetail"
      title="操作日志详情"
      width="700px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="追踪ID">
          {{ currentLog.traceId }}
        </el-descriptions-item>
        <el-descriptions-item label="操作人">
          {{ currentLog.operator }}
        </el-descriptions-item>
        <el-descriptions-item label="操作人ID">
          {{ currentLog.operatorId }}
        </el-descriptions-item>
        <el-descriptions-item label="操作IP">
          {{ currentLog.operationIp }}
        </el-descriptions-item>
        <el-descriptions-item label="操作模块">
          {{ currentLog.operationModule }}
        </el-descriptions-item>
        <el-descriptions-item label="操作名称">
          {{ currentLog.operationName }}
        </el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="getTypeTagType(currentLog.operationType)" size="small">
            {{ getTypeText(currentLog.operationType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作状态">
          <el-tag :type="currentLog.operationStatus === '成功' ? 'success' : 'danger'" size="small">
            {{ currentLog.operationStatus }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="请求方法">
          {{ currentLog.requestMethod }}
        </el-descriptions-item>
        <el-descriptions-item label="耗时">
          {{ currentLog.costTime }} ms
        </el-descriptions-item>
        <el-descriptions-item label="请求URL" :span="2">
          {{ currentLog.requestUrl }}
        </el-descriptions-item>
        <el-descriptions-item label="浏览器">
          {{ currentLog.browser }}
        </el-descriptions-item>
        <el-descriptions-item label="操作系统">
          {{ currentLog.os }}
        </el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <div class="code-block">
            <pre>{{ formatJson(currentLog.requestParams) }}</pre>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="返回结果" :span="2">
          <div class="code-block">
            <pre>{{ formatJson(currentLog.responseResult) }}</pre>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="操作时间" :span="2">
          {{ currentLog.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2" v-if="currentLog.errorMsg">
          <div class="error-block">{{ currentLog.errorMsg }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="异常堆栈" :span="2" v-if="currentLog.exceptionStack">
          <div class="code-block error-block">
            <pre>{{ currentLog.exceptionStack }}</pre>
          </div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog
      v-model="showClearDialog"
      title="清理历史日志"
      width="400px"
    >
      <el-form>
        <el-form-item label="保留天数">
          <el-input-number v-model="clearDays" :min="1" :max="365" />
          <span style="margin-left: 10px; color: #999;">天内的日志将被保留</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showClearDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmClearLogs">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Pagination from '@/components/common/Pagination.vue'
import { getPage, deleteLogs, clearLogs } from '@/api/log'

const loading = ref(false)
const tableData = ref([])
const selectedIds = ref([])

const searchForm = reactive({
  username: '',
  module: '',
  operationType: '',
  operationStatus: '',
  timeRange: null
})

const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

const showDetail = ref(false)
const currentLog = ref({})
const showClearDialog = ref(false)
const clearDays = ref(30)

const dateShortcuts = [
  {
    text: '今天',
    value: () => {
      const today = new Date()
      const start = new Date(today.setHours(0, 0, 0, 0))
      const end = new Date(today.setHours(23, 59, 59, 999))
      return [start, end]
    }
  },
  {
    text: '昨天',
    value: () => {
      const yesterday = new Date()
      yesterday.setTime(yesterday.getTime() - 3600 * 1000 * 24)
      const start = new Date(yesterday.setHours(0, 0, 0, 0))
      const end = new Date(yesterday.setHours(23, 59, 59, 999))
      return [start, end]
    }
  },
  {
    text: '最近7天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    }
  },
  {
    text: '最近30天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    }
  }
]

const getTypeText = (type) => {
  const typeMap = {
    INSERT: '新增',
    UPDATE: '修改',
    DELETE: '删除',
    SELECT: '查询',
    LOGIN: '登录',
    LOGOUT: '登出',
    EXPORT: '导出',
    IMPORT: '导入',
    OTHER: '其他',
    EXCEPTION: '异常'
  }
  return typeMap[type] || type
}

const getTypeTagType = (type) => {
  const typeMap = {
    INSERT: 'success',
    UPDATE: 'warning',
    DELETE: 'danger',
    SELECT: 'info',
    LOGIN: 'primary',
    LOGOUT: 'info',
    EXPORT: 'warning',
    OTHER: 'info',
    EXCEPTION: 'danger'
  }
  return typeMap[type] || 'info'
}

const formatJson = (str) => {
  if (!str) return ''
  try {
    return JSON.stringify(JSON.parse(str), null, 2)
  } catch {
    return str
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      username: searchForm.username,
      module: searchForm.module,
      operationType: searchForm.operationType,
      operationStatus: searchForm.operationStatus,
      startTime: searchForm.timeRange?.[0],
      endTime: searchForm.timeRange?.[1],
      page: pagination.page,
      limit: pagination.limit
    }
    const res = await getPage(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取日志列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.module = ''
  searchForm.operationType = ''
  searchForm.operationStatus = ''
  searchForm.timeRange = null
  handleSearch()
}

const handlePagination = () => {
  fetchData()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleDetail = (row) => {
  currentLog.value = { ...row }
  showDetail.value = true
}

const handleClearLogs = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的日志')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedIds.value.length} 条日志吗？`,
    '提示',
    { type: 'warning' }
  ).then(async () => {
    try {
      await deleteLogs(selectedIds.value)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error('删除日志失败:', error)
    }
  }).catch(() => {})
}

const handleClearOldLogs = () => {
  showClearDialog.value = true
}

const confirmClearLogs = async () => {
  try {
    await clearLogs(clearDays.value)
    ElMessage.success('清理成功')
    showClearDialog.value = false
    fetchData()
  } catch (error) {
    console.error('清理日志失败:', error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.table-toolbar {
  margin-bottom: 16px;
}

.code-block {
  max-height: 200px;
  overflow: auto;
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  
  pre {
    margin: 0;
    font-size: 12px;
    white-space: pre-wrap;
    word-break: break-all;
  }
}

.error-block {
  color: #f56c6c;
}
</style>
