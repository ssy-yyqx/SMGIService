<template>
  <div>
    <template v-if="commentList && commentList.length > 0">
      <div class="main">
        <div v-for="(item, index) in commentList" :key="index" class="list">
          <div class="content">
            <div class="left">
              <span class="user">{{ item.createBy }} </span>发布于：
              <span class="time">{{ item.createTime | formatTime }}</span>
            </div>
          </div>
          <p class="info">
            {{ item.content }}
          </p>
        </div>
      </div>
      <div class="pager-section">
        <el-pagination
          :current-page="pager.pageNumber"
          :page-sizes="pager.pageSizes"
          :page-size="pager.pageSize"
          layout="prev, pager, next"
          :total="pager.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </template>
    <div v-else class="no-data">
      暂无评论
    </div>
    <div class="ipt">
      <el-input v-model="content" type="textarea" placeholder="我的评价..." :rows="6" />
      <el-button size="medium" type="primary" @click="publish">发布</el-button>
      <el-button
        size="medium"
      >取消</el-button>
    </div>
  </div>
</template>
<script>
import * as API from '@/api/content'
export default {
  props: {
    moduleId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      // 评论内容
      content: '',
      // 评论列表
      commentList: [],
      // 分页
      pager: {
        pageNumber: 1,
        pageSize: 10,
        total: 0,
        pageSizes: [10, 15, 20, 30]
      }
    }
  },
  created() {
    this.getCommentList()
  },
  methods: {
    // 获取评论列表
    getCommentList() {
      const params = {
        moduleId: this.moduleId,
        pageSize: this.pager.pageSize,
        pageNumber: this.pager.pageNumber
      }
      API.getCommentList(params).then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          this.commentList = data.list
          this.pager.total = data.total
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 发布
    publish() {
      if (!this.content) {
        this.$message.warning('请输入评论内容！')
      }
      API.addComment({
        content: this.content,
        moduleId: this.moduleId
      }).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.getCommentList()
          this.content = ''
        } else {
          this.$message.error(msg)
        }
      })
    },
    handleSizeChange(val) {
      this.pager.pageSize = val
      this.getCommentList()
    },
    handleCurrentChange(val) {
      this.pager.pageNumber = val
      this.getCommentList()
    }
  }
}
</script>

<style lang="scss">
.main {
  // display: flex;
  height: 416px;
  overflow: auto;
  font-size: 16px;
  .list {
    border: 1px solid #e9e9e9;
    margin-bottom: 10px;
    padding: 10px;
    font-size: 14px;
    .content {
      width: 100%;
      display: flex;
      justify-content: space-between;
      .user{
        color: #535353;
      }
      .time{
        color: #717171;
      }
    }
    .info{
      text-indent: 2em;
      color: #717171;
    }
  }
}

.pager-section {
  width: 100%;
  height: 30px;
  margin: 10px 0;
  text-align: right;
}
.el-textarea__inner {
  //el_input中的隐藏属性
  resize: none; //主要是这个样式
}
.ipt{
  // display: flex;
  position: relative;
  :nth-child(1){
    width: 93%;
  }
  :nth-child(2){
    position: absolute;
    top: 20px;
    right: 35px;
  }
  :nth-child(3){
    position: absolute;
    top: 70px;
    right: 35px;
  }

}
.no-data{
  padding: 20px;
  font-size: 16px;
  color: #717171;
}
</style>
