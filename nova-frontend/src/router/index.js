import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'
import useUserStore from '@/store/modules/user'
import useMenuStore from '@/store/modules/menu'
import Layout from '@/views/layout/index.vue'

export const constantRoutes = [
  {
    path: '/',
    name: 'Layout',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: {
          title: '仪表盘',
          icon: 'Odometer',
          affix: true
        }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      hidden: true
    }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面不存在',
      hidden: true
    }
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: {
      title: '无权限',
      hidden: true
    }
  }
]

export const asyncRoutes = [
  {
    path: '/',
    name: 'Layout',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: {
          title: '仪表盘',
          icon: 'Odometer',
          affix: true
        }
      }
    ]
  },
  {
    path: '/system',
    name: 'System',
    component: Layout,
    redirect: '/system/user',
    meta: {
      title: '系统管理',
      icon: 'Setting'
    },
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: {
          title: '用户管理',
          icon: 'User',
          permission: 'system:user:list'
        }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: {
          title: '角色管理',
          icon: 'UserFilled',
          permission: 'system:role:list'
        }
      },
      {
        path: 'menu',
        name: 'Menu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: {
          title: '菜单管理',
          icon: 'Menu',
          permission: 'system:menu:list'
        }
      },
      {
        path: 'log',
        name: 'Log',
        component: () => import('@/views/system/log/index.vue'),
        meta: {
          title: '操作日志',
          icon: 'Document',
          permission: 'system:log:list'
        }
      },
      {
        path: 'config',
        name: 'SystemConfig',
        component: () => import('@/views/system/config/index.vue'),
        meta: {
          title: '系统配置',
          icon: 'Setting',
          permission: 'system:config:list'
        }
      }
    ]
  },
  {
    path: '/child',
    name: 'Child',
    component: Layout,
    redirect: '/child/list',
    meta: {
      title: '托育管理',
      icon: 'School'
    },
    children: [
      {
        path: 'list',
        name: 'ChildList',
        component: () => import('@/views/child/index.vue'),
        meta: {
          title: '幼儿列表',
          icon: 'List',
          permission: 'child:list'
        }
      },
      {
        path: 'archive',
        name: 'ChildArchive',
        component: () => import('@/views/child/archive.vue'),
        meta: {
          title: '幼儿档案',
          icon: 'Folder',
          permission: 'child:archive'
        }
      }
    ]
  },
  {
    path: '/class',
    name: 'Class',
    component: Layout,
    redirect: '/class/list',
    meta: {
      title: '班级管理',
      icon: 'Grid'
    },
    children: [
      {
        path: 'list',
        name: 'ClassList',
        component: () => import('@/views/class/index.vue'),
        meta: {
          title: '班级列表',
          icon: 'List',
          permission: 'class:list'
        }
      },
      {
        path: 'arrange',
        name: 'ClassArrange',
        component: () => import('@/views/class/arrange.vue'),
        meta: {
          title: '班级编排',
          icon: 'Grid',
          permission: 'class:arrange'
        }
      }
    ]
  },
  {
    path: '/attendance',
    name: 'Attendance',
    component: Layout,
    redirect: '/attendance/record',
    meta: {
      title: '出勤管理',
      icon: 'Calendar'
    },
    children: [
      {
        path: 'record',
        name: 'AttendanceRecord',
        component: () => import('@/views/attendance/index.vue'),
        meta: {
          title: '考勤记录',
          icon: 'Clock',
          permission: 'attendance:record'
        }
      },
      {
        path: 'statistics',
        name: 'AttendanceStatistics',
        component: () => import('@/views/attendance/statistics.vue'),
        meta: {
          title: '考勤统计',
          icon: 'DataAnalysis',
          permission: 'attendance:statistics'
        }
      }
    ]
  },
  {
    path: '/payment',
    name: 'Payment',
    component: Layout,
    redirect: '/payment/list',
    meta: {
      title: '财务管理',
      icon: 'Wallet'
    },
    children: [
      {
        path: 'list',
        name: 'PaymentList',
        component: () => import('@/views/payment/index.vue'),
        meta: {
          title: '收费列表',
          icon: 'List',
          permission: 'payment:list'
        }
      },
      {
        path: 'item',
        name: 'PaymentItem',
        component: () => import('@/views/payment/item.vue'),
        meta: {
          title: '收费标准',
          icon: 'PriceTag',
          permission: 'payment:item'
        }
      },
      {
        path: 'refund',
        name: 'RefundList',
        component: () => import('@/views/payment/refund.vue'),
        meta: {
          title: '退费管理',
          icon: 'RefreshLeft',
          permission: 'payment:refund'
        }
      },
      {
        path: 'balance',
        name: 'BalanceList',
        component: () => import('@/views/payment/balance.vue'),
        meta: {
          title: '余额管理',
          icon: 'Coin',
          permission: 'payment:balance'
        }
      },
      {
        path: 'contract',
        name: 'Contract',
        component: () => import('@/views/contract/index.vue'),
        meta: {
          title: '合同管理',
          icon: 'Tickets',
          permission: 'contract:list'
        }
      },
      {
        path: 'bill',
        name: 'Bill',
        component: () => import('@/views/bill/index.vue'),
        meta: {
          title: '账单管理',
          icon: 'Memo',
          permission: 'bill:list'
        }
      }
    ]
  },
  {
    path: '/report',
    name: 'Report',
    component: Layout,
    redirect: '/report/finance',
    meta: {
      title: '财务报表',
      icon: 'DataBoard'
    },
    children: [
      {
        path: 'finance',
        name: 'FinanceReport',
        component: () => import('@/views/report/finance.vue'),
        meta: {
          title: '财务报表',
          icon: 'TrendCharts',
          permission: 'report:finance'
        }
      },
      {
        path: 'attendance',
        name: 'AttendanceReport',
        component: () => import('@/views/report/attendance.vue'),
        meta: {
          title: '出勤报表',
          icon: 'DataAnalysis',
          permission: 'report:attendance'
        }
      }
    ]
  },
  {
    path: '/alert',
    name: 'Alert',
    component: Layout,
    redirect: '/alert/list',
    meta: {
      title: '预警管理',
      icon: 'Bell'
    },
    children: [
      {
        path: 'list',
        name: 'AlertList',
        component: () => import('@/views/alert/index.vue'),
        meta: {
          title: '预警列表',
          icon: 'List',
          permission: 'alert:list'
        }
      },
      {
        path: 'rule',
        name: 'AlertRule',
        component: () => import('@/views/alert/rule.vue'),
        meta: {
          title: '预警规则',
          icon: 'SetUp',
          permission: 'alert:rule'
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
    meta: {
      hidden: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: [...constantRoutes],
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

export function resetRouter() {
  const newRouter = createRouter({
    history: createWebHistory(),
    routes: constantRoutes
  })
  router.matcher = newRouter.matcher
}

const whiteList = ['/login', '/404', '/403']

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - Nova` : 'Nova管理系统'
  
  const hasToken = getToken()
  
  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      const userStore = useUserStore()
      const menuStore = useMenuStore()
      
      if (userStore.roles.length === 0) {
        try {
          await userStore.getUserInfoAction()
          
          if (!menuStore.isRoutesLoaded) {
            await menuStore.generateRoutes()
          }
          
          next({ ...to, replace: true })
        } catch (error) {
          await userStore.resetState()
          menuStore.resetState()
          next({
            path: '/login',
            query: { redirect: to.fullPath }
          })
        }
      } else {
        next()
      }
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    }
  }
})

router.afterEach(() => {
})

export default router
