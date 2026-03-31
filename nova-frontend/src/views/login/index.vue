<template>
  <div class="login-container">
    <!-- 左侧品牌区域 -->
    <div class="login-brand">
      <div class="brand-content">
        <div class="brand-logo">
          <div class="logo-icon">
            <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M24 4L42 14V34L24 44L6 34V14L24 4Z" stroke="currentColor" stroke-width="2" fill="none"/>
              <path d="M24 4V44" stroke="currentColor" stroke-width="2"/>
              <path d="M6 14L42 34" stroke="currentColor" stroke-width="2"/>
              <path d="M42 14L6 34" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <h1 class="brand-name">Nova</h1>
        </div>
        <p class="brand-slogan">企业级管理系统解决方案</p>
        <div class="brand-features">
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              </svg>
            </div>
            <span>安全可靠</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/>
                <polyline points="12,6 12,12 16,14"/>
              </svg>
            </div>
            <span>高效便捷</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                <circle cx="9" cy="7" r="4"/>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
              </svg>
            </div>
            <span>团队协作</span>
          </div>
        </div>
      </div>
      <!-- 装饰元素 -->
      <div class="brand-decoration">
        <div class="decoration-circle circle-1"></div>
        <div class="decoration-circle circle-2"></div>
        <div class="decoration-circle circle-3"></div>
        <div class="decoration-dots"></div>
      </div>
    </div>

    <!-- 右侧登录表单区域 -->
    <div class="login-form-wrapper">
      <div class="login-form-container">
        <div class="form-header">
          <h2 class="form-title">欢迎登录</h2>
          <p class="form-subtitle">请输入您的账号信息</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              prefix-icon="User"
              clearable
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item prop="captcha" v-if="showCaptcha">
            <div class="captcha-wrapper">
              <el-input
                v-model="loginForm.captcha"
                placeholder="请输入验证码"
                size="large"
                prefix-icon="Key"
                clearable
                @keyup.enter="handleLogin"
              />
              <div class="captcha-image" @click="refreshCaptcha">
                <img v-if="captchaUrl" :src="captchaUrl" alt="验证码" />
                <span v-else class="captcha-placeholder">点击获取</span>
              </div>
            </div>
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="rememberMe">记住密码</el-checkbox>
            <el-link type="primary" :underline="false" class="forgot-link">
              忘记密码?
            </el-link>
          </div>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="login-button"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="form-footer">
          <span class="footer-text">还没有账号?</span>
          <el-link type="primary" :underline="false">联系管理员</el-link>
        </div>
      </div>

      <!-- 版权信息 -->
      <div class="copyright">
        <p>Copyright © 2024 Nova. All Rights Reserved.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import useUserStore from '@/store/modules/user'
import { getRememberedAccount, setRememberedAccount, removeRememberedAccount } from '@/utils/auth'

// 路由
const router = useRouter()
const route = useRoute()

// 用户状态
const userStore = useUserStore()

// 表单引用
const loginFormRef = ref(null)

// 加载状态
const loading = ref(false)

// 记住密码
const rememberMe = ref(false)

// 显示验证码
const showCaptcha = ref(false)

// 验证码URL
const captchaUrl = ref('')

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  captcha: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码长度为 4 个字符', trigger: 'blur' }
  ]
}

// 刷新验证码
const refreshCaptcha = () => {
  captchaUrl.value = `/api/captcha?t=${Date.now()}`
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    loading.value = true

    // 调用登录接口
    await userStore.loginAction(loginForm)

    // 处理记住密码
    if (rememberMe.value) {
      setRememberedAccount(loginForm.username, loginForm.password)
    } else {
      removeRememberedAccount()
    }

    ElMessage.success('登录成功')

    // 跳转到目标页面或首页
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (error) {
    console.error('登录失败:', error)
    if (showCaptcha.value) {
      refreshCaptcha()
    }
  } finally {
    loading.value = false
  }
}

