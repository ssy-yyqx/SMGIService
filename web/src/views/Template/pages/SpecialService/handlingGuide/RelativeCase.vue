<template>
  <div class="case-section">
    <div v-for="(item, index) in cases" :key="index" class="case-item">
      <div class="case-item__header">
        <i />{{ item.title }}
      </div>
      <div class="case-item__content">
        {{ item.content }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      cases: [
        {
          title: ' 案例一',
          content: ''
        },
        {
          title: ' 案例二',
          content: ''
        }
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
.case-section{
  .case-item{
    background: #FFFFFF;
    border: 1px solid #EAECEF;
    margin-bottom: 20px;
    &__header{
      height: 53px;
      background: #FAFAFA;
      font-size: 16px;
      font-weight: bold;
      color: #313131;
      padding: 18px 20px;
      i{
        width: 4px;
        height: 0px;
        border-top: 9px solid #0454CF;
        border-bottom: 9px solid #DD180F;
        display: inline-block;
        margin-bottom: -3px;
        border-radius: 2px;
        margin-right: 5px;
      }
    }
    &__content{
      height: 200px;
      overflow-y: auto;
    }
  }
}

</style>
