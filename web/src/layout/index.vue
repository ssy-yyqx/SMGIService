<template>
  <div id="AppWrapper" class="app-wrapper">
    <Header />
    <app-main />
    <Footer />
    <Login />
    <Register />
  </div>
</template>

<script>
import { Header, AppMain, Footer } from './components'
import Login from './components/Login'
import Register from './components/Register'
import { getToken, getUserName } from '@/utils/auth'
export default {
  name: 'Layout',
  components: {
    Header,
    AppMain,
    Footer,
    Login,
    Register
  },
  created() {
    // 存储随机生成的用户名
    if (getToken()) {
      this.$store.dispatch('user/setName', getUserName())
    } else {
      this.$store.dispatch('user/setName', '游客' + new Date().getTime())
    }
  },
  methods: {
  }
}
</script>

<style lang="scss" scoped>
  @import "~@/styles/mixin.scss";
  @import "~@/styles/variables.scss";

  .app-wrapper {
    @include clearfix;
    position: relative;
    height: 100%;
    width: 100%;
    overflow-x: hidden;
    overflow-y: auto;
    background: #FFFFFF;
  }
</style>
