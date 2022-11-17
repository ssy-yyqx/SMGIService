<template>
  <div>
    <Paper title="我的在线办理">
      <div slot="content">
        <el-form :inline="true" :model="formInline" class="demo-form-inline">
          <el-form-item>
            <el-input v-model="formInline.search" placeholder="请输入内容..." />
          </el-form-item>
          <el-form-item>
            <el-button @click="onSubmit">查询</el-button>
          </el-form-item>
        </el-form>
        <el-table
          :data="tableData"
          header-cell-class-name="table-header"
          style="width: 100%"
        >
          <el-table-column prop="date" label="序号" align="center" />

          <el-table-column prop="name" label="在线服务名称" align="center" />

          <el-table-column prop="address" label="时间" align="center" />

          <el-table-column prop="address" label="服务成果文件" align="center" />
        </el-table>
        <div class="pagination">
          <el-pagination
            :page-sizes="[10, 20, 30, 40]"
            :page-size="pageSize"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            :current-page="currentPage"
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
      tableData: [],
      currentPage: 10,
      pageSize: 1,
      total: 0,
      formInline: {}
    }
  },
  created() {
    this.getOnlineDeal()
  },

  methods: {
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`)
    },
    handleCurrentChange(val) {
      console.log(`当前页: ${val}`)
    },
    // 查询
    onSubmit() {
      this.getOnlineDeal()
    },
    // 获取列表
    getOnlineDeal() {
      const prams = {
        search: this.formInline.search
      }
      API.getOnlineDeal(prams).then(res => {
        const { code, msg, data } = res
        if (String(code) === '200') {
          this.tableData = data
          this.total = data.total
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
