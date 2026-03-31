<template>
  <div class="dashboard-container">
    <el-row :gutter="16" class="stat-cards">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <el-icon :size="24"><Avatar /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.childCount }}</div>
            <div class="stat-label">在园幼儿数</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <el-icon :size="24"><Wallet /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.monthIncome }}</div>
            <div class="stat-label">本月收入</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
            <el-icon :size="24"><Memo /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingBills }}</div>
            <div class="stat-label">待确认账单</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
            <el-icon :size="24"><Bell /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.alertCount }}</div>
            <div class="stat-label">预警数量</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="16">
        <el-card class="quick-entry-card">
          <template #header>
            <div class="card-header">
              <span>快捷入口</span>
            </div>
          </template>
          <div class="quick-grid">
            <div
              v-for="item in filteredQuickEntries"
              :key="item.path"
              class="quick-item"
              @click="handleNavigate(item.path)"
            >
              <div class="quick-icon">
                <el-icon :size="28"><component :is="item.icon" /></el-icon>
              </div>
              <span>{{ item.title }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card class="alert-card">
          <template #header>
            <div class="card-header">
              <span>预警提醒</span>
              <el-badge :value="alerts.length" :max="99" class="alert-badge">
                <el-icon><Bell /></el-icon>
              </el-badge>
            </div>
          </template>
          <div class="alert-list">
            <div v-if="alerts.length === 0" class="empty-alert">
              <el-empty description="暂无预警" :image-size="80" />
            </div>
            <div
              v-for="alert in alerts"
              :key="alert.id"
              class="alert-item"
              :class="'alert-' + alert.level"
              @click="handleAlertClick(alert)"
            >
              <div class="alert-icon">
                <el-icon v-if="alert.level === 'danger'"><WarningFilled /></el-icon>
                <el-icon v-else-if="alert.level === 'warning'"><Warning /></el-icon>
                <el-icon v-else><InfoFilled /></el-icon>
              </div>
              <div class="alert-content">
                <div class="alert-title">{{ alert.title }}</div>
                <div class="alert-desc">{{ alert.description }}</div>
                <div class="alert-time">{{ alert.createTime }}</div>
              </div>
              <el-tag
                :type="alert.level === 'danger' ? 'danger' : alert.level === 'warning' ? 'warning' : 'info'"
                size="small"
              >
                {{ alert.level === 'danger' ? '紧急' : alert.level === 'warning' ? '警告' : '提示' }}
              </el-tag>
            </div>
          </div>
          <div v-if="alerts.length > 0" class="alert-footer">
            <el-button type="primary" link @click="handleNavigate('/alert/list')">
              查看全部预警
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="info-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>本月收费统计</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <div class="chart-mock">
              <div class="bar" style="height: 60%;"><span>幼儿学费</span></div>
              <div class="bar" style="height: 80%;"><span>餐费</span></div>
              <div class="bar" style="height: 45%;"><span>教材费</span></div>
              <div class="bar" style="height: 70%;"><span>活动费</span></div>
              <div class="bar" style="height: 55%;"><span>其他</span></div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>幼儿出勤率</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <div class="chart-mock line">
              <div class="line-point" style="left: 10%; bottom: 70%;"></div>
              <div class="line-point" style="left: 25%; bottom: 85%;"></div>
              <div class="line-point" style="left: 40%; bottom: 75%;"></div>
              <div class="line-point" style="left: 55%; bottom: 90%;"></div>
              <div class="line-point" style="left: 70%; bottom: 80%;"></div>
              <div class="line-point" style="left: 85%; bottom: 88%;"></div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Avatar, Wallet, Memo, Bell, WarningFilled, Warning, InfoFilled, ArrowRight } from '@element-plus/icons-vue'
import useMenuStore from '@/store/modules/menu'

const router = useRouter()
const menuStore = useMenuStore()

const stats = reactive({
  childCount: '256',
  monthIncome: '¥128,500',
  pendingBills: '12',
  alertCount: '5'
})

const alerts = ref([
  {
    id: 1,
    title: '幼儿张小明合同即将到期',
    description: '合同将于7天后到期，请及时续签',
    level: 'warning',
    createTime: '2024-01-15 10:30'
  },
  {
    id: 2,
    title: '李小红家长欠费提醒',
    description: '已欠费2个月，共计¥4,600',
    level: 'danger',
    createTime: '2024-01-15 09:20'
  },
  {
    id: 3,
    title: '中班出勤率低于80%',
    description: '本周出勤率为75%，请关注',
    level: 'info',
    createTime: '2024-01-14 16:45'
  },
  {
    id: 4,
    title: '王小红体检报告待更新',
    description: '体检报告已过期，请提醒家长更新',
    level: 'warning',
    createTime: '2024-01-14 14:20'
  }
])

