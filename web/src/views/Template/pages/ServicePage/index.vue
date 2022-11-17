<template>
  <Paper :title="title">
    <div slot="opera">
      <span class="opera-item" @click="gotoIntroduce">介绍</span>
      <span class="opera-item" @click="gotoHandling">办理指南</span>
      <span class="opera-item" @click="gotoOnlineHandling">{{ routerData.handleType === 1 ? '在线办理' : '预约线下' }}</span>
      <span v-if="headerInfo.collect" class="opera-item" @click="gotoCancelCollect">取消收藏（{{ headerInfo.collectCount }}）</span>
      <span v-else class="opera-item" @click="gotoCollect">收藏（{{ headerInfo.collectCount }}）</span>
      <span v-if="headerInfo.support" class="opera-item" @click="gotoCancelPraise">取消点赞（{{ headerInfo.supportCount }}）</span>
      <span v-else class="opera-item" @click="gotoPraise">点赞（{{ headerInfo.supportCount }}）</span>
      <span class="opera-item" @click="gotoComment">评论（{{ headerInfo.commentCount }}）</span>
    </div>
    <div slot="content">
      <Introduce v-if="activeTab === 'introduce' && introduceData" :page-data="introduceData" />
      <HandlingGuide v-if="activeTab === 'handlingGuide' && headerInfo.guideFile" :page-data="headerInfo.guideFile" />
      <Comment v-if="activeTab === 'comment'" :module-id="moduleId" />
    </div>
  </Paper>
</template>

<script>
import Introduce from './introduce/index.vue'
import HandlingGuide from './handlingGuide/index.vue'
import Comment from '../../components/comment/index.vue'
import Praise from '../../mixins/praise'
import Collect from '../../mixins/collect'
import * as API from '@/api/content'
export default {
  components: { Introduce, HandlingGuide, Comment },
  mixins: [Praise, Collect],
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
    // 获取点赞、收藏等信息
    this.getPraiseAndCollectInfo()
    // 获取页面数据
    this.getServicePageData()
  },
  methods: {
    // 获取点赞和收藏的信息
    getPraiseAndCollectInfo() {
      API.getPraiseAndCollectInfo({ id: this.moduleId }).then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          this.headerInfo = data
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 介绍页面
    gotoIntroduce() {
      this.activeTab = 'introduce'
    },
    // 办理指南页面
    gotoHandling() {
      this.activeTab = 'handlingGuide'
    },
    // 在线办理页面
    gotoOnlineHandling() {
      this.$router.push({ name: this.headerInfo.redirectUrl })
    },
    // 评论
    gotoComment() {
      this.activeTab = 'comment'
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
      API.getServicePageData(this.moduleId).then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          this.setServiceData(data)
        } else {
          this.$message.error(msg)
        }
      })
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
