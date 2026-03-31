<template>
  <div class="navbar-container">
    <div class="navbar-left">
      <div class="collapse-btn" @click="toggleCollapse">
        <el-icon :size="20">
          <Fold v-if="!isCollapse" />
          <Expand v-else />
        </el-icon>
      </div>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
          {{ item.meta?.title || item.name }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="navbar-right">
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="32" class="user-avatar">
            {{ username?.charAt(0)?.toUpperCase() || 'U' }}
          </el-avatar>
          <span class="username">{{ username || '用户' }}</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <el-dialog
      v-model="profileDialogVisible"
      title="个人中心"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        v-loading="loading"
      >
        <el-form-item label="用户名">
          <el-input v-model="formData.username" disabled />
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="角色">
          <el-tag
            v-for="role in formData.roleNames"
            :key="role"
            type="primary"
            class="role-tag"
          >
            {{ role }}
          </el-tag>
          <span v-if="!formData.roleNames || formData.roleNames.length === 0">暂无角色</span>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="profileDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          保存修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Fold, Expand, ArrowDown, User, Setting, SwitchButton } from '@element-plus/icons-vue'
import useUserStore from '@/store/modules/user'
import { updateCurrentUserInfo } from '@/api/user'

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['toggle-collapse'])

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const username = computed(() => userStore.username)

const breadcrumbs = computed(() => {
  return route.matched.filter(item => item.meta?.title)
})

const toggleCollapse = () => {
  emit('toggle-collapse')
}

const profileDialogVisible = ref(false)
const formRef = ref(null)
const loading = ref(false)
const submitLoading = ref(false)

const formData = reactive({
  username: '',
  realName: '',
  phone: '',
  roleNames: []
})

const formRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const initFormData = () => {
  const userInfo = userStore.userInfo
  if (userInfo) {
    formData.username = userInfo.username || ''
    formData.realName = userInfo.realName || ''
    formData.phone = userInfo.phone || ''
    formData.roleNames = userStore.roleNames || []
  }
}

watch(profileDialogVisible, (val) => {
  if (val) {
    initFormData()
  }
})

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      profileDialogVisible.value = true
      break
    case 'logout':
      handleLogout()
      break
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    await updateCurrentUserInfo({
      realName: formData.realName,
      phone: formData.phone
    })
    ElMessage.success('修改成功')
    profileDialogVisible.value = false
    userStore.getUserInfo()
  } catch (error) {
    console.error('修改失败:', error)
  } finally {
    submitLoading.value = false
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logoutAction()
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.navbar-container {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  z-index: 10;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 16px;

  .collapse-btn {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: 4px;
    transition: background 0.3s;

    &:hover {
      background: #f5f7fa;
    }
  }
}

.navbar-right {
  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 4px;
    transition: background 0.3s;

    &:hover {
      background: #f5f7fa;
    }

    .user-avatar {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .username {
      font-size: 14px;
      color: #333;
    }
  }
}

.role-tag {
  margin-right: 8px;
}
</style>
