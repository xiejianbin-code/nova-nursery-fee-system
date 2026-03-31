import { get, post, put, del } from './index'

// 获取收费标准列表（分页）
export function getFeeTemplateList(params) {
  return get('/fee-template/page', params)
}

// 获取收费标准详情
export function getFeeTemplateDetail(id) {
  return get(`/fee-template/${id}`)
}

// 新增收费标准
export function addFeeTemplate(data) {
  return post('/fee-template', data)
}

// 编辑收费标准
export function editFeeTemplate(data) {
  return put('/fee-template', data)
}

// 更新收费标准状态
export function updateFeeTemplateStatus(id, status) {
  return put('/fee-template/status', null, { params: { id, status } })
}

// 获取启用的收费标准列表
export function getEnabledFeeTemplates() {
  return get('/fee-template/enabled')
}
