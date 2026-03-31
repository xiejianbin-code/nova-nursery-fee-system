import { get, post, put, del } from './index'

// 获取退费列表（分页）
export function getRefundPage(params) {
  return get('/refund/page', params)
}

// 获取幼儿退费记录
export function getRefundByChildId(childId) {
  return get(`/refund/child/${childId}`)
}

// 退费登记
export function registerRefund(data) {
  return post('/refund', data)
}
