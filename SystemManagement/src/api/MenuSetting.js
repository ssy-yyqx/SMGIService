import request from '@/utils/request'

// 获取表格列表数据
export function getTableList(params) {
  return request({
    url: '/module/tree',
    method: 'get',
    params
  })
  // return new Promise(resolve => {
  //   const result = {
  //     code: 200,
  //     data: [
  //       // {
  //       //   id: 0,
  //       //   moduleName: '首页',
  //       //   templateType: 'model1',
  //       //   sort: 0,
  //       //   router: 'dashboard',
  //       //   isShow: true
  //       // },
  //       {
  //         id: 1,
  //         moduleName: '新闻聚焦',
  //         templateType: 'ListAndDetail',
  //         sort: 0,
  //         router: 'InternationalNews',
  //         isShow: true,
  //         children: [
  //           {
  //             id: 11,
  //             moduleName: '国际要闻',
  //             templateType: 'ListAndDetail',
  //             sort: 0,
  //             router: 'InternationalNews',
  //             isShow: true
  //           },
  //           {
  //             id: 12,
  //             moduleName: '军媒视点',
  //             templateType: 'ListAndDetail',
  //             sort: 0,
  //             router: 'MilitaryMedia',
  //             isShow: true
  //           }
  //         ]
  //       }, {
  //         id: 2,
  //         moduleName: '通知公告',
  //         templateType: 'ListAndDetail',
  //         sort: 0,
  //         router: 'WeekMeeting',
  //         isShow: true,
  //         children: [
  //           {
  //             id: 21,
  //             moduleName: '一周会议',
  //             templateType: 'ListAndDetail',
  //             sort: 0,
  //             router: 'WeekMeeting',
  //             isShow: true
  //           },
  //           {
  //             id: 22,
  //             moduleName: '中心公告',
  //             templateType: 'ListAndDetail',
  //             sort: 0,
  //             router: 'CenterNotice',
  //             isShow: true
  //           }
  //         ]
  //       }, {
  //         id: 3,
  //         moduleName: '中心职能',
  //         templateType: 'model1',
  //         sort: 0,
  //         router: 'CenterIntroduce',
  //         isShow: true,
  //         children: [{
  //           id: 31,
  //           moduleName: '中心介绍',
  //           templateType: 'model1',
  //           sort: 0,
  //           router: 'CenterIntroduce',
  //           isShow: true
  //         }, {
  //           id: 32,
  //           moduleName: '一室',
  //           templateType: 'model1',
  //           sort: 0,
  //           router: 'RoomIntroduce1',
  //           isShow: true
  //         }]
  //       }, {
  //         id: 4,
  //         moduleName: '业务导航',
  //         templateType: 'model1',
  //         sort: 0,
  //         router: 'BusinessNavigation5_1_1_1',
  //         isShow: true,
  //         children: [
  //           {
  //             id: 41,
  //             moduleName: '空间基准建立维持及应用',
  //             templateType: 'model1',
  //             sort: 0,
  //             router: 'BusinessNavigation5_1_1',
  //             isShow: true,
  //             children: [
  //               {
  //                 id: 411,
  //                 moduleName: '大地基准',
  //                 templateType: 'model1',
  //                 sort: 0,
  //                 router: 'BusinessNavigation5_1_1',
  //                 isShow: true,
  //                 children: [
  //                   {
  //                     id: 4111,
  //                     moduleName: '大地控制点',
  //                     templateType: 'model1',
  //                     sort: 0,
  //                     router: 'BusinessNavigation5_1_1',
  //                     isShow: true
  //                   }
  //                 ]
  //               }
  //             ]
  //           }
  //         ]
  //       }, {
  //         id: 5,
  //         moduleName: '特色服务',
  //         templateType: 'model1',
  //         sort: 0,
  //         router: 'SpecialService6_1_1',
  //         isShow: true,
  //         children: [
  //           {
  //             id: 51,
  //             moduleName: '在线解算服务',
  //             templateType: 'model1',
  //             sort: 0,
  //             router: 'SpecialService6_1_1',
  //             isShow: true,
  //             children: [
  //               {
  //                 id: 511,
  //                 moduleName: '磁场强度计算',
  //                 templateType: 'model1',
  //                 sort: 0,
  //                 router: 'SpecialService6_1_1',
  //                 isShow: true
  //               },
  //               {
  //                 id: 512,
  //                 moduleName: '磁偏角计算',
  //                 templateType: 'model1',
  //                 sort: 0,
  //                 router: 'SpecialService6_1_2',
  //                 isShow: true
  //               }
  //             ]
  //           }
  //         ]
  //       }],
  //     msg: 'success'
  //   }
  //   resolve(result)
  // })
}
// 添加菜单
export function addMenu(data) {
  return request({
    url: '/module/add',
    method: 'put',
    data
  })
}
// 修改菜单
export function editMenu(data) {
  return request({
    url: '/module/edit',
    method: 'post',
    data
  })
}
// 删除菜单
export function deleteMenu(data) {
  return request({
    url: '/module/del/' + data.id,
    method: 'delete'
  })
}
// 设置菜单显隐性
export function setMenuShow(data) {
  return request({
    url: '/module/show',
    method: 'post',
    data
  })
}
// 菜单排序
export function sortMenu(data) {
  return request({
    url: '/module/sort',
    method: 'post',
    data
  })
}

