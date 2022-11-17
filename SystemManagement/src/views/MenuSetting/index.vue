<template>
  <div class="menu-setting-section">
    <div class="toolbar-section">
      <el-button type="primary" @click="onHandleAddMenu">新增</el-button>
    </div>
    <div class="table-section">
      <el-table
        :data="tableData"
        style="width: 100%;margin-bottom: 20px;"
        row-key="id"
        border
        default-expand-all
        :tree-props="{children: 'childrenList', hasChildren: 'hasChildren'}"
        height="100%"
      >
        <el-table-column
          prop="moduleName"
          label="栏目"
          sortable
          width="260"
        />
        <el-table-column
          prop="id"
          label="Id"
          sortable
          width="180"
        />
        <el-table-column
          prop="templateType"
          label="模型"
        />
        <el-table-column
          prop="router"
          label="路由"
        />
        <el-table-column
          prop="sort"
          label="顺序"
        >
          <template slot-scope="scope">
            <el-button icon="el-icon-top" circle @click="gotoTop(scope.row)" />
            <el-button icon="el-icon-bottom" circle @click="gotoBottom(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column
          prop="isShow"
          label="是否显示"
        >
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.isShow"
              @change="onChangeShow(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          label="添加子栏目"
        >
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.moduleType != 2"
              size="mini"
              type="primary"
              @click="handleAddSubMenu(scope.$index, scope.row)"
            >添加</el-button>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
        >
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
  </div>
</template>

<script>
import * as API from '@/api/MenuSetting'
const formDataInit = {
  moduleName: '',
  templateType: '',
  router: '',
  sort: '',
  isShow: false,
  isLink: false,
  linkUrl: '',
  parentModuleId: '',
  moduleType: false
}
export default {
  data() {
    return {
      // 表格数据
      tableData: [],
      // 是否显示弹窗
      dialogVisible: false,
      // 弹窗标题
      title: '',
      // 表单数据
      formData: Object.assign({}, formDataInit)
    }
  },
  created() {
    this.getMenuList()
  },
  methods: {
    deepSetData(data) {
      data.forEach(item => {
        item.isShow = String(item.isShow) === '1'
        if (item.childrenList) {
          this.deepSetData(item.childrenList)
        }
      })
    },
    // 获取栏目数据
    getMenuList() {
      API.getTableList().then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          this.deepSetData(data)
          this.tableData = data
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 添加主菜单
    onHandleAddMenu() {
      this.$router.push({ name: 'MenuAdd' })
    },
    // 添加子栏目
    handleAddSubMenu(index, row) {
      this.$router.push({ name: 'MenuAdd', query: { id: row.id }})
    },
    // 修改
    handleEdit(index, row) {
      this.$router.push({ name: 'MenuEdit', query: row })
    },
    // 删除
    handleDelete(index, row) {
      this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        API.deleteMenu({ id: row.id }).then(res => {
          const { code, msg } = res
          if (String(code) === '200') {
            this.getMenuList()
            this.$message({
              type: 'success',
              message: '删除成功!'
            })
          } else {
            this.$message.error(msg)
          }
        })
      })
    },
    // 修改显隐性
    onChangeShow(row) {
      API.setMenuShow({
        isShow: row.isShow ? 1 : 2,
        moduleId: row.id
      }).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.$message.success('设置成功！')
          this.getMenuList()
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 上移
    gotoTop(row) {
      API.sortMenu({
        sort: 'up',
        moduleId: row.id
      }).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.$message.success('上移成功！')
          this.getMenuList()
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 下移
    gotoBottom(row) {
      API.sortMenu({
        sort: 'down',
        moduleId: row.id
      }).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.$message.success('下移成功！')
          this.getMenuList()
        } else {
          this.$message.error(msg)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.menu-setting-section{
  height: 100%;
  .toolbar-section{
    margin: 10px 0;
  }
  .table-section{
    height: calc(100% - 60px);
  }
}
</style>
