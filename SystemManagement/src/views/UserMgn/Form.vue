<template>
  <el-form ref="form" :model="formData" :rules="rules" label-width="80px">
    <el-form-item label="用户名" prop="username">
      <el-input v-model="formData.username" disabled />
    </el-form-item>
    <el-form-item label="真实姓名" prop="realName">
      <el-input v-model="formData.realName" />
    </el-form-item>
    <el-form-item label="用户类型" prop="roleName">
      <el-select v-model="formData.roleName" placeholder="请选择" @change="roleNameChange">
        <el-option
          v-for="item in options"
          :key="item.id"
          :label="item.roleName"
          :value="item.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="单位" prop="company">
      <el-input v-model="formData.company" />
    </el-form-item>
    <el-form-item label="联系电话" prop="phoneNumber">
      <el-input v-model="formData.phoneNumber" />
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="formData.email" />
    </el-form-item>
    <el-form-item label="登录密码">
      <el-button type="primary" @click="initPwd">初始化</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import * as API from '@/api/UserMgn'
export default {
  props: {
    formData: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data() {
    return {
      options: [],
      rules: {
        // question: [
        //   { required: true, message: '请输入问题', trigger: 'blur' }
        // ],
        // reply: [
        //   { required: true, message: '请输入解决方案', trigger: 'blur' }
        // ]
      }
    }
  },
  mounted() {
    this.getRole()
  },
  methods: {
    // 初始化密码
    initPwd() {
      const data = {
        id: this.formData.id
      }
      API.resetPwd(data).then(res => {
        if (res.code === 200) {
          this.$message.success(res.msg)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    // 是否通过校验
    isValidateForm() {
      return this.$refs['form'].validate()
    },
    // 获取表单数据
    getFormData() {
      return this.formData
    },
    getRole() {
      API.getRole().then(res => {
        if (res.code === 200) {
          this.options = res.data
        }
      })
    },
    roleNameChange(val) {
      console.log(val)
      this.$emit('optionss', val)
    }
  }
}
</script>

<style>

</style>
