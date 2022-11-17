import request from '@/utils/request'
// 获取列表
export function getTableList(params) {
  return request({
    url: '/system/log/list',
    method: 'get',
    params
  })
}
