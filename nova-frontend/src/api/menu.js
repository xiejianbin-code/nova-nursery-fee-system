import { get, post, put, del } from './index'

// 获取用户菜单列表
export function getUserMenus() {
  return get('/menu/user')
}

// 获取菜单树
export function getTree() {
  return get('/menu/tree')
}

// 获取菜单详情
export function getById(id) {
  return get(`/menu/${id}`)
}

// 新增菜单
export function create(data) {
  return post('/menu', data)
}

// 更新菜单
export function update(data) {
  return put('/menu', data)
}

// 删除菜单
export function deleteById(id) {
  return del(`/menu/${id}`)
}
