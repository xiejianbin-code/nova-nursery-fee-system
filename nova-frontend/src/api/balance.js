import { get, post, put, del } from './index'

// 获取幼儿三账户余额
export function getChildBalance(childId) {
  return get(`/balance/${childId}`)
}

// 获取余额列表（分页）
export function getBalanceList(params) {
  return get('/balance', params)
}

// 获取余额变动记录（分页）
export function getBalanceRecords(params) {
  return get('/balance/record', params)
}
