<template>
  <div class="online-section">
    <div class="online" :class="{'active': activeButton === 'online'}" @click="openServiceDialog">
      <svg-icon icon-class="onlineService" class-name="icon-size" />
      <div class="title">在线客服</div>
      <div v-show="activeButton === 'online'" class="online-dialog">
        <div class="header">
          <div class="header-left">
            <!-- <div class="avatar" /> -->
            <div class="name">{{ name }}</div>
          </div>
          <div class="header-right">
            <i class="icon el-icon-close" @click.stop="closeDialog" />
          </div>
        </div>
        <div id="RecordSection" class="records-section">
          <template v-for="(item, index) in records">
            <div v-if="item.isCenter" :key="index" class="warning">
              {{ item.content }}
              <span v-if="item.isClose" class="reopen-button" click="operaWebsocket">重连</span>
            </div>
            <div v-else :key="index" class="clearfix">
              <div :key="index" :class="item.isMe ? 'right-item' : 'left-item'">
                {{ item.content }}
                <div class="arrow" />
              </div>
            </div>
          </template>
        </div>
        <div class="message-section">
          <div class="input-section">
            <el-input v-model="message" type="textarea" placeholder="请反馈您的问题,我们将尽快回复" rows="5" />
          </div>
          <div class="button-section">
            <el-button @click="send">发送</el-button>
          </div>
        </div>
      </div>
    </div>
    <div class="intelligence" :class="{'active': activeButton === 'intelligence'}" @click="intelligenceReply">
      <svg-icon icon-class="intelligenceService" class-name="icon-size" />
      <div class="title">智能回复</div>
      <IntelligenceReply v-show="activeButton === 'intelligence'" @closeDialog="closeDialog" />
    </div>
    <!-- <el-badge :value="10" class="item" :max="99"> -->
    <div class="messageMgn" :class="{'active': activeButton === 'messageMgn'}" @click="messageMgn">
      <svg-icon icon-class="messageMgn" class-name="icon-size" />
      <div class="title">留言管理</div>
      <Message v-if="activeButton === 'messageMgn'" @setHistoryDialog="setHistoryDialog" @closeDialog="closeDialog" />
      <HistoryMessage v-if="isShowHistoryMessage" @setHistoryDialog="setHistoryDialog" />
    </div>
    <!-- </el-badge> -->
    <div class="totop" :class="{'active': activeButton === 'toTop'}" @click="gotoTop">
      <svg-icon icon-class="toTop" class-name="icon-size" />
      <div class="title">返回顶部</div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { parseTime } from '@/utils/index'
