<template>
  <div class="service-page-container">
    <div class="service-page-item">
      <div class="label">页签：</div>
      <el-checkbox-group v-model="checkList">
        <el-checkbox :label="1">服务介绍</el-checkbox>
        <el-checkbox :label="2">接口说明</el-checkbox>
        <el-checkbox :label="3">常见问题</el-checkbox>
        <el-checkbox :label="4">使用案例</el-checkbox>
      </el-checkbox-group>
    </div>
    <div class="service-page-item">
      <div class="label">图片：</div>
      <div class="right-content">
        <el-upload
          class="avatar-uploader"
          action="/dev-api/content/upload/pic"
          name="multipartFile"
          :headers="headers"
          :show-file-list="false"
          :on-success="handlePictureSuccess"
          :before-upload="beforePictureUpload"
        >
          <img v-if="imageUrl" :src="imageUrl" class="avatar">
          <i v-else class="el-icon-plus avatar-uploader-icon" />
        </el-upload>
      </div>
    </div>
    <div v-show="hasServiceIntrocude" class="service-page-item">
      <div class="label">服务介绍：</div>
      <div id="ServiceContent" class="right-content" style="height: 300px;width: 500px" />
    </div>
    <div v-show="hasInterfaceIntroduce" class="service-page-item">
      <div class="label">接口说明：</div>
      <div id="InterfaceIntroduce" class="right-content" style="height: 300px;width: 500px" />
    </div>
    <div v-show="hasQuestion" class="service-page-item">
      <div class="label">常见问题：</div>
      <div class="right-content">
        <div v-for="(item, index) in questionList" :key="'question'+index" class="question-item">
          <div class="question-item-row">
            <label for="">问题：</label>
            <el-input v-model="item.titleName" type="textarea" />
          </div>
          <div class="question-item-row">
            <label for="">答案：</label>
            <el-input v-model="item.infoContent" type="textarea" />
          </div>
        </div>
        <div class="add-button" @click="onHandleAddQuestion">
          <i class="el-icon-plus" />
        </div>
      </div>
    </div>
    <div v-show="hasCase" class="service-page-item">
      <div class="label">使用案例：</div>
      <div class="right-content">
        <div v-for="(item, index) in caseList" :key="'case' + index" class="case-item">
          <div class="case-item-row">
            <label for="">标题：</label>
            <el-input v-model="item.titleName" />
          </div>
          <div class="case-item-row">
            <label for="">案例：</label>
            <el-input v-model="item.infoContent" type="textarea" />
          </div>
        </div>
        <div class="add-button" @click="onHandleAddCase">
          <i class="el-icon-plus" />
        </div>
      </div>
    </div>
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
          <el-radio :label="3">无需办理</el-radio>
        </el-radio-group>
      </div>
    </div>
    <div v-if="handleType!==3" class="service-page-item">
      <div class="label">跳转栏目：</div>
      <div class="right-content">
        <el-cascader
          ref="serviceCascader"
          v-model="selectedService"
          :options="serviceListData"
          :props="servieSelectProps"
          @change="handleChangeService"
        />
      </div>
    </div>
    <div class="button-section">
      <el-button type="default" @click="onHandleReset">重置</el-button>
      <el-button type="primary" @click="onHandlePreview">预览</el-button>
      <el-button type="primary" :loading="isSaving" :disabled="isSaving" @click="onHandleSubmit">提交</el-button>
    </div>
    <el-dialog
      title="预览"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      width="1400"
    >
      <ServicePage :content-data="pageParams" />
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'
import * as MenuAPI from '@/api/MenuSetting'
import * as API from '@/api/ContentMgn'
import ServicePage from '../../PreviewTemplate/pages/ServicePage'
export default {
  components: { ServicePage },
  props: {
    id: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      checkList: [1, 2, 3, 4],
      // 上传请求头部信息
      headers: {
        Authorization: 'Bearer ' + getToken()
      },
      // 图片地址
      imageUrl: '',
      // 服务介绍编辑器
      serviceIntroduceEditor: null,
      // 服务介绍内容
      serviceIntrocuce: '',
      // 接口说明编辑器
      interfaceIntroduceEditor: null,
      // 接口说明内容
      interfaceIntroduce: '',
      // 问题列表
      questionList: [
        {
          titleName: '',
          infoContent: ''
        }
      ],
      // 案例列表
      caseList: [
        {
          titleName: '',
          infoContent: ''
        }
      ],
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
      // 特色服务cascader数据
      serviceListData: [],
      // 跳转栏目
      selectedService: '',
      // 跳转栏目对象
      selectedServiceObj: {},
      // 跳转栏目参数配置
      servieSelectProps: {
        children: 'childrenList',
        value: 'id',
        label: 'moduleName',
        leaf: 'leaf',
        emitPath: false
      },
      // 是否正在保存
      isSaving: false,
      // 保存类型
      type: 'add',
      // 编辑时的ID
      contentId: '',
      // 是否显示预览窗口
      dialogVisible: false,
      // 页面参数发
      pageParams: {}
    }
  },
  computed: {
    // 是否有服务介绍
    hasServiceIntrocude() {
      return this.judgeHasPage(1)
    },
    // 是否有接口说明
    hasInterfaceIntroduce() {
      return this.judgeHasPage(2)
    },
    // 是否有常见问题
    hasQuestion() {
      return this.judgeHasPage(3)
    },
    // 是否有案例
    hasCase() {
      return this.judgeHasPage(4)
    }
  },
  created() {
    // 获取跳转栏目列表
    this.getMenuList()
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
    API.getServicePageData(this.id).then(res => {
      const { code, data, msg } = res
      if (String(code) === '200') {
        if (data) {
          this.type = 'edit'
          // 设置表单数据
          this.setFormData(data)
        } else {
          this.type = 'add'
        }
        // 服务介绍编辑器初始化
        document.getElementById('ServiceContent').innerHTML = this.serviceIntrocuce
        const config = Object.assign({}, defaultConfig, { data: this.serviceIntrocuce })
        ClassicEditor
          .create(document.querySelector('#ServiceContent'), config).then(editor => {
            this.serviceIntroduceEditor = editor
          })
          // 接口说明编辑器初始化
        document.getElementById('InterfaceIntroduce').innerHTML = this.interfaceIntroduce
        ClassicEditor
          .create(document.querySelector('#InterfaceIntroduce'), defaultConfig).then(editor => {
            this.interfaceIntroduceEditor = editor
          })
      } else {
        this.$message.error(msg)
      }
    })
  },
  methods: {
    // 设置表单数据
    setFormData(data) {
      // 图片
      this.imageUrl = data.imageUrl
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
      // 跳转栏目
      this.selectedService = data.redirectModuleId
      this.selectedServiceObj = {
        id: data.redirectModuleId,
        router: data.redirectUrl
      }
      // tab和tab里的数据拆分
      const checkList = []
      data.tabList.forEach(item => {
        checkList.push(item.contentType)
        // 服务简介
        if (item.contentType === 1) {
          this.serviceIntrocuce = item.content
        } else if (item.contentType === 2) {
          // 接口说明
          this.interfaceIntroduce = item.content
        } else if (item.contentType === 3) {
          // 常见问题
          this.questionList = item.tabInfoList
        } else if (item.contentType === 4) {
          // 使用案例
          this.caseList = item.tabInfoList
        }
      })
      this.checkList = checkList
      // 编辑ID
      this.contentId = data.id
    },
    judgeHasPage(value) {
      return this.checkList.find(item => item === value)
    },
    // 封面上传成功
    handlePictureSuccess(res, file) {
      this.imageUrl = res.msg
    },
    // 图片上传成功
    beforePictureUpload(file) {
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
    // 添加问题
    onHandleAddQuestion() {
      this.questionList.push({
        titleName: '',
        infoContent: ''
      })
    },
    // 添加案例
    onHandleAddCase() {
      this.caseList.push({
        titleName: '',
        infoContent: ''
      })
    },
    // 附件上传成功
    handleSuccess(res, file) {
      this.guideFileId = res.msg
      this.fileList.push(file)
      // this.formData.fileData = {
      //   fileName: file.name,
      //   id: res.msg
      // }
    },
    // 附件删除
    handleRemove(file, fileList) {
      this.fileList = []
      // this.formData.fileData = this.fileList
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
    // 深度递归栏目数据
    deepSetData(data) {
      data.forEach(item => {
        item.leaf = String(item.moduleType) === '2'
        if (item.leaf) {
          delete item.childrenList
        }
        if (item.childrenList) {
          this.deepSetData(item.childrenList)
        }
      })
    },
    // 获取栏目数据
    getMenuList() {
      MenuAPI.getTableList().then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          // 临时代码
          this.serviceListData = data.find(item => item.id === 'a87b0a9239944845979c0c450b5d6c5d').childrenList
          this.deepSetData(this.serviceListData)
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 切换跳转栏目
    handleChangeService() {
      const node = this.$refs.serviceCascader.getCheckedNodes()
      this.selectedServiceObj = node[0].data
    },
    // 重置
    onHandleReset() {

    },
    // 获取tab数据
    getIntroduceTabsData() {
      const tabContent = []
      this.checkList.sort()
      this.checkList.forEach(item => {
        // 服务简介
        if (item === 1) {
          tabContent.push({
            contentType: 1,
            tabType: 1,
            tabName: '服务简介',
            content: this.serviceIntroduceEditor.getData()
          })
        } else if (item === 2) {
          // 接口说明
          tabContent.push({
            contentType: 2,
            tabType: 1,
            tabName: '接口说明',
            content: this.interfaceIntroduceEditor.getData()
          })
        } else if (item === 3) {
          // 常见问题
          tabContent.push({
            contentType: 3,
            tabType: 2,
            tabName: '常见问题',
            tableInfoList: this.questionList
          })
        } else if (item === 4) {
          // 使用案例
          tabContent.push({
            contentType: 4,
            tabType: 2,
            tabName: '使用案例',
            tableInfoList: this.caseList
          })
        }
      })
      return tabContent
    },
    // 提交
    onHandleSubmit() {
      this.isSaving = true
      const params = {
        moduleId: this.id,
        guideFileId: this.guideFileId,
        handleType: this.handleType,
        imageUrl: this.imageUrl,
        redirectModuleId: this.selectedServiceObj.id,
        redirectUrl: this.selectedServiceObj.router,
        tabContent: this.getIntroduceTabsData()
      }
      if (this.type === 'add') {
        API.addServicePageData(params).then(res => {
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
        API.editServicePageData(params).then(res => {
          const { code, msg } = res
          if (String(code) === '200') {
            this.$message.success('修改成功')
          } else {
            this.$message.error(msg)
          }
          this.isSaving = false
        })
      }
    },
    // 预览
    onHandlePreview() {
      this.pageParams = {
        moduleId: this.id,
        guideFileId: this.guideFileId,
        guideFileData: this.fileList[0],
        handleType: this.handleType,
        imageUrl: this.imageUrl,
        redirectModuleId: this.selectedServiceObj.id,
        redirectUrl: this.selectedServiceObj.router,
        tabList: this.getIntroduceTabsData()
      }
      this.dialogVisible = true
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
    .right-content{
      flex: 1;
      .question-item, .case-item{
        border: 1px dashed #dfdfdf;
        border-radius: 5px;
        padding: 10px 20px;
        margin: 10px 0;
        label{
          font-weight: 400;
          width: 60px;
        }
        &-row{
          display: flex;
          padding: 10px 0;
          align-items: center;
        }
      }
      .add-button{
        border: 1px dashed #dfdfdf;
        border-radius: 5px;
        text-align: center;
        cursor: pointer;
        &:hover{
          border-color: #409EFF;
          .el-icon-plus{
            color: #409EFF;
          }
        }
        .el-icon-plus{
          font-size: 40px;
          color: #717171;
        }
      }
    }
  }
  .button-section{
    width: 900px;
    padding: 30px 20px;
    text-align: center;
  }
}
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
  width: 800px;
}
</style>
