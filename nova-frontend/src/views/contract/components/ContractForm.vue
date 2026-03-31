<template>
  <el-dialog
    :model-value="visible"
    title="新增合同"
    width="650px"
    :close-on-click-modal="false"
    @update:model-value="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="幼儿" prop="childId">
        <el-select
          v-model="formData.childId"
          placeholder="请选择幼儿"
          filterable
          style="width: 100%"
        >
          <el-option
            v-for="item in childOptions"
            :key="item.id"
            :label="item.childName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="合同名称" prop="contractName">
        <el-input
          v-model="formData.contractName"
          placeholder="请输入合同名称"
          maxlength="100"
        />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="课程类型" prop="courseType">
            <el-select v-model="formData.courseType" placeholder="请选择课程类型" style="width: 100%">
              <el-option
                v-for="item in courseTypes"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="合同金额" prop="amount">
            <el-input-number
              v-model="formData.amount"
              :min="0"
              :precision="2"
              placeholder="请输入合同金额"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="服务周期" prop="servicePeriod">
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
          <el-form-item label="保教费" prop="educationFee">
            <el-input-number
              v-model="formData.educationFee"
              :min="0"
              :precision="2"
              placeholder="请输入保教费"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="伙食费" prop="foodFee">
            <el-input-number
              v-model="formData.foodFee"
              :min="0"
              :precision="2"
              placeholder="请输入伙食费"
              style="width: 100%"
            />
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
import { addContract, getCourseTypes } from '@/api/contract'
import { getChildOptions } from '@/api/child'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['update:visible', 'success'])

// 表单引用
const formRef = ref(null)

// 提交加载状态
const submitLoading = ref(false)

// 幼儿选项
const childOptions = ref([])

// 课程类型
const courseTypes = ref([])

// 表单数据
const formData = reactive({
  childId: null,
  contractName: '',
  courseType: '',
  amount: 0,
  servicePeriod: [],
  educationFee: 0,
  foodFee: 0,
  remark: ''
})

// 表单校验规则
const rules = {
  childId: [
    { required: true, message: '请选择幼儿', trigger: 'change' }
  ],
  contractName: [
    { required: true, message: '请输入合同名称', trigger: 'blur' }
  ],
  courseType: [
    { required: true, message: '请选择课程类型', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入合同金额', trigger: 'blur' }
  ],
  servicePeriod: [
    { required: true, message: '请选择服务周期', trigger: 'change' }
  ]
}

// 获取幼儿选项
const fetchChildOptions = async () => {
  try {
    const res = await getChildOptions({ status: 1 })
    childOptions.value = res.data || []
  } catch (error) {
    console.error('获取幼儿选项失败:', error)
  }
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
      resetForm()
    }
  }
)

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    childId: null,
    contractName: '',
    courseType: '',
    amount: 0,
    servicePeriod: [],
    educationFee: 0,
    foodFee: 0,
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

    const submitData = {
      ...formData,
      startDate: formData.servicePeriod[0],
      endDate: formData.servicePeriod[1]
    }
    delete submitData.servicePeriod

    await addContract(submitData)
    ElMessage.success('新增成功')
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
  fetchChildOptions()
  fetchCourseTypes()
})
</script>

<style lang="scss" scoped>
:deep(.el-input-number) {
  width: 100%;
}
</style>
