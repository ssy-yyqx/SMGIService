<template>
  <div class="info-container">
    <div class="info-section">
      <!-- <span class="date">{{ date }}</span> -->
      <span class="seperate" />
      <template v-if="!token">
        <span class="btn" @click="onHandleLogin"> 登录 </span>
        <span class="seperate" />
        <span class="btn" @click="onHandleRegister"> 注册 </span>
        <span class="seperate" />
      </template>
      <template v-else>
        <span class="btn" @click="gotoUserCenter"> 个人中心 </span>
        <span class="seperate" />
        <span class="btn" @click="onHandleLogout"> 退出 </span>
        <span class="seperate" />
      </template>
      <span class="management-system" @click="gotoManagementSystem">运维管理</span>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getToken } from '@/utils/auth'
import * as API from '@/api/user.js'
export default {
  data() {
    return {

    }
  },
  computed: {
    ...mapGetters(['token'])
  },
  created() {
    const token = getToken()
    if (token) {
      this.$store.dispatch('user/setToken', token)
    }
  },
  methods: {
    // 登录
    onHandleLogin() {
      this.$store.dispatch('user/setLoginDialog', true)
    },
    // 注册
    onHandleRegister() {
      this.$store.dispatch('user/setRegisterDialog', true)
    },
    // 跳转至个人中心
    gotoUserCenter() {
      const currentLeftMenuList = [
        {
          'id': '8_1',
          'name': '我的账户',
          'routerName': 'MyAccount'
        },
        {
          'id': '8_2',
          'name': '我的在线办理',
          'routerName': 'MyOnlineHandling'
        },
        {
          'id': '8_3',
          'name': '我的申请预约',
          'routerName': 'MyApply'
        },
        {
          'id': '8_4',
          'name': '我的收藏',
          'routerName': 'MyCollect'
        },
        {
          'id': '8_5',
          'name': '我的点赞',
          'routerName': 'MyPraise'
        },
        {
          'id': '8_6',
          'name': '我的审批',
          'routerName': 'MyApprove'
        }
      ]
      this.$store.dispatch('user/setLeftMenu', currentLeftMenuList)
      this.$store.dispatch('user/activeMenuName', '个人中心')
      this.$router.push({ name: 'MyAccount' })
    },
    // 退出登录
    onHandleLogout() {
      API.logout().then(res => {
        if (String(res.code) === '200') {
          this.$store.dispatch('user/setToken', '')
          this.$router.push({ name: 'dashboard' })
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    gotoManagementSystem() {
      window.open('http://192.168.1.108:9081/#/UserMgn/index', '_blank')
    }
  }
}
</script>

<style lang="scss" scoped>
.info-container{
  width: 100%;
  height: 40px;
  line-height: 40px;
  background: rgba(0,0,0, 0.3);
  .info-section{
    width: 1400px;
    height: 100%;
    margin: 0 auto;
    text-align: right;
    font-size: 14px;
    color: #FFFFFF;
    .date{
      margin-right: 20px;
    }
    .seperate{
      display: inline-block;
      width: 1px;
      background-color: #FFFFFF;
      height: 14px;
      margin: 0 5px -2px 3px;
    }
    .btn{
      cursor: pointer;
    }
  }
  .management-system{
    cursor: pointer;
    margin-left: 20px;
  }
}
.logout{
  cursor: pointer;
  text-align: center;
}
</style>
