<template>
  <Paper :title="title">
    <template slot="content">
      <div class="detail-section">
        <div class="detail-container">
          <div class="title-section">
            {{ data.title }}
          </div>
          <div class="time-section">
            {{ data.createTime | formatTime }}
          </div>
          <div class="content-section ck-content clearfix" v-html="data.content" />
          <div v-if="data.fileData" class="download-section">
            <div v-if="data.fileData.fileName">
              <span>相关文件：</span>
              <span class="download-item" @click="download">{{ data.fileData.fileName }}</span></div>
          </div>
        </div>
      </div>
    </template>
  </Paper>
</template>

<script>
import * as API from '@/api/content.js'
export default {
  data() {
    return {
      data: {
        title: '标题'
      },
      title: '详情'
    }
  },
  created() {
    const { id } = this.$route.query
    this.getDetailInfo(id)
  },
  methods: {
    // 获取详细信息
    getDetailInfo(id) {
      API.getInfoDetail({ id }).then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          this.data = data
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 下载
    download() {
      // 创建URL
      const a = document.createElement('a')
      a.download = this.data.fileData.fileName
      a.href = '/dev-api/file/download/' + this.data.fileData.id
      document.body.appendChild(a)
      // 下载文件
      a.click()
      document.body.removeChild(a)
    }
  }
}
</script>

<style lang="scss" scoped>
.detail-section{
  width: 100%;
  height: 100%;
  background: #F9F9F9;
  .detail-container{
    padding: 10px;
    background: #FFFFFF;
    min-height: 100%;
    .title-section{
      text-align: center;
      color: #313131;
      font-size: 22px;
      padding: 30px 20px;
    }
    .time-section{
      text-align: center;
    }
    .content-section{
      font-size: 16px;
    }
    .download-section{
      padding: 20px 0;
      .download-item{
        color: #717171;
        cursor: pointer;
        &:hover{
          color: blue;
        }
      }
    }
  }
}

</style>
<style>
.ck-content .image img {
    display: block;
    margin: 0 auto;
    max-width: 100%;
    min-width: 100%;
}
</style>
