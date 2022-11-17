<!-- eslint-disable vue/multi-word-component-names -->
<template>
    <div class="container">
        <div class="data-section">
            <div class="opera-section">
              <el-upload
                ref="refUpload"
                action="/upload"
                multiple
                :before-remove="beforeRemove"
                :on-change="
                  (file, fileList) => fileChange(file, fileList)
                "
                :file-list="fileList"
                :auto-upload="false"
                >
                <el-button size="small" type="primary">选取数据</el-button>
              </el-upload>
              <el-button
                style="margin-left: 10px;"
                type="primary"
                @click="upload()"
                >上传</el-button>
                <el-dropdown class="upload-drop" trigger="hover">
                <el-button
                  type="primary"
                  icon="el-icon-upload2"
                  id="uploadFileId"
                  >上传<i class="el-icon-arrow-down el-icon--right"></i
                ></el-button>
                  <el-dropdown-menu >
                  <el-dropdown-item @click.native="handleUploadFileBtnClick(1)"
                    >上传文件</el-dropdown-item
                  >
                  <el-dropdown-item @click.native="handleUploadFileBtnClick(2)"
                    >上传文件夹</el-dropdown-item
                  >
                  <!-- <el-dropdown-item
                    @click="handleUploadFileBtnClick(3)"
                    title="截图粘贴或拖拽上传"
                    >拖拽上传</el-dropdown-item
                  > -->
                  </el-dropdown-menu>
              </el-dropdown>
            </div>
            <div class="table-section">
                <el-table
                    :data="tableData"
                    stripe
                    height="100%"
                    border
                >
                    <el-table-column
                        v-for="(column, index) in columns"
                        :key="index"
                        :prop="column.prop"
                        :label="column.label"
                        :width="column.width"
                        :align="column.align"
                    >
                        <template #default="scope">
                        <template v-if="column.useSlot">
                            <template v-if="column.prop === 'opera'">
                            <el-button
                                type="success"
                                @click="onHandleStart(scope.row)"
                            >下载</el-button>
                            <!-- <el-button
                                type="primary"
                                @click="onHandlePause(scope.row)"
                            >暂停</el-button> -->
                            <el-button
                                type="danger"
                                @click="onHandleDelete(scope.row)"
                            >删除</el-button>
                            </template>
                            <template v-else>
                              {{ 'asd' }}
                            </template>
                        </template>
                        <template v-else>
                            {{ scope.row[column.prop] }}
                        </template>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </div>

    </div>
</template>

<script>
import * as API from '@/api/download'
import axios from 'axios'
import { downloadResponseUtil } from '@/utils'

export default {
  name: 'download',
  data () {
    return {
      // 文件列表
      fileList: [],
      // 表格数据
      tableData: [],
      // 表格列
      columns: [
        {
          label: '文件名称',
          prop: 'fileName',
          align: 'center'
        },
        {
          label: '文件大小',
          prop: 'fileSize',
          align: 'center'
        },
        {
          label: '操作',
          prop: 'opera',
          align: 'center',
          useSlot: true
        }
      ]
    }
  },
  created () {
    this.getTableList()
  },
  computed: {

  },
  mounted: function () {
    this.$root.Bus.$on('getTableList', () => {
      console.log('getTableList')
        this.getTableList()
    })
  },
  methods: {
    onHandleStart (data) {
      // axios({
      //   method: 'get',
      //   url: process.env.VUE_APP_BASE_API + '/download/' + data.fileId,
      //   responseType: 'blob', // 二进制格式
      // }).then((res) => {
      //   downloadResponseUtil(res)
      // })
      window.open(process.env.VUE_APP_NT_API + '/download/' + data.fileId)
    },
    onHandlePause (data) {
      //
    },
    onHandleDelete (data) {
      this.$confirm(`确定删除文件： ${data.fileName}?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const params = {
          identifiers: [data.md5].toString()
        }
        API.deleteFile(params)
          .then((res) => {
            const { code, data, msg } = res
            if (String(code) === '200') {
              this.getTableList()
            } else {
              this.$message.error(msg)
            }
        })
      })
    },
    beforeRemove(file) {
      this.isProgress = false
      return this.$confirm(`确定移除 ${file.name}？`)
    },
    // 选取文件
    fileChange(file, fileList) {
      const arr = []
      for (const i in fileList) {
        arr.push(fileList[i].raw)
      }
      this.fileList = arr
    },
    upload() {
      if (this.fileList.length <= 0) {
        return this.$message.warning('请选择上传的文件')
      }
      var formData = new FormData()
      formData.append('bucket', 'other')
      for (const i in this.fileList) {
        formData.append('multipartFiles', this.fileList[i])
      }
      API.upload(formData).then((res) => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          this.$message.success(msg)
          this.$refs.refUpload.clearFiles()
          this.fileList = []
        } else {
          this.$message.error(msg)
        }
      })
    },
    getTableList () {
      API.getfileList().then((res) => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          this.tableData = data
        } else {
          this.$message.error(msg)
        }
      })
    },
    /**
		 * 上传文件按钮点击事件
		 * @description 通过Bus通信，开启全局上传文件流程
		 * @param uploadType 上传方式 0-文件上传 1-文件夹上传 2-粘贴图片或拖拽上传
		 */
		handleUploadFileBtnClick(uploadType) {
      console.log('handleUploadFileBtnClick')
      this.$root.Bus.$emit('openUploader', uploadType)
		},
  }
}
</script>

<style lang="scss" scoped>
.button-section{
  margin: 5px;
  text-align: right;
}
.container {
  position: relative;
  width: 100%;
  height: 100%;
  padding: 20px;
  overflow-y: auto;
  .data-section {
    width: 100%;
    height: 720px;
    margin-right: 100px;
    padding: 20px 20px 20px 20px;
    background: #ffffff;
    box-shadow: 0px 1px 6px 0px rgba(0, 0, 0, 0.06);
    border-radius: 4px;
    .opera-section {
      display: flex;
      float: left;
      justify-content: space-between;
      .search-section {
        display: flex;
        .other-search-condition {
          display: flex;
          flex-wrap: wrap;
          .search-item {
            display: flex;
            align-items: baseline;
            margin-right: 20px;
            .label {
              font-size: 14px;
              font-weight: normal;
              display: inline-block;
              word-break: keep-all;
            }
          }
        }
      }
    }
    .table-section {
      height: 620px;
      margin-right: 100px;
      padding: auto;
    }
    .pager-section {
      margin-top: 10px;
      text-align: right;
    }
  }
  .map-section {
    width: 100%;
    height: 600px;
    margin-top: 10px;
    display: flex;
    .map-container {
      flex: 1;
      background: #ffffff;
      border-radius: 4px;
      .map-title {
        height: 30px;
        text-align: center;
        margin: 10px;
        font-size: 18px;
      }
      .picture-section {
        width: 100%;
        height: calc(100% - 100px);
      }
      .map-info {
        max-height: 30px;
        line-height: 24px;
        padding: 10px;
      }
    }
    .map-info-section {
      width: 400px;
      background: #ffffff;
      border: 1px #dfdfdf;
      margin-left: 10px;
      border-radius: 4px;
      .map-info-item {
        padding: 10px;
        font-size: 14px;
      }
    }
  }
}
</style>
