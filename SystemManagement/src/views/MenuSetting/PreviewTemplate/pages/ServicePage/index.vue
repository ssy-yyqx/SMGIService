<template>
  <Paper :title="title">
    <div slot="opera">
      <span class="opera-item" @click="gotoIntroduce">介绍</span>
      <span class="opera-item" @click="gotoHandling">办理指南</span>
      <span class="opera-item">{{ routerData.handleType === 1 ? '在线办理' : '预约线下' }}</span>
      <span class="opera-item">收藏（0）</span>
      <span class="opera-item">点赞（0）</span>
      <span class="opera-item">评论（0）</span>
    </div>
    <div slot="content">
      <Introduce v-if="activeTab === 'introduce' && introduceData" :page-data="introduceData" />
      <HandlingGuide v-if="activeTab === 'handlingGuide' && headerInfo.guideFile" :page-data="headerInfo.guideFile" />
    </div>
  </Paper>
</template>

<script>
import Introduce from './introduce/index.vue'
import HandlingGuide from './handlingGuide/index.vue'
export default {
  components: { Introduce, HandlingGuide },
  props: {
    contentData: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data() {
    return {
      // 标题
      title: '',
      // 菜单id
      moduleId: '',
      // 菜单路由
      routerName: '',
      // 当前选中的tab
      activeTab: 'introduce',
      // 头部信息
      headerInfo: {},
      // 介绍页面数据
      introduceData: null,
      // 办理指南数据
      guideData: null,
      // 路由跳转数据
      routerData: {}
    }
  },
  watch: {
    $route: {
      handler: function(val) {
        const { title, id, router } = val.meta
        this.title = title
        this.moduleId = id
        this.routerName = router
        if (val.params.transferTab) {
          this.activeTab = val.params.transferTab
        }
      },
      // 深度观察监听
      deep: true,
      immediate: true
    }
  },
  created() {
    // 获取页面数据
    this.getServicePageData()
  },
  methods: {
    // 介绍页面
    gotoIntroduce() {
      this.activeTab = 'introduce'
    },
    // 办理指南页面
    gotoHandling() {
      this.activeTab = 'handlingGuide'
    },
    // 设置服务数据
    setServiceData(data) {
      // 介绍页面数据
      this.introduceData = {
        imageUrl: data.imageUrl,
        tabs: data.tabList
      }
      // 办理指南数据
      this.guideData = {
        guideFileId: data.guideFileId,
        guideFileData: data.guideFileData
      }
      // 路由跳转数据
      this.routerData = {
        handleType: data.handleType,
        redirectModuleId: data.redirectModuleId,
        redirectUrl: data.redirectUrl
      }
    },
    // 获取服务页面数据
    getServicePageData() {
      this.setServiceData(this.contentData)
    }
  }
}
</script>

<style lang="scss" scoped>
.opera-item{
  cursor: pointer;
  padding-left: 10px;
  font-size: 16px;
  color: #0454CF;
}
</style>
