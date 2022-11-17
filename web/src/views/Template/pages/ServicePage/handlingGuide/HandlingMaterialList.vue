<template>
  <div class="material-list-section">
    <el-table
      :data="tableData"
      header-cell-class-name="table-header"
      style="width: 100%"
    >
      <el-table-column
        prop="num"
        label="序号"
        width="180"
      />
      <el-table-column
        prop="name"
        label="材料名称"
        width="180"
      />
      <el-table-column
        prop="example"
        label="材料填写样本"
      />
      <el-table-column
        prop="from"
        label="来源渠道"
        width="180"
      />
      <el-table-column
        prop="type"
        label="材料形式"
        width="180"
      />
      <el-table-column
        prop="essensial"
        label="材料必要性"
      />
    </el-table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tableData: [{
        num: 1,
        name: '材料1',
        example: '材料1例子',
        from: '来源',
        type: '文件',
        essensial: '必须'
      }, {
        num: 2,
        name: '材料1',
        example: '材料1例子',
        from: '来源',
        type: '文件',
        essensial: '必须'
      }, {
        num: 3,
        name: '材料1',
        example: '材料1例子',
        from: '来源',
        type: '文件',
        essensial: '必须'
      }]
    }
  }
}
</script>

<style lang="scss">
.table-header{
  background: #FAFAFA !important;
}
</style>
