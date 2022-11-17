<template>
  <div class="cases-container">
    <div v-for="(item, index) in tabData" :key="index" class="case-item">
      <div class="case-item__header">
        <i />{{ item.titleName }}
      </div>
      <div class="case-item__content">
        {{ item.infoContent }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    tabData: {
      type: Array,
      default: () => {
        return []
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.cases-container{
  padding: 10px;
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
      padding: 20px;
    }
  }
}
</style>