import IntelligenceReply from './IntelligenceReply.vue'
import Message from './MessageMgn/Message.vue'
import HistoryMessage from './MessageMgn/HistoryMessage.vue'
import * as API from '@/api/content'
export default {
  components: { IntelligenceReply, Message, HistoryMessage },
  data() {
    return {
      activeButton: '',
      // 发送的信息
      message: '',
      // 聊天记录
      records: [
        {
          isCenter: true,
          content: '欢迎光临！我单位为用户提供专业、高效的业务咨询。如您有相关疑问，工作日可与客服人员进行在线沟通！',
          time: parseTime(new Date())
        }
      ],
      // websocket: null,
      // 是否显示历史留言
      isShowHistoryMessage: false
    }
  },
  computed: {
    ...mapGetters(['name'])
  },
  watch: {
    name(val, old) {
      if (window.websocket) {
        const sendMessage = {
          oldName: old,
          newName: val,
          messageType: 8
        }
        // 发送数据
        window.websocket.send(JSON.stringify(sendMessage))
      }
    }
  },
  beforeDestroy() {
    if (window.websocket) {
      window.websocket.close()
      window.websocket = null
    }
  },
  methods: {
    // 打开在线客服弹窗
    openServiceDialog() {
      if (!window.websocket) {
        if (this.supportWebsocket) {
          API.hasAlreadyOpen(this.name).then(res => {
            const { code, data } = res
            if (String(code) === '200') {
              if (data) {
                this.$message.warning('您已连接在线客服，请在原窗口进行沟通！')
                this.activeButton = ''
              } else {
                this.operaWebsocket()
              }
            }
          })
        } else {
          this.$message.warning('您的浏览器版本太低！请先升级浏览器！')
        }
      }
      this.activeButton = 'online'
    },
    operaWebsocket() {
      window.websocket = new WebSocket(`ws://192.168.1.40:8080/upgrade/ws/asset/${this.name}@user_chat`)
      // 连接发生错误的回调方法
      window.websocket.onerror = () => {
        this.records.push({
          isCenter: true,
          content: '连接失败',
          time: parseTime(new Date())
        })
        this.$nextTick(() => {
          document.getElementById('RecordSection').scrollTop = document.getElementById('RecordSection').scrollHeight
        })
      }
      // 连接成功建立的回调方法
      window.websocket.onopen = () => {
        this.records.push({
          isCenter: true,
          content: '连接成功',
          time: parseTime(new Date())
        })
        this.$nextTick(() => {
          document.getElementById('RecordSection').scrollTop = document.getElementById('RecordSection').scrollHeight
        })
      }

      // 接收到消息的回调方法
      window.websocket.onmessage = (event) => {
        const received_msg = event.data // 接收到的数据
        const obj = JSON.parse(received_msg) // json数据
        const { messageType, onlineUsers, textMessage } = obj
        if (messageType === 2) { // 下线
          this.records.push({
            isCenter: true,
            content: '客服不在线...',
            time: parseTime(new Date())
          })
        } else if (messageType === 3) { // 在线名单
          this.records.push({
            isCenter: true,
            content: `客服${onlineUsers}为您服务！`,
            time: parseTime(new Date())
          })
        } else if (messageType === 4) { // 普通消息
          this.records.push({
            isCenter: false,
            isMe: false,
            content: textMessage,
            time: parseTime(new Date())
          })
        } else if (messageType === 5) { // 系统消息
          this.records.push({
            isCenter: true,
            content: textMessage,
            time: parseTime(new Date())
          })
        } else if (messageType === 6) { // 关闭websocket
          this.records.push({
            isCenter: true,
            content: '您已掉线了!',
            isClose: true,
            time: parseTime(new Date())
          })
          window.websocket.close()
          window.websocket = null
        } else if (messageType === 7) {
          this.records.push({
            isCenter: true,
            content: textMessage,
            isClose: true,
            time: parseTime(new Date())
          })
          window.websocket.close()
          window.websocket = null
        }
        this.$nextTick(() => {
          document.getElementById('RecordSection').scrollTop = document.getElementById('RecordSection').scrollHeight
        })
      }

      // 连接关闭的回调方法
      window.websocket.onclose = function() {}
    },
    // 是否支持websocket
    supportWebsocket() {
      if ('WebSocket' in window) {
        return true
      }
      return false
    },
    // 发送消息
    send() {
      const message = this.message
      const sendMessage = {
        'message': message,
        'username': this.name,
        'to': 'admin',
        'chat_type': 'user_chat'
      }
      // 发送数据
      window.websocket.send(JSON.stringify(sendMessage))

      this.records.push({
        isCenter: false,
        isMe: true,
        content: message,
        time: parseTime(new Date())
      })
      this.$nextTick(() => {
        document.getElementById('RecordSection').scrollTop = document.getElementById('RecordSection').scrollHeight
      })
      this.message = ''
    },
    // 关闭客服弹窗
    closeDialog() {
      this.isShowDialog = false
      this.activeButton = ''
    },
    // 返回顶部
    gotoTop() {
      this.activeButton = 'topTop'
      document.getElementById('AppWrapper').scrollTop = 0
    },
    // 智能回复
    intelligenceReply() {
      this.activeButton = 'intelligence'
    },
    // 留言管理
    messageMgn() {
      this.activeButton = 'messageMgn'
    },
    // 设置历史留言窗口状态
    setHistoryDialog(flag) {
      this.isShowHistoryMessage = flag
    }
  }
}
</script>

<style lang="scss" scoped>
.online-section{
  position: fixed;
  right: calc((100% - 1560px) / 2);
  bottom: 60px;
  font-size: 12px;
  z-index: 999;
  .online{
    width: 76px;
    height: 80px;
    padding: 10px 0;
    text-align: center;
    cursor: pointer;
    color: #434343;
    position: relative;
    .title{
      padding: 10px;
    }
    .online-dialog{
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
            background: #dfdfdf;
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
      .records-section{
        height: calc(100% - 167px);
        padding: 20px;
        overflow-y: auto;
        .warning{
          background: #FDF7D7;
          padding: 10px;
          line-height: 24px;
          margin: 10px;
          text-align: center;
        }
        .left-item{
          max-width: 240px;
          border: 1px solid #DFDFDF;
          padding: 10px;
          border-radius: 3px;
          margin-top: 20px;
          float: left;
          position: relative;
          .arrow{
            position: absolute;
            left: -6px;
            top: 10px;
            width: 0;
            height: 0;
            border-top: 6px solid transparent;
            border-right: 6px solid #dfdfdf;
            border-bottom: 6px solid transparent;
            &::before{
              content: " ";
              position: absolute;
              width: 0;
              height: 0;
              top: -5px;
              left: 1px;
              border-top: 5px solid transparent;
              border-right: 5px solid #FFFFFF;
              border-bottom: 5px solid transparent;
            }
          }
        }
        .right-item{
          max-width: 240px;
          padding: 10px;
          border-radius: 3px;
          margin-top: 20px;
          float: right;
          background: #9EEA6A;
          position: relative;
          .arrow{
            position: absolute;
            right: -6px;
            top: 10px;
            width: 0;
            height: 0;
            border-top: 6px solid transparent;
            border-left: 6px solid #9EEA6A;
            border-bottom: 6px solid transparent;
          }
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
  }
  .totop, .intelligence, .messageMgn{
    width: 70px;
    height: 80px;
    padding: 10px 0;
    text-align: center;
    cursor: pointer;
    color: #434343;
    position: relative;
    .title{
      padding: 10px;
    }
  }
  .active{
    background: #0454CF;
    color: #FFFFFF;
  }
}
</style>
<style lang="scss">
.reopen-button{
  font-size: 12px;
  color: blue;
}
.icon-size{
  width: 38px !important;
  height: 38px !important;
  color: #A9A9A9;
}
.active{
  .icon-size{
    color: #FFFFFF;
  }
}
</style>
