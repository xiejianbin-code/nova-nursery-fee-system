<template>
  <el-dialog
    :model-value="visible"
    title="预警处理"
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
      <el-form-item label="幼儿姓名">
        <el-input :model-value="alertData?.childName" disabled />
      </el-form-item>
      <el-form-item label="预警类型">
        <el-tag :type="alertTypeMap[alertData?.alertType]" size="small">
          {{ alertData?.alertTypeName || alertData?.alertType }}
        </el-tag>
      </el-form-item>
      <el-form-item label="预警内容">
        <el-input :model-value="alertData?.alertContent" type="textarea" :rows="2" disabled />
      </el-form-item>
      <el-form-item label="触发时间">
        <el-input :model-value="alertData?.triggerTime" disabled />
      </el-form-item>
      <el-form-item label="处理说明" prop="handleRemark">
        <el-input
          v-model="formData.handleRemark"
          type="textarea"
          :rows="3"
          placeholder="请输入处理说明"
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
import { put } from '@/api/index'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  alertData: {
    type: Object,
    default: () => ({})
  }
})

// Emits
const emit = defineEmits(['update:visible', 'success'])

// 预警类型映射
const alertTypeMap = {
  'CONTRACT_EXPIRE': 'warning',
  'EDUCATION_FEE_EXPIRE': 'warning',
  'MEAL_FEE_EXPIRE': 'warning',
  'OVERDUE': 'danger',
  'EDUCATION_FEE_LOW': 'danger',
  'MEAL_FEE_LOW': 'danger',
  'OTHER_FEE_LOW': 'danger'
}

// 表单引用
const formRef = ref(null)

// 提交加载状态
const submitLoading = ref(false)

// 表单数据
const formData = reactive({
  handleRemark: ''
})

// 表单校验规则
const rules = {
  handleRemark: [
    { required: true, message: '请输入处理说明', trigger: 'blur' }
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
    handleRemark: ''
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

    await put('/alert/handle', null, {
      params: {
        id: props.alertData.id,
        remark: formData.handleRemark
      }
    })

    ElMessage.success('处理成功')
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
