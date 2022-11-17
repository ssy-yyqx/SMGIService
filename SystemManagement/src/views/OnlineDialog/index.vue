<template>
  <div class="online-section">
    <div class="online" :class="{'active': activeButton === 'online'}" @click="openServiceDialog">
      <svg-icon icon-class="onlineService" class-name="icon-size" />
      <div class="title">在线客服</div>
      <div v-show="activeButton === 'online'" class="online-dialog">
        <div class="left-user">
          <template v-if="userList && userList.length > 0">
            <div v-for="item in userList" :key="item.name" class="user-item" @click="setCurrentUser(item)">{{ item.name }}</div>
          </template>
          <div v-else>暂无在线用户</div>
        </div>
        <div class="right-message">
          <div class="header">
            <div class="header-left">
              <!-- <div class="avatar" /> -->
              <div class="name">{{ currentUser }}<span v-if="currentUser&&!isOnline">(离线)</span></div>
            </div>
            <div class="header-right">
              <i class="icon el-icon-close" @click.stop="closeDialog" />
            </div>
          </div>
          <div class="records-section">
            <template v-for="(item, index) in records">
              <div v-if="item.isCenter" :key="index" class="warning">
                {{ item.content }}
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
              <el-button :disabled="!isOnline" @click="send">发送</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="messageMgn" :class="{'active': activeButton === 'messageMgn'}" @click="messageMgn">
      <svg-icon icon-class="messageMgn" class-name="icon-size" />
      <div class="title">问题管理</div>
      <MessageMgn v-if="activeButton === 'messageMgn'" @closeDialog="closeDialog" />
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { parseTime } from '@/utils/index'
import MessageMgn from './MessageMgn.vue'
export default {
  components: { MessageMgn },
  data() {
    return {
      activeButton: '',
      // 发送的信息
      message: '',
      // 用户列表
      userList: [],
      // 当前用户
      currentUser: '',
      // 聊天记录
      records: [],
      websocket: null,
      // 当前的用户列表
      currentUserList: []
    }
  },
  computed: {
    ...mapGetters(['name']),
    // 在线用户数量
    onlineNum() {
      return this.currentUserList.length
    },
    // 是否在线
    isOnline() {
      const onlineUser = this.currentUserList.find(item => item === this.currentUser)
      return onlineUser
    }
  },
  beforeDestroy() {
    if (window.websocket) {
      window.websocket.close()
    }
  },
  methods: {
    // 打开在线客服弹窗
    openServiceDialog() {
      if (!window.websocket) {
        if (this.supportWebsocket) {
          this.operaWebsocket()
        } else {
          this.$message.warning('您的浏览器版本太低！请先升级浏览器！')
        }
      }
      this.activeButton = 'online'
    },
    // 打开websocket
    operaWebsocket() {
      window.websocket = new WebSocket(`ws://192.168.1.40:8080/upgrade/ws/asset/${this.name}@admin_chat`)
      // 连接发生错误的回调方法
      window.websocket.onerror = () => {
        this.$message.error('连接失败!服务器异常！')
      }
      // 连接成功建立的回调方法
      window.websocket.onopen = () => {
        this.$message.success('您已上线！')
      }

      // 接收到消息的回调方法
      window.websocket.onmessage = (event) => {
        var received_msg = event.data // 接收到的数据
        var obj = JSON.parse(received_msg) // json数据
        // 1上线/2下线/3在线名单/4发信息
        const { messageType, onlineUsers, textMessage, username, fromusername } = obj
        if (messageType === 1) { // 上线，更新用户名单
          this.$message.success(`[系统]:客户${username}上线了`)
          this.setUserRecords(onlineUsers)
        } else if (messageType === 2) { // 下线，更新用户名单
          this.$message.error(`[系统]:客户${username}下线了`)
          this.setUserRecords(onlineUsers)
        } else if (messageType === 3) { // 在线客户列表更新
          this.setUserRecords(onlineUsers)
        } else if (messageType === 4) { // 普通消息
          const user = this.userList.find(item => item.name === fromusername)
          const message = {
            isCenter: false,
            isMe: false,
            content: textMessage,
            time: parseTime(new Date())
          }
          user.records.push(message)
          // 如果是当前正在聊天的客户，则更新聊天记录
          if (username === this.currentUser) {
            this.records = message
          }
        } else if (messageType === 5) { // 系统消息
          const user = this.userList.find(item => item.name === fromusername)
          const message = {
            isCenter: false,
            isMe: true,
            content: textMessage,
            time: parseTime(new Date())
          }
          user.records.push(message)
          // 如果是当前正在聊天的客户，则更新聊天记录
          if (username === this.currentUser) {
            this.records = message
          }
        } else if (messageType === 8) {
          // 更换姓名
          const user = this.userList.find(item => item.name === obj.oldName)
          if (user) {
            user.name = obj.newName
          }
          // 更新当前窗口名字
          if (this.currentUser === obj.oldName) {
            this.currentUser = obj.newName
          }
          // 更新当前在线用户列表
          const index = this.currentUserList.findIndex(item => item === obj.oldName)
          this.currentUserList[index] = obj.newName
        }
      }

      // 连接关闭的回调方法
      window.websocket.onclose = function() {}
    },
    // 设置用户对话记录
    setUserRecords(userList) {
      this.currentUserList = userList
      userList.forEach(item => {
        const user = this.userList.find(origionItem => origionItem.name === item)
        if (!user) {
          this.userList.push({
            name: item,
            records: []
          })
        }
      })
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
        'to': this.currentUser,
        'chat_type': 'admin_chat'
      }
      // 发送数据
      window.websocket.send(JSON.stringify(sendMessage))
      const user = this.userList.find(item => item.name === this.currentUser)
      user.records.push({
        isCenter: false,
        isMe: true,
        content: message,
        time: parseTime(new Date())
      })
      this.message = ''
    },
    // 关闭客服弹窗
    closeDialog() {
      this.activeButton = ''
    },
    // 设置当前用户
    setCurrentUser(user) {
      this.currentUser = user.name
      this.records = user.records
    },
    // 打开留言窗口
    messageMgn() {
      this.activeButton = 'messageMgn'
    }
  }
}
</script>

<style lang="scss" scoped>
.online-section{
  position: fixed;
  right: 20px;
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
      left: -560px;
      bottom: 0px;
      width: 559px;
      height: 540px;
      box-shadow: 0px 4px 13px 0px rgba(0, 0, 0, 0.24);
      text-align: left;
      background: #FFFFFF;
      color: #535353;
      display: flex;
      .left-user{
        width: 200px;
        padding: 10px;
        height: 100%;
        overflow: auto;
        background: #DFDEDE;
        .user-item{
          font-size: 14px;
          line-height: 30px;
          cursor: pointer;
        }
      }
      .right-message{
        width: calc(100% - 200px);
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
          width: calc(100% - 200px);
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
  }
  .messageMgn{
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
