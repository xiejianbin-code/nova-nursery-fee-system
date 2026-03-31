import { getToken } from './auth'
import router from '@/router'

// 权限白名单，不需要登录即可访问的页面
const whiteList = ['/login', '/404', '/403']

// 用户权限列表缓存
let permissionsCache = null

/**
 * 检查用户是否已登录
 * @returns {boolean}
 */
export function checkLogin() {
  return !!getToken()
}

/**
 * 检查路由是否在白名单中
 * @param {string} path - 路由路径
 * @returns {boolean}
 */
export function isInWhiteList(path) {
  return whiteList.some(item => path.startsWith(item))
}

/**
 * 设置用户权限列表
 * @param {Array} permissions - 权限列表
 */
export function setPermissions(permissions) {
  permissionsCache = permissions
  sessionStorage.setItem('permissions', JSON.stringify(permissions))
}

/**
 * 获取用户权限列表
 * @returns {Array}
 */
export function getPermissions() {
  if (permissionsCache) {
    return permissionsCache
  }
  
  const permissions = sessionStorage.getItem('permissions')
  if (permissions) {
    permissionsCache = JSON.parse(permissions)
    return permissionsCache
  }
  
  return []
}

/**
 * 清除权限缓存
 */
export function clearPermissions() {
  permissionsCache = null
  sessionStorage.removeItem('permissions')
}

/**
 * 检查用户是否拥有某个权限
 * @param {string} permission - 权限标识
 * @returns {boolean}
 */
export function hasPermission(permission) {
  const permissions = getPermissions()
  
  if (!permissions || permissions.length === 0) {
    return false
  }
  
  // 如果有超级管理员权限，直接返回true
  if (permissions.includes('*')) {
    return true
  }
  
  return permissions.includes(permission)
}

/**
 * 检查用户是否拥有任意一个权限
 * @param {Array} permissionList - 权限标识列表
 * @returns {boolean}
 */
export function hasAnyPermission(permissionList) {
  if (!permissionList || permissionList.length === 0) {
    return false
  }
  
  return permissionList.some(permission => hasPermission(permission))
}

/**
 * 检查用户是否拥有所有权限
 * @param {Array} permissionList - 权限标识列表
 * @returns {boolean}
 */
export function hasAllPermissions(permissionList) {
  if (!permissionList || permissionList.length === 0) {
    return false
  }
  
  return permissionList.every(permission => hasPermission(permission))
}

/**
 * 权限指令
 * 用于在模板中移除没有权限的元素
 * 使用方式: v-permission="'user:add'" 或 v-permission="['user:add', 'user:edit']"
 */
export const permissionDirective = {
  mounted(el, binding) {
    const { value } = binding
    
    if (!value) {
      return
    }
    
    let hasAuth = false
    
    if (Array.isArray(value)) {
      hasAuth = hasAnyPermission(value)
    } else {
      hasAuth = hasPermission(value)
    }
    
    if (!hasAuth) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
}

/**
 * 角色检查
 * @param {string} role - 角色标识
 * @returns {boolean}
 */
export function hasRole(role) {
  const roles = getRoles()
  
  if (!roles || roles.length === 0) {
    return false
  }
  
  // 如果有超级管理员角色，直接返回true
  if (roles.includes('admin') || roles.includes('super_admin')) {
    return true
  }
  
  return roles.includes(role)
}

/**
 * 获取用户角色列表
 * @returns {Array}
 */
export function getRoles() {
  const roles = sessionStorage.getItem('roles')
  return roles ? JSON.parse(roles) : []
}

/**
 * 设置用户角色列表
 * @param {Array} roles - 角色列表
 */
export function setRoles(roles) {
  sessionStorage.setItem('roles', JSON.stringify(roles))
}

/**
 * 清除角色缓存
 */
export function clearRoles() {
  sessionStorage.removeItem('roles')
}

/**
 * 路由守卫
 * 用于检查页面访问权限
 */
export function setupPermissionGuard() {
  router.beforeEach(async (to, from, next) => {
    // 检查是否在白名单中
    if (isInWhiteList(to.path)) {
      next()
      return
    }
    
    // 检查是否已登录
    if (!checkLogin()) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
    
    // 检查页面权限
    if (to.meta && to.meta.permission) {
      const pagePermission = to.meta.permission
      
      if (!hasPermission(pagePermission)) {
        next('/403')
        return
      }
    }
    
    next()
  })
}

export default {
  hasPermission,
  hasAnyPermission,
  hasAllPermissions,
  hasRole,
  getRoles,
  setRoles,
  getPermissions,
  setPermissions,
  clearPermissions,
  clearRoles,
  permissionDirective,
  setupPermissionGuard
}
