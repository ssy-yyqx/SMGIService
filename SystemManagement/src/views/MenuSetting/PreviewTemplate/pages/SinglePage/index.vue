<template>
  <div class="single-page-content">
    <div class="title">
      {{ contentData.title }}
    </div>
    <div class="content ck-content clearfix" v-html="contentData.content" />
  </div>
</template>

<script>
export default {
  props: {
    contentData: {
      type: Object,
      default: () => {
        return {
          title: '标题',
          content: '内容'
        }
      }
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
