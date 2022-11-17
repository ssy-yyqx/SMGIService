<template>
  <div class="single-page-container">
    <el-form ref="form" style="width: 880px" :model="formData" :rules="rules" label-width="80px">
      <el-form-item label="标题" prop="title">
        <el-input v-model="formData.title" />
      </el-form-item>
      <el-form-item label="图片">
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
      <el-form-item label="内容" prop="content">
        <div id="editor1" style="height: 300px;" />
      </el-form-item>
      <el-form-item>
        <div class="button-section">
          <el-button @click="onHandleReset">重置</el-button>
          <el-button type="primary" @click="onHandlePreview">预览</el-button>
          <el-button v-if="!submitLoading" type="primary" @click="onHandleSubmit">提交</el-button>
          <el-button v-else type="primary" :loading="true">提 交</el-button>
        </div>
      </el-form-item>
    </el-form>
    <el-dialog
      title="预览"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      width="1400"
    >
      <SinglePage :content-data="formData" />
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'
import * as API from '@/api/ContentMgn'
import SinglePage from '../../PreviewTemplate/pages/SinglePage'
export default {
  components: { SinglePage },
  props: {
    id: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      // 是否显示预览弹窗
      dialogVisible: false,
      editor: null,
      // 上传请求头部信息
      headers: {
        Authorization: 'Bearer ' + getToken()
      },
      formData: {
        title: '',
        content: '',
        imageUrl: ''
      },
      rules: {
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入内容', trigger: 'blur' }
        ]
      },
      // 页面类型 新增/修改
      type: 'add',
      // 提交loading
      submitLoading: false
    }
  },
  mounted() {
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
    this.getContentData().then(() => {
      ClassicEditor
        .create(document.querySelector('#editor1'), defaultConfig).then(editor => {
          this.editor = editor
        })
        .catch(error => {
          console.error(error)
        })
    })
  },
  methods: {
    // 获取内容
    getContentData() {
      return new Promise(resolve => {
        API.getCenterPageInfo({ id: this.id }).then(res => {
          const { code, data, msg } = res
          if (String(code) === '200') {
            if (data && data.content) {
              this.formData = data.content
              this.type = 'edit'
              // 回显数据
              document.getElementById('editor1').innerHTML = this.formData.content
            } else {
              this.type = 'add'
            }
            resolve()
          } else {
            this.$message.error(msg)
          }
        })
      })
    },
    // 重置
    onHandleReset() {
      this.getContentData().then(() => {
        this.editor.setData(this.formData.content)
      })
    },
    // 提交
    onHandleSubmit() {
      this.formData.content = this.editor.getData()
      this.$refs.form.validate(valid => {
        if (valid) {
          this.submitLoading = true
          // 新增
          if (this.type === 'add') {
            const params = Object.assign({ moduleId: this.id }, this.formData)
            API.addCenterPageInfo(params).then(res => {
              const { code, msg } = res
              if (String(code) === '200') {
                this.$message.success('新增成功！')
              } else {
                this.$message.error(msg)
              }
              this.submitLoading = false
            })
          } else { // 修改
            const params = {
              contentId: this.formData.id,
              title: this.formData.title,
              content: this.formData.content,
              imageUrl: this.formData.imageUrl
            }
            API.editCenterPageInfo(params).then(res => {
              const { code, msg } = res
              if (String(code) === '200') {
                this.$message.success('修改成功！')
              } else {
                this.$message.error(msg)
              }
              this.submitLoading = false
            })
          }
        }
      })
    },
    // 预览
    onHandlePreview() {
      this.formData.content = this.editor.getData()
      this.dialogVisible = true
    },
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
    }
  }
}
</script>
<style lang="scss" scoped>
.single-page-container{
  width: 100%;
  overflow-y: auto;
  height: calc(100vh - 220px);
}
</style>
<style>
.ck-editor__editable_inline {
  height: 400px;
  width: 800px;
}
</style>
