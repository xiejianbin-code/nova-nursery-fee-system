<template>
  <el-dialog
    :model-value="visible"
    title="添加其他费用"
    width="500px"
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
      <el-form-item label="费用名称" prop="feeItem">
        <el-input
          v-model="formData.feeItem"
          placeholder="请输入费用名称"
          maxlength="50"
        />
      </el-form-item>
      <el-form-item label="费用金额" prop="amount">
        <el-input-number
          v-model="formData.amount"
          :min="0.01"
          :precision="2"
          placeholder="请输入费用金额"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="说明" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="请输入说明"
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
import { addOtherFee } from '@/api/bill'

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
  feeItem: '',
  amount: null,
  description: ''
})

const rules = {
  feeItem: [
    { required: true, message: '请输入费用名称', trigger: 'blur' }
  ],
  amount: [
    { required: true, message: '请输入费用金额', trigger: 'blur' }
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
  Object.assign(formData, {
    feeItem: '',
    amount: null,
    description: ''
  })
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

    await addOtherFee({
      billId: props.billData.id,
      feeItem: formData.feeItem,
      amount: formData.amount,
      description: formData.description
    })

    ElMessage.success('添加成功')
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
