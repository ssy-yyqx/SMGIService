import request from '@/utils/request'
// 获取新闻和公告信息
export function getNewsAndNoticeInfo(params) {
  return request({
    url: '/portal/overview',
    method: 'get',
    params
  })
}

// 获取智能回复问题列表
export function getQuestionList(params) {
  return request({
    url: '/smart/list',
    method: 'get',
    params
  })
}

// 获取历史留言个数
export function getHistoryMessageNumber(params) {
  return request({
    url: '/questions/unread',
    method: 'get',
    params
  })
}

// 留言
export function addMessage(data) {
  return request({
    url: '/questions/question',
    method: 'post',
    data
  })
}

// 获取历史留言列表
export function getHistoryMessageList(params) {
  return request({
    url: '/questions/list/user',
    method: 'get',
    params
  })
}

// 确定留言是否已读
export function confirmMessageHasRead(data) {
  return request({
    url: '/questions/read/' + data.id,
    method: 'put'
  })
}
