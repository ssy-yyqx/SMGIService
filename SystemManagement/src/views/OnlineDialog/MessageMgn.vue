<template>
  <div class="history-message-container">
    <div class="header">
      <div class="header-left">
        <div class="name">留言管理</div>
      </div>
      <div class="header-right">
        <i class="icon el-icon-close" @click.stop="closeDialog" />
      </div>
    </div>
    <div class="search-section">
      <el-input v-model="keyword" placeholder="请输入问题名称" class="input-with-select">
        <el-button slot="append" icon="el-icon-search" @click="onHandleSearch" />
      </el-input>
    </div>
    <div class="table-section">
      <el-table
        :data="tableData"
        height="100%"
        border
        style="width: 100%"
      >
        <el-table-column
          label="编号"
          type="index"
          width="50"
        />
        <el-table-column
          prop="questionBy"
          label="提问者"
          width="180"
        />
        <el-table-column
          label="提问时间"
          width="90"
        >
          <template slot-scope="scope">
            {{ scope.row.questionTime | formatTime }}
          </template>
        </el-table-column>
        <el-table-column
          prop="questionContent"
          label="问题内容"
          width="180"
        />
        <el-table-column
          label="问题答复"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.replyContent"
              type="textarea"
              placeholder="请输入答复"
              rows="3"
            /></template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="100"
        >
          <template slot-scope="scope">
            <el-button v-if="scope.row.isReplied === 0" type="primary" size="mini" @click="onHandleSend(scope.row)">发送</el-button>
            <el-button v-if="scope.row.isReplied === 1" type="default" size="mini" @click="onHandleRecall(scope.row)">撤回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="pager-section">
      <el-pagination
        :current-page="pager.pageNumber"
        :page-sizes="[5, 10, 15, 20]"
        :page-size="pager.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pager.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import * as API from '@/api/user'
export default {
  data() {
    return {
      // 搜索条件
      keyword: '',
      // 表格数据
      tableData: [],
      // 分页信息
      pager: {
        pageSize: 5,
        pageNumber: 1,
        total: 0
      }
    }
  },
  created() {
    this.getTableData()
  },
  methods: {
    // 获取表格数据
    getTableData() {
      const params = {
        keyword: this.keyword,
        pageSize: this.pager.pageSize,
        pageNumber: this.pager.pageNumber
      }
      API.getMessageList(params).then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          if (data) {
            this.tableData = data.list
            this.pager.total = data.total
          }
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 搜索
    onHandleSearch() {
      this.getTableData()
    },
    // 关闭弹窗
    closeDialog() {
      this.$emit('closeDialog', false)
    },
    // 切换每页条数
    handleSizeChange(val) {
      this.pager.pageSize = val
      this.getTableData()
    },
    // 切换当前页
    handleCurrentChange(val) {
      this.pager.pageNumber = val
      this.getTableData()
    },
    // 发送
    onHandleSend(data) {
      const params = {
        id: data.id,
        replyContent: data.replyContent
      }
      API.replyMessage(params).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.$message.success('答复成功！')
          this.getTableData()
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 撤回
    onHandleRecall(data) {
      API.recallMessage({ id: data.id }).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.$message.success('撤回成功')
          this.getTableData()
        } else {
          this.$message.error(msg)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.history-message-container{
  position: absolute;
  left: -860px;
  bottom: 0px;
  width: 859px;
  height: 380px;
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
  .search-section{
    width: 300px;
    padding: 20px 10px 10px 10px;
  }
  .table-section{
    padding: 10px;
    height: 200px;
  }
  .pager-section{
    text-align: right;
    padding: 10px 0;
  }
}
</style>
