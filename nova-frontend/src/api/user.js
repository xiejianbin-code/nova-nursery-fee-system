import { get, post, put, del } from './index'

// 用户登录
export function login(data) {
  return post('/auth/login', data)
}

// 用户登出
export function logout() {
  return post('/auth/logout')
}

// 获取当前用户信息
export function getUserInfo() {
  return get('/auth/info')
}

// 分页查询用户
export function getPage(params) {
  return get('/user/page', params)
}

// 获取用户详情
export function getById(id) {
  return get(`/user/${id}`)
}

// 新增用户
export function create(data) {
  return post('/user', data)
}

// 更新用户
export function update(data) {
  return put('/user', data)
}

// 删除用户
export function deleteById(id) {
  return del(`/user/${id}`)
}

// 更新用户状态
export function updateStatus(id, status) {
  return put(`/user/${id}/status`, { status })
}

// 获取当前用户权限
export function getUserPermissions() {
  return get('/user/permissions')
}

// 修改密码
export function changePassword(data) {
  return post('/user/changePassword', data)
}

// 重置密码
export function resetPassword(id) {
  return post(`/user/${id}/resetPassword`)
}

// 更新当前用户信息
export function updateCurrentUserInfo(data) {
  return put('/user/current', data)
}
