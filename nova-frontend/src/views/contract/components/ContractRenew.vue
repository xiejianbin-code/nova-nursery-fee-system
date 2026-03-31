<template>
  <el-dialog
    :model-value="visible"
    title="合同续签"
    width="550px"
    :close-on-click-modal="false"
    @update:model-value="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="原合同编号">
        <el-input :model-value="contractData?.contractNo" disabled />
      </el-form-item>
      <el-form-item label="幼儿姓名">
        <el-input :model-value="contractData?.childName" disabled />
      </el-form-item>
      <el-form-item label="新合同名称" prop="contractName">
        <el-input
          v-model="formData.contractName"
          placeholder="请输入新合同名称"
          maxlength="100"
        />
      </el-form-item>
      <el-form-item label="新服务周期" prop="servicePeriod">
        <el-date-picker
          v-model="formData.servicePeriod"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 100%"
        />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="合同金额" prop="amount">
            <el-input-number
              v-model="formData.amount"
              :min="0"
              :precision="2"
              placeholder="请输入金额"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="课程类型" prop="courseType">
            <el-select v-model="formData.courseType" placeholder="请选择" style="width: 100%">
              <el-option
                v-for="item in courseTypes"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入备注"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { renewContract, getCourseTypes } from '@/api/contract'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  contractData: {
    type: Object,
    default: () => ({})
  }
})

// Emits
const emit = defineEmits(['update:visible', 'success'])

// 表单引用
const formRef = ref(null)

// 提交加载状态
const submitLoading = ref(false)

// 课程类型
const courseTypes = ref([])

// 表单数据
const formData = reactive({
  contractName: '',
  servicePeriod: [],
  amount: 0,
  courseType: '',
  remark: ''
})

// 表单校验规则
const rules = {
  contractName: [
    { required: true, message: '请输入新合同名称', trigger: 'blur' }
  ],
  servicePeriod: [
    { required: true, message: '请选择新服务周期', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入合同金额', trigger: 'blur' }
  ],
  courseType: [
    { required: true, message: '请选择课程类型', trigger: 'change' }
  ]
}

// 获取课程类型
const fetchCourseTypes = async () => {
  try {
    const res = await getCourseTypes()
    courseTypes.value = res.data || []
  } catch (error) {
    console.error('获取课程类型失败:', error)
  }
}

// 监听visible变化
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      // 初始化表单数据
      if (props.contractData) {
        formData.contractName = props.contractData.contractName + '(续签)'
        formData.courseType = props.contractData.courseType
        formData.amount = props.contractData.amount
      }
    } else {
      resetForm()
    }
  }
)

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    contractName: '',
    servicePeriod: [],
    amount: 0,
    courseType: '',
    remark: ''
  })
  formRef.value?.resetFields()
}

// 关闭弹窗
const handleClose = () => {
  resetForm()
  emit('update:visible', false)
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true

    await renewContract({
      id: props.contractData.id,
      contractName: formData.contractName,
      startDate: formData.servicePeriod[0],
      endDate: formData.servicePeriod[1],
      amount: formData.amount,
      courseType: formData.courseType,
      remark: formData.remark
    })

    ElMessage.success('续签成功')
    emit('success')
    handleClose()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 初始化
onMounted(() => {
  fetchCourseTypes()
})
</script>

<style lang="scss" scoped>
:deep(.el-input-number) {
  width: 100%;
}
</style>
