import request from '@/utils/request'

// 角色列表
export function getList(data) {
  return request({
    url: '/system/role/list',
    method: 'post',
    data
  })
}

// 获取所有权限列表
export function permission(params) {
  return request({
    url: '/system/role/permission',
    method: 'get',
    params
  })
}

// 修改角色权限
export function edit(data) {
  return request({
    url: '/system/role/edit',
    method: 'put',
    data
  })
}