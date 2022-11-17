import request from '@/utils/request'
// 登录
export function login(data) {
  return request({
    url: '/manager/login',
    method: 'post',
    data
  })
}
// 登出
export function logout() {
  return request({
    url: '/logout',
    method: 'post'
  })
}
// 获取用户信息
export function getInfo() {
  return new Promise((resolve, reject) => {
    resolve({
      code: '200',
      data: {
        name: 'admin',
        avatar: 'image'
      }
    })
  })
}

// 获取留言列表
export function getMessageList(params) {
  return request({
    url: '/questions/list/admin',
    method: 'get',
    params
  })
}
// 留言答复
export function replyMessage(data) {
  return request({
    url: '/questions/reply/reply',
    method: 'put',
    data
  })
}
// 留言撤回
export function recallMessage(data) {
  return request({
    url: '/questions/reply/withdraw/' + data.id,
    method: 'put'
  })
}
