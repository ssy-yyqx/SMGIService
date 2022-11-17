<template>
  <div class="question-container">
    <div class="search-section">
      <el-input v-model="keyword" placeholder="请输入问题名称" class="input-with-select">
        <el-button slot="append" icon="el-icon-search" @click="onHandleSearch" />
      </el-input>
    </div>
    <div class="button-section">
      <el-button type="primary" @click="onHandleAdd">新增</el-button>
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
          prop="question"
          label="问题内容"
          width="180"
        />
        <el-table-column
          prop="reply"
          label="解决方案"
        />
        <el-table-column
          label="操作"
          width="180"
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
      <Form v-if="dialogVisible" ref="form" :form-data="formData" />
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="onSubmit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import Form from './Form.vue'
import * as API from '@/api/QuestionMgn'
const formDataInit = {
  question: '',
  reply: ''
}
export default {
  components: { Form },
  data() {
    return {
      // 搜索条件
      keyword: '',
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
      }
    }
  },
  watch: {
    type(val) {
      if (val === 'add') {
        this.title = '新增'
      } else if (val === 'edit') {
        this.title = '修改'
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
        API.deleteQuestion({ id: data.id }).then(res => {
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
      this.getTableData()
    },
    // 提交
    onSubmit() {
      const Form = this.$refs.form
      Form.isValidateForm().then(valid => {
        if (valid) {
          const params = Form.getFormData()
          if (this.type === 'add') {
            API.addQuestion(params).then(res => {
              const { code, msg } = res
              if (String(code) === '200') {
                this.dialogVisible = false
                this.$message.success('添加问题成功！')
                this.getTableData()
              } else {
                this.$message.error(msg)
              }
            })
          } else {
            API.updateQuestion(params).then(res => {
              const { code, msg } = res
              if (String(code) === '200') {
                this.dialogVisible = false
                this.$message.success('修改问题成功！')
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
  }
  .button-section{
    padding: 20px 0;
  }
  .table-section{
    height: calc(100vh - 240px);
  }
  .pager-section{
    text-align: right;
    padding: 20px 0;
  }
}
</style>
