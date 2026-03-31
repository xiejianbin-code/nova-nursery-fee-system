import { get, post, put, del } from './index'

// 分页查询系统配置
export function getPage(params) {
  return get('/system/config/page', params)
}

// 获取所有启用的系统配置
export function getList() {
  return get('/system/config/list')
}

// 根据配置键获取配置值
export function getConfigValue(configKey) {
  return get(`/system/config/value/${configKey}`)
}

// 获取系统配置详情
export function getById(id) {
  return get(`/system/config/${id}`)
}

// 新增系统配置
export function create(data) {
  return post('/system/config', data)
}

// 更新系统配置
export function update(data) {
  return put('/system/config', data)
}

// 删除系统配置
export function deleteById(id) {
  return del(`/system/config/${id}`)
}

// 批量删除系统配置
export function batchDelete(ids) {
  return del('/system/config/batch', ids)
}

// 更新系统配置状态
export function updateStatus(id, status) {
  return put(`/system/config/status/${id}`, { status })
}