const allQuickEntries = [
  { path: '/child', title: '幼儿管理', icon: 'Avatar' },
  { path: '/class', title: '班级管理', icon: 'Grid' },
  { path: '/attendance', title: '出勤管理', icon: 'Calendar' },
  { path: '/payment/list', title: '收费管理', icon: 'Wallet' },
  { path: '/bill', title: '账单管理', icon: 'Memo' },
  { path: '/contract', title: '合同管理', icon: 'Tickets' },
  { path: '/system/user', title: '用户管理', icon: 'User' }
]

const filteredQuickEntries = computed(() => {
  const userMenus = menuStore.menus || []
  const userMenuPaths = new Set()
  
  const collectPaths = (menus) => {
    menus.forEach(menu => {
      if (menu.routePath) {
        userMenuPaths.add(menu.routePath)
      }
      if (menu.children && menu.children.length > 0) {
        collectPaths(menu.children)
      }
    })
  }
  
  collectPaths(userMenus)
  
  return allQuickEntries.filter(entry => {
    return userMenuPaths.has(entry.path)
  })
})

const handleNavigate = (path) => {
  router.push(path)
}

const handleAlertClick = (alert) => {
  router.push('/alert/list')
}

onMounted(() => {
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 0;
}

.stat-cards {
  margin-bottom: 16px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 16px;
  transition: transform 0.3s, box-shadow 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
  }

  .stat-info {
    .stat-value {
      font-size: 24px;
      font-weight: 600;
      color: #333;
      line-height: 1.2;
    }

    .stat-label {
      font-size: 14px;
      color: #909399;
      margin-top: 4px;
    }
  }
}

.quick-entry-card {
  .card-header {
    font-size: 16px;
    font-weight: 500;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .quick-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;

    @media (max-width: 1200px) {
      grid-template-columns: repeat(4, 1fr);
    }

    @media (max-width: 768px) {
      grid-template-columns: repeat(2, 1fr);
    }
  }

  .quick-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    padding: 20px 16px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;
    background: #fafafa;

    &:hover {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);

      .quick-icon {
        transform: scale(1.1);
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }
    }

    .quick-icon {
      width: 56px;
      height: 56px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      transition: all 0.3s;
    }

    span {
      font-size: 14px;
      color: #606266;
      font-weight: 500;
    }
  }
}

.alert-card {
  height: 100%;

  .card-header {
    font-size: 16px;
    font-weight: 500;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .alert-badge {
      cursor: pointer;
    }
  }

  .alert-list {
    max-height: 320px;
    overflow-y: auto;

    &::-webkit-scrollbar {
      width: 4px;
    }

    &::-webkit-scrollbar-thumb {
      background: #dcdfe6;
      border-radius: 2px;
    }
  }

  .empty-alert {
    padding: 20px 0;
  }

  .alert-item {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    padding: 12px;
    border-radius: 8px;
    margin-bottom: 8px;
    cursor: pointer;
    transition: background 0.3s;

    &:hover {
      background: #f5f7fa;
    }

    &:last-child {
      margin-bottom: 0;
    }

    &.alert-danger {
      .alert-icon {
        color: #f56c6c;
      }
    }

    &.alert-warning {
      .alert-icon {
        color: #e6a23c;
      }
    }

    &.alert-info {
      .alert-icon {
        color: #409eff;
      }
    }

    .alert-icon {
      font-size: 20px;
      flex-shrink: 0;
      margin-top: 2px;
    }

    .alert-content {
      flex: 1;
      min-width: 0;

      .alert-title {
        font-size: 14px;
        color: #303133;
        font-weight: 500;
        margin-bottom: 4px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .alert-desc {
        font-size: 12px;
        color: #909399;
        margin-bottom: 4px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .alert-time {
        font-size: 12px;
        color: #c0c4cc;
      }
    }
  }

  .alert-footer {
    text-align: center;
    padding-top: 12px;
    border-top: 1px solid #f0f0f0;
    margin-top: 12px;
  }
}

.info-row {
  margin-top: 16px;
}

.chart-card {
  .card-header {
    font-size: 16px;
    font-weight: 500;
  }

  .chart-placeholder {
    height: 200px;
    display: flex;
    align-items: flex-end;
    justify-content: center;
  }

  .chart-mock {
    display: flex;
    align-items: flex-end;
    gap: 24px;
    height: 160px;
    padding: 0 20px;

    .bar {
      width: 40px;
      background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
      border-radius: 4px 4px 0 0;
      display: flex;
      align-items: flex-end;
      justify-content: center;
      transition: height 0.3s;

      span {
        writing-mode: vertical-rl;
        font-size: 12px;
        color: #fff;
        padding-bottom: 8px;
      }
    }
  }

  .chart-mock.line {
    position: relative;
    width: 100%;
    height: 160px;
    background: linear-gradient(to top, #f5f7fa 0%, transparent 100%);
    border-radius: 4px;

    .line-point {
      position: absolute;
      width: 12px;
      height: 12px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 50%;
      border: 2px solid #fff;
      box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
    }
  }
}
</style>
