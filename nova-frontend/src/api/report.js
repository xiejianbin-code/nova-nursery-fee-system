import { get, post } from './index'

// 获取月度收入汇总报表
export function getMonthlyIncomeReport(params) {
  return get('/report/monthlyIncome', params)
}

// 导出月度收入汇总报表
export function exportMonthlyIncomeReport(params) {
  return get('/report/exportMonthlyIncome', params)
}

// 获取班级收费统计报表
export function getClassFeeReport(params) {
  return get('/report/classFee', params)
}

// 导出班级收费统计报表
export function exportClassFeeReport(params) {
  return get('/report/exportClassFee', params)
}

// 获取欠费名单报表
export function getArrearsReport(params) {
  return get('/report/arrears', params)
}

// 导出欠费名单报表
export function exportArrearsReport(params) {
  return get('/report/exportArrears', params)
}

// 获取幼儿余额统计报表
export function getBalanceReport(params) {
  return get('/report/balance', params)
}

// 导出幼儿余额统计报表
export function exportBalanceReport(params) {
  return get('/report/exportBalance', params)
}

// 获取合同管理统计报表
export function getContractReport(params) {
  return get('/report/contract', params)
}

// 导出合同管理统计报表
export function exportContractReport(params) {
  return get('/report/exportContract', params)
}

// 获取其他费用收支明细报表
export function getOtherFeeReport(params) {
  return get('/report/otherFee', params)
}

// 导出其他费用收支明细报表
export function exportOtherFeeReport(params) {
  return get('/report/exportOtherFee', params)
}

// 获取收入趋势数据
export function getIncomeTrend(params) {
  return get('/report/incomeTrend', params)
}

// 获取收费构成数据
export function getFeeComposition(params) {
  return get('/report/feeComposition', params)
}

// 获取报表概览数据
export function getReportOverview(params) {
  return get('/report/overview', params)
}
