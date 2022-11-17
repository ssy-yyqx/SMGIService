<template>
  <Paper :title="title">
    <template slot="content">
      <div class="single-page-content">
        <div class="title">
          {{ contentData.title }}
        </div>
        <div class="content ck-content clearfix" v-html="contentData.content" />
      </div>
    </template>
  </Paper>
</template>

<script>
import * as API from '@/api/content'
export default {
  data() {
    return {
      title: '',
      moduleId: '',
      contentData: {
        title: '标题',
        content: '内容'
      }
    }
  },
  watch: {
    $route: {
      handler: function(val) {
        const { title, id } = val.meta
        this.title = title
        this.moduleId = id
      },
      // 深度观察监听
      deep: true,
      immediate: true
    }
  },
  created() {
    this.getContentData()
  },
  methods: {
    // 获取内容数据
    getContentData() {
      API.getCenterPageInfo({ id: this.moduleId }).then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          if (data && data.content) {
            this.contentData = data.content
          }
        } else {
          this.$message.error(msg)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.single-page-content{
  .title{
    text-align: center;
    color: #313131;
    font-size: 22px;
    padding: 30px 20px;
  }
  .content{
    text-align: left;
    font-size: 16px;
    line-height: 28px;
  }
}
</style>
