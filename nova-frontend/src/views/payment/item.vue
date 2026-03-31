<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 搜索栏 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="模板名称">
            <el-input v-model="searchForm.templateName" placeholder="请输入模板名称" clearable style="width: 200px" />
          </el-form-item>
          <el-form-item label="课程类型">
            <el-select v-model="searchForm.courseType" placeholder="请选择课程类型" clearable style="width: 150px">
              <el-option label="按月" value="MONTHLY" />
              <el-option label="按学期" value="SEMESTER" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
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
          <el-button type="primary" @click="handleAdd">新增收费标准</el-button>
        </div>

        <!-- 表格 -->
        <el-table :data="tableData" v-loading="loading" border stripe>
          <el-table-column prop="templateName" label="模板名称" min-width="150" />
          <el-table-column prop="courseType" label="课程类型" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="row.courseType === 'MONTHLY' ? 'primary' : 'success'" size="small">
                {{ row.courseType === 'MONTHLY' ? '按月' : '按学期' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-switch
                v-model="row.status"
                :active-value="1"
                :inactive-value="0"
                @change="handleStatusChange(row)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="version" label="版本号" width="100" align="center" />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleView(row)">查看</el-button>
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="900px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="模板名称" prop="templateName">
              <el-input v-model="form.templateName" placeholder="请输入模板名称" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程类型" prop="courseType">
              <el-select v-model="form.courseType" placeholder="请选择课程类型" style="width: 100%" :disabled="isView">
                <el-option label="按月" value="MONTHLY" />
                <el-option label="按学期" value="SEMESTER" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 年龄段收费明细 -->
        <el-divider content-position="left">年龄段收费明细</el-divider>
        <el-table :data="form.items" border style="margin-bottom: 20px">
          <el-table-column prop="ageRange" label="年龄段" min-width="150">
            <template #default="{ row, $index }">
              <el-input v-model="row.ageRange" placeholder="如：3-4岁" :disabled="isView" />
            </template>
          </el-table-column>
          <el-table-column prop="educationFee" label="保教费" min-width="150">
            <template #default="{ row }">
              <el-input-number v-model="row.educationFee" :precision="2" :min="0" style="width: 100%" :disabled="isView" />
            </template>
          </el-table-column>
          <el-table-column prop="mealFee" label="伙食费" min-width="150">
            <template #default="{ row }">
              <el-input-number v-model="row.mealFee" :precision="2" :min="0" style="width: 100%" :disabled="isView" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" v-if="!isView">
            <template #default="{ $index }">
              <el-button type="danger" link @click="handleRemoveItem($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="handleAddItem" v-if="!isView" style="margin-bottom: 20px">添加年龄段</el-button>

        <!-- 其他费用 -->
        <el-divider content-position="left">其他费用</el-divider>
        <el-table :data="form.otherFees" border>
          <el-table-column prop="feeCode" label="费用代码" min-width="120">
            <template #default="{ row }">
              <el-input v-model="row.feeCode" placeholder="费用代码" :disabled="isView" />
            </template>
          </el-table-column>
          <el-table-column prop="feeName" label="费用名称" min-width="120">
            <template #default="{ row }">
              <el-input v-model="row.feeName" placeholder="费用名称" :disabled="isView" />
            </template>
          </el-table-column>
          <el-table-column prop="chargeCycle" label="收取周期" min-width="120">
            <template #default="{ row }">
              <el-select v-model="row.chargeCycle" placeholder="收取周期" style="width: 100%" :disabled="isView">
                <el-option label="按月" value="MONTHLY" />
                <el-option label="按学期" value="SEMESTER" />
                <el-option label="一次性" value="ONCE" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="feeStandard" label="收费标准" min-width="120">
            <template #default="{ row }">
              <el-input-number v-model="row.feeStandard" :precision="2" :min="0" style="width: 100%" :disabled="isView" />
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-switch v-model="row.status" :active-value="1" :inactive-value="0" :disabled="isView" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" v-if="!isView">
            <template #default="{ $index }">
              <el-button type="danger" link @click="handleRemoveOtherFee($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="handleAddOtherFee" v-if="!isView" style="margin-top: 10px">添加其他费用</el-button>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit" v-if="!isView">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import Pagination from '@/components/common/Pagination.vue'
import { getFeeTemplateList, getFeeTemplateDetail, addFeeTemplate, editFeeTemplate, updateFeeTemplateStatus } from '@/api/feeTemplate'

const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({
  templateName: '',
  courseType: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增收费标准')
const isView = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  templateName: '',
  courseType: '',
  status: 1,
  items: [],
  otherFees: []
})

const rules = {
  templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await getFeeTemplateList(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取收费标准列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.templateName = ''
  searchForm.courseType = ''
  searchForm.status = null
  handleSearch()
}

const handlePagination = () => {
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增收费标准'
  isView.value = false
  Object.assign(form, {
    id: null,
    templateName: '',
    courseType: '',
    status: 1,
    items: [{ ageRange: '', educationFee: 0, mealFee: 0 }],
    otherFees: []
  })
  dialogVisible.value = true
}

const handleView = async (row) => {
  dialogTitle.value = '查看收费标准'
  isView.value = true
  try {
    const res = await getFeeTemplateDetail(row.id)
    Object.assign(form, res.data)
    dialogVisible.value = true
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑收费标准'
  isView.value = false
  try {
    const res = await getFeeTemplateDetail(row.id)
    Object.assign(form, res.data)
    dialogVisible.value = true
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

const handleStatusChange = async (row) => {
  try {
    await updateFeeTemplateStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
    console.error('更新状态失败:', error)
  }
}

const handleAddItem = () => {
  form.items.push({ ageRange: '', educationFee: 0, mealFee: 0 })
}

const handleRemoveItem = (index) => {
  form.items.splice(index, 1)
}

const handleAddOtherFee = () => {
  form.otherFees.push({ feeCode: '', feeName: '', chargeCycle: '', feeStandard: 0, status: 1 })
}

const handleRemoveOtherFee = (index) => {
  form.otherFees.splice(index, 1)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.items.length === 0) {
        ElMessage.warning('请至少添加一个年龄段收费明细')
        return
      }
      
      submitLoading.value = true
      try {
        if (form.id) {
          await editFeeTemplate(form)
          ElMessage.success('修改成功')
        } else {
          await addFeeTemplate(form)
          ElMessage.success('新增成功')
        }
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
  fetchData()
})
</script>

<style lang="scss" scoped>
.table-toolbar {
  margin-bottom: 16px;
}
</style>
