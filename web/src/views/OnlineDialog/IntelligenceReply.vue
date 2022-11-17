<template>
  <div class="intelligence-container">
    <div class="header">
      <div class="header-left">
        <div class="avatar">
          <svg-icon icon-class="intelligenceService" class-name="icon-size" />
        </div>
        <div class="name">智能回复</div>
      </div>
      <div class="header-right">
        <i class="icon el-icon-close" @click.stop="closeDialog" />
      </div>
    </div>
    <div class="question-section">
      <template v-if="isShowReply">
        <!-- 答复 -->
        <div class="reply-section">
          <div class="return-btn" @click="returnToQuestion"><<返回问题列表</div>
          <div class="reply-content">
            {{ currentQuestion.reply }}
          </div>
        </div>
      </template>
      <template v-else>
        <!-- 问题列表 -->
        <div v-for="(item, index) in questionList" :key="index" class="question-item" @click="gotoReply(item)">
          {{ item.question }}
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import * as API from '@/api/dashboard'
export default {
  data() {
    return {
      // 问题列表
      questionList: [],
      // 当前问题
      currentQuestion: {},
      // 是否显示答复
      isShowReply: false
    }
  },
  created() {
    this.getQuestionList()
  },
  methods: {
    // 获取问题列表
    getQuestionList() {
      API.getQuestionList({ pageNumber: 1, pageSize: 100 }).then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          this.questionList = data.list
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 跳转到回复页面
    gotoReply(data) {
      this.isShowReply = true
      this.currentQuestion = data
    },
    // 返回问题列表
    returnToQuestion() {
      this.isShowReply = false
    },
    // 关闭弹窗
    closeDialog() {
      this.$emit('closeDialog')
    }
  }
}
</script>

<style lang="scss" scoped>
.intelligence-container{
  position: absolute;
  left: -360px;
  bottom: 0px;
  width: 359px;
  height: 540px;
  box-shadow: 0px 4px 13px 0px rgba(0, 0, 0, 0.24);
  text-align: left;
  background: #FFFFFF;
  color: #535353;
  .header{
    height: 60px;
    padding: 10px;
    background: #0454CF;
    display: flex;
    justify-content: space-between;
    align-items: center;
    &-left {
      display: flex;
      align-items: center;
      .avatar{
        width: 40px;
        height: 40px;
        border-radius: 20px;
        margin-right: 10px;
      }
      .name{
        display: inline-block;
        color: #FFFFFF;
      }
    }
    &-right{
      .icon{
        font-size: 16px;
        color: #FFFFFF;
      }
    }
  }
  .question-section{
    height: calc(100% - 167px);
    padding: 20px;
    overflow-y: auto;
    .question-item{
      height: 30px;
      line-height: 30px;
      text-decoration: underline;
    }
    .return-btn{
      padding: 20px 0;
      color: blue;
    }
  }
}
.icon-size{
  width: 38px !important;
  height: 38px !important;
  color: #A9A9A9;
}
</style>
