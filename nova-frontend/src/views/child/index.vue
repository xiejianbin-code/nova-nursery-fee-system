<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 搜索栏 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="幼儿姓名">
            <el-input v-model="searchForm.name" placeholder="请输入幼儿姓名" clearable style="width: 200px" />
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
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
              <el-option label="在园" :value="1" />
              <el-option label="离园" :value="0" />
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
          <el-button type="primary" @click="handleAdd">新增幼儿</el-button>
        </div>

        <!-- 表格 -->
        <el-table :data="tableData" v-loading="loading" border stripe>
          <el-table-column prop="name" label="幼儿姓名" min-width="100" />
          <el-table-column prop="gender" label="性别" width="80" align="center">
            <template #default="{ row }">
              {{ row.gender === 1 ? '男' : '女' }}
            </template>
          </el-table-column>
          <el-table-column prop="birthDate" label="出生日期" width="120" />
          <el-table-column prop="parentName" label="家长姓名" width="100" />
          <el-table-column prop="contactPhone" label="联系电话" width="120" />
          <el-table-column prop="status" label="状态" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                {{ row.status === 1 ? '在园' : '离园' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="enrollDate" label="入园日期" width="120" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="幼儿姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入幼儿姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker
            v-model="form.birthDate"
            type="date"
            placeholder="请选择出生日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="班级" prop="classId">
          <el-select v-model="form.classId" placeholder="请选择班级" style="width: 100%">
            <el-option
              v-for="item in classOptions"
              :key="item.id"
              :label="item.className"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="入园日期" prop="enrollDate">
          <el-date-picker
            v-model="form.enrollDate"
            type="date"
            placeholder="请选择入园日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="家长姓名" prop="parentName">
          <el-input v-model="form.parentName" placeholder="请输入家长姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="微信号">
          <el-input v-model="form.wechat" placeholder="请输入微信号" />
        </el-form-item>
        <el-form-item label="紧急联系人">
          <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人" />
        </el-form-item>
        <el-form-item label="紧急联系电话">
          <el-input v-model="form.emergencyPhone" placeholder="请输入紧急联系电话" />
        </el-form-item>
        <el-form-item label="接送人">
          <el-input v-model="form.pickupPerson" placeholder="请输入接送人" />
        </el-form-item>
        <el-form-item label="过敏史">
          <el-input
            v-model="form.allergyHistory"
            type="textarea"
            :rows="2"
            placeholder="请输入过敏史"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">在园</el-radio>
            <el-radio :value="0">离园</el-radio>
          </el-radio-group>
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
import { getChildList, addChild, editChild, deleteChild } from '@/api/child'
import { getClassOptions } from '@/api/class'

const loading = ref(false)
const tableData = ref([])
const classOptions = ref([])

const searchForm = reactive({
  name: '',
  classId: null,
  status: null
})

const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增幼儿')
const submitLoading = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  gender: 1,
  birthDate: '',
  classId: null,
  enrollDate: '',
  parentName: '',
  contactPhone: '',
  wechat: '',
  emergencyContact: '',
  emergencyPhone: '',
  pickupPerson: '',
  allergyHistory: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入幼儿姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  birthDate: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  enrollDate: [{ required: true, message: '请选择入园日期', trigger: 'change' }],
  parentName: [{ required: true, message: '请输入家长姓名', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const fetchClassOptions = async () => {
  try {
    const res = await getClassOptions()
    classOptions.value = res.data || []
  } catch (error) {
    console.error('获取班级选项失败:', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      limit: pagination.limit,
      ...searchForm
    }
    const res = await getChildList(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取幼儿列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.classId = null
  searchForm.status = null
  handleSearch()
}

const handlePagination = () => {
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增幼儿'
  Object.assign(form, {
    id: null,
    name: '',
    gender: 1,
    birthDate: '',
    classId: null,
    enrollDate: '',
    parentName: '',
    contactPhone: '',
    wechat: '',
    emergencyContact: '',
    emergencyPhone: '',
    pickupPerson: '',
    allergyHistory: '',
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑幼儿'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该幼儿吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteChild(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (form.id) {
          await editChild(form)
          ElMessage.success('修改成功')
        } else {
          await addChild(form)
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
  fetchClassOptions()
  fetchData()
})
</script>

<style lang="scss" scoped>
.table-toolbar {
  margin-bottom: 16px;
}
</style>
