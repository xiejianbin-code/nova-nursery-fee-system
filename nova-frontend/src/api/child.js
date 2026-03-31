import { get, post, put, del } from './index'

// 获取幼儿列表（分页）
export function getChildList(params) {
  return get('/child/page', params)
}

// 获取幼儿选项列表（不分页）
export function getChildOptions(params) {
  return get('/child/list', params)
}

// 获取幼儿详情
export function getChildDetail(id) {
  return get(`/child/${id}`)
}

// 新增幼儿
export function addChild(data) {
  return post('/child', data)
}

// 编辑幼儿
export function editChild(data) {
  return put('/child', data)
}

// 删除幼儿
export function deleteChild(id) {
  return del(`/child/${id}`)
}
