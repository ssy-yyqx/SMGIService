<template>
  <div class="question-container">
    <div v-for="(item, index) in tabData" :key="index" class="problem-item">
      <div class="problem-item__header">
        <div class="icon">{{ index+1 }}</div>
        <div class="text">{{ item.titleName }}</div>
      </div>
      <div class="problem-item__content">
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
.question-container{
  padding: 10px;
  .problem-item{
    &__header{
      padding: 10px;
      display: flex;
      align-items: center;
      .icon{
        width: 47px;
        height: 51px;
        line-height: 51px;
        margin-right: 10px;
        text-indent: 20px;
        background: url('../../../../../assets/images/icon-bg.png') no-repeat;
        font-size: 24px;
        font-weight: bold;
        color: #FFFFFF;
      }
      .text{
        font-size: 16px;
        font-weight: bold;
        color: #313131;
      }
    }
    &__content{
      font-size: 14px;
      font-weight: 400;
      color: #999999;
      line-height: 24px;
      padding-left: 67px;
    }
  }
}
</style>
