import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },

  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    name: 'dashboard',
    children: [{
      path: 'dashboard',
      name: 'dashboard',
      component: () => import('@/views/dashboard/index'),
      meta: { title: '首页', icon: 'dashboard' }
    }]
  },
  {
    path: '/detail',
    component: () => import('@/views/Template/pages/ListAndDetail/Detail'),
    hidden: true
  },
  {
    path: '/search',
    component: () => import('@/layout/search')

  },
  {
    path: '/UserCenter',
    component: Layout,
    redirect: '/UserCenter/MyAccount',
    name: 'MyAccount',
    children: [
      {
        path: 'MyAccount',
        name: 'MyAccount',
        component: () => import('@/views/UserCenter/MyAccount'),
        meta: { title: '我的账户', icon: '' }
      },
      {
        path: 'MyOnlineHandling',
        name: 'MyOnlineHandling',
        component: () => import('@/views/UserCenter/MyOnlineHandling'),
        meta: { title: '我的在线办理', icon: '' }
      },
      {
        path: 'MyApply',
        name: 'MyApply',
        component: () => import('@/views/UserCenter/MyApply'),
        meta: { title: '我的申请预约', icon: '' }
      },
      {
        path: 'MyCollect',
        name: 'MyCollect',
        component: () => import('@/views/UserCenter/MyCollect'),
        meta: { title: '我的收藏', icon: '' }
      },
      {
        path: 'MyPraise',
        name: 'MyPraise',
        component: () => import('@/views/UserCenter/MyPraise'),
        meta: { title: '我的点赞', icon: '' }
      },
      {
        path: 'MyApprove',
        name: 'MyApprove',
        component: () => import('@/views/UserCenter/MyApprove'),
        meta: { title: '我的审批', icon: '' }
      }
    ]
  }
  // 404 page must be placed at the end !!!
  // { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
