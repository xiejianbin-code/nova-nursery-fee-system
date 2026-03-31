<template>
  <el-dialog
    v-model="visible"
    title="配置权限"
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="permission-header">
      <el-checkbox
        v-model="checkAll"
        :indeterminate="isIndeterminate"
        @change="handleCheckAll"
      >
        全选
      </el-checkbox>
      <el-button type="primary" link @click="handleExpandAll">
        {{ isExpandAll ? '折叠全部' : '展开全部' }}
      </el-button>
    </div>
    <el-tree
      ref="treeRef"
      :data="menuTree"
      :props="treeProps"
      show-checkbox
      node-key="id"
      :default-expand-all="isExpandAll"
      :default-checked-keys="checkedKeys"
      @check="handleCheck"
    >
      <template #default="{ node, data }">
        <span class="tree-node">
          <el-icon v-if="data.icon" class="node-icon">
            <component :is="data.icon" />
          </el-icon>
          <span>{{ data.menuName }}</span>
        </span>
      </template>
    </el-tree>
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
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getMenus, updateMenus } from '@/api/role'
import { getTree as getMenuTree } from '@/api/menu'

// 定义Props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  roleId: {
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

// 树引用
const treeRef = ref(null)

// 加载状态
const loading = ref(false)

// 菜单树数据
const menuTree = ref([])

// 已选中的菜单ID
const checkedKeys = ref([])

// 是否全选
const checkAll = ref(false)

// 是否半选
const isIndeterminate = ref(false)

// 是否展开全部
const isExpandAll = ref(true)

// 树属性配置
const treeProps = {
  children: 'children',
  label: 'menuName'
}

// 获取菜单树
const fetchMenuTree = async () => {
  try {
    const res = await getMenuTree()
    menuTree.value = res.data || []
  } catch (error) {
    console.error('获取菜单树失败:', error)
  }
}

// 获取角色菜单
const fetchRoleMenus = async () => {
  if (!props.roleId) return
  try {
    const res = await getMenus(props.roleId)
    checkedKeys.value = res.data || []
    await nextTick()
    updateCheckAllStatus()
  } catch (error) {
    console.error('获取角色菜单失败:', error)
  }
}

// 更新全选状态
const updateCheckAllStatus = () => {
  const allNodeIds = getAllNodeIds(menuTree.value)
  const checkedNodeIds = treeRef.value?.getCheckedKeys() || []
  checkAll.value = checkedNodeIds.length === allNodeIds.length && allNodeIds.length > 0
  isIndeterminate.value = checkedNodeIds.length > 0 && checkedNodeIds.length < allNodeIds.length
}

// 获取所有节点ID
const getAllNodeIds = (nodes) => {
  let ids = []
  nodes.forEach(node => {
    ids.push(node.id)
    if (node.children && node.children.length) {
      ids = ids.concat(getAllNodeIds(node.children))
    }
  })
  return ids
}

// 全选/取消全选
const handleCheckAll = (val) => {
  if (val) {
    treeRef.value?.setCheckedNodes(menuTree.value)
  } else {
    treeRef.value?.setCheckedKeys([])
  }
  isIndeterminate.value = false
}

// 展开/折叠全部
const handleExpandAll = () => {
  isExpandAll.value = !isExpandAll.value
  const nodes = treeRef.value?.store?.nodesMap || {}
  Object.values(nodes).forEach(node => {
    node.expanded = isExpandAll.value
  })
}

// 选中变化
const handleCheck = () => {
  updateCheckAllStatus()
}

// 关闭弹窗
const handleClose = () => {
  checkedKeys.value = []
  checkAll.value = false
  isIndeterminate.value = false
  visible.value = false
}

// 提交
const handleSubmit = async () => {
  if (!props.roleId) return
  try {
    loading.value = true
    const menuIds = treeRef.value?.getCheckedKeys() || []
    await updateMenus(props.roleId, menuIds)
    ElMessage.success('配置成功')
    emit('success')
    handleClose()
  } catch (error) {
    console.error('配置权限失败:', error)
  } finally {
    loading.value = false
  }
}

// 监听弹窗打开
watch(visible, (val) => {
  if (val) {
    fetchMenuTree()
    fetchRoleMenus()
  }
})
</script>

<style lang="scss" scoped>
.permission-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 6px;

  .node-icon {
    font-size: 16px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
