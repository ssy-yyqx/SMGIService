/**
 * Created by PanJiaChen on 16/11/18.
 */
import { Message } from 'element-ui'
/**
 * Parse the time to string
 * @param {(Object|string|number)} time
 * @param {string} cFormat
 * @returns {string | null}
 */
export function parseTime(time, cFormat) {
  if (arguments.length === 0 || !time) {
    return null
  }
  const format = cFormat || '{y}-{m}-{d} {h}:{i}:{s}'
  let date
  if (typeof time === 'object') {
    date = time
  } else {
    if ((typeof time === 'string')) {
      if ((/^[0-9]+$/.test(time))) {
        // support "1548221490638"
        time = parseInt(time)
      } else {
        // support safari
        // https://stackoverflow.com/questions/4310953/invalid-date-in-safari
        time = time.replace(new RegExp(/-/gm), '/')
      }
    }

    if ((typeof time === 'number') && (time.toString().length === 10)) {
      time = time * 1000
    }
    date = new Date(time)
  }
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  const time_str = format.replace(/{([ymdhisa])+}/g, (result, key) => {
    const value = formatObj[key]
    // Note: getDay() returns 0 on Sunday
    if (key === 'a') { return ['日', '一', '二', '三', '四', '五', '六'][value ] }
    return value.toString().padStart(2, '0')
  })
  return time_str
}

/**
 * @param {number} time
 * @param {string} option
 * @returns {string}
 */
export function formatTime(time, option) {
  if (('' + time).length === 10) {
    time = parseInt(time) * 1000
  } else {
    time = +time
  }
  const d = new Date(time)
  const now = Date.now()

  const diff = (now - d) / 1000

  if (diff < 30) {
    return '刚刚'
  } else if (diff < 3600) {
    // less 1 hour
    return Math.ceil(diff / 60) + '分钟前'
  } else if (diff < 3600 * 24) {
    return Math.ceil(diff / 3600) + '小时前'
  } else if (diff < 3600 * 24 * 2) {
    return '1天前'
  }
  if (option) {
    return parseTime(time, option)
  } else {
    return (
      d.getMonth() +
      1 +
      '月' +
      d.getDate() +
      '日' +
      d.getHours() +
      '时' +
      d.getMinutes() +
      '分'
    )
  }
}

/**
 * @param {string} url
 * @returns {Object}
 */
export function param2Obj(url) {
  const search = decodeURIComponent(url.split('?')[1]).replace(/\+/g, ' ')
  if (!search) {
    return {}
  }
  const obj = {}
  const searchArr = search.split('&')
  searchArr.forEach(v => {
    const index = v.indexOf('=')
    if (index !== -1) {
      const name = v.substring(0, index)
      const val = v.substring(index + 1, v.length)
      obj[name] = val
    }
  })
  return obj
}

/**
 * 格式化排序条件
 * @param prop
 * @param order
 */
export function formatOrder(prop, order) {
  return '`' + prop.replace(/([A-Z])/g, '_$1').toLowerCase() + '` ' + order.replace('ending', '')
}

/**
 * 字段名化排序条件
 * @param prop
 */
export function formatColumn(prop) {
  return prop.replace(/([A-Z])/g, '_$1').toLowerCase()
}

/**
 * 下载文件请求状态码处理
 * @param response
 */
export function downloadResponseUtil(response) {
  const data = response.data
  if (data.type === 'application/json') {
    // 创建一个FileReader实例
    const reader = new FileReader()
    // 读取文件,结果用字符串形式表示
    reader.readAsText(data, 'utf-8')
    // 读取完成后,**获取reader.result**
    reader.onload = function() {
      const res = JSON.parse(reader.result)
      if (res.code === 999) {
        Message({
          message: res.msg || res.message || 'Error',
          type: 'error',
          duration: 5 * 1000
        })
        return Promise.reject(res)
      }
      if (res.code !== 200) {
        if (res.code === 401) {
          this.$store.dispatch('user/logout').then((res) => {
            location.reload()
          }).catch((res) => {
            console.log(res)
          })
        } else {
          Message({
            message: res.msg || res.message || 'Error',
            type: 'error',
            duration: 5 * 1000
          })
          return Promise.reject(new Error(res.msg || res.message || 'Error'))
        }
      } else {
        return res
      }
    }
  } else {
    const link = document.createElement('a')
    const fileName = response.headers.filename
    console.log(fileName)
    link.href = window.URL.createObjectURL(data)
    link.download = decodeURIComponent(fileName)
    link.click()
  }
}
