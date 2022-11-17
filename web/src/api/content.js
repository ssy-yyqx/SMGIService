import request from '@/utils/request'

// 获取新闻/公告列表
export function getInfoList(params) {
  return request({
    url: '/portal/module/info/page/',
    method: 'get',
    params
  })
}

// 获取新闻/公告详情
export function getInfoDetail(params) {
  return request({
    url: '/portal/content/' + params.id,
    method: 'get'
  })
}

// 获取天气信息
export function getWeather() {
  return request({
    url: '/portal/weather',
    method: 'get'
  })
}

// 搜索数据
export function getQueryLike(params) {
  return request({
    url: '/es/queryLike',
    method: 'get',
    params
  })
}
// 搜索查看更多
export function getListmore(data) {
  return request({
    url: '/es/content/more',
    method: 'post',
    data
  })
}

// 获取中心概况信息
export function getCenterPageInfo(data) {
  return request({
    url: '/portal/module/single/page/' + data.id,
    method: 'get'
  })
}

// 获取点赞和收藏信息
export function getPraiseAndCollectInfo(data) {
  return request({
    url: '/portal/module/header/' + data.id,
    method: 'get'
  })
}

// 点赞
export function savePraise(data) {
  return request({
    url: '/user/support',
    method: 'post',
    data
  })
}
// 热门搜索
export function hotwordtopfive(params) {
  return request({
    url: '/es/get/hotwordtopfive',
    method: 'get',
    params
  })
}

export function gethotwordtopfive(params) {
  return request({
    url: '/es/get/hotwordtopfive/' + params,
    method: 'get'

  })
}
// 取消点赞(页面)
export function cancelPraise(data) {
  return request({
    url: '/user/support/cancel/user/' + data.router,
    method: 'delete'
  })
}

// 收藏
export function saveCollect(data) {
  return request({
    url: '/user/collect',
    method: 'post',
    data
  })
}
// 取消收藏(页面)
export function cancelCollect(data) {
  return request({
    url: '/user/collect/cancel/user/' + data.router,
    method: 'delete'
  })
}

// 获取评论列表
export function getCommentList(params) {
  return request({
    url: '/user/comments/list',
    method: 'get',
    params
  })
}
// 提交评论
export function addComment(data) {
  return request({
    url: '/user/comments/add',
    method: 'post',
    data
  })
}
// 获取服务页面数据
export function getServicePageData(id) {
  return request({
    url: '/portal/module/service/' + id,
    method: 'get'
  })
}
// websocket
export function hasAlreadyOpen(name) {
  return request({
    url: '/websocket/exist/' + name,
    method: 'get'
  })
}
// 申请服务
export function applyService(data) {
  return request({
    url: '/user/appointment/appointment',
    method: 'post',
    data
  })
}
