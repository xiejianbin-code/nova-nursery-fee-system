import { get, del } from './index'

export function getPage(params) {
  return get('/log/page', params)
}

export function deleteLogs(ids) {
  return del(`/log/${ids.join(',')}`)
}

export function clearLogs(days) {
  return del('/log/clear', { days })
}
