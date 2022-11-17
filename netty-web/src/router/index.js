import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

export const constantRoutes = [
  {
    path: '/',
    name: 'home',
    meta: {
          title: '主页'
    },
    component: () => import('@/layout/layout'),
    redirect: '/download',
    children: [
      {
        meta: {
          title: '文件列表'
        },
        path: '/download',
        name: 'Download',
        component: () => import('@/views/download/download')
      },
      {
        meta: {
          title: '上传列表'
        },
        path: '/upload',
        name: 'Upload',
        component: () => import('@/components/upload/upload')
      },
      {
        meta: {
          title: '长链接测试'
        },
        path: '/websocket',
        name: 'Websocket',
        component: () => import('@/views/websocket/websocket')
      }
    ]
  }
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
