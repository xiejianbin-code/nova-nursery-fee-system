<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-code">403</div>
      <div class="error-desc">
        <h2>无访问权限</h2>
        <p>抱歉，您没有权限访问此页面</p>
      </div>
      <div class="error-actions">
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goBack">返回上一页</el-button>
      </div>
    </div>
    <div class="error-illustration">
      <svg viewBox="0 0 400 300" fill="none" xmlns="http://www.w3.org/2000/svg">
        <rect x="100" y="80" width="200" height="140" rx="8" fill="#f0f2f5" stroke="#667eea" stroke-width="4"/>
        <circle cx="200" cy="150" r="40" fill="#667eea"/>
        <path d="M180 150L220 150M200 130L200 170" stroke="#fff" stroke-width="6" stroke-linecap="round"/>
        <rect x="140" y="220" width="120" height="20" rx="4" fill="#e4e8eb"/>
      </svg>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style lang="scss" scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  padding: 40px;
  gap: 80px;
}

.error-content {
  text-align: center;
}

.error-code {
  font-size: 120px;
  font-weight: 700;
  background: linear-gradient(135deg, #f56c6c 0%, #e6a23c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
  margin-bottom: 24px;
}

.error-desc {
  h2 {
    font-size: 24px;
    color: #333;
    margin: 0 0 12px 0;
  }

  p {
    font-size: 14px;
    color: #666;
    margin: 0 0 32px 0;
  }
}

.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.error-illustration {
  width: 300px;
  height: 300px;

  svg {
    width: 100%;
    height: 100%;
  }
}

@media (max-width: 768px) {
  .error-page {
    flex-direction: column-reverse;
    gap: 40px;
  }

  .error-code {
    font-size: 80px;
  }

  .error-illustration {
    width: 200px;
    height: 200px;
  }
}
</style>
