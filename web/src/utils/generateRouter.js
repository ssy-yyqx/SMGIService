const TEMPLATE_DICT = {
  ListAndDetail: 3, // 新闻+公告
  SinglePage: 2, // 中心概况
  DataPage: 5, // 数据页面
  ServicePage: 4, // 服务页面,
  SpecicalService: 6 // 特色服务
}
/* Layout */
import Layout from '@/layout'
export default function generateRouter(menu) {
  const router = []
  menu.forEach(item => {
    // 首页
    if (!item.childrenList || item.childrenList.length < 1) {
      // router.push({
      //   path: '/',
      //   component: Layout,
      //   redirect: '/' + item.icon,
      //   name: item.router,
      //   children: [{
      //     path: item.icon,
      //     name: item.router,
      //     // 添加模板
      //     component: () => import('@/views/dashboard/index'),
      //     meta: { title: item.moduleName }
      //   }]
      // })
    } else {
      const menus = getChildrenMenus(item)
      // 子路由生成
      const childrenRouter = []
      menus.forEach(menuItem => {
        childrenRouter.push({
          path: menuItem.router,
          name: menuItem.router,
          component: () => {
            if (menuItem.templateType === TEMPLATE_DICT.ListAndDetail) {
              return import('@/views/Template/pages/ListAndDetail/index')
            } else if (menuItem.templateType === TEMPLATE_DICT.ServicePage) {
              return import('@/views/Template/pages/ServicePage/index')
            } else if (menuItem.templateType === TEMPLATE_DICT.DataPage) {
              return import('@/views/Template/pages/DataPage/index')
            } else if (menuItem.templateType === TEMPLATE_DICT.SinglePage) {
              return import('@/views/Template/pages/SinglePage/index')
            } else {
              return import('@/views/Template/pages/SpecialService/index')
            }
          },
          meta: { title: menuItem.moduleName, id: menuItem.id, router: menuItem.router }
        })
        // 如果是列表+详情模板，则添加详情页面路由
        if (menuItem.templateType === TEMPLATE_DICT.ListAndDetail) {
          childrenRouter.push({
            path: menuItem.router + 'Detail',
            name: menuItem.router + 'Detail',
            component: () => import('@/views/Template/pages/ListAndDetail/Detail'),
            meta: { title: menuItem.moduleName + '详情', id: menuItem.id, router: menuItem.router + 'Detail' }
          })
        }
      })
      router.push({
        path: '/' + item.icon,
        component: Layout,
        redirect: `/${item.icon}/${menus[0].router}`,
        name: item.router,
        children: childrenRouter
      })
    }
  })
  // 添加404页面
  router.push({ path: '*', redirect: '/404', hidden: true })
  return router
}
// 获取子菜单
function getChildrenMenus(menu) {
  const childrenMenus = []
  function getChild(data) {
    if (data.childrenList && data.childrenList.length > 0) {
      data.childrenList.forEach(dataItem => {
        getChild((dataItem))
      })
    } else {
      childrenMenus.push(data)
    }
  }
  if (menu.childrenList && menu.childrenList.length > 0) {
    menu.childrenList.forEach(item => {
      getChild((item))
    })
  } else {
    childrenMenus.push(menu)
  }
  return childrenMenus
}
