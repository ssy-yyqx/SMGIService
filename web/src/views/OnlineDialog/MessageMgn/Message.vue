<template>
  <div class="message-container">
    <div class="header">
      <div class="header-left">
        <div class="avatar">
          <svg-icon icon-class="intelligenceService" class-name="icon-size" />
        </div>
        <div class="name">留言</div>
      </div>
      <div class="header-right">
        <i class="icon el-icon-close" @click.stop="closeDialog" />
      </div>
    </div>
    <div class="history-message" @click="openHistoryDialog">我的历史留言（{{ messageNumber }}）</div>
    <div class="message-section">
      <div class="input-section">
        <el-input v-model="message" type="textarea" placeholder="如果客服不在线，请在此留言，我们会及时联系您。" rows="6" />
      </div>
      <div class="button-section">
        <el-button @click="send">发送</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import * as API from '@/api/dashboard'
export default {
  data() {
    return {
      // 历史留言数目
      messageNumber: 0,
      message: ''
    }
  },
  created() {
    this.getHistoryMessageNumber()
  },
  methods: {
    // 获取历史消息数
    getHistoryMessageNumber() {
      API.getHistoryMessageNumber().then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          this.messageNumber = data
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 关闭消息弹窗
    closeDialog() {
      this.$emit('closeDialog')
    },
    // 打开历史留言弹窗
    openHistoryDialog() {
      this.$emit('setHistoryDialog', true)
    },
    // 发送留言
    send() {
      const params = {
        questionContent: this.message
      }
      API.addMessage(params).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.getHistoryMessageNumber()
          this.message = ''
        } else {
          this.$message.error(msg)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.message-container{
  position: absolute;
  left: -360px;
  bottom: 0px;
  width: 359px;
  height: 250px;
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
  .history-message{
    height: 20px;
    line-height: 20px;
    text-decoration: underline;
    padding: 20px 10px;
    &:hover{
      color: blue;
    }
  }
  .message-section{
    position: absolute;
    width: 100%;
    bottom: 0px;
    display: flex;
    align-items: center;
    border: 1px solid #DFDFDF;
    ::v-deep .el-textarea__inner{
      border: none;
    }
    .input-section{
      width: 290px;
    }
    .button-section{
      margin-left: 5px;
    }
  }
}
.icon-size{
  width: 38px !important;
  height: 38px !important;
  color: #A9A9A9;
}
</style>
