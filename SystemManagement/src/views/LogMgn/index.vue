<template>
  <div class="log-container">
    <div class="search-section">
      <el-input v-model="keyword" placeholder="请输入用户名" class="input-with-select">
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
          prop="operateUsername"
          label="用户名"
          width="180"
        />
        <el-table-column
          prop="operateRealName"
          label="真实姓名"
        />
        <el-table-column
          prop="operateRoleName"
          label="用户类型"
          width="180"
        />
        <el-table-column
          prop="moduleName"
          label="操作模块"
        />
        <el-table-column
          prop="ipAddr"
          label="用户IP"
          width="180"
        />
        <el-table-column
          label="操作时间"
        >
          <template slot-scope="scope">
            {{ scope.row.createTime | formatTimeFull }}
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="pager-section">
      <el-pagination
        :current-page="pager.pageNumber"
        :page-sizes="[10, 20, 30, 40]"
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
import * as API from '@/api/LogMgn'
export default {
  data() {
    return {
      // 搜索条件
      keyword: '',
      // 表格数据
      tableData: [],
      // 分页信息
      pager: {
        pageSize: 10,
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
      API.getTableList(params).then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          this.tableData = data.list
          this.pager.total = data.total
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 搜索
    onHandleSearch() {
      this.getTableData()
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
    }
  }
}
</script>

<style lang="scss" scoped>
.log-container{
  .search-section{
    width: 400px;
    margin-bottom: 20px;
  }
  .table-section{
    height: calc(100vh - 200px);
  }
  .pager-section{
    text-align: right;
    padding: 20px 0;
  }
}
</style>
