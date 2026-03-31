<template>
  <el-dialog
    v-model="visible"
    :title="dialogTitle"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
    >
      <el-form-item label="上级菜单" prop="parentId">
        <el-tree-select
          v-model="formData.parentId"
          :data="menuTreeData"
          :props="treeProps"
          check-strictly
          clearable
          placeholder="请选择上级菜单"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="菜单名称" prop="menuName">
        <el-input
          v-model="formData.menuName"
          placeholder="请输入菜单名称"
          maxlength="50"
        />
      </el-form-item>
      <el-form-item label="菜单类型" prop="menuType">
        <el-radio-group v-model="formData.menuType">
          <el-radio :label="1">目录</el-radio>
          <el-radio :label="2">菜单</el-radio>
          <el-radio :label="3">按钮</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="路由路径" prop="routePath" v-if="formData.menuType !== 3">
        <el-input
          v-model="formData.routePath"
          placeholder="请输入路由路径"
          maxlength="200"
        />
      </el-form-item>
      <el-form-item label="组件路径" prop="componentPath" v-if="formData.menuType === 2">
        <el-input
          v-model="formData.componentPath"
          placeholder="请输入组件路径"
          maxlength="200"
        />
      </el-form-item>
      <el-form-item label="权限标识" prop="permission" v-if="formData.menuType === 3">
        <el-input
          v-model="formData.permission"
          placeholder="请输入权限标识"
          maxlength="100"
        />
      </el-form-item>
      <el-form-item label="图标" prop="icon" v-if="formData.menuType !== 3">
        <IconSelect v-model="formData.icon" />
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number
          v-model="formData.sort"
          :min="0"
          :max="999"
          controls-position="right"
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
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import IconSelect from '@/components/common/IconSelect.vue'
import { create, update, getById, getTree } from '@/api/menu'

// 定义Props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  menuId: {
    type: [Number, String],
    default: null
  },
  parentId: {
    type: [Number, String],
    default: null
  }
})

// 定义Emits
const emit = defineEmits(['update:modelValue', 'success'])

// 弹窗显示状态
const visible = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emit('update:modelValue', val)
  }
})

// 弹窗标题
const dialogTitle = computed(() => {
  return props.menuId ? '编辑菜单' : '新增菜单'
})

// 是否为编辑模式
const isEdit = computed(() => {
  return !!props.menuId
})

// 表单引用
const formRef = ref(null)

// 加载状态
const loading = ref(false)

// 菜单树数据
const menuTreeData = ref([])

// 树属性配置
const treeProps = {
  children: 'children',
  label: 'menuName',
  value: 'id'
}

// 表单数据
const formData = reactive({
  id: null,
  parentId: null,
  menuName: '',
  menuType: 1,
  routePath: '',
  componentPath: '',
  permission: '',
  icon: '',
  sort: 0,
  status: 1
})

// 表单验证规则
const formRules = {
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' },
    { min: 2, max: 50, message: '菜单名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  menuType: [
    { required: true, message: '请选择菜单类型', trigger: 'change' }
  ],
  routePath: [
    { required: true, message: '请输入路由路径', trigger: 'blur' }
  ],
  componentPath: [
    { required: true, message: '请输入组件路径', trigger: 'blur' }
  ],
  permission: [
    { required: true, message: '请输入权限标识', trigger: 'blur' }
  ]
}

// 获取菜单树
const fetchMenuTree = async () => {
  try {
    const res = await getTree()
    menuTreeData.value = [{ id: 0, menuName: '根目录', children: res.data || [] }]
  } catch (error) {
    console.error('获取菜单树失败:', error)
  }
}

// 获取菜单详情
const fetchMenuDetail = async () => {
  if (!props.menuId) return
  try {
    const res = await getById(props.menuId)
    const data = res.data
    formData.id = data.id
    formData.parentId = data.parentId || 0
    formData.menuName = data.menuName
    formData.menuType = data.menuType
    formData.routePath = data.routePath
    formData.componentPath = data.componentPath
    formData.permission = data.permission
    formData.icon = data.icon || ''
    formData.sort = data.sort
    formData.status = data.status
  } catch (error) {
    console.error('获取菜单详情失败:', error)
  }
}

// 重置表单
const resetForm = () => {
  formData.id = null
  formData.parentId = props.parentId || 0
  formData.menuName = ''
  formData.menuType = 1
  formData.routePath = ''
  formData.componentPath = ''
  formData.permission = ''
  formData.icon = ''
  formData.sort = 0
  formData.status = 1
  formRef.value?.resetFields()
}

// 关闭弹窗
const handleClose = () => {
  resetForm()
  visible.value = false
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    loading.value = true
    const submitData = { ...formData }
    if (submitData.parentId === 0) {
      submitData.parentId = null
    }
    if (isEdit.value) {
      await update(submitData)
      ElMessage.success('更新成功')
    } else {
      await create(submitData)
      ElMessage.success('新增成功')
    }
    emit('success')
    handleClose()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    loading.value = false
  }
}

// 监听弹窗打开
watch(visible, (val) => {
  if (val) {
    resetForm()
    fetchMenuTree()
    if (props.menuId) {
      fetchMenuDetail()
    } else {
      formData.parentId = props.parentId || 0
    }
  }
})
</script>

<style lang="scss" scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>