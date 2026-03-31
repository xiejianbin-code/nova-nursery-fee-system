<template>
  <el-dialog
    :model-value="visible"
    title="账单减免"
    width="450px"
    :close-on-click-modal="false"
    @update:model-value="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="80px"
    >
      <el-form-item label="账单编号">
        <el-input :model-value="billData?.billNo" disabled />
      </el-form-item>
      <el-form-item label="幼儿姓名">
        <el-input :model-value="billData?.childName" disabled />
      </el-form-item>
      <el-form-item label="当前应交">
        <el-input :model-value="`¥${(billData?.actualAmount || 0).toFixed(2)}`" disabled />
      </el-form-item>
      <el-form-item label="减免金额" prop="discountAmount">
        <el-input-number
          v-model="formData.discountAmount"
          :min="0.01"
          :max="billData?.actualAmount || 999999"
          :precision="2"
          placeholder="请输入减免金额"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="减免原因" prop="discountReason">
        <el-input
          v-model="formData.discountReason"
          type="textarea"
          :rows="3"
          placeholder="请输入减免原因"
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
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { discountBill } from '@/api/bill'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  billData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:visible', 'success'])

const formRef = ref(null)
const submitLoading = ref(false)

const formData = reactive({
  discountAmount: null,
  discountReason: ''
})

const rules = {
  discountAmount: [
    { required: true, message: '请输入减免金额', trigger: 'blur' }
  ],
  discountReason: [
    { required: true, message: '请输入减免原因', trigger: 'blur' }
  ]
}

watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      resetForm()
    }
  }
)

const resetForm = () => {
  formData.discountAmount = null
  formData.discountReason = ''
  formRef.value?.resetFields()
}

const handleClose = () => {
  resetForm()
  emit('update:visible', false)
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true

    await discountBill({
      billId: props.billData.id,
      discountAmount: formData.discountAmount,
      discountReason: formData.discountReason
    })

    ElMessage.success('减免成功')
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
:deep(.el-input-number) {
  width: 100%;
}
</style>
