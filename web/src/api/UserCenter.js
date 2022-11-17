import request from '@/utils/request'
// 获取用户信息
export function getUserInfo(params) {
  return request({
    url: '/user/info',
    method: 'get',
    params
  })
}
// 修改用户信息
export function updateUserInfo(data) {
  return request({
    url: '/user/updateinfo',
    method: 'post',
    data
  })
}
// 重置密码
export function resetPassword(data) {
  return request({
    url: '/user/updatepwdinfo',
    method: 'post',
    data
  })
}
// 校验密码
export function validatePassword(data) {
  return request({
    url: '/user/checkpwd',
    method: 'post',
    data
  })
}
// 在线办理 在线办理列表
export function getOnlineDeal(params) {
  return request({
    url: '/user/onlineDeal/list',
    method: 'get',
    params
  })
}
// 申请预约列表
export function getAppointment(params) {
  return request({
    url: '/user/appointment/list',
    method: 'get',
    params
  })
}
// 申请预约完成
export function getFinish(params, id) {
  return request({
    url: '/user/offlineAppointment/finish/' + id,
    method: 'get',
    params
  })
}
// 撤销预约
export function cancelApproval(id) {
  return request({
    url: '/user/appointment/cancel/' + id,
    method: 'put'
  })
}
// 我的审批列表
export function getApproveList(params) {
  return request({
    url: '/user/examinet/list',
    method: 'get',
    params
  })
}
// 审批通过
export function passApprove(data) {
  return request({
    url: '/user/appointment/updatepass',
    method: 'post',
    data
  })
}
// 审批不通过
export function notPpassApprove(data) {
  return request({
    url: '/user/appointment/updatenotpass',
    method: 'post',
    data
  })
}
// 用户收藏列表
export function getCollection(data) {
  return request({
    url: '/user/collection/list',
    method: 'post',
    data
  })
}
// 取消收藏(个人中心)
export function cancelCollectInUserCenter(data) {
  return request({
    url: '/user/collect/cancel/' + data.id,
    method: 'delete'
  })
}
//  用户点赞列表
export function getSupportList(data) {
  return request({
    url: '/user/support/list',
    method: 'post',
    data
  })
}
// 取消点赞(个人中心)
export function cancelPriseInUserCenter(data) {
  return request({
    url: '/user/support/cancel/' + data.id,
    method: 'delete'
  })
}
