<template>
  <div>
    <Paper title="我的审批">
      <div slot="content">
        <el-form :inline="true" :model="form" class="demo-form-inline">
          <el-form-item label="申请事项名称">
            <el-input v-model="form.keyword" placeholder="请输入内容..." />
          </el-form-item>
          <el-form-item label="类型">
            <el-radio-group v-model="form.status">
              <el-radio :label="0">全部</el-radio>
              <el-radio :label="1">只筛选待审批</el-radio>
            </el-radio-group>
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
          <el-table-column prop="sequenceCode" label="序号" align="center" width="120" />

          <el-table-column prop="moduleName" label="申请事项名称" align="center" />

          <el-table-column prop="approveFileId" label="所提材料" align="center" />

          <el-table-column prop="status" label="状态" align="center">
            <template slot-scope="scope">
              {{ scope.row.status | formatApproveStatus }}
            </template>
          </el-table-column>

          <el-table-column label="审批操作" align="center">
            <template slot-scope="scope">
              <template v-if="scope.row.status === 1">
                <el-button
                  size="mini"
                  @click="onHandleApprove(scope.$index, scope.row)"
                >通过</el-button>
                <el-button
                  size="mini"
                  @click="onHandleNoApprove(scope.$index, scope.row)"
                >未通过</el-button>
              </template>
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
    <el-dialog
      :title="title"
      :visible.sync="dialogVisible"
      width="30%"
      :close-on-press-escape="false"
      :show-close="false"
      :close-on-click-modal="false"
    >
      <template v-if="isPass">
        <div>
          <el-input
            v-model="reason"
            type="textarea"
            :rows="4"
            placeholder="请输入详细信息"
          />
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="onHandleConfirmPass">确 定</el-button>
        </span>
      </template>
      <template v-else>
        <div>
          <el-input
            v-model="reason"
            type="textarea"
            :rows="4"
            placeholder="请输入未通过原因"
          />
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="onHandleConfirmNotPass">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import * as API from '@/api/UserCenter'
export default {
  data() {
    return {
      tableData: [],
      // 分页参数
      pager: {
        pageNumber: 1,
        pageSize: 10,
        total: 0
      },
      // 查询参数
      form: {
        keyword: '',
        status: ''
      },
      // 未通过弹窗
      dialogVisible: false,
      // 当前操作行
      currentRow: {},
      // 审批不通过原因
      reason: '',
      // 是否是通过弹窗
      isPass: true,
      // 弹窗标题
      title: ''
    }
  },
  watch: {
    dialogVisible: {
      handler() {
        this.reason = ''
      }
    }
  },
  created() {
    this.getTableData()
  },
  methods: {
    // 搜索
    onHandleSearch() {
      this.getTableData()
    },
    // 获取表格数据
    getTableData() {
      const params = Object.assign({
        keyword: this.form.keyword,
        pageNumber: this.pager.pageNumber,
        pageSize: this.pager.pageSize
      })
      if (this.form.status) {
        params.status = this.form.status
      }
      API.getApproveList(params).then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          this.tableData = data.list
          this.pager.total = data.total
        } else {
          this.$message.error(msg)
        }
      })
    },
    handleSizeChange(val) {
      this.pager.pageSize = val
      this.getTableData()
    },
    handleCurrentChange(val) {
      this.pager.pageNumber = val
      this.getTableData()
    },
    // 审批通过
    onHandleApprove(index, row) {
      this.isPass = true
      this.title = '详细联系信息'
      this.currentRow = row
      this.dialogVisible = true
    },
    // 审批通过确认
    onHandleConfirmPass() {
      if (!this.reason) {
        this.$message.warning('请输入详细信息！')
        return
      }
      const params = {
        appointmentId: this.currentRow.id,
        detail: this.reason
      }
      API.passApprove(params).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.$message.success('审批通过成功！')
          this.getTableData()
          this.dialogVisible = false
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 审批不通过
    onHandleNoApprove(index, row) {
      this.isPass = false
      this.title = '未通过原因'
      this.currentRow = row
      this.dialogVisible = true
    },
    // 审批不通过确认
    onHandleConfirmNotPass() {
      if (!this.reason) {
        this.$message.warning('请输入审批不通过原因！')
        return
      }
      const params = {
        appointmentId: this.currentRow.id,
        detail: this.reason
      }
      API.notPpassApprove(params).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.$message.success('审批不通过成功！')
          this.dialogVisible = false
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
.pagination {
  float: right;
  margin-top: 10px;
}
.table-header {
  background: #fafafa !important;
}
</style>
