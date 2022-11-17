<template>
  <div class="introduce-container">
    <div class="left-section">
      <el-image
        style="width: 100%; height: 100%"
        :src="pageData.imageUrl"
        fit="fit"
      />
    </div>
    <div class="right-section">
      <el-tabs v-model="activeName" type="card">
        <el-tab-pane v-for="item in pageData.tabs" :key="item.id" :label="item.tabName" :name="item.id">
          <component :is="item.component" :tab-data="item.tabData" />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import Introduce from './Introduce.vue'
import Interface from './Interface.vue'
import CommonQuestions from './CommonQuestions.vue'
import Cases from './Cases.vue'
export default {
  components: { Introduce, Interface, CommonQuestions, Cases },
  props: {
    pageData: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data() {
    return {
      activeName: ''
    }
  },
  created() {
    this.activeName = this.pageData.tabs[0].id
    // 数据设置
    this.pageData.tabs.forEach(item => {
      // 服务简介
      if (item.contentType === 1) {
        item.component = 'Introduce'
        item.tabData = item.content
      } else if (item.contentType === 2) {
        // 接口说明
        item.component = 'Interface'
        item.tabData = item.content
      } else if (item.contentType === 3) {
        // 常见问题
        item.component = 'CommonQuestions'
        item.tabData = item.tabInfoList
      } else if (item.contentType === 4) {
        // 使用案例
        item.component = 'Cases'
        item.tabData = item.tabInfoList
      }
    })
  }
}
</script>

<style lang="scss" scoped>
.introduce-container{
  display: flex;
  .left-section{
    width: 400px;
    height: 100%;
  }
  .right-section{
    margin-left: 20px;
    width: calc(100% - 420px);
  }
}
</style>
