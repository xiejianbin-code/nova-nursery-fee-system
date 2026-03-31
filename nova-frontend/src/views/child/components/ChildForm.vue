<template>
  <el-dialog
    :model-value="visible"
    :title="formData.id ? '编辑幼儿' : '新增幼儿'"
    width="600px"
    :close-on-click-modal="false"
    @update:model-value="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="幼儿姓名" prop="childName">
            <el-input
              v-model="formData.childName"
              placeholder="请输入幼儿姓名"
              maxlength="20"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="formData.gender">
              <el-radio :label="1">男</el-radio>
              <el-radio :label="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="出生日期" prop="birthDate">
            <el-date-picker
              v-model="formData.birthDate"
              type="date"
              placeholder="请选择出生日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属班级" prop="classId">
            <el-select v-model="formData.classId" placeholder="请选择班级" style="width: 100%">
              <el-option
                v-for="item in classOptions"
                :key="item.id"
                :label="item.className"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="家长姓名" prop="parentName">
            <el-input
              v-model="formData.parentName"
              placeholder="请输入家长姓名"
              maxlength="20"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话" prop="parentPhone">
            <el-input
              v-model="formData.parentPhone"
              placeholder="请输入联系电话"
              maxlength="20"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="家长关系" prop="parentRelation">
            <el-select v-model="formData.parentRelation" placeholder="请选择关系" style="width: 100%">
              <el-option label="父亲" value="父亲" />
              <el-option label="母亲" value="母亲" />
              <el-option label="爷爷" value="爷爷" />
              <el-option label="奶奶" value="奶奶" />
              <el-option label="外公" value="外公" />
              <el-option label="外婆" value="外婆" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="入园日期" prop="entryDate">
            <el-date-picker
              v-model="formData.entryDate"
              type="date"
              placeholder="请选择入园日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="家庭住址" prop="address">
        <el-input
          v-model="formData.address"
          placeholder="请输入家庭住址"
          maxlength="200"
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
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { addChild, editChild } from '@/api/child'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  formData: {
    type: Object,
    default: () => ({})
  },
  classOptions: {
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

// 表单数据
const formData = reactive({
  id: null,
  childName: '',
  gender: 1,
  birthDate: '',
  classId: null,
  parentName: '',
  parentPhone: '',
  parentRelation: '',
  entryDate: '',
  address: '',
  remark: ''
})

// 表单校验规则
const rules = {
  childName: [
    { required: true, message: '请输入幼儿姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度为2-20个字符', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  birthDate: [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ],
  classId: [
    { required: true, message: '请选择所属班级', trigger: 'change' }
  ],
  parentName: [
    { required: true, message: '请输入家长姓名', trigger: 'blur' }
  ],
  parentPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  parentRelation: [
    { required: true, message: '请选择家长关系', trigger: 'change' }
  ],
  entryDate: [
    { required: true, message: '请选择入园日期', trigger: 'change' }
  ]
}

// 监听props变化，初始化表单
watch(
  () => props.formData,
  (newVal) => {
    if (newVal && Object.keys(newVal).length > 0) {
      Object.assign(formData, {
        id: newVal.id || null,
        childName: newVal.childName || '',
        gender: newVal.gender || 1,
        birthDate: newVal.birthDate || '',
        classId: newVal.classId || null,
        parentName: newVal.parentName || '',
        parentPhone: newVal.parentPhone || '',
        parentRelation: newVal.parentRelation || '',
        entryDate: newVal.entryDate || '',
        address: newVal.address || '',
        remark: newVal.remark || ''
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
    childName: '',
    gender: 1,
    birthDate: '',
    classId: null,
    parentName: '',
    parentPhone: '',
    parentRelation: '',
    entryDate: '',
    address: '',
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

    const api = formData.id ? editChild : addChild
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
:deep(.el-date-editor) {
  width: 100%;
}
</style>
