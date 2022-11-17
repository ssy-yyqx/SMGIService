<template>
  <div class="flow-section">
    <el-steps direction="vertical" :space="50">
      <el-step v-for="(item,index) in steps" :key="index" :title="item" />
    </el-steps>
  </div>
</template>

<script>
export default {
  data() {
    return {
      steps: [
        '进入测绘及地理空间中心官网',
        '站内导航模块',
        '点击“大地控制点”按钮',
        '点击办事指南或在线办理等对应按钮',
        '在站内导航模块查看服务的办理流程、若需要在线办理，点击“在线办理”，进入特色服务模块进行在线办理业务。',
        '点击“在线办理”，登陆账户',
        '选择要下载的数据',
        '办理完成'
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
.flow-section{
  background: #FAFAFA;
  padding: 20px 30px;
  ::v-deep .el-step__line{
    background-color: #0454CF;
  }
  ::v-deep .el-step__head.is-wait {
    color: #FFFFFF;
    border-color: #0454CF;
  }
  ::v-deep .el-step__icon{
    background: #0454CF;
  }
  ::v-deep .el-step__title.is-wait{
    color: #313131;
    font-weight: bold;
    font-size: 14px;
  }
}
</style>
