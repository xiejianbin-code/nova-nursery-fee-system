<template>
  <el-dialog
    :model-value="visible"
    title="批量出勤登记"
    width="800px"
    :close-on-click-modal="false"
    @update:model-value="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="80px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="班级" prop="classId">
            <el-select
              v-model="formData.classId"
              placeholder="请选择班级"
              style="width: 100%"
              @change="handleClassChange"
            >
              <el-option
                v-for="item in classOptions"
                :key="item.id"
                :label="item.className"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="日期" prop="date">
            <el-date-picker
              v-model="formData.date"
              type="date"
              placeholder="请选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <el-button type="success" size="small" @click="handleBatchStatus(1)">全部正常到园</el-button>
      <el-button type="warning" size="small" @click="handleBatchStatus(2)">全部请假</el-button>
      <el-button type="info" size="small" @click="handleBatchStatus(3)">全部病假</el-button>
      <el-button type="danger" size="small" @click="handleBatchStatus(4)">全部缺勤</el-button>
    </div>

    <!-- 幼儿列表 -->
    <el-table
      ref="tableRef"
      :data="childList"
      border
      stripe
      max-height="400px"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" />
      <el-table-column prop="name" label="幼儿姓名" min-width="100" />
      <el-table-column prop="gender" label="性别" width="80">
        <template #default="{ row }">
          {{ row.gender === 1 ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column label="出勤状态" width="150">
        <template #default="{ row }">
          <el-select v-model="row.status" placeholder="请选择" size="small">
            <el-option label="正常到园" :value="1" />
            <el-option label="请假" :value="2" />
            <el-option label="病假" :value="3" />
            <el-option label="缺勤" :value="4" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="备注" min-width="150">
        <template #default="{ row }">
          <el-input v-model="row.remark" placeholder="备注" size="small" />
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { batchRegisterAttendance } from '@/api/attendance'
import { getChildList } from '@/api/child'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  classOptions: {
    type: Array,
    default: () => []
  },
  date: {
    type: String,
    default: ''
  }
})

// Emits
const emit = defineEmits(['update:visible', 'success'])

// 表单引用
const formRef = ref(null)
const tableRef = ref(null)

// 提交加载状态
const submitLoading = ref(false)

// 表单数据
const formData = reactive({
  classId: null,
  date: ''
})

// 表单校验规则
const rules = {
  classId: [
    { required: true, message: '请选择班级', trigger: 'change' }
  ],
  date: [
    { required: true, message: '请选择日期', trigger: 'change' }
  ]
}

// 幼儿列表
const childList = ref([])

// 选中的幼儿
const selectedChildren = ref([])

// 监听visible变化
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      formData.date = props.date || new Date().toISOString().split('T')[0]
      formData.classId = null
      childList.value = []
    }
  }
)

// 班级变化时获取幼儿列表
const handleClassChange = async (classId) => {
  if (!classId) {
    childList.value = []
    return
  }

  try {
    const res = await getChildList({ page: 1, limit: 1000, classId, status: 1 })
    childList.value = (res.data?.records || []).map(item => ({
      ...item,
      status: 1,
      remark: ''
    }))
  } catch (error) {
    console.error('获取幼儿列表失败:', error)
  }
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedChildren.value = selection
}

// 批量设置状态
const handleBatchStatus = (status) => {
  if (selectedChildren.value.length === 0) {
    ElMessage.warning('请先选择幼儿')
    return
  }
  selectedChildren.value.forEach(item => {
    item.status = status
  })
}

// 关闭弹窗
const handleClose = () => {
  childList.value = []
  selectedChildren.value = []
  emit('update:visible', false)
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    if (childList.value.length === 0) {
      ElMessage.warning('请选择班级后再提交')
      return
    }

    submitLoading.value = true

    const normalChildIds = childList.value
      .filter(item => item.status === 1)
      .map(item => item.id)
    const leaveChildIds = childList.value
      .filter(item => item.status === 2)
      .map(item => item.id)
    const sickChildIds = childList.value
      .filter(item => item.status === 3)
      .map(item => item.id)
    const absentChildIds = childList.value
      .filter(item => item.status === 4)
      .map(item => item.id)

    await batchRegisterAttendance({
      classId: formData.classId,
      attendanceDate: formData.date,
      childIds: normalChildIds.length > 0 ? normalChildIds : undefined,
      leaveChildIds: leaveChildIds.length > 0 ? leaveChildIds : undefined,
      sickChildIds: sickChildIds.length > 0 ? sickChildIds : undefined,
      absentChildIds: absentChildIds.length > 0 ? absentChildIds : undefined
    })

    ElMessage.success('登记成功')
    emit('success')
    handleClose()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}
</script>

<style lang="scss" scoped>
.quick-actions {
  margin-bottom: 16px;
}
</style>
