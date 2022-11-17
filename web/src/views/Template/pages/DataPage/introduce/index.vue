<template>
  <div class="introduce">
    <div class="top-section">
      <div class="left-section">
        <el-image
          style="width: 100%; height: 100%"
          :src="MapImg"
          fit="fit"
        />
      </div>
      <div class="right-section">
        <table class="table">
          <tr class="table-tr">
            <td class="table-label">成果名称</td>
            <td class="table-value">全国大地控制点数据</td>
          </tr>
          <tr>
            <td class="table-label">成果描述</td>
            <td class="table-value">数据为全国大地控制数据，为用户提供数据检索、下载等服务。</td>
          </tr>
          <tr class="table-tr">
            <td class="table-label">数据格式</td>
            <td class="table-value">XXXX</td>
          </tr>
          <tr>
            <td class="table-label">保密单位</td>
            <td class="table-value">XXXX</td>
          </tr>
          <tr class="table-tr">
            <td class="table-label">密级</td>
            <td class="table-value">XXXX</td>
          </tr>
          <tr>
            <td class="table-label">测制年代</td>
            <td class="table-value">XXXX</td>
          </tr>
          <tr class="table-tr">
            <td class="table-label">区域</td>
            <td class="table-value">XXXX</td>
          </tr>
          <tr>
            <td class="table-label">坐标系统</td>
            <td class="table-value">XXXX</td>
          </tr>
          <tr class="table-tr">
            <td class="table-label">高程基准</td>
            <td class="table-value">XXXX</td>
          </tr>
          <tr>
            <td class="table-label">数据量</td>
            <td class="table-value">XXXX</td>
          </tr>
          <tr class="table-tr">
            <td class="table-label">比例尺</td>
            <td class="table-value">XXXX</td>
          </tr>
        </table>
      </div>
    </div>
    <div class="bottom-section">
      <el-table
        :data="tableData"
        header-cell-class-name="table-header"
        border
        style="width: 100%"
      >
        <el-table-column
          label="编号"
          type="index"
          width="50"
        />
        <el-table-column prop="name" label="成果名称" align="center" />
        <el-table-column prop="type" label="成果格式" align="center" />
        <el-table-column prop="time" label="生产时间" align="center" />
        <el-table-column prop="level" label="密级" align="center" />
        <el-table-column prop="company" label="保管单位" align="center" />
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleDetail(scope.$index, scope.row)"
            >详细</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog
      title="详细信息"
      :visible.sync="dialogVisible"
      width="30%"
    >
      <div class="detail-info-section">
        <div class="info-item">
          <span class="info-item-label">成果名称：</span>
          <span class="info-item-value">{{ detailInfo.name }}</span>
        </div>
        <div class="info-item">
          <span class="info-item-label">成果格式：</span>
          <span class="info-item-value">{{ detailInfo.type }}</span>
        </div>
        <div class="info-item">
          <span class="info-item-label">生产时间：</span>
          <span class="info-item-value">{{ detailInfo.time }}</span>
        </div>
        <div class="info-item">
          <span class="info-item-label">密级：</span>
          <span class="info-item-value">{{ detailInfo.level }}</span>
        </div>
        <div class="info-item">
          <span class="info-item-label">保管单位：</span>
          <span class="info-item-value">{{ detailInfo.company }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import MapImg from '@/assets/images/map.jpg'
export default {
  data() {
    return {
      // 地图图片
      MapImg: MapImg,
      // 表格数据
      tableData: [
        {
          name: '陕西省大地控制点数据',
          type: 'shp',
          time: '2020-01-01',
          level: '公开',
          company: 'XXX'
        },
        {
          name: '陕西省大地控制点数据',
          type: 'shp',
          time: '2020-01-01',
          level: '公开',
          company: 'XXX'
        },
        {
          name: '陕西省大地控制点数据',
          type: 'shp',
          time: '2020-01-01',
          level: '公开',
          company: 'XXX'
        },
        {
          name: '陕西省大地控制点数据',
          type: 'shp',
          time: '2020-01-01',
          level: '公开',
          company: 'XXX'
        },
        {
          name: '陕西省大地控制点数据',
          type: 'shp',
          time: '2020-01-01',
          level: '公开',
          company: 'XXX'
        }
      ],
      // 弹窗是否显示
      dialogVisible: false,
      // 详细信息
      detailInfo: {}
    }
  },
  methods: {
    // 点击详细按钮
    handleDetail(index, data) {
      this.dialogVisible = true
      this.detailInfo = data
    }
  }
}
</script>

<style lang="scss" scoped>
.introduce{
  .top-section{
    display: flex;
    // height: 400px;
    .left-section{
      width: 400px;
      height: 100%;
    }
    .right-section{
      margin-left: 20px;
      width: calc(100% - 420px);
      table,table tr th, table tr td{
        border:1px solid #EAECEF;
      }
      .table{
        width: 100%;
        min-height: 32px;
        line-height: 32px;
        text-align: left;
        border-collapse: collapse;
      }
      .table-label{
        width: 100px;
        font-size: 14px;
        font-weight: bold;
        color: #24292E;
        text-align: center;
        background: #FAFAFA;
      }
      .table-value{
        font-size: 14px;
        font-weight: 400;
        color: #313131;
        padding: 0 10px;
      }
    }
  }
  .bottom-section{
    width: 100%;
    margin-top: 20px;
  }
}
.table-header {
  background: #fafafa !important;
}
.detail-info-section{
  .info-item{
    font-size: 16px;
    line-height: 30px;
  }
}
</style>
