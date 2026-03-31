<template>
  <el-dialog
    :model-value="visible"
    title="收费登记"
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
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="收费类型" prop="paymentType">
            <el-select v-model="formData.paymentType" placeholder="请选择" style="width: 100%">
              <el-option
                v-for="item in paymentTypes"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="收费方式" prop="paymentMethod">
            <el-select v-model="formData.paymentMethod" placeholder="请选择" style="width: 100%">
              <el-option
                v-for="item in paymentMethods"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="金额" prop="amount">
        <el-input-number
          v-model="formData.amount"
          :min="0"
          :precision="2"
          placeholder="请输入金额"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="收费日期" prop="paymentDate">
        <el-date-picker
          v-model="formData.paymentDate"
          type="date"
          placeholder="请选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 100%"
        />
      </el-form-item>
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
import { registerPayment } from '@/api/payment'
import { getChildOptions } from '@/api/child'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  paymentTypes: {
    type: Array,
    default: () => []
  },
  paymentMethods: {
    type: Array,
    default: () => []
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

// 表单数据
const formData = reactive({
  childId: null,
  paymentType: '',
  paymentMethod: '',
  amount: 0,
  paymentDate: new Date().toISOString().split('T')[0],
  remark: ''
})

// 表单校验规则
const rules = {
  childId: [
    { required: true, message: '请选择幼儿', trigger: 'change' }
  ],
  paymentType: [
    { required: true, message: '请选择收费类型', trigger: 'change' }
  ],
  paymentMethod: [
    { required: true, message: '请选择收费方式', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入金额', trigger: 'blur' }
  ],
  paymentDate: [
    { required: true, message: '请选择收费日期', trigger: 'change' }
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
    paymentType: '',
    paymentMethod: '',
    amount: 0,
    paymentDate: new Date().toISOString().split('T')[0],
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

    await registerPayment(formData)
    ElMessage.success('登记成功')
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
})
</script>

<style lang="scss" scoped>
:deep(.el-input-number) {
  width: 100%;
}
</style>
