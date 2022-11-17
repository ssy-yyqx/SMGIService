import router from './router'
import store from './store'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import getPageTitle from '@/utils/get-page-title'
import generateRouter from '@/utils/generateRouter'
import { getToken } from '@/utils/auth'

NProgress.configure({ showSpinner: false }) // NProgress Configuration
const permissionRouters = ['/UserCenter/MyAccount']

router.beforeEach(async(to, from, next) => {
  const permssionRoute = permissionRouters.find(item => item === to.path)
  if (permssionRoute && !getToken()) {
    next({ path: '/' })
    return
  }
  // start progress bar
  NProgress.start()

  // set page title
  document.title = getPageTitle(to.meta.title)
  // 已生成路由
  if (store.state.user.isGenerateRoute) {
    next()
    NProgress.done()
  } else {
    try {
      // 获取菜单数据
      const menus = await store.dispatch('user/getMenuList', {})
      // 根据菜单生成路由
      const routers = generateRouter(menus)
      // 动态添加路由
      router.addRoutes(routers)
      // 存储已生成路由
      store.dispatch('user/setIsGenerateRoute', true)
      next({ ...to, replace: true })
    } catch (error) {
      console.log('error')
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  // finish progress bar
  NProgress.done()
})
