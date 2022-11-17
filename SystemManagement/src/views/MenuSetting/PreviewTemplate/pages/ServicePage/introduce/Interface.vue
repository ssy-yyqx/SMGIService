<template>
  <div class="interface-container ck-content clearfix" v-html="tabData" />
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
.interface-container{
  padding: 10px;
  line-height: 30px;
  font-size: 16px;
}
</style>
