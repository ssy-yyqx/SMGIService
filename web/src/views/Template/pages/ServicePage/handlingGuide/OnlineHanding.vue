<template>
  <div class="handling-section">
    <div class="search-section">
      <el-input v-model="input3" placeholder="请输入数据标识" class="input-with-select">
        <el-button slot="append" icon="el-icon-search" />
      </el-input>
    </div>
    <div class="table-section">
      <el-table
        :data="tableData"
        header-cell-class-name="table-header"
        style="width: 100%"
      >
        <el-table-column
          label="数据标识"
          prop="dataMarker"
        />
        <el-table-column
          label="日期"
          prop="date"
        />
        <el-table-column
          label="缩略图"
          prop="thumbnail"
        />
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleView(scope.$index, scope.row)"
            >详情</el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDownload(scope.$index, scope.row)"
            >下载</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tableData: [
        {
          dataMarker: '全国大地控制点数据',
          date: '2021-05-20',
          thumbnail: '1'
        },
        {
          dataMarker: '陕西省大地控制点数据',
          date: '2021-05-21',
          thumbnail: '2'
        }
      ]
    }
  }
}
</script>
<style lang="scss" scoped>
.search-section{
  width: 500px;
  margin-bottom: 10px;
}
</style>
<style lang="scss">
.table-header{
  background: #FAFAFA !important;
}
</style>
