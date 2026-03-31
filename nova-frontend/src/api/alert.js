import { get, post, put, del } from './index'

// 获取预警列表
export function getAlertList(params) {
  return get('/alert/page', params)
}

// 获取预警详情
export function getAlertDetail(id) {
  return get('/alert/detail', { id })
}

// 处理预警
export function handleAlert(data) {
  return post('/alert/handle', data)
}

// 批量处理预警
export function batchHandleAlert(data) {
  return post('/alert/batchHandle', data)
}

// 忽略预警
export function ignoreAlert(id) {
  return post('/alert/ignore', { id })
}

// 删除预警记录
export function deleteAlert(id) {
  return del('/alert/delete', { id })
}

// 获取预警类型列表
export function getAlertTypes() {
  return get('/alert/types')
}

// 获取预警统计
export function getAlertStatistics(params) {
  return get('/alert/statistics', params)
}

// 获取预警配置列表
export function getAlertConfigs() {
  return get('/alert/configs')
}

// 更新预警配置
export function updateAlertConfig(data) {
  return post('/alert/updateConfig', data)
}

// 获取未处理预警数量
export function getUnhandledAlertCount() {
  return get('/alert/unhandledCount')
}
