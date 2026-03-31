import { defineStore } from 'pinia'
import { getUserMenus } from '@/api/menu'
import router from '@/router'
import Layout from '@/views/layout/index.vue'

/**
 * 菜单状态管理
 */
export const useMenuStore = defineStore('menu', {
  state: () => ({
    menus: [],
    routes: [],
    isRoutesLoaded: false
  }),

  getters: {
    getMenus: (state) => state.menus,
    getRoutes: (state) => state.routes,
    getIsRoutesLoaded: (state) => state.isRoutesLoaded
  },

  actions: {
    /**
     * 设置菜单列表
     * @param {Array} menus - 菜单列表
     */
    setMenus(menus) {
      this.menus = menus
    },

    /**
     * 设置路由列表
     * @param {Array} routes - 路由列表
     */
    setRoutes(routes) {
      this.routes = routes
    },

    /**
     * 设置路由加载状态
     * @param {boolean} status - 加载状态
     */
    setRoutesLoaded(status) {
      this.isRoutesLoaded = status
    },

    /**
     * 获取用户菜单并生成路由
     */
    async generateRoutes() {
      try {
        const res = await getUserMenus()
        const menus = res.data || []
        this.setMenus(menus)

        const routes = this.buildRoutes(menus)
        this.setRoutes(routes)

        routes.forEach(route => {
          router.addRoute(route)
        })

        // 添加404路由
        router.addRoute({
          path: '/:pathMatch(.*)*',
          redirect: '/404',
          meta: { hidden: true }
        })

        this.setRoutesLoaded(true)
        return routes
      } catch (error) {
        console.error('生成路由失败:', error)
        return []
      }
    },

    /**
     * 构建路由配置
     * @param {Array} menus - 菜单列表
     * @returns {Array} 路由配置
     */
    buildRoutes(menus) {
      const routes = []

      if (!menus || menus.length === 0) {
        return routes
      }

      menus.forEach(menu => {
        const route = this.buildRoute(menu)
        if (route) {
          routes.push(route)
        }
      })

      return routes
    },

    /**
     * 构建单个路由
     * @param {Object} menu - 菜单项
     * @returns {Object} 路由配置
     */
    buildRoute(menu) {
      if (!menu) {
        return null
      }

      // 后端字段映射到前端字段
      const route = {
        path: menu.routePath || menu.path || '/',
        name: menu.name || `menu_${menu.id}`,
        meta: {
          title: menu.menuName || menu.title || '未命名',
          icon: menu.icon,
          hidden: menu.hidden === 1,
          permission: menu.permission,
          menuType: menu.menuType
        }
      }

      // 处理组件路径
      if (menu.componentPath === 'Layout' || menu.component === 'Layout') {
        route.component = Layout
      } else {
        const componentPath = menu.componentPath || menu.component
        if (componentPath) {
          const modules = import.meta.glob('@/views/**/*.vue')
          const fullComponentPath = `/src/views/${componentPath}.vue`
          route.component = modules[fullComponentPath]
        }
      }

      // 处理子菜单
      if (menu.children && menu.children.length > 0) {
        route.children = menu.children
          .filter(child => child !== null && child !== undefined)
          .map(child => this.buildRoute(child))
          .filter(Boolean)
      }

      // 处理重定向
      if (menu.redirect) {
        route.redirect = menu.redirect
      }

      return route
    },

    /**
     * 重置菜单状态
     */
    resetState() {
      this.menus = []
      this.routes = []
      this.isRoutesLoaded = false
    }
  }
})

export default useMenuStore