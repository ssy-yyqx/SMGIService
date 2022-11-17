import Vue from 'vue'
import App from './App.vue'
import './registerServiceWorker'
import router from './router'
import store from './store'
// 加载 element 组件库
import ElementUI from 'element-ui'
import locale from 'element-ui/lib/locale/lang/zh-CN' // lang i18n
// 加载 element 组件库的样式
import 'element-ui/lib/theme-chalk/index.css'
// 加载vue-uploader
import uploader from 'vue-simple-uploader'
import globalUploader from '@/components/upload/upload'
// 引入文件操作相关插件
// import fileOperationPlugins from '@/plugins/fileOperationPlugins.js'

// for (let key in fileOperationPlugins) {
// 	app.config.globalProperties[`$${key}`] = fileOperationPlugins[key]
// }
Vue.component('globalUploader', globalUploader)

Vue.use(ElementUI, { locale, size: 'small' })
Vue.use(uploader)

const Bus = new Vue();

new Vue({
    el: '#app',
    router,
    store,
    data() {
      return {
        Bus
      }
    },
    render: h => h(App)
  })
