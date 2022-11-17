<template>
  <el-form ref="form" :model="formData" label-width="80px">
    <el-form-item label="标题" prop="title">
      <el-input v-model="formData.title" />
    </el-form-item>
    <el-form-item label="封面图片">
      <el-upload
        class="avatar-uploader"
        action="/dev-api/content/upload/pic"
        name="multipartFile"
        :headers="headers"
        :show-file-list="false"
        :on-success="handleAvatarSuccess"
        :before-upload="beforeAvatarUpload"
      >
        <img v-if="formData.imageUrl" :src="formData.imageUrl" class="avatar">
        <i v-else class="el-icon-plus avatar-uploader-icon" />
      </el-upload>
    </el-form-item>
    <el-form-item label="文件上传">
      <el-upload
        action="/dev-api/content/upload"
        name="multipartFile"
        :headers="headers"
        :on-preview="handlePreview"
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
    </el-form-item>
    <el-form-item label="内容">
      <div id="editor" />
    </el-form-item>
  </el-form>
</template>

<script>
import { getToken } from '@/utils/auth'
export default {
  props: {
    moduleId: {
      type: String,
      default: ''
    },
    formData: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data() {
    return {
      // 表单数据
      // 上传文件列表
      fileList: [],
      // 上传请求头部信息
      headers: {
        Authorization: 'Bearer ' + getToken()
      },
      editor: null,
      uploadParam: {
        fileType: 3
      }
    }
  },
  created() {
    if (this.formData.fileData) {
      this.fileList = [
        Object.assign({ name: this.formData.fileData.fileName }, this.formData.fileData)
      ]
    } else {
      this.fileList = []
    }
  },
  mounted() {
    document.getElementById('editor').innerHTML = this.formData.content
    const defaultConfig = {
      toolbar: ['heading', '|', 'bold', 'italic', 'blockQuote', 'bulletedList', 'numberedList', 'link', 'imageUpload'],
      image: {
        toolbar: [
          'imageTextAlternative',
          'imageStyle:alignLeft',
          'imageStyle:alignCenter',
          'imageStyle:alignRight'
        ]
      },
      language: 'zh-cn',
      ckfinder: {
        uploadUrl: '/dev-api/file/upload/pic'
      }
    }
    ClassicEditor
      .create(document.querySelector('#editor'), defaultConfig).then(editor => {
        this.editor = editor
        console.log(Array.from(this.editor.ui.componentFactory.names()))
      })
      .catch(error => {
        console.error(error)
      })
  },
  methods: {
    // 封面上传成功
    handleAvatarSuccess(res, file) {
      this.formData.imageUrl = res.msg
    },
    beforeAvatarUpload(file) {
      const fileTypes = ['image/png', 'image/jpg', 'image/jpeg']
      const isJPG = fileTypes.find(item => item === file.type)
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传图片只能是JPG格式或者PNG格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    },
    // 附件上传成功
    handleSuccess(res, file) {
      this.formData.dataId = res.msg
      this.fileList.push(file)
      this.formData.fileData = {
        fileName: file.name,
        id: res.msg
      }
    },
    handleRemove(file, fileList) {
      this.fileList = []
      this.formData.fileData = this.fileList
      this.dataId = ''
    },
    handlePreview(file) {
      console.log(file)
    },
    handleExceed(files, fileList) {
      this.$message.warning('只能选择一个文件！')
    },
    beforeRemove(file, fileList) {
      return this.$confirm(`确定移除 ${file.name}？`)
    },
    // 获取表单数据
    getFormData() {
      return Object.assign({}, this.formData, {
        moduleId: this.moduleId,
        content: this.editor.getData()
      })
    }
  }
}
</script>

<style>
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>
<style>
.ck-editor__editable_inline {
  height: 400px;
  width: 600px;
}
</style>
