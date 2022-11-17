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
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },

  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },

  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [{
      path: 'dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/index'),
      meta: { title: '首页', icon: 'dashboard' }
    }]
  },
  {
    path: '/MenuSetting',
    component: Layout,
    redirect: '/MenuSetting/index',
    meta: { title: '栏目管理', icon: 'setting' },
    children: [
      {
        path: 'index',
        name: 'MenuSetting',
        component: () => import('@/views/MenuSetting/index')
        // meta: { title: '栏目管理', icon: 'setting' }
      },
      {
        path: 'MenuAdd',
        name: 'MenuAdd',
        component: () => import('@/views/MenuSetting/CreateMenu/index'),
        meta: { title: '新增栏目', icon: 'setting' }
      },
      {
        path: 'MenuEdit',
        name: 'MenuEdit',
        component: () => import('@/views/MenuSetting/EditMenu/index'),
        meta: { title: '修改栏目', icon: 'setting' }
      }
    ]
  },
  {
    path: '/UserMgn',
    component: Layout,
    redirect: '/UserMgn/index',
    children: [
      {
        path: 'index',
        name: 'UserMgn',
        component: () => import('@/views/UserMgn/index'),
        meta: { title: '用户管理', icon: 'monitor' }
      }
    ]
  },
  {
    path: '/PriorityMgn',
    component: Layout,
    redirect: '/PriorityMgn/index',
    children: [
      {
        path: 'index',
        name: 'PriorityMgn',
        component: () => import('@/views/PriorityMgn/index'),
        meta: { title: '权限管理', icon: 'monitor' }
      }
    ]
  },
  {
    path: '/LogMgn',
    component: Layout,
    redirect: '/LogMgn/index',
    children: [
      {
        path: 'index',
        name: 'LogMgn',
        component: () => import('@/views/LogMgn/index'),
        meta: { title: '日志管理', icon: 'monitor' }
      }
    ]
  },
  {
    path: '/QuestionMgn',
    component: Layout,
    redirect: '/QuestionMgn/index',
    children: [
      {
        path: 'index',
        name: 'QuestionMgn',
        component: () => import('@/views/QuestionMgn/index'),
        meta: { title: '问题管理', icon: 'monitor' }
      }
    ]
  },

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
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
