<template>
  <div class="introduce-container ck-content clearfix" v-html="tabData" />
</template>

<script>
export default {
  props: {
    tabData: {
      type: String,
      default: ''
    }
  }
}
</script>

<style lang="scss" scoped>
.introduce-container{
  padding: 10px;
  line-height: 30px;
  font-size: 16px;
  p{
    text-indent: 20px;
  }
}
</style>
