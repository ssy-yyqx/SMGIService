<template>
  <div class="problem-section">
    <div v-for="(item, index) in problems" :key="index" class="problem-item">
      <div class="problem-item__header">
        <div class="icon">{{ index+1 }}</div>
        <div class="text">{{ item.question }}</div>
      </div>
      <div class="problem-item__content">
        {{ item.answer }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      problems: [
        {
          question: '可以在线直接下载数据吗？',
          answer: '根据数据分类及密级保护规定，部分数据支持在线下载，结合您具体的业务需求哦。线上办理点击“在线办理”，现在根据具体业务流程您准备好材料，走线下流程。如有不清楚的，您可咨询人工服务：123456。'
        },
        {
          question: '请问详细的业务办理时间是？',
          answer: '线上业务支持24小时办理；线下法定工作日8:00-12:00、13:00-18:00。'
        }
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
.problem-section{
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
