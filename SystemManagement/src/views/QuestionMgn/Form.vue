<template>
  <el-form ref="form" :model="formData" :rules="rules" label-width="80px">
    <el-form-item label="问题内容" prop="question">
      <el-input v-model="formData.question" />
    </el-form-item>
    <el-form-item label="解决方案" prop="reply">
      <el-input v-model="formData.reply" type="textarea" />
    </el-form-item>
  </el-form>
</template>

<script>
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
      rules: {
        question: [
          { required: true, message: '请输入问题', trigger: 'blur' }
        ],
        reply: [
          { required: true, message: '请输入解决方案', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    // 是否通过校验
    isValidateForm() {
      return this.$refs['form'].validate()
    },
    // 获取表单数据
    getFormData() {
      return this.formData
    }
  }
}
</script>

<style>

</style>
