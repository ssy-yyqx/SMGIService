import axios from 'axios'
import { Message } from 'element-ui'

// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 10000 * 20 // request timeout
})
// request interceptor
service.interceptors.request.use(
  config => {
    return config
  },
  error => {
    // do something with request error
    return Promise.reject(error)
  }
)
// response interceptor
service.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
  */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  response => {
    const res = response.data
    // if the custom code is not 20000, it is judged as an error.
    // if (res && !res.code) {
    //   return res
    // }
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
        //
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
  },
  error => {
    const errorInfo = error.response.data
    var message = errorInfo ? errorInfo.data : ''
    message = message.concat(errorInfo ? errorInfo.msg : '服务器异常！')
    Message({
      message: message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
