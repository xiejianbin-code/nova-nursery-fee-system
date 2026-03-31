import { get, post, put, del } from './index'

// 获取收费列表（分页）
export function getPaymentList(params) {
  return get('/payment/page', params)
}

// 获取收费详情
export function getPaymentDetail(id) {
  return get(`/payment/${id}`)
}

// 收费登记
export function registerPayment(data) {
  return post('/payment', data)
}

// 作废收费记录
export function cancelPayment(id, reason) {
  return put('/payment/void', { id, reason })
}

// 获取幼儿收费记录
export function getPaymentByChildId(childId) {
  return get(`/payment/child/${childId}`)
}
