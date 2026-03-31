import { defineStore } from 'pinia'
import { login, logout, getUserInfo } from '@/api/user'
import { getToken, setToken, removeToken, setUserInfo, removeUserInfo, clearAuth } from '@/utils/auth'
import { setPermissions, clearPermissions, setRoles, clearRoles } from '@/utils/permission'
import router, { resetRouter } from '@/router'
import useMenuStore from '@/store/modules/menu'

// 用户状态管理
export const useUserStore = defineStore('user', {
  // 状态
  state: () => ({
    // 用户Token
    token: getToken() || '',
    // 用户信息
    userInfo: null,
    // 用户名
    username: '',
    // 用户头像
    avatar: '',
    // 用户角色列表
    roles: [],
    // 用户角色名称列表
    roleNames: [],
    // 用户权限列表
    permissions: []
  }),

  // 计算属性
  getters: {
    // 是否已登录
    isLogin: (state) => !!state.token,
    // 获取用户名
    getUsername: (state) => state.userInfo?.username || state.username,
    // 获取头像
    getAvatar: (state) => state.userInfo?.avatar || state.avatar
  },

  // 方法
  actions: {
    /**
     * 用户登录
     * @param {Object} loginForm - 登录表单数据
     */
    async loginAction(loginForm) {
      try {
        const { username, password } = loginForm
        const res = await login({ username: username.trim(), password })
        
        // 保存Token
        this.token = res.data.token
        setToken(res.data.token)
        
        return res
      } catch (error) {
        return Promise.reject(error)
      }
    },

    /**
     * 获取用户信息
     */
    async getUserInfoAction() {
      try {
        const res = await getUserInfo()
        const { user, roles, roleNames, permissions } = res.data
        
        // 保存用户信息
        this.userInfo = user
        this.username = user.username
        this.avatar = user.avatar || ''
        this.roles = roles || []
        this.roleNames = roleNames || []
        this.permissions = permissions || []
        
        // 保存到本地存储
        setUserInfo(user)
        setRoles(roles || [])
        setPermissions(permissions || [])
        
        return res
      } catch (error) {
        return Promise.reject(error)
      }
    },

    /**
     * 用户登出
     */
    async logoutAction() {
      try {
        await logout()
      } catch (error) {
        console.error('登出接口调用失败:', error)
      } finally {
        // 清除菜单状态
        const menuStore = useMenuStore()
        menuStore.resetState()
        // 重置路由
        resetRouter()
        
        // 清除状态
        this.resetState()
        // 清除本地存储
        clearAuth()
        clearPermissions()
        clearRoles()
        // 跳转登录页
        router.push('/login')
      }
    },

    /**
     * 重置状态
     */
    resetState() {
      this.token = ''
      this.userInfo = null
      this.username = ''
      this.avatar = ''
      this.roles = []
      this.permissions = []
    },

    /**
     * 设置Token
     * @param {string} token
     */
    setTokenAction(token) {
      this.token = token
      setToken(token)
    },

    /**
     * 设置用户信息
     * @param {Object} userInfo
     */
    setUserInfoAction(userInfo) {
      this.userInfo = userInfo
      this.username = userInfo.username
      this.avatar = userInfo.avatar || ''
      setUserInfo(userInfo)
    },

    /**
     * 检查是否拥有某个权限
     * @param {string} permission
     * @returns {boolean}
     */
    hasPermission(permission) {
      if (this.permissions.includes('*')) {
        return true
      }
      return this.permissions.includes(permission)
    },

    /**
     * 检查是否拥有某个角色
     * @param {string} role
     * @returns {boolean}
     */
    hasRole(role) {
      if (this.roles.includes('admin') || this.roles.includes('super_admin')) {
        return true
      }
      return this.roles.includes(role)
    }
  },

  // 持久化配置（可选）
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'user',
        storage: localStorage,
        paths: ['token', 'username', 'avatar']
      }
    ]
  }
})

export default useUserStore
