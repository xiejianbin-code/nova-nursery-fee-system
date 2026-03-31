<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 表格区域 -->
      <div class="table-area">
        <!-- 工具栏 -->
        <div class="table-toolbar">
          <el-button type="primary" @click="handleAdd(null)">新增菜单</el-button>
          <el-button @click="toggleExpandAll">
            {{ isExpandAll ? '折叠全部' : '展开全部' }}
          </el-button>
        </div>

        <!-- 表格 -->
        <el-table
          ref="tableRef"
          :data="tableData"
          v-loading="loading"
          border
          stripe
          row-key="id"
          :default-expand-all="isExpandAll"
          :tree-props="{ children: 'children' }"
        >
          <el-table-column prop="menuName" label="菜单名称" min-width="180" />
          <el-table-column prop="icon" label="图标" width="100" align="center">
            <template #default="{ row }">
              <el-icon v-if="row.icon" :size="18">
                <component :is="row.icon" />
              </el-icon>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="sort" label="排序" width="80" align="center" />
          <el-table-column prop="menuType" label="类型" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getTypeTagType(row.menuType)">
                {{ getTypeText(row.menuType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="routePath" label="路由路径" min-width="150" />
          <el-table-column prop="permission" label="权限标识" min-width="150" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 1 ? '启用' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button
                type="success"
                link
                @click="handleAdd(row)"
                v-if="row.menuType !== 3"
              >
                新增
              </el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 菜单表单弹窗 -->
    <MenuForm
      v-model="showForm"
      :menu-id="currentMenuId"
      :parent-id="currentParentId"
      @success="handleFormSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import MenuForm from './components/MenuForm.vue'
import { getTree, deleteById } from '@/api/menu'

// 表格引用
const tableRef = ref(null)

// 加载状态
const loading = ref(false)

// 表格数据
const tableData = ref([])

// 是否展开全部
const isExpandAll = ref(true)

// 弹窗显示状态
const showForm = ref(false)

// 当前菜单ID
const currentMenuId = ref(null)

// 当前父级菜单ID
const currentParentId = ref(null)

// 获取菜单类型文本
const getTypeText = (type) => {
  const typeMap = {
    1: '目录',
    2: '菜单',
    3: '按钮'
  }
  return typeMap[type] || '未知'
}

// 获取菜单类型标签类型
const getTypeTagType = (type) => {
  const typeMap = {
    1: 'primary',
    2: 'success',
    3: 'warning'
  }
  return typeMap[type] || 'info'
}

// 获取表格数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getTree()
    tableData.value = res.data || []
  } catch (error) {
    console.error('获取菜单树失败:', error)
  } finally {
    loading.value = false
  }
}

// 切换展开/折叠
const toggleExpandAll = () => {
  isExpandAll.value = !isExpandAll.value
  toggleRowExpansion(tableData.value, isExpandAll.value)
}

// 递归展开/折叠行
const toggleRowExpansion = (data, expanded) => {
  data.forEach(row => {
    tableRef.value?.toggleRowExpansion(row, expanded)
    if (row.children && row.children.length) {
      toggleRowExpansion(row.children, expanded)
    }
  })
}

// 新增菜单
const handleAdd = (row) => {
  currentMenuId.value = null
  currentParentId.value = row ? row.id : null
  showForm.value = true
}

// 编辑菜单
const handleEdit = (row) => {
  currentMenuId.value = row.id
  currentParentId.value = null
  showForm.value = true
}

// 删除菜单
const handleDelete = (row) => {
  const hasChildren = row.children && row.children.length
  const message = hasChildren
    ? `菜单【${row.menuName}】下存在子菜单，确定要删除吗？`
    : `确定要删除菜单【${row.menuName}】吗？`

  ElMessageBox.confirm(message, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await deleteById(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error('删除菜单失败:', error)
    }
  }).catch(() => {})
}

// 表单提交成功
const handleFormSuccess = () => {
  fetchData()
}

// 初始化
onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.table-toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
}

.table-area {
  max-height: calc(100vh - 120px);
  overflow-y: auto;
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
  }
  
  &::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
  }
}
</style>