<template>
  <div :class="{'has-logo':showLogo}">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="variables.menuBg"
        :text-color="variables.menuText"
        :unique-opened="false"
        :active-text-color="variables.menuActiveText"
        :collapse-transition="false"
        mode="vertical"
      >
        <sidebar-item v-for="route in routes" :key="route.path" :item="route" :base-path="route.path" />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Logo from './Logo'
import SidebarItem from './SidebarItem'
import variables from '@/styles/variables.scss'

export default {
  components: { SidebarItem, Logo },
  data() {
    return {
      routes: []
    }
  },
  computed: {
    ...mapGetters([
      'sidebar'
    ]),
    // routes() {
    //   console.log('routes:', this.$router.options.routes)
    //   return this.$router.options.routes
    // },
    activeMenu() {
      const route = this.$route
      const { path } = route
      return path
    },
    showLogo() {
      return this.$store.state.settings.sidebarLogo
    },
    variables() {
      return variables
    },
    isCollapse() {
      return !this.sidebar.opened
    }
  },
  created() {
    this.routes = this.getMenus()
  },
  methods: {
    getMenus() {
      return [
        {
          path: 'dashboard',
          name: 'Dashboard',
          meta: { title: '首页', icon: 'dashboard' }
        },
        {
          path: '/UserMgn/index',
          name: 'MenuSeUserMgntting',
          meta: { title: '用户管理', icon: 'el-icon-s-custom' }
        },
        {
          path: '/PriorityMgn/index',
          name: 'PriorityMgn',
          meta: { title: '权限管理', icon: 'el-icon-key' }
        },
        {
          path: '/LogMgn/index',
          name: 'LogMgn',
          meta: { title: '日志管理', icon: 'el-icon-document-copy' }
        },
        {
          path: '/QuestionMgn/index',
          name: 'QuestionMgn',
          meta: { title: '问题管理', icon: 'el-icon-question' }
        },
        {
          path: '/MenuSetting/index',
          name: 'MenuSetting',
          meta: { title: '栏目管理', icon: 'el-icon-menu' }
        }
      ]
    }
  }
}
</script>
