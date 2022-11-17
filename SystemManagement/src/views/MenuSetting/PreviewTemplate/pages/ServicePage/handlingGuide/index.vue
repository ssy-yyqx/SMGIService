<template>
  <div class="guide-section">
    <span class="download-item" @click="download">{{ pageData.fileName }}</span>
  </div>
</template>

<script>
export default {
  props: {
    pageData: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  methods: {
    // 下载
    download() {
      // 创建URL
      const a = document.createElement('a')
      a.download = this.pageData.fileName
      a.href = '/dev-api/file/download/' + this.pageData.id
      document.body.appendChild(a)
      // 下载文件
      a.click()
      document.body.removeChild(a)
    }
  }
}
</script>

<style lang="scss" scoped>
.guide-section{
  padding: 20px;
  .download-item{
    color: #717171;
    cursor: pointer;
    &:hover{
      color: blue;
    }
  }
}
</style>
