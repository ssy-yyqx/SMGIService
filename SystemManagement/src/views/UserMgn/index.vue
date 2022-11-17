<template>
  <div class="question-container">
    <div class="search-section">
      <el-input v-model="username" placeholder="请输入用户名" class="input-with-select">
        <el-button slot="append" icon="el-icon-search" @click="onHandleSearch" />
      </el-input>
    </div>
    <!-- <div class="button-section">
      <el-button type="primary" @click="onHandleAdd">新增</el-button>
    </div> -->
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
          align="center"
        />
        <el-table-column
          prop="username"
          label="用户名"
          align="center"
        />
        <el-table-column
          prop="realName"
          label="真实姓名"
          align="center"
        />
        <el-table-column
          prop="roleName"
          label="用户类型"
          align="center"
        />
        <el-table-column
          prop="phoneNumber"
          label="联系电话"
          align="center"
        />
        <el-table-column
          prop="email"
          label="邮箱"
          align="center"
        />
        <el-table-column
          label="操作"
          align="center"
        >
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="onHandleEdit(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="onHandleDelete(scope.row)">删除</el-button>
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
    <el-dialog
      :title="title"
      :visible.sync="dialogVisible"
      width="50%"
      :close-on-click-modal="false"
    >
      <Form v-if="dialogVisible" ref="form" :form-data="formData" @optionss="optionss" />
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="onSubmit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import Form from './Form.vue'
import * as API from '@/api/UserMgn'
const formDataInit = {
  question: '',
  reply: ''
}
export default {
  components: { Form },
  data() {
    return {
      // 搜索条件
      username: '',
      // 表格数据
      tableData: [],
      // 是否显示弹窗
      dialogVisible: false,
      // 表单数据
      formData: Object.assign({}, formDataInit),
      // 弹窗类型
      type: 'add',
      // 弹窗标题
      title: '',
      pager: {
        pageSize: 10,
        pageNumber: 1,
        total: 0
      },
      roleId: ''
    }
  },
  watch: {
    type(val) {
      if (val === 'add') {
        this.title = '新增'
      } else if (val === 'edit') {
        this.title = '编辑'
      }
    }
  },
  created() {
    this.getTableData()
  },
  methods: {
    optionss(val) {
      console.log(val)
      this.roleId = val
    },
    // 获取表格数据
    getTableData() {
      const data = {
        username: this.username,
        pageSize: this.pager.pageSize,
        pageNumber: this.pager.pageNumber
      }
      API.getTableList(data).then(res => {
        const { code, rows, msg, total } = res
        if (String(code) === '200') {
          this.tableData = rows
          this.pager.total = total
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 搜索
    onHandleSearch() {
      this.getTableData()
    },
    // 点击添加按钮
    onHandleAdd() {
      this.type = 'add'
      this.dialogVisible = true
      this.formData = Object.assign({}, formDataInit)
    },
    // 修改
    onHandleEdit(data) {
      this.type = 'edit'
      this.dialogVisible = true
      this.formData = data
    },
    // 删除
    onHandleDelete(data) {
      this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        API.deleteUser(data.id).then(res => {
          const { code, msg } = res
          if (String(code) === '200') {
            this.$message({
              type: 'success',
              message: '删除成功!'
            })
            this.getTableData()
          } else {
            this.$message.error(msg)
          }
        })
      })
    },
    // 切换每页条数
    handleSizeChange(val) {
      this.pager.pageSize = val
      this.getTableData()
    },
    // 切换当前页
    handleCurrentChange(val) {
      this.pager.pageNumber = val
      this.getTableData
    },
    // 提交
    onSubmit() {
      const Form = this.$refs.form
      Form.isValidateForm().then(valid => {
        if (valid) {
          let params = Form.getFormData()
          if (this.type === 'add') {
            API.addUser(params).then(res => {
              const { code, msg } = res
              if (String(code) === '200') {
                this.dialogVisible = false
                this.$message.success('添加用户成功！')
                this.getTableData()
              } else {
                this.$message.error(msg)
              }
            })
          } else {
            params = {
              ...params,
              userId: params.id,
              roleId: this.roleId || params.roleId
            }
            API.updateUser(params).then(res => {
              const { code, msg } = res
              if (String(code) === '200') {
                this.dialogVisible = false
                this.$message.success('修改用户成功！')
                this.getTableData()
              } else {
                this.$message.error(msg)
              }
            })
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.question-container{
  .search-section{
    width: 400px;
    margin-bottom: 18px;
  }
  .button-section{
    padding: 20px 0;
  }
  .table-section{
    height: calc(100vh - 181px);
  }
  .pager-section{
    text-align: right;
    padding: 20px 0;
  }
}
</style>
