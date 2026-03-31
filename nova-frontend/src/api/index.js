import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken, removeToken, removeUserInfo } from '@/utils/auth'
import router from '@/router'

let isRedirecting = false

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 在请求头中添加Token
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data

    // 根据业务状态码处理响应
    if (res.code === 200 || res.code === 0) {
      return res
    }

    // Token过期或未登录
    if (res.code === 401) {
      if (!isRedirecting) {
        isRedirecting = true
        ElMessageBox.confirm('登录状态已过期，请重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          clearAuthAndRedirect()
        }).catch(() => {
          clearAuthAndRedirect()
        })
      }
      return Promise.reject(new Error(res.message || '登录已过期'))
    }

    // 权限不足
    if (res.code === 403) {
      ElMessage.error('权限不足，无法访问')
      return Promise.reject(new Error(res.message || '权限不足'))
    }

    // 其他错误
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    console.error('响应错误:', error)
    
    // 处理HTTP错误状态
    let message = '请求失败'
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未授权，请登录'
          if (!isRedirecting) {
            isRedirecting = true
            clearAuthAndRedirect()
          }
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求地址不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        case 502:
          message = '网关错误'
          break
        case 503:
          message = '服务不可用'
          break
        case 504:
          message = '网关超时'
          break
        default:
          message = error.response.data?.message || '请求失败'
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时'
    } else if (error.message.includes('Network Error')) {
      message = '网络连接异常'
    }

    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// 清除认证信息并跳转登录页
function clearAuthAndRedirect() {
  removeToken()
  removeUserInfo()
  isRedirecting = false
  router.push('/login')
}

// 封装GET请求
export function get(url, params, config = {}) {
  return service({
    method: 'get',
    url,
    params,
    ...config
  })
}

// 封装POST请求
export function post(url, data, config = {}) {
  return service({
    method: 'post',
    url,
    data,
    ...config
  })
}

// 封装PUT请求
export function put(url, data, config = {}) {
  return service({
    method: 'put',
    url,
    data,
    ...config
  })
}

// 封装DELETE请求
export function del(url, params, config = {}) {
  return service({
    method: 'delete',
    url,
    params,
    ...config
  })
}

// 封装文件上传请求
export function upload(url, file, onProgress) {
  const formData = new FormData()
  formData.append('file', file)

  return service({
    method: 'post',
    url,
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: (progressEvent) => {
      if (onProgress) {
        const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        onProgress(percent)
      }
    }
  })
}

// 导出axios实例
export default service
