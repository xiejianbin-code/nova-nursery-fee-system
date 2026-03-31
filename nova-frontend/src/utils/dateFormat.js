/**
 * 日期时间格式化工具函数
 * 提供统一的日期时间格式化方法
 */

/**
 * 补零函数
 * @param {number} num - 需要补零的数字
 * @param {number} length - 目标长度，默认为2
 * @returns {string} 补零后的字符串
 */
function padZero(num, length = 2) {
  return String(num).padStart(length, '0')
}

/**
 * 解析日期参数
 * @param {Date|string|number} date - 日期参数
 * @returns {Date} Date对象
 */
function parseDate(date) {
  if (!date) return new Date()
  
  if (date instanceof Date) {
    return date
  }
  
  if (typeof date === 'string') {
    // 处理IOS日期格式兼容问题
    if (/^\d{4}-\d{2}-\d{2}$/.test(date)) {
      return new Date(date.replace(/-/g, '/'))
    }
    if (/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/.test(date)) {
      return new Date(date.replace(/-/g, '/'))
    }
    return new Date(date)
  }
  
  if (typeof date === 'number') {
    return new Date(date)
  }
  
  return new Date()
}

/**
 * 格式化日期时间为 YYYY-MM-DD HH:mm:ss
 * @param {Date|string|number} date - 日期参数
 * @param {string} format - 格式化模板，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的日期时间字符串
 */
export function formatDateTime(date, format = 'YYYY-MM-DD HH:mm:ss') {
  const d = parseDate(date)
  
  if (isNaN(d.getTime())) {
    return '-'
  }
  
  const year = d.getFullYear()
  const month = d.getMonth() + 1
  const day = d.getDate()
  const hours = d.getHours()
  const minutes = d.getMinutes()
  const seconds = d.getSeconds()
  
  return format
    .replace(/YYYY/g, year)
    .replace(/YY/g, String(year).slice(-2))
    .replace(/MM/g, padZero(month))
    .replace(/M/g, month)
    .replace(/DD/g, padZero(day))
    .replace(/D/g, day)
    .replace(/HH/g, padZero(hours))
    .replace(/H/g, hours)
    .replace(/mm/g, padZero(minutes))
    .replace(/m/g, minutes)
    .replace(/ss/g, padZero(seconds))
    .replace(/s/g, seconds)
}

/**
 * 格式化日期为 YYYY-MM-DD
 * @param {Date|string|number} date - 日期参数
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date) {
  return formatDateTime(date, 'YYYY-MM-DD')
}

/**
 * 格式化时间为 HH:mm:ss
 * @param {Date|string|number} date - 日期参数
 * @returns {string} 格式化后的时间字符串
 */
export function formatTime(date) {
  return formatDateTime(date, 'HH:mm:ss')
}

/**
 * 格式化为简短日期 MM-DD
 * @param {Date|string|number} date - 日期参数
 * @returns {string} 格式化后的日期字符串
 */
export function formatShortDate(date) {
  return formatDateTime(date, 'MM-DD')
}

/**
 * 格式化为简短时间 HH:mm
 * @param {Date|string|number} date - 日期参数
 * @returns {string} 格式化后的时间字符串
 */
export function formatShortTime(date) {
  return formatDateTime(date, 'HH:mm')
}

/**
 * 获取相对时间描述
 * @param {Date|string|number} date - 日期参数
 * @returns {string} 相对时间描述
 */
export function formatRelativeTime(date) {
  const d = parseDate(date)
  
  if (isNaN(d.getTime())) {
    return '-'
  }
  
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  
  // 小于1分钟
  if (diff < 60 * 1000) {
    return '刚刚'
  }
  
  // 小于1小时
  if (diff < 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 1000))}分钟前`
  }
  
  // 小于24小时
  if (diff < 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 60 * 1000))}小时前`
  }
  
  // 小于30天
  if (diff < 30 * 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (24 * 60 * 60 * 1000))}天前`
  }
  
  // 小于12个月
  if (diff < 12 * 30 * 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (30 * 24 * 60 * 60 * 1000))}个月前`
  }
  
  // 超过12个月
  return `${Math.floor(diff / (12 * 30 * 24 * 60 * 60 * 1000))}年前`
}

/**
 * 获取星期几
 * @param {Date|string|number} date - 日期参数
 * @returns {string} 星期几
 */
export function getWeekDay(date) {
  const d = parseDate(date)
  
  if (isNaN(d.getTime())) {
    return '-'
  }
  
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return weekDays[d.getDay()]
}

/**
 * 判断是否为今天
 * @param {Date|string|number} date - 日期参数
 * @returns {boolean}
 */
export function isToday(date) {
  const d = parseDate(date)
  const today = new Date()
  
  return d.getFullYear() === today.getFullYear() &&
    d.getMonth() === today.getMonth() &&
    d.getDate() === today.getDate()
}

/**
 * 判断是否为昨天
 * @param {Date|string|number} date - 日期参数
 * @returns {boolean}
 */
export function isYesterday(date) {
  const d = parseDate(date)
  const yesterday = new Date()
  yesterday.setDate(yesterday.getDate() - 1)
  
  return d.getFullYear() === yesterday.getFullYear() &&
    d.getMonth() === yesterday.getMonth() &&
    d.getDate() === yesterday.getDate()
}

/**
 * 智能格式化日期
 * 今天的显示时间，昨天的显示"昨天"，其他显示日期
 * @param {Date|string|number} date - 日期参数
 * @returns {string}
 */
export function formatSmartDate(date) {
  const d = parseDate(date)
  
  if (isNaN(d.getTime())) {
    return '-'
  }
  
  if (isToday(d)) {
    return formatShortTime(d)
  }
  
  if (isYesterday(d)) {
    return '昨天'
  }
  
  return formatDate(d)
}
