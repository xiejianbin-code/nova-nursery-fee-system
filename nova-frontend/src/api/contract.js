import { get, post, put, del } from './index'

// 获取合同列表
export function getContractList(params) {
  return get('/contract/page', params)
}

// 获取合同选项列表（不分页）
export function getContractOptions() {
  return get('/contract/list')
}

// 获取合同详情
export function getContractDetail(id) {
  return get(`/contract/${id}`)
}

// 新增合同
export function addContract(data) {
  return post('/contract', data)
}

// 合同变更
export function changeContract(data) {
  return put('/contract/change', data)
}

// 合同终止
export function terminateContract(id, reason) {
  return put('/contract/terminate', null, { params: { id, reason } })
}

// 合同续签
export function renewContract(data) {
  return put('/contract/renew', data)
}

// 获取合同变更记录
export function getContractChangeRecords(contractId) {
  return get(`/contract/changeRecords/${contractId}`)
}

// 获取启用的收费标准列表
export function getEnabledFeeTemplates() {
  return get('/fee-template/enabled')
}

// 获取课程类型列表
export function getCourseTypes() {
  return get('/contract/course-types')
}
