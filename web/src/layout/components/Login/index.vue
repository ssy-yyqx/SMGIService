<template>
  <el-dialog
    title="登录"
    :visible.sync="isLoginDialog"
    width="30%"
    :before-close="handleClose"
    :close-on-click-modal="false"
  >
    <el-form label-position="right" label-width="80px" :model="form">
      <el-form-item label="用户名">
        <el-input v-model="form.username" placeholder="用户名/手机号" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" placeholder="请输入登陆密码" />
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-tooltip class="item" effect="dark" content="请联系管理员：13862365263" placement="top">
        <span class="reset-password">重置密码<i class="el-icon-question" /></span>
      </el-tooltip>

      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="onHandleLogin">登 陆</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { mapGetters } from 'vuex'
import * as API from '@/api/user.js'
import { setToken, setUserName } from '@/utils/auth'
export default {
  data() {
    return {
      form: {
        username: '',
        password: ''
      }
    }
  },
  computed: {
    ...mapGetters([
      'isLoginDialog'
    ])
  },
  methods: {
    // 关闭弹窗
    handleClose() {
      this.$store.dispatch('user/setLoginDialog', false)
    },
    // 登录
    onHandleLogin() {
      API.login(this.form).then(res => {
        const { code, token, msg } = res
        if (String(code) === '200') {
          this.$store.dispatch('user/setLoginDialog', false)
          this.$message.success('登陆成功')
          // 存储token
          setToken(token)
          this.$store.dispatch('user/setToken', token)
          setUserName(this.form.username)
          this.$store.dispatch('user/setName', this.form.username)
        } else {
          this.$message.error(msg)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.reset-password{
  font-size: 14px;
  color: #535353;
  cursor: pointer;
  margin-right: 20px;
}
</style>
