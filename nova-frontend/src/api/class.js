import { get, post, put, del } from './index'

// 获取班级列表（分页）
export function getClassList(params) {
  return get('/class/page', params)
}

// 获取班级选项列表
export function getClassOptions() {
  return get('/class/options')
}

// 获取班级详情
export function getClassDetail(id) {
  return get(`/class/${id}`)
}

// 新增班级
export function addClass(data) {
  return post('/class', data)
}

// 编辑班级
export function editClass(data) {
  return put('/class', data)
}

// 删除班级
export function deleteClass(id) {
  return del(`/class/${id}`)
}

// 获取老师列表
export function getTeacherList() {
  return get('/user/teachers')
}
