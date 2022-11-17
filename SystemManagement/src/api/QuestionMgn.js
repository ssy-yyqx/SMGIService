import request from '@/utils/request'
// 获取列表
export function getTableList(params) {
  return request({
    url: '/smart/list',
    method: 'get',
    params
  })
}
// 添加问题
export function addQuestion(data) {
  return request({
    url: '/smart/insert',
    method: 'post',
    data
  })
}
// 修改问题
export function updateQuestion(data) {
  return request({
    url: '/smart/update',
    method: 'put',
    data
  })
}
// 删除问题
export function deleteQuestion(data) {
  return request({
    url: '/smart/delete/' + data.id,
    method: 'delete'
  })
}
