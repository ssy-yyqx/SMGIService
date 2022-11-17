<template>
  <Paper :title="title">
    <div slot="opera">
      <span class="opera-item" @click="gotoIntroduce">介绍</span>
      <span class="opera-item" @click="gotoNavigation">业务导航</span>
      <span class="opera-item" @click="gotoHandling">办理指南</span>
      <span class="opera-item" @click="gotoApply">我要申请</span>
      <span v-if="headerInfo.collect" class="opera-item" @click="gotoCancelCollect">取消收藏（{{ headerInfo.collectCount }}）</span>
      <span v-else class="opera-item" @click="gotoCollect">收藏（{{ headerInfo.collectCount }}）</span>
      <span v-if="headerInfo.support" class="opera-item" @click="gotoCancelPraise">取消点赞（{{ headerInfo.supportCount }}）</span>
      <span v-else class="opera-item" @click="gotoPraise">点赞（{{ headerInfo.supportCount }}）</span>
      <span class="opera-item" @click="gotoComment">评论（{{ headerInfo.commentCount }}）</span>
    </div>
    <div slot="content">
      <template v-if="activeTab === 'introduce'">
        <component :is="component" />
      </template>
      <HandlingGuide v-if="activeTab === 'handlingGuide' && headerInfo.guideFile" :page-data="headerInfo.guideFile" />
      <Comment v-if="activeTab === 'comment'" :module-id="moduleId" />
      <el-dialog
        title="我要申请"
        :visible.sync="dialogVisible"
        :close-on-click-modal="false"
        width="30%"
      >
        <Apply ref="apply" :dialog-visible="dialogVisible" />
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="onHandleApply">确 定</el-button>
        </span>
      </el-dialog>
    </div>
  </Paper>
</template>

<script>
import { mapGetters } from 'vuex'
import HandlingGuide from './handlingGuide/index.vue'
import Comment from '../../components/comment/index.vue'
import SpecialService6_1_1 from './services/SpecialService6_1_1'
import SpecialService6_1_2 from './services/SpecialService6_1_2'
import SpecialService6_2_1 from './services/SpecialService6_2_1'
import Apply from './apply'
import Praise from '../../mixins/praise'
import Collect from '../../mixins/collect'
import * as API from '@/api/content'
export default {
  components: { HandlingGuide, Comment, SpecialService6_1_1, SpecialService6_1_2, SpecialService6_2_1, Apply },
  mixins: [Praise, Collect],
  data() {
    return {
      title: '',
      // 菜单id
      moduleId: '',
      // 菜单路由
      routerName: '',
      // 当前选中的tab
      activeTab: 'introduce',
      // 头部信息
      headerInfo: {},
      // 我要申请弹窗
      dialogVisible: false
    }
  },
  computed: {
    ...mapGetters(['token'])
  },
  watch: {
    $route: {
      handler: function(val) {
        this.component = val.name
        const { title, id, router } = val.meta
        this.title = title
        this.moduleId = id
        this.routerName = router
      },
      // 深度观察监听
      deep: true,
      immediate: true
    }
  },
  created() {
    // 获取点赞、收藏等信息
    this.getPraiseAndCollectInfo()
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
    // 跳转到业务导航
    gotoNavigation() {
      this.$router.push({ name: this.headerInfo.redirectUrl })
    },
    // 介绍页面
    gotoIntroduce() {
      this.activeTab = 'introduce'
    },
    // 办理指南页面
    gotoHandling() {
      this.activeTab = 'handlingGuide'
    },
    // 我要申请
    gotoApply() {
      if (!this.token) {
        this.$store.dispatch('user/setLoginDialog', true)
      } else {
        this.dialogVisible = true
      }
    },
    // 确定申请
    onHandleApply() {
      const data = this.$refs.apply.getFormData()
      if (!data.dataId) {
        this.$message.warning('请先上传文件！')
        return
      }
      API.applyService({
        approveFileId: data.dataId,
        moduleId: this.moduleId
      }).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.$message.success('审核材料已提交成功！您可在【个人中心】查看审核结果并预约线下办理。')
        } else {
          this.$message.error(msg)
        }
      })
      this.dialogVisible = false
    },
    // 评论
    gotoComment() {
      this.activeTab = 'comment'
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
.red{
  margin-left: 10px;
  color: #DD180F;
  font-size: 14px;
}
.seperate{
  width: 100%;
  height: 1px;
  background: #D6D6D6;
  margin: 20px 0;
}
.button-section{
  padding: 20px 0;
  text-align: right;
}
.result-section{
  .title{
    font-size: 16px;
    color: #313131;
    margin-bottom: 10px;
  }
  .content{
    line-height: 24px;
    font-size: 14px;
    color: #0454CF;
  }
}
</style>

