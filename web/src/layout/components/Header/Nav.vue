<template>
  <section class="nav-container">
    <div class="nav-content">
      <div
        v-for="(item, index) in menu"
        :key="item.id"
        class="nav-item"
        :class="activeMenu === item.router ? 'nav-item-active' : ''"
        @click="onHandleChangeMenu(item, index)"
      >
        <span class="nav-item__img">
          <svg-icon :icon-class="item.icon" />
        </span>
        <span class="nav-item__text">{{ item.moduleName }}</span>
      </div>
    </div>
  </section>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'HeaderNav',
  data() {
    return {
      activeMenu: ''
    }
  },
  computed: {
    ...mapGetters(['menu'])
  },
  watch: {
    $route: {
      handler(val) {
        const routerName = val.matched[0].name
        // 设置当前选中的头部菜单
        this.activeMenu = routerName
        // 设置当前菜单
        this.setActiveMenuName(routerName)
        // 设置左侧菜单
        this.setLeftMenuList(routerName)
      },
      immediate: true
    }
  },
  created() {
    // 设置当前选中的头部菜单
    this.activeMenu = this.$route.matched[0].name
    // 设置当前菜单
    this.setActiveMenuName(this.activeMenu)
    // 设置左侧菜单
    this.setLeftMenuList(this.activeMenu)
  },
  methods: {
    setLeftMenuList(routerName) {
      if (routerName !== 'MyAccount') {
        let currentLeftMenuList
        this.menu.every((item) => {
          currentLeftMenuList = item.childrenList
          return item.router !== routerName
        })
        // 设置左侧菜单
        if (currentLeftMenuList && currentLeftMenuList.length > 0) {
          this.$store.dispatch('user/setLeftMenu', currentLeftMenuList)
        } else {
          this.$store.dispatch('user/setLeftMenu', [])
        }
      } else {
        const currentLeftMenuList = [
          {
            id: '8_1',
            moduleName: '我的账户',
            router: 'MyAccount'
          },
          {
            id: '8_2',
            moduleName: '我的在线办理',
            router: 'MyOnlineHandling'
          },
          {
            id: '8_3',
            moduleName: '我的申请预约',
            router: 'MyApply'
          },
          {
            id: '8_4',
            moduleName: '我的收藏',
            router: 'MyCollect'
          },
          {
            id: '8_5',
            moduleName: '我的点赞',
            router: 'MyPraise'
          },
          {
            id: '8_6',
            moduleName: '我的审批',
            router: 'MyApprove'
          }
        ]
        this.$store.dispatch('user/setLeftMenu', currentLeftMenuList)
      }
    },
    // 获取菜单名称
    setActiveMenuName(routerName) {
      if (routerName !== 'MyAccount') {
        const item = this.menu.find((item) => item.router === routerName)
        this.$store.dispatch('user/activeMenuName', item ? item.moduleName : '')
      } else {
        this.$store.dispatch('user/activeMenuName', '个人中心')
      }
    },
    // 切换菜单
    onHandleChangeMenu(menu, index) {
      if (menu.router === this.activeMenu) {
        return
      }
      this.activeMenu = menu.router
      // 设置当前菜单
      this.setActiveMenuName(this.activeMenu)
      if (menu.children && menu.children.length > 0) {
        this.$store.dispatch('user/setLeftMenu', menu.children)
      } else {
        this.$store.dispatch('user/setLeftMenu', [])
      }
      this.$router.push({ name: menu.router })
      console.log(menu.router)
    }
  }
}
</script>

<style lang="scss" scoped>
.nav-container {
  width: 100%;
  // height: 60px;
  line-height: 60px;
  background: rgba(4, 84, 207, 0.41);
  .nav-content {
    width: 1400px;
    height: 100%;
    margin: 0 auto;
    display: flex;
    .nav-item {
      cursor: pointer;
      text-align: center;
      color: #ffffff;
      position: relative;
      margin-right: 40px;
      &:hover,
      &-active {
        color: #4ae1e6;
        &::after {
          content: ' ';
          height: 4px;
          width: 100%;
          background-color: #4ae1e6;
          position: absolute;
          bottom: 0px;
          left: 0px;
        }
      }
      &__img {
        margin-right: 10px;
      }
      &__text {
        font-size: 14px;
      }
    }
  }
}
</style>
