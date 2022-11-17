<template>
  <div>
    <Paper title="我的申请预约">
      <div slot="content">
        <el-form :inline="true" :model="form" class="demo-form-inline">
          <el-form-item>
            <el-input v-model="form.keyword" placeholder="请输入内容..." clearable />
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="form.status" placeholder="请选择类型" clearable>
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
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
            prop="sequenceCode"
            label="序号"
            width="120"
          />
          <el-table-column prop="moduleName" label="服务名称" align="center" />
          <el-table-column prop="status" label="审批状态" align="center">
            <template slot-scope="scope">
              {{ scope.row.status | formatApproveStatus }}
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="时间" align="center">
            <template slot-scope="scope">
              {{ scope.row.createTime | formatTime }}
            </template>
          </el-table-column>
          <el-table-column prop="detail" label="详情" align="center" />
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <!-- <el-button
                size="mini"
                @click="handleEdit(scope.$index, scope.row)"
              >预约</el-button> -->
              <el-button
                v-if="scope.row.status === 1"
                size="mini"
                type="danger"
                @click="onHandleCancel(scope.$index, scope.row)"
              >撤销</el-button>
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
    <popUp
      :dialog-visible="dialogVisible"
      @takeAway="takeAway"
      @determine="determine"
    >
      <div slot="content">
        <el-form ref="upForm" :model="upForm" label-width="120px">
          <el-row>
            <el-col :span="22">
              <el-form-item label="时间选择：">
                <el-date-picker
                  v-model="upForm.date"
                  style="width: 100%"
                  type="datetime"
                  placeholder="选择日期时间"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="22">
              <el-form-item label="详细地址：">
                <el-input v-model="upForm.address" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="22">
              <el-form-item label="联系人：">
                <el-input v-model="upForm.name" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="22">
              <el-form-item label="联系电话：">
                <el-input v-model="upForm.tel" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </popUp>
  </div>
</template>

<script>
import * as API from '@/api/UserCenter'
import popUp from '@/components/popUp'
// import { AppMain } from '@/layout/components'
import { APPROVAL_STATUS } from '@/common/dict'
export default {
  components: {
    popUp
  },
  data() {
    return {
      keyword: '',
      tableData: [],
      // 分页参数
      pager: {
        pageNumber: 1,
        pageSize: 10,
        total: 0
      },
      form: {},
      dialogVisible: false,
      upForm: {},
      rowId: '',
      options: APPROVAL_STATUS
    }
  },
  created() {
    this.getAppointment()
  },
  methods: {
    // 查询
    onHandleSearch() {
      this.getAppointment()
    },
    // 申请预约列表
    getAppointment() {
      const prams = Object.assign({}, this.form, {
        pageNumber: this.pager.pageNumber,
        pageSize: this.pager.pageSize
      })
      API.getAppointment(prams).then(res => {
        const { code, msg, data } = res
        if (String(code) === '200') {
          this.tableData = data.list
          this.total = data.total
        } else {
          this.$message.error(msg)
        }
      })
    },
    handleSizeChange(val) {
      this.pager.pageSize = val
      this.getAppointment()
    },
    handleCurrentChange(val) {
      this.pager.pageNumber = val
      this.getAppointment()
    },
    // 预约
    handleEdit(index, row) {
      console.log(index, row)
      this.dialogVisible = true
      this.upForm = {}
      this.rowId = row.id
    },
    // 撤销
    onHandleCancel(index, row) {
      API.cancelApproval(row.id).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.$message.success('撤销成功！')
          this.getAppointment()
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 预约对话框取消事件
    takeAway(val) {
      this.dialogVisible = false
      this.upForm = {}
    },
    // 预约对话框确定事件
    determine(val) {
      this.dialogVisible = false
      API.getFinish({}, '1').then(res => {
        const { code, msg, data } = res
        if (String(code) === '200') {
          this.upForm = {}
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
