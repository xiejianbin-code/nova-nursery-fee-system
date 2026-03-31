import { get, post, put, del } from './index'

// 获取账单列表
export function getBillList(params) {
  return get('/bill/page', params)
}

// 获取账单详情
export function getBillDetail(id) {
  return get(`/bill/${id}`)
}

// 生成账单
export function generateBill(data) {
  return post('/bill/generate', data)
}

// 批量生成账单
export function batchGenerateBill(data) {
  return post('/bill/batch-generate', data)
}

// 生成上月账单
export function generateMonthlyBills() {
  return post('/bill/generate-monthly')
}

// 确认账单
export function confirmBill(id) {
  return put('/bill/confirm', null, { params: { id } })
}

// 重新核算账单
export function recalculateBill(id) {
  return put('/bill/recalculate', null, { params: { id } })
}

// 账单减免
export function discountBill(data) {
  return put('/bill/discount', data)
}

// 添加其他费用
export function addOtherFee(data) {
  return post('/bill/other-fee', data)
}

// 删除其他费用
export function deleteOtherFee(id) {
  return del('/bill/other-fee', { id })
}

// 获取账单统计
export function getBillStatistics(params) {
  return get('/bill/statistics', params)
}

// 导出账单
export function exportBill(params) {
  return get('/bill/export', params)
}

// 获取账单明细
export function getBillItems(billId) {
  return get(`/bill/${billId}/items`)
}
