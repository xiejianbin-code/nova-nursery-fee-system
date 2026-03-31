<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-code">404</div>
      <div class="error-desc">
        <h2>页面不存在</h2>
        <p>抱歉，您访问的页面不存在或已被移除</p>
      </div>
      <div class="error-actions">
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goBack">返回上一页</el-button>
      </div>
    </div>
    <div class="error-illustration">
      <svg viewBox="0 0 400 300" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="200" cy="150" r="100" fill="#f0f2f5"/>
        <path d="M160 120L240 180M240 120L160 180" stroke="#667eea" stroke-width="8" stroke-linecap="round"/>
        <circle cx="200" cy="150" r="120" stroke="#667eea" stroke-width="4" stroke-dasharray="20 10" fill="none"/>
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
