import request from '@/utils/request'

export function getfileList () {
  return request({
    url: '/file/list',
    method: 'post'
  })
}

export function upload (data) {
  return request({
    url: '/upload',
    method: 'get',
    data
  })
}

export function deleteFile (params) {
  return request({
    url: '/file/delete',
    method: 'post',
    params
  })
}