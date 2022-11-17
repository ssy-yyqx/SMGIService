<template>
  <div class="list-detail-container">
    <div class="toolbar-section">
      <el-button type="primary" @click="onHandleAdd">新增</el-button>
    </div>
    <div class="table-section">
      <el-table
        :data="tableData"
        header-cell-class-name="table-header"
        border
        height="100%"
        style="width: 100%;"
      >
        <el-table-column
          prop="imageUrl"
          label="封面图片"
          width="200"
        >
          <template slot-scope="scope">
            <el-image
              style="width: 60px; height: 60px;cursor: pointer;"
              :src="scope.row.imageUrl"
              fit="fit"
            />
          </template>
        </el-table-column>
        <el-table-column
          prop="title"
          label="标题"
        />
        <el-table-column
          prop="createTime"
          label="创建时间"
        >
          <template slot-scope="scope">
            {{ scope.row.createTime | formatTime }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleEdit(scope.$index, scope.row)"
            >修改</el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)"
            >删除</el-button>
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
      width="920px"
      :close-on-click-modal="false"
    >
      <Form v-if="dialogVisible" ref="form" :module-id="id" :form-data="formData" />
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="onHandlePreview">预览</el-button>
        <el-button v-if="!submitLoading" type="primary" @click="onHandleSubmit">提 交</el-button>
        <el-button v-else type="primary" :loading="true">提 交</el-button>
      </span>
      <el-dialog
        width="1100px"
        title="预览"
        :visible.sync="innerVisible"
        append-to-body
      >
        <PreviewPage :data="formData" />
        <div slot="footer" class="dialog-footer">
          <el-button @click="innerVisible = false">关 闭</el-button>
        </div>
      </el-dialog>
    </el-dialog>
  </div>
</template>

<script>
import * as API from '@/api/ContentMgn'
import Form from './Form.vue'
import PreviewPage from '../../PreviewTemplate/pages/ListAndDetail/Detail.vue'
const initForm = {
  title: '',
  imageUrl: '',
  dataId: '',
  content: ''
}
const FORM_TYPE = {
  ADD: 'ADD',
  EDIT: 'EDIT'
}
export default {
  components: { Form, PreviewPage },
  props: {
    id: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      // 预览弹窗
      innerVisible: false,
      tableData: [],
      // 弹窗标题
      title: '',
      // 是否显示弹窗
      dialogVisible: false,
      // 表单数据
      formData: Object.assign({}, initForm),
      // 表单类型
      type: FORM_TYPE.ADD,
      // 分页信息
      pager: {
        pageSize: 10,
        pageNumber: 1,
        total: 0
      },
      // 提交loading
      submitLoading: false
    }
  },
  watch: {
    id: {
      handler(val) {
        if (val) {
          this.getTableData(val)
        }
      },
      immediate: true
    }
  },
  methods: {
    // 获取表格数据
    getTableData(id) {
      const params = {
        moduleId: id,
        pageSize: this.pager.pageSize,
        pageNumber: this.pager.pageNumber
      }
      API.getTableList(params).then(res => {
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
    // 新增
    onHandleAdd() {
      this.type = FORM_TYPE.ADD
      this.title = '新增'
      this.dialogVisible = true
      this.formData = Object.assign({}, initForm)
    },
    // 修改
    handleEdit(index, row) {
      this.type = FORM_TYPE.EDIT
      this.title = '修改'
      this.dialogVisible = true
      this.formData = row
    },
    // 删除
    handleDelete(index, row) {
      this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const params = {
          contentId: row.id,
          moduleId: this.id
        }
        API.deleteNewsOrNotice(params).then(res => {
          const { code, msg } = res
          if (String(code) === '200') {
            this.$message({
              type: 'success',
              message: '删除成功!'
            })
            this.getTableData(this.id)
          } else {
            this.$message.error(msg)
          }
        })
      })
    },
    // 切换每页条数
    handleSizeChange(val) {
      this.pager.pageSize = val
      this.getTableData(this.id)
    },
    // 切换当前页
    handleCurrentChange(val) {
      this.pager.pageNumber = val
      this.getTableData(this.id)
    },
    // 提交
    onHandleSubmit() {
      this.submitLoading = true
      const params = this.$refs.form.getFormData()
      if (this.type === FORM_TYPE.ADD) {
        API.addNewsOrNotice(params).then(res => {
          const { code, msg } = res
          if (String(code) === '200') {
            this.$message.success('添加成功！')
            this.getTableData(this.id)
          } else {
            this.$message.error(msg)
          }
          this.submitLoading = false
        })
      } else {
        params.contentId = params.id
        API.editNewsOrNotice(params).then(res => {
          const { code, msg } = res
          if (String(code) === '200') {
            this.$message.success('修改成功！')
            this.getTableData(this.id)
          } else {
            this.$message.error(msg)
          }
          this.submitLoading = false
        })
      }
      this.dialogVisible = false
    },
    // 预览
    onHandlePreview() {
      this.formData = this.$refs.form.getFormData()
      this.innerVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
.list-detail-container{
  .toolbar-section{
    margin-bottom: 10px;
  }
  .table-section{
    height: calc(100vh - 240px)
  }
  .pager-section{
    text-align: right;
    padding: 20px 0;
  }
}
</style>
<style lang="scss">
.table-header{
  background: #f0f7ff !important;
}
</style>