// 初始化
onMounted(() => {
  // 检查是否有记住的账号
  const remembered = getRememberedAccount()
  if (remembered) {
    loginForm.username = remembered.username
    loginForm.password = atob(remembered.password)
    rememberMe.value = true
  }
})
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

// 左侧品牌区域
.login-brand {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 60px;
  position: relative;
  overflow: hidden;

  @media (max-width: 968px) {
    display: none;
  }
}

.brand-content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: #fff;
}

.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 20px;

  .logo-icon {
    width: 56px;
    height: 56px;
    color: #fff;

    svg {
      width: 100%;
      height: 100%;
    }
  }

  .brand-name {
    font-size: 42px;
    font-weight: 700;
    letter-spacing: 2px;
    margin: 0;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  }
}

.brand-slogan {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 48px;
  letter-spacing: 1px;
}

.brand-features {
  display: flex;
  gap: 40px;
  justify-content: center;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;

  .feature-icon {
    width: 48px;
    height: 48px;
    background: rgba(255, 255, 255, 0.15);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(10px);

    svg {
      width: 24px;
      height: 24px;
      color: #fff;
    }
  }

  span {
    font-size: 14px;
    opacity: 0.9;
  }
}

// 装饰元素
.brand-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);

  &.circle-1 {
    width: 300px;
    height: 300px;
    top: -100px;
    left: -100px;
  }

  &.circle-2 {
    width: 200px;
    height: 200px;
    bottom: 100px;
    right: -50px;
  }

  &.circle-3 {
    width: 150px;
    height: 150px;
    bottom: -50px;
    left: 20%;
  }
}

.decoration-dots {
  position: absolute;
  top: 20%;
  right: 10%;
  width: 100px;
  height: 100px;
  background-image: radial-gradient(rgba(255, 255, 255, 0.2) 2px, transparent 2px);
  background-size: 20px 20px;
}

// 右侧登录表单区域
.login-form-wrapper {
  width: 480px;
  min-width: 480px;
  display: flex;
  flex-direction: column;
  background: #fff;
  position: relative;

  @media (max-width: 968px) {
    width: 100%;
    min-width: auto;
  }
}

.login-form-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 60px;
  max-width: 400px;
  margin: 0 auto;
  width: 100%;

  @media (max-width: 480px) {
    padding: 40px 24px;
  }
}

.form-header {
  margin-bottom: 40px;
}

.form-title {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 8px 0;
}

.form-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.login-form {
  .el-form-item {
    margin-bottom: 24px;
  }

  :deep(.el-input__wrapper) {
    padding: 0 16px;
    border-radius: 8px;
    box-shadow: 0 0 0 1px #e5e7eb;
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 0 0 1px #c0c4cc;
    }

    &.is-focus {
      box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2), 0 0 0 1px #667eea;
    }
  }

  :deep(.el-input__inner) {
    height: 48px;
    line-height: 48px;
  }

  :deep(.el-input__prefix) {
    font-size: 18px;
    color: #9ca3af;
  }
}

.captcha-wrapper {
  display: flex;
  gap: 12px;

  .el-input {
    flex: 1;
  }

  .captcha-image {
    width: 120px;
    height: 48px;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    background: #f5f7fa;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid #e5e7eb;
    transition: all 0.3s;

    &:hover {
      border-color: #667eea;
    }

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .captcha-placeholder {
      font-size: 12px;
      color: #9ca3af;
    }
  }
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  :deep(.el-checkbox__label) {
    color: #6b7280;
    font-size: 14px;
  }

  .forgot-link {
    font-size: 14px;
  }
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
  }

  &:active {
    transform: translateY(0);
  }
}

.form-footer {
  text-align: center;
  margin-top: 32px;

  .footer-text {
    font-size: 14px;
    color: #6b7280;
    margin-right: 4px;
  }
}

.copyright {
  padding: 24px;
  text-align: center;
  border-top: 1px solid #f0f0f0;

  p {
    font-size: 12px;
    color: #9ca3af;
    margin: 0;
  }
}
</style>
