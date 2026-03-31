<template>
  <el-dialog
    :model-value="visible"
    title="合同变更"
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
      <el-form-item label="合同编号">
        <el-input :model-value="contractData?.contractNo" disabled />
      </el-form-item>
      <el-form-item label="幼儿姓名">
        <el-input :model-value="contractData?.childName" disabled />
      </el-form-item>
      <el-form-item label="变更类型" prop="changeType">
        <el-select v-model="formData.changeType" placeholder="请选择变更类型" style="width: 100%">
          <el-option label="服务周期变更" value="服务周期变更" />
          <el-option label="费用变更" value="费用变更" />
          <el-option label="课程变更" value="课程变更" />
          <el-option label="其他变更" value="其他变更" />
        </el-select>
      </el-form-item>
      <el-form-item label="变更原因" prop="changeReason">
        <el-input
          v-model="formData.changeReason"
          type="textarea"
          :rows="3"
          placeholder="请输入变更原因"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="变更内容" prop="changeContent">
        <el-input
          v-model="formData.changeContent"
          type="textarea"
          :rows="3"
          placeholder="请输入变更内容"
          maxlength="500"
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
import { changeContract } from '@/api/contract'

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

// 表单数据
const formData = reactive({
  changeType: '',
  changeReason: '',
  changeContent: ''
})

// 表单校验规则
const rules = {
  changeType: [
    { required: true, message: '请选择变更类型', trigger: 'change' }
  ],
  changeReason: [
    { required: true, message: '请输入变更原因', trigger: 'blur' }
  ],
  changeContent: [
    { required: true, message: '请输入变更内容', trigger: 'blur' }
  ]
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
    changeType: '',
    changeReason: '',
    changeContent: ''
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

    await changeContract({
      id: props.contractData.id,
      ...formData
    })

    ElMessage.success('变更成功')
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
</style>
