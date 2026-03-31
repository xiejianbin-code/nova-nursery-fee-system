<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 搜索栏 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="幼儿">
            <el-select v-model="searchForm.childId" placeholder="请选择幼儿" clearable filterable style="width: 180px">
              <el-option
                v-for="item in childOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="班级">
            <el-select v-model="searchForm.classId" placeholder="请选择班级" clearable style="width: 150px">
              <el-option
                v-for="item in classOptions"
                :key="item.id"
                :label="item.className"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="日期">
            <el-date-picker
              v-model="searchForm.date"
              type="date"
              placeholder="请选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 180px"
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
          <el-button type="primary" @click="handleRegister">单个登记</el-button>
          <el-button type="primary" @click="handleBatchRegister">批量登记</el-button>
          <el-button type="success" @click="handleExport">导出</el-button>
        </div>

        <!-- 表格 -->
        <el-table :data="tableData" v-loading="loading" border stripe>
          <el-table-column prop="childName" label="幼儿姓名" min-width="100" />
          <el-table-column prop="className" label="班级" min-width="100" />
          <el-table-column prop="attendanceDate" label="出勤日期" width="120" />
          <el-table-column prop="status" label="出勤状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="attendanceStatusMap[row.status]" size="small">
                {{ attendanceStatusText[row.status] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="registerUserName" label="登记人" width="100" />
          <el-table-column prop="registerTime" label="登记时间" width="180" />
          <el-table-column prop="remark" label="备注" min-width="150" />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleUpdate(row)">修改状态</el-button>
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

    <!-- 单个登记弹窗 -->
    <el-dialog
      v-model="registerVisible"
      title="单个出勤登记"
      width="500px"
    >
      <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" label-width="100px">
        <el-form-item label="幼儿" prop="childId">
          <el-select v-model="registerForm.childId" placeholder="请选择幼儿" filterable style="width: 100%">
            <el-option
              v-for="item in childOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="出勤日期" prop="attendanceDate">
          <el-date-picker
            v-model="registerForm.attendanceDate"
            type="date"
            placeholder="请选择出勤日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="出勤状态" prop="status">
          <el-select v-model="registerForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="正常到园" :value="1" />
            <el-option label="请假" :value="2" />
            <el-option label="病假" :value="3" />
            <el-option label="缺勤" :value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="registerVisible = false">取消</el-button>
        <el-button type="primary" :loading="registerLoading" @click="submitRegister">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量登记弹窗 -->
    <AttendanceBatch
      v-model:visible="batchVisible"
      :class-options="classOptions"
      :date="searchForm.date"
      @success="handleFormSuccess"
    />

    <!-- 修改状态弹窗 -->
    <el-dialog
      v-model="updateVisible"
      title="修改出勤状态"
      width="400px"
    >
      <el-form :model="updateForm" label-width="80px">
        <el-form-item label="幼儿姓名">
          <el-input :model-value="updateForm.childName" disabled />
        </el-form-item>
        <el-form-item label="出勤状态">
          <el-select v-model="updateForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="正常到园" :value="1" />
            <el-option label="请假" :value="2" />
            <el-option label="病假" :value="3" />
            <el-option label="缺勤" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="updateForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="updateVisible = false">取消</el-button>
        <el-button type="primary" :loading="updateLoading" @click="submitUpdate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import Pagination from '@/components/common/Pagination.vue'
import AttendanceBatch from './components/AttendanceBatch.vue'
import { getAttendanceList, registerAttendance, updateAttendance, exportAttendance } from '@/api/attendance'
import { getClassOptions } from '@/api/class'
import { getChildList } from '@/api/child'

const attendanceStatusMap = {
  1: 'success',
  2: 'warning',
  3: 'info',
  4: 'danger'
}

const attendanceStatusText = {
  1: '正常到园',
  2: '请假',
  3: '病假',
  4: '缺勤'
}

const loading = ref(false)
const tableData = ref([])
const classOptions = ref([])
const childOptions = ref([])

const searchForm = reactive({
  childId: null,
  classId: null,
  date: new Date().toISOString().split('T')[0]
})

const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

const registerVisible = ref(false)
const registerLoading = ref(false)
const registerFormRef = ref(null)
const registerForm = reactive({
  childId: null,
  attendanceDate: new Date().toISOString().split('T')[0],
  status: 1
})

const registerRules = {
  childId: [{ required: true, message: '请选择幼儿', trigger: 'change' }],
  attendanceDate: [{ required: true, message: '请选择出勤日期', trigger: 'change' }],
  status: [{ required: true, message: '请选择出勤状态', trigger: 'change' }]
}

const batchVisible = ref(false)
const updateVisible = ref(false)
const updateLoading = ref(false)
const updateForm = reactive({
  id: null,
  childName: '',
  status: 1,
  remark: ''
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
    const res = await getChildList({ page: 1, limit: 10000, status: 1 })
    childOptions.value = res.data?.records || []
  } catch (error) {
    console.error('获取幼儿列表失败:', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      limit: pagination.limit,
      childId: searchForm.childId,
      classId: searchForm.classId,
      date: searchForm.date
    }
    const res = await getAttendanceList(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取出勤列表失败:', error)
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
  searchForm.date = new Date().toISOString().split('T')[0]
  handleSearch()
}

const handlePagination = () => {
  fetchData()
}

const handleRegister = () => {
  registerForm.childId = null
  registerForm.attendanceDate = new Date().toISOString().split('T')[0]
  registerForm.status = 1
  registerVisible.value = true
}

const submitRegister = async () => {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      registerLoading.value = true
      try {
        await registerAttendance({
          childId: registerForm.childId,
          attendanceDate: registerForm.attendanceDate,
          status: registerForm.status
        })
        ElMessage.success('登记成功')
        registerVisible.value = false
        fetchData()
      } catch (error) {
        console.error('登记失败:', error)
      } finally {
        registerLoading.value = false
      }
    }
  })
}

const handleBatchRegister = () => {
  batchVisible.value = true
}

const handleExport = async () => {
  if (!searchForm.classId) {
    ElMessage.warning('请先选择班级')
    return
  }
  try {
    const dateObj = new Date(searchForm.date)
    const params = {
      classId: searchForm.classId,
      year: dateObj.getFullYear(),
      month: dateObj.getMonth() + 1
    }
    const res = await exportAttendance(params)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `出勤记录_${searchForm.date}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
  }
}

const handleUpdate = (row) => {
  updateForm.id = row.id
  updateForm.childName = row.childName
  updateForm.status = row.status
  updateForm.remark = row.remark || ''
  updateVisible.value = true
}

const submitUpdate = async () => {
  updateLoading.value = true
  try {
    await updateAttendance({
      id: updateForm.id,
      status: updateForm.status,
      remark: updateForm.remark
    })
    ElMessage.success('修改成功')
    updateVisible.value = false
    fetchData()
  } catch (error) {
    console.error('修改失败:', error)
  } finally {
    updateLoading.value = false
  }
}

const handleFormSuccess = () => {
  fetchData()
}

onMounted(() => {
  fetchClassOptions()
  fetchChildOptions()
  fetchData()
})
</script>

<style lang="scss" scoped>
.table-toolbar {
  margin-bottom: 16px;
}
</style>
