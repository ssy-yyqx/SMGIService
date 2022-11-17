<template>
  <div class="question-container">
    <div class="search-section">
      <el-input v-model="keyword" placeholder="请输入用户类型" class="input-with-select">
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
          prop="roleName"
          label="用户类型"
          align="center"
        />
        <el-table-column
          :formatter="formatDate"
          prop="createTime"
          label="创建时间"
          align="center"
        />
        <el-table-column
          label="权限"
          align="center"
        >
          <template slot-scope="scope">

            <div
              v-for="(item,index) in scope.row.permissionStrList"
              :key="index"
              style="display:inline-block"
            >
              <el-checkbox
                v-model="scope.row.permissionStrList[index].hasPermission"
                style="margin:15px"
                @change="edited(scope.row,item,$event)"
              >
                {{ item.permName }}
              </el-checkbox>
            </div>
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
import * as API from '@/api/PriorityMgn'
export default {
  data() {
    return {
      // 搜索条件
      keyword: '',
      // 表格数据
      tableData: [],
      // 弹窗类型
      type: 'add',
      // 弹窗标题
      title: '',
      // Ischeckbox: true,
      pager: {
        pageSize: 10,
        pageNumber: 1,
        total: 0
      },
      checkboxData: []
      // checked: []
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
  async created() {
    await this.permission()
    this.getTableData()
  },
  methods: {
    edited(row, val, event) {
      const ids = []
      row.permissionStrList.forEach(item => {
        if (item.hasPermission) {
          ids.push(item.id)
        }
      })
      const data = {
        permStrIds: ids,
        id: row.id
      }
      API.edit(data).then(res => {
        if (res.code === 200) {
          this.$message.success(res.msg)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    async permission() {
      const res = await API.permission()
      if (res.code === 200) {
        this.checkboxData = res.data
      }
    },
    // 格式化表格时间
    formatDate(row, column) {
      // 获取单元格数据
      const date = row[column.property]
      if (date) {
        return this.transformDate(date)
      } else {
        return ''
      }
    },
    // 标准时间格式转yyyy-MM-dd HH:mm:ss
    transformDate(date) {
      if (date) {
        const dt = new Date(date)
        return dt.getFullYear() + '-' +
              ((dt.getMonth() + 1) < 10 ? ('0' + (dt.getMonth() + 1)) : (dt.getMonth() + 1)) + '-' +
              (dt.getDate() < 10 ? ('0' + dt.getDate()) : dt.getDate()) + ' ' +
              (dt.getHours() < 10 ? ('0' + dt.getHours()) : dt.getHours()) + ':' +
              (dt.getMinutes() < 10 ? ('0' + dt.getMinutes()) : dt.getMinutes()) + ':' +
              (dt.getSeconds() < 10 ? ('0' + dt.getSeconds()) : dt.getSeconds())
      } else {
        return ''
      }
    },
    // 获取表格数据
    getTableData() {
      const data = {
        pageNumber: this.pager.pageNumber,
        pageSize: this.pager.pageSize,
        roleName: this.keyword
      }
      API.getList(data).then(res => {
        if (res.code === 200) {
          this.tableData = res.rows
          // 循环遍历表格数据拿到下面的permissionList
          this.tableData.forEach(tableItem => {
            // 定义一个新数组用来放处理好的数据  (表格里的permissionList)
            const permissionList = []
            // 遍历多选框的值
            this.checkboxData.forEach(item => {
              // 将多选框的值加入一个新对象里
              const permissionItem = Object.assign({}, item)
              // 如果表格数据里的permissionStrList里的ID === 多选框里的id   将表格里的hasPermission变为true  反之为false
              if (tableItem.permissionStrList.find(permissionItem => permissionItem.id === item.id)) {
                permissionItem.hasPermission = true
              } else {
                permissionItem.hasPermission = false
              }
              // 将处理好的数据放入新数组里
              permissionList.push(permissionItem)
            })
            // 将组装好的permissionList  赋值给表格里的permissionStrList
            tableItem.permissionStrList = permissionList
          })
          this.pager.total = res.total
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
      this.getTableData
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
    height: calc(100vh - 240px);
  }
  .pager-section{
    text-align: right;
    padding: 20px 0;
  }
}
</style>
