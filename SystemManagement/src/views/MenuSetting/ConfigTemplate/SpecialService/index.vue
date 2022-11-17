<template>
  <div class="service-page-container">
    <div class="service-page-item">
      <div class="label">办理指南：</div>
      <div class="right-content">
        <el-upload
          action="/dev-api/content/uploadGuide"
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
          <el-button size="small" type="primary">点击上传</el-button>
        </el-upload>
      </div>
    </div>
    <div class="service-page-item">
      <div class="label">办理类型：</div>
      <div class="right-content">
        <el-radio-group v-model="handleType">
          <el-radio :label="1">在线办理</el-radio>
          <el-radio :label="2">预约线下</el-radio>
        </el-radio-group>
      </div>
    </div>
    <div class="button-section">
      <el-button type="default" @click="onHandleReset">重置</el-button>
      <el-button type="primary" :loading="isSaving" :disabled="isSaving" @click="onHandleSubmit">提交</el-button>
    </div>
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'
import * as API from '@/api/ContentMgn'
export default {
  props: {
    id: {
      type: String,
      default: ''
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
      // 文件上传参数
      uploadParam: {
        fileType: 3
      },
      // 办理指南文件地址
      guideFileId: '',
      // 办理类型
      handleType: 1,
      // 是否正在保存
      isSaving: false
    }
  },
  created() {
    // 获取页面数据
    this.getPageData()
  },
  methods: {
    // 获取页面数据
    getPageData() {
      API.getSpecialServicePageData(this.id).then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          if (data) {
            this.type = 'edit'
            // 设置表单数据
            this.setFormData(data)
          } else {
            this.type = 'add'
          }
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 设置表单数据
    setFormData(data) {
      // 办理指南文件
      this.guideFileId = data.guideFileId
      if (data.guideFileData) {
        this.fileList = [
          Object.assign({ name: data.guideFileData.fileName }, data.guideFileData)
        ]
      } else {
        this.fileList = []
      }
      // 办理类型
      this.handleType = data.handleType
    },
    // 附件上传成功
    handleSuccess(res, file) {
      this.guideFileId = res.msg
      this.fileList.push(file)
    },
    // 附件删除
    handleRemove(file, fileList) {
      this.fileList = []
      this.guideFileId = ''
    },
    // 文件上传个数限制
    handleExceed(files, fileList) {
      this.$message.warning('只能选择一个文件！')
    },
    // 文件移除确认
    beforeRemove(file, fileList) {
      return this.$confirm(`确定移除 ${file.name}？`)
    },
    // 重置
    onHandleReset() {

    },
    // 提交
    onHandleSubmit() {
      this.isSaving = true
      const params = {
        moduleId: this.id,
        guideFileId: this.guideFileId,
        handleType: this.handleType,
        content: ''
      }
      if (this.type === 'add') {
        API.addSpecialServicePageData(params).then(res => {
          const { code, msg } = res
          if (String(code) === '200') {
            this.$message.success('新增成功')
          } else {
            this.$message.error(msg)
          }
          this.isSaving = false
        })
      } else {
        params.contentId = this.contentId
        API.editSpecialServicePageData(params).then(res => {
          const { code, msg } = res
          if (String(code) === '200') {
            this.$message.success('修改成功')
          } else {
            this.$message.error(msg)
          }
          this.isSaving = false
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.service-page-container{
  height: calc(100vh - 128px);
  overflow-y: auto;
  padding: 20px;
  .service-page-item{
    width: 900px;
    display: flex;
    padding: 10px 0;
    .label{
      font-size: 14px;
      font-weight: bold;
      width: 100px;
    }
  }
  .button-section{
    width: 900px;
    padding: 30px 20px;
  }
}
</style>
