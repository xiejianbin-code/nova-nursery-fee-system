<template>
  <el-dialog
    :model-value="visible"
    :title="formData.id ? '编辑班级' : '新增班级'"
    width="500px"
    :close-on-click-modal="false"
    @update:model-value="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="班级名称" prop="className">
        <el-input
          v-model="formData.className"
          placeholder="请输入班级名称"
          maxlength="50"
        />
      </el-form-item>
      <el-form-item label="班主任" prop="teacherName">
        <el-input
          v-model="formData.teacherName"
          placeholder="请输入班主任姓名"
          maxlength="20"
        />
      </el-form-item>
      <el-form-item label="联系电话" prop="teacherPhone">
        <el-input
          v-model="formData.teacherPhone"
          placeholder="请输入联系电话"
          maxlength="20"
        />
      </el-form-item>
      <el-form-item label="班级容量" prop="capacity">
        <el-input-number
          v-model="formData.capacity"
          :min="1"
          :max="100"
          placeholder="请输入班级容量"
        />
      </el-form-item>
      <el-form-item label="班级描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="请输入班级描述"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :label="1">启用</el-radio>
          <el-radio :label="0">停用</el-radio>
        </el-radio-group>
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
import { addClass, editClass } from '@/api/class'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  formData: {
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
  id: null,
  className: '',
  teacherName: '',
  teacherPhone: '',
  capacity: 30,
  description: '',
  status: 1
})

// 表单校验规则
const rules = {
  className: [
    { required: true, message: '请输入班级名称', trigger: 'blur' },
    { min: 2, max: 50, message: '班级名称长度为2-50个字符', trigger: 'blur' }
  ],
  teacherName: [
    { required: true, message: '请输入班主任姓名', trigger: 'blur' }
  ],
  teacherPhone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  capacity: [
    { required: true, message: '请输入班级容量', trigger: 'blur' }
  ]
}

// 监听props变化，初始化表单
watch(
  () => props.formData,
  (newVal) => {
    if (newVal && Object.keys(newVal).length > 0) {
      Object.assign(formData, {
        id: newVal.id || null,
        className: newVal.className || '',
        teacherName: newVal.teacherName || '',
        teacherPhone: newVal.teacherPhone || '',
        capacity: newVal.capacity || 30,
        description: newVal.description || '',
        status: newVal.status ?? 1
      })
    } else {
      resetForm()
    }
  },
  { immediate: true, deep: true }
)

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    id: null,
    className: '',
    teacherName: '',
    teacherPhone: '',
    capacity: 30,
    description: '',
    status: 1
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

    const api = formData.id ? editClass : addClass
    await api(formData)

    ElMessage.success(formData.id ? '编辑成功' : '新增成功')
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
