import { get, post, put, del } from './index'

// 分页查询角色
export function getPage(params) {
  return get('/role/page', params)
}

// 获取所有角色列表
export function getList() {
  return get('/role/list')
}

// 获取角色详情
export function getById(id) {
  return get(`/role/${id}`)
}

// 新增角色
export function create(data) {
  return post('/role', data)
}

// 更新角色
export function update(data) {
  return put('/role', data)
}

// 删除角色
export function deleteById(id) {
  return del(`/role/${id}`)
}

// 获取角色菜单
export function getMenus(roleId) {
  return get(`/role/${roleId}/menus`)
}

// 更新角色菜单
export function updateMenus(roleId, menuIds) {
  return post(`/role/${roleId}/menus`, menuIds)
}
