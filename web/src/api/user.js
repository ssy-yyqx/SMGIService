import request from '@/utils/request'
// import menJson from './menu.json'
// 登录
export function login(data) {
  return request({
    url: '/portal/login',
    method: 'post',
    data
  })
}
// 注册
export function register(data) {
  return request({
    url: '/portal/register',
    method: 'put',
    data
  })
}
// 登出
export function logout() {
  return request({
    url: '/logout',
    method: 'post'
  })
}
// 获取菜单列表
export function getMenuList() {
  return request({
    url: '/portal/module',
    method: 'get'
  })
  // return new Promise((resolve, reject) => {
  //   resolve({
  //     code: 20000,
  //     data: [
  //       {
  //         id: 0,
  //         moduleName: '首页',
  //         templateType: 'model1',
  //         sort: 0,
  //         router: 'Dashboard',
  //         isShow: true,
  //         image: 'dashboard'
  //       },
  //       {
  //         id: 1,
  //         moduleName: '新闻聚焦',
  //         templateType: 'ListAndDetail',
  //         sort: 0,
  //         router: 'InternationalNews',
  //         isShow: true,
  //         image: 'news',
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
  //         templateType: 'model1',
  //         sort: 0,
  //         router: 'WeekMeeting',
  //         isShow: true,
  //         image: 'notice',
  //         children: [
  //           {
  //             id: 21,
  //             moduleName: '一周会议',
  //             templateType: 'model1',
  //             sort: 0,
  //             router: 'WeekMeeting',
  //             isShow: true
  //           },
  //           {
  //             id: 22,
  //             moduleName: '中心公告',
  //             templateType: 'model1',
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
  //         image: 'centerIntroduce',
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
  //         templateType: 'BusinessNavigation',
  //         sort: 0,
  //         router: 'BusinessNavigation5_1_1_1',
  //         isShow: true,
  //         image: 'businessNavigation',
  //         children: [
  //           {
  //             id: 41,
  //             moduleName: '空间基准建立维持及应用',
  //             templateType: 'BusinessNavigation',
  //             sort: 0,
  //             router: 'BusinessNavigation5_1_1',
  //             isShow: true,
  //             children: [
  //               {
  //                 id: 411,
  //                 moduleName: '大地基准',
  //                 templateType: 'BusinessNavigation',
  //                 sort: 0,
  //                 router: 'BusinessNavigation5_1_1',
  //                 isShow: true,
  //                 children: [
  //                   {
  //                     id: 4111,
  //                     moduleName: '大地控制点',
  //                     templateType: 'BusinessNavigation',
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
  //         templateType: 'SpecialService',
  //         sort: 0,
  //         router: 'SpecialService6_1_1',
  //         isShow: true,
  //         image: 'specialService',
  //         children: [
  //           {
  //             id: 51,
  //             moduleName: '在线解算服务',
  //             templateType: 'SpecialService',
  //             sort: 0,
  //             router: 'SpecialService6_1_1',
  //             isShow: true,
  //             children: [
  //               {
  //                 id: 511,
  //                 moduleName: '磁场强度计算',
  //                 templateType: 'SpecialService',
  //                 sort: 0,
  //                 router: 'SpecialService6_1_1',
  //                 isShow: true
  //               },
  //               {
  //                 id: 512,
  //                 moduleName: '磁偏角计算',
  //                 templateType: 'SpecialService',
  //                 sort: 0,
  //                 router: 'SpecialService6_1_2',
  //                 isShow: true
  //               }
  //             ]
  //           }
  //         ]
  //       }]
  //   })
  // })
}

