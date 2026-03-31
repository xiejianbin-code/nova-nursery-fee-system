import { get, post, put, del } from './index'

// 获取出勤列表（分页）
export function getAttendanceList(params) {
  return get('/attendance/page', params)
}

// 获取出勤记录列表
export function getAttendanceRecords(queryDTO) {
  return get('/attendance/list', queryDTO)
}

// 获取出勤统计
export function getAttendanceStatistics(queryDTO) {
  return get('/attendance/statistics', queryDTO)
}

// 登记出勤
export function registerAttendance(data) {
  return post('/attendance/register', data)
}

// 批量登记出勤
export function batchRegisterAttendance(data) {
  return post('/attendance/batch', data)
}

// 修改出勤状态
export function updateAttendance(data) {
  return put('/attendance', data)
}

// 删除出勤记录
export function deleteAttendance(id) {
  return del(`/attendance/${id}`)
}

// 导出出勤记录
export function exportAttendance(params) {
  return get('/attendance/export', params, { responseType: 'blob' })
}
