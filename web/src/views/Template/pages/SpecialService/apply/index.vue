<template>
  <el-upload
    action="/dev-api/content/upload"
    name="multipartFile"
    :headers="headers"
    :on-remove="handleRemove"
    :before-remove="beforeRemove"
    :data="uploadParam"
    :on-exceed="handleExceed"
    :on-success="handleSuccess"
    :file-list="fileList"
    :limit="1"
  >
    <el-button size="small" type="primary">点击上传文件</el-button>
  </el-upload>
</template>

<script>
import { getToken } from '@/utils/auth'
export default {
  props: {
    dialogVisible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      // 上传请求头部信息
      headers: {
        Authorization: 'Bearer ' + getToken()
      },
      // 上传文件列表
      fileList: [],
      // 上传参数
      uploadParam: {
        fileType: 5
      },
      // 上传后的文件ID
      dataId: ''
    }
  },
  watch: {
    dialogVisible: {
      handler() {
        this.fileList = []
      },
      immediate: true
    }
  },
  methods: {
    // 附件上传成功
    handleSuccess(res, file) {
      this.dataId = res.msg
      this.fileList.push(file)
    },
    handleRemove(file, fileList) {
      this.fileList = []
      this.dataId = ''
    },
    handleExceed(files, fileList) {
      this.$message.warning('只能选择一个文件！')
    },
    beforeRemove(file, fileList) {
      return this.$confirm(`确定移除 ${file.name}？`)
    },
    // 获取数据--父组件调用
    getFormData() {
      return {
        dataId: this.dataId
      }
    }
  }
}
</script>

<style>

</style>
