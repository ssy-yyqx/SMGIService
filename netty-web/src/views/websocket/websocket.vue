<template>
    <div class="container">
        <div>
            <el-input style="width: 300px;margin-right: 20px;" v-model="input" placeholder="Please input" />
            <el-button 
                style="width: 50px;margin-right: 20px;" 
                type="primary" 
                round
                @click="send(this.input)"
            >send</el-button>
        </div>
        <el-descriptions title="responseText">
            <el-descriptions-item 
                label="消息"
            >{{ this.responseText }}</el-descriptions-item>
        </el-descriptions>
    </div>
</template>

<script>

export default {
    name: 'websocket0',
    data() {
        return{
            socket: '',
            responseText: '',
            input: ''
        }
    },
    created() {
        this.initWebsocket()
    },
    methods: {
        initWebsocket() {
            this.responseText = ''
            if(!window.WebSocket){
                window.WebSocket = window.MozWebSocket;
            }
            if(window.WebSocket){
                console.log("ws:" + location.host + "/ws-api/ws")
                this.socket = new WebSocket("ws:" + location.host + "/ws-api/ws");
                console.log(this.socket)
                this.socket.onmessage = function(event){
                    this.responseText += event.data+"\r\n";
                    console.log(this.responseText)
                };
                this.socket.onopen = function(event){
                    this.responseText = "Netty-WebSocket服务。。。。。。连接  \r\n";
                };
                this.socket.onclose = function(event){
                    this.responseText = "Netty-WebSocket服务。。。。。。关闭 \r\n";
                };
            }else{
                alert("您的浏览器不支持WebSocket协议！");
            }
        },
        send(message){
            if(!window.WebSocket){return;}
            if(this.socket.readyState == WebSocket.OPEN){
                this.socket.send(message);
            }else{
                alert("WebSocket 连接没有建立成功！");
            }
        }
    }
}
</script>

<style lang="scss" scoped>
.button-section{
  margin: 5px;
  text-align: right;
}
.container {
  position: relative;
  width: 100%;
  height: 100%;
  padding: 20px;
  overflow-y: auto;
  .data-section {
    width: 100%;
    height: 720px;
    margin: auto;
    padding: 10px 20px 20px 20px;
    background: #ffffff;
    box-shadow: 0px 1px 6px 0px rgba(0, 0, 0, 0.06);
    border-radius: 4px;
    .opera-section {
      display: flex;
      justify-content: space-between;
      .search-section {
        display: flex;
        .other-search-condition {
          display: flex;
          flex-wrap: wrap;
          .search-item {
            display: flex;
            align-items: baseline;
            margin-right: 20px;
            .label {
              font-size: 14px;
              font-weight: normal;
              display: inline-block;
              word-break: keep-all;
            }
          }
        }
      }
    }
    .table-section {
      height: 620px;
      margin: auto;
      padding: auto;
    }
    .pager-section {
      margin-top: 10px;
      text-align: right;
    }
  }
  .map-section {
    width: 100%;
    height: 600px;
    margin-top: 10px;
    display: flex;
    .map-container {
      flex: 1;
      background: #ffffff;
      border-radius: 4px;
      .map-title {
        height: 30px;
        text-align: center;
        margin: 10px;
        font-size: 18px;
      }
      .picture-section {
        width: 100%;
        height: calc(100% - 100px);
      }
      .map-info {
        max-height: 30px;
        line-height: 24px;
        padding: 10px;
      }
    }
    .map-info-section {
      width: 400px;
      background: #ffffff;
      border: 1px #dfdfdf;
      margin-left: 10px;
      border-radius: 4px;
      .map-info-item {
        padding: 10px;
        font-size: 14px;
      }
    }
  }
}
</style>