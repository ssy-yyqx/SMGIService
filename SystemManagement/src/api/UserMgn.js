import request from '@/utils/request'
// 获取列表
export function getTableList(data) {
  return request({
    url: '/system/user/list',
    method: 'post',
    data
  })
}
// 添加用户
export function addUser(data) {
  return request({
    url: '/system/user/insert',
    method: 'post',
    data
  })
}
// 修改用户
export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data
  })
}
// 删除用户
export function deleteUser(userIds) {
  return request({
    url: '/system/user/' + userIds,
    method: 'delete'
  })
}

// 获取角色列表
export function getRole(params) {
  return request({
    url: '/system/user/role',
    method: 'get',
    params
  })
}

// 重置密码
export function resetPwd(data) {
  return request({
    url: '/system/user/resetPwd',
    method: 'post',
    data
  })
}
