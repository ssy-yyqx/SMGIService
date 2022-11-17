<template>
  <div class="base-info-section">
    <table class="table">
      <tr class="table-tr">
        <td class="table-label">服务介绍</td>
        <td class="table-value" colspan="3">{{ baseInfo.serviceIntroduce }}</td>
      </tr>
      <tr>
        <td class="table-label">数据详情</td>
        <td class="table-value" colspan="3">{{ baseInfo.dataDetail }}</td>
      </tr>
      <tr>
        <td class="table-label">主体单位</td>
        <td class="table-value">{{ baseInfo.subjectUnit }}</td>
        <td class="table-label">分管部门</td>
        <td class="table-value">{{ baseInfo.chartUnit }}</td>
      </tr>
      <tr>
        <td class="table-label">密级</td>
        <td class="table-value">{{ baseInfo.serviceIntroduce }}</td>
        <td class="table-label">办理方式</td>
        <td class="table-value">{{ baseInfo.handleMethod }}</td>
      </tr>
      <tr>
        <td class="table-label">咨询方式</td>
        <td class="table-value" colspan="3">{{ baseInfo.askMode }}</td>
      </tr>
      <tr>
        <td class="table-label">办公时间</td>
        <td class="table-value" colspan="3">{{ baseInfo.officeHours }}。</td>
      </tr>
      <tr>
        <td class="table-label">办公地点</td>
        <td class="table-value" colspan="3">{{ baseInfo.officeAddress }}</td>
      </tr>
      <tr>
        <td class="table-label">数据目录</td>
        <td class="table-value" colspan="3">{{ baseInfo.dataCategory }}</td>
      </tr>
      <tr>
        <td class="table-label">数据缩略图</td>
        <td class="table-value" colspan="3">{{ baseInfo.dataThumbnail }}</td>
      </tr>
    </table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      baseInfo: {
        serviceIntroduce: '为用户提供大地控制点数据的检索、下载服务。',
        dataDetail: '坐标：WGS84；数据格式：csv、shp',
        subjectUnit: '测绘及空间地理空间中心',
        chartUnit: '三室',
        secretLevel: '公开',
        handleMethod: '线上',
        askMode: '电话咨询：123456    邮件咨询：123456@163.com',
        officeHours: '法定工作日： 8:00-12:00；13:00-18:00',
        officeAddress: '陕西省西安市飞天路25号',
        dataCategory: '数据目录',
        dataThumbnail: ''
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.base-info-section{
  table,table tr th, table tr td{
    border:1px solid #EAECEF;
  }
  .table{
     width: 100%;
     min-height: 50px;
     line-height: 50px;
     text-align: left;
     border-collapse: collapse;
  }
  .table-label{
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
</style>
