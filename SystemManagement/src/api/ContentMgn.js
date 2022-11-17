import request from '@/utils/request'
/** ********新闻、公告接口*************/
// 获取列表
export function getTableList(params) {
  return request({
    url: '/content/info/page/',
    method: 'get',
    params
  })
}
// 添加新闻/公告
export function addNewsOrNotice(data) {
  return request({
    url: '/content/add/info',
    method: 'put',
    data
  })
}
// 修改新闻/公告
export function editNewsOrNotice(data) {
  return request({
    url: '/content/edit/info',
    method: 'post',
    data
  })
}
// 删除新闻/公告
export function deleteNewsOrNotice(data) {
  return request({
    url: '/content/del',
    method: 'delete',
    data
  })
}

// 获取中心概况信息
export function getCenterPageInfo(data) {
  return request({
    url: '/content/single/page/' + data.id,
    method: 'get'
  })
}

// 添加中心概况信息
export function addCenterPageInfo(data) {
  return request({
    url: '/content/add/single',
    method: 'put',
    data
  })
}

// 修改中心概况信息
export function editCenterPageInfo(data) {
  return request({
    url: '/content/edit/single',
    method: 'post',
    data
  })
}

// 添加服务页面数据
export function addServicePageData(data) {
  return request({
    url: '/content/add/service',
    method: 'put',
    data
  })
}
// 修改服务页面数据
export function editServicePageData(data) {
  return request({
    url: '/content/edit/service',
    method: 'post',
    data
  })
}
// 获取服务页面数据
export function getServicePageData(id) {
  return request({
    url: '/content/service/' + id,
    method: 'get'
  })
}
// 获取跳转特色服务列表
export function getGotoNavigationList(type) {
  return request({
    url: '/content/apply/' + type,
    method: 'get'
  })
}
// 添加特色服务页面数据
export function addSpecialServicePageData(data) {
  return request({
    url: '/content/add/apply',
    method: 'put',
    data
  })
}
// 修改特色服务页面数据
export function editSpecialServicePageData(data) {
  return request({
    url: '/content/edit/apply',
    method: 'post',
    data
  })
}
// 获取特色服务页面数据
export function getSpecialServicePageData(id) {
  return request({
    url: '/content/apply/' + id,
    method: 'get'
  })
}
