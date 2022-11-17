<template>
  <div>
    <Paper title="我的收藏">
      <div slot="content">
        <el-form :inline="true" class="demo-form-inline">
          <el-form-item>
            <el-input v-model="keyword" placeholder="请输入内容..." clearable />
          </el-form-item>
          <el-form-item>
            <el-button @click="onHandleSearch">查询</el-button>
          </el-form-item>
        </el-form>
        <el-table
          :data="tableData"
          header-cell-class-name="table-header"
          style="width: 100%"
        >
          <el-table-column
            label="序号"
            type="index"
            width="50"
          />
          <el-table-column prop="name" label="名称" align="center" />
          <el-table-column prop="address" label="收藏时间" align="center">
            <template slot-scope="scope">
              {{ scope.row.createTime | formatTime }}
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <el-button
                size="mini"
                @click="handleCancel(scope.$index, scope.row)"
              >取消收藏</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
            :page-sizes="[10, 20, 30, 40]"
            :page-size="pager.pageSize"
            :total="pager.total"
            :current-page="pager.pageNumber"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </Paper>
  </div>
</template>

<script>
import * as API from '@/api/UserCenter'
export default {
  data() {
    return {
      // 表格数据
      tableData: [],
      // 查询参数
      keyword: '',
      // 分页参数
      pager: {
        pageNumber: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  created() {
    // 获取表格数据
    this.getCollection()
  },
  methods: {
    // 查询
    onHandleSearch() {
      this.getCollection()
    },
    // 表格数据列表
    getCollection(id) {
      const params = {
        keys: this.keyword,
        pageSize: this.pager.pageSize,
        pageNumber: this.pager.pageNumber
      }
      API.getCollection(params).then(res => {
        const { msg, code, data } = res
        if (String(code) === '200') {
          if (data && data.list) {
            this.tableData = data.list
            this.pager.total = data.total
          }
        } else {
          this.$message.error(msg)
        }
      })
    },
    handleSizeChange(val) {
      this.pager.pageSize = val
      this.getCollection()
    },
    handleCurrentChange(val) {
      this.pager.pageNumber = val
      this.getCollection()
    },
    // 取消收藏
    handleCancel(index, row) {
      API.cancelCollectInUserCenter({ id: row.id }).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.getCollection()
        } else {
          this.$message.error(msg)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.pagination {
  float: right;
  margin-top: 10px;
}
.table-header {
  background: #fafafa !important;
}
</style>
