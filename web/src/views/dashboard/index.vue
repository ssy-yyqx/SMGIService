<template>
  <div class="dashboard-section">
    <!-- <Carousels /> -->
    <News :list="infos.newList" />
    <Notice :list="infos.noticeList" />
    <CenterIntroduce :list="infos.centerList" />
    <Navigation />
    <SpecialService />
    <RelativeLink />
  </div>
</template>

<script>
import Carousels from './Carousels.vue'
import News from './News.vue'
import Notice from './Notice.vue'
import CenterIntroduce from './CenterIntroduce.vue'
import Navigation from './Navigation.vue'
import SpecialService from './SpecialService.vue'
import RelativeLink from './RelativeLink.vue'
import * as API from '@/api/dashboard'
export default {
  name: 'Dashboard',
  components: { Carousels, News, Notice, CenterIntroduce, Navigation, SpecialService, RelativeLink },
  data() {
    return {
      infos: {}
    }
  },
  created() {
    this.getNewsAndNoticeInfo()
  },
  methods: {
    // 获取新闻和公告信息
    getNewsAndNoticeInfo() {
      API.getNewsAndNoticeInfo().then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          this.infos = data
        } else {
          this.$message.error(msg)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-section{
  min-height: 1000px;
  .top-section{
    display: flex;
    justify-content: space-between;
    &__left{
      width: 400px;
    }
    &__middle{
      flex: 1;
      display: flex;
      align-items: center;
      margin: 0 5px;
      justify-content: center;
      background-color: #FFF;
    }
    &__right{
      width: 400px;
    }
  }
  .bottom-section{
    margin-top: 10px;
  }
}
</style>
