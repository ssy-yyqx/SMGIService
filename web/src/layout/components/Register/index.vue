<template>
  <el-dialog
    title="注册"
    :visible.sync="isRegisterDialog"
    width="30%"
    :before-close="handleClose"
    :close-on-click-modal="false"
  >
    <el-form label-position="right" label-width="80px" :model="form" :rules="rules">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="真实姓名" prop="realName">
        <el-input v-model="form.realName" placeholder="请输入真实姓名" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" type="password" placeholder="请输入密码" />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" type="password" placeholder="请输入确认密码" />
      </el-form-item>
      <el-form-item label="手机" prop="phoneNumber">
        <el-input v-model="form.phoneNumber" placeholder="请输入手机号码" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="单位" prop="company">
        <el-input v-model="form.company" placeholder="请输入单位" />
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="onHandleRegister">注 册</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { mapGetters } from 'vuex'
import * as API from '@/api/user.js'
import { validatePhone } from '@/utils/validate'
export default {
  data() {
    return {
      form: {
        username: '',
        realName: '',
        password: '',
        confirmPassword: '',
        phoneNumber: '',
        email: '',
        company: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 0, max: 30, message: '不能超过30字符', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' },
          { min: 0, max: 15, message: '不能超过15字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value === '') {
                callback(new Error('请再次输入密码'))
              } else if (value !== this.form.password) {
                callback(new Error('两次输入密码不一致!'))
              } else {
                callback()
              }
            }, trigger: 'blur'
          }
        ],
        phoneNumber: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value === '') {
                callback('请输入手机号码')
              } else if (!validatePhone(value)) {
                callback('请输入正确的手机号码')
              } else {
                callback()
              }
            }, trigger: 'blur'
          }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] },
          { min: 0, max: 50, message: '不能超过50字符', trigger: 'blur' }
        ],
        company: [
          { min: 0, max: 50, message: '不能超过50字符', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters([
      'isRegisterDialog'
    ])
  },
  methods: {
    // 关闭弹窗
    handleClose() {
      this.$store.dispatch('user/setRegisterDialog', false)
    },
    // 注册
    onHandleRegister() {
      API.register(this.form).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.$message.success('注册成功！')
          this.$store.dispatch('user/setRegisterDialog', false)
          this.$store.dispatch('user/setLoginDialog', true)
        } else {
          this.$message.error(msg)
        }
      })
    }
  }
}
</script>

<style>

</style>
