// Token存储的Key
const TokenKey = import.meta.env.VITE_TOKEN_KEY || 'nova_token'

// 获取Token
export function getToken() {
  return localStorage.getItem(TokenKey)
}

// 设置Token
export function setToken(token) {
  return localStorage.setItem(TokenKey, token)
}

// 移除Token
export function removeToken() {
  return localStorage.removeItem(TokenKey)
}

// 获取用户信息
export function getUserInfo() {
  const userInfo = localStorage.getItem('nova_userInfo')
  return userInfo ? JSON.parse(userInfo) : null
}

// 设置用户信息
export function setUserInfo(userInfo) {
  return localStorage.setItem('nova_userInfo', JSON.stringify(userInfo))
}

// 移除用户信息
export function removeUserInfo() {
  return localStorage.removeItem('nova_userInfo')
}

// 清除所有登录相关信息
export function clearAuth() {
  removeToken()
  removeUserInfo()
}

// 检查是否已登录
export function isAuthenticated() {
  return !!getToken()
}

// 获取记住的账号
export function getRememberedAccount() {
  const account = localStorage.getItem('nova_remembered_account')
  return account ? JSON.parse(account) : null
}

// 设置记住账号
export function setRememberedAccount(username, password) {
  return localStorage.setItem('nova_remembered_account', JSON.stringify({
    username,
    password: btoa(password)
  }))
}

// 移除记住的账号
export function removeRememberedAccount() {
  return localStorage.removeItem('nova_remembered_account')
}
