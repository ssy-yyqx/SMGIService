<template>
  <div>
    <Paper title="基本信息">
      <div slot="content">
        <el-form ref="form" :model="form" :rules="rules" label-width="120px">
          <el-row>
            <el-col :span="9">
              <el-form-item label="用户名：" prop="username">
                <el-input v-model="form.username" disabled placeholder="请输入用户名" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="9">
              <el-form-item label="真实姓名：" prop="realName">
                <el-input v-model="form.realName" placeholder="请输入真实姓名" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="9">
              <el-form-item label="单位：" prop="company">
                <el-input v-model="form.company" placeholder="请输入单位" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="9">
              <el-form-item label="角色：">
                <el-input v-model="userRoles" disabled placeholder="请输入角色" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="9">
              <el-form-item label="手机号码：" prop="phoneNumber">
                <el-input v-model="form.phoneNumber" placeholder="请输入手机号码" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="9">
              <el-form-item label="邮箱：" prop="email">
                <el-input v-model="form.email" placeholder="请输入邮箱" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="9">
              <div class="button-section">
                <el-button @click="onReset">重置</el-button>
                <el-button type="primary" @click="onSubmit">提交</el-button>
              </div>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </Paper>
    <Paper title="安全设置">
      <div slot="content">
        <el-form label-width="120px">
          <el-row>
            <el-col :span="4">
              <el-form-item label="登陆密码：">
                ******
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item>
                <el-button
                  class="amend"
                  type="primary"
                  @click="onEditPassWord"
                >修改</el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </Paper>
    <el-dialog
      title="重置密码"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      width="30%"
      :before-close="handleClose"
    >
      <el-form ref="ruleForm" :model="passwordForm" status-icon :rules="passwordRules" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" autocomplete="off" />
        </el-form-item>
        <el-form-item label="新密码" prop="passworld">
          <el-input v-model="passwordForm.passworld" type="password" autocomplete="off" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassworld">
          <el-input v-model="passwordForm.confirmPassworld" type="password" autocomplete="off" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="onConfirmPassword">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import * as API from '@/api/UserCenter'
import { validatePhone } from '@/utils/validate'
export default {
  data() {
    return {
      // 用户信息表单
      form: {},
      // 用户信息校验规则
      rules: {
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' },
          { min: 0, max: 15, message: '不能超过15字符', trigger: 'blur' }
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
      },
      // 密码修改表单
      passwordForm: {},
      // 密码修改表单验证规则
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入旧密码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              API.validatePassword({ password: value }).then(res => {
                const { code, data, msg } = res
                if (String(code) === '200') {
                  if (data) {
                    callback()
                  } else {
                    callback(new Error(msg))
                  }
                } else {
                  callback(new Error(msg))
                }
              })
            }, trigger: 'blur'
          }
        ],
        passworld: [
          { required: true, message: '请输入新密码', trigger: 'blur' }
        ],
        confirmPassworld: [
          { required: true, message: '请输入确认密码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value === '') {
                callback(new Error('请再次输入密码'))
              } else if (value !== this.passwordForm.passworld) {
                callback(new Error('两次输入密码不一致!'))
              } else {
                callback()
              }
            }, trigger: 'blur'
          }
        ]
      },
      // 是否显示修改密码弹窗
      dialogVisible: false
    }
  },
  computed: {
    userRoles() {
      const roles = []
      if (this.form.roleList) {
        this.form.roleList.forEach(item => {
          roles.push(item.roleName)
        })
        return roles.join(',')
      }
      return ''
    }
  },
  created() {
    // 获取用户信息
    this.getUserInfo()
  },
  methods: {
    // 获取用户信息
    getUserInfo() {
      API.getUserInfo().then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          this.form = data
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 重置
    onReset() {
      this.getUserInfo()
    },
    // 提交
    onSubmit() {
      const { id, realName, phoneNumber, email, company } = this.form
      const params = {
        id,
        realname: realName,
        phonenumber: phoneNumber,
        email,
        company
      }
      API.updateUserInfo(params).then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          console.log(data)
        } else {
          this.$$message.error(msg)
        }
      })
    },
    // 修改密码
    onEditPassWord() {
      this.dialogVisible = true
    },
    // 修改密码提交
    onConfirmPassword() {
      const params = {
        userid: this.form.id,
        passworld: this.passwordForm.passworld
      }
      API.resetPassword(params).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.$message.success('修改密码成功！')
          this.$store.dispatch('user/setToken', '')
          this.dialogVisible = false
          this.$router.push({ name: 'dashboard' })
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 关闭修改密码弹窗
    handleClose() {
      this.dialogVisible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.button-section{
  text-align: center;
  padding: 10px;
}
.amend {
  margin-left: -100px;
}
</style>
