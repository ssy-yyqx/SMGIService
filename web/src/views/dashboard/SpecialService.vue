<template>
  <Paper title="特色服务" :is-white-bg="true">
    <div class="tab-bar-section">
      <div v-for="(item, index) in tabs" :key="index" class="tab-item" :class="{'tab-item-active': item.id === currentTab}" @click="onHandleClickTab(item)">
        {{ item.name }}
      </div>
    </div>
    <div class="tab-content-section">
      <div v-for="item in contents" :key="item.id" class="tab-content-item" @click="gotoSpecialPage(item)">
        <i />
        <span>{{ item.name }}</span>
      </div>
    </div>
  </Paper>
</template>

<script>
import Paper from './components/Paper.vue'
import menuJson from '@/api/menu.json'
export default {
  components: { Paper },
  data() {
    return {
      currentTab: '6_1'
    }
  },
  computed: {
    tabs() {
      return menuJson.find(item => item.id === '6').children
    },
    contents() {
      return this.tabs.find(item => item.id === this.currentTab).children
    }
  },
  methods: {
    onHandleClickTab(data) {
      this.currentTab = data.id
    },
    // 跳转到特色服务页面
    gotoSpecialPage(data) {
      this.$router.push({ name: data.routerName })
    }
  }
}
</script>

<style lang="scss" scoped>
.tab-bar-section{
  width: 100%;
  height: 70px;
  background: #F5F5F5;
  .tab-item{
    height: 70px;
    line-height: 70px;
    padding: 0 37px;
    display: inline-block;
    text-align: center;
    font-weight: bold;
    color: #313131;
    font-size: 18px;
    cursor: pointer;
    &-active{
      color: #FFFFFF;
      background: #0454CF;
    }
  }
}
.tab-content-section{
  width: 100%;
  padding: 20px 20px 20px 10px;
  .tab-content-item{
    width: 230px;
    margin-left: 10px;
    display: inline-block;
    cursor: pointer;
    i{
      height: 10px;
      width: 10px;
      margin-right: 5px;
      display: inline-block;
      background:linear-gradient(to left,#0454CF,#0454CF) no-repeat left top,
                linear-gradient(to left,#DD180F,#DD180F) no-repeat right top,
                linear-gradient(to left,#DD180F,#DD180F) no-repeat left bottom,
                linear-gradient(to left,#0454CF,#0454CF) no-repeat right bottom,
              #2E8B57;
      background-size:5px 5px,5px 5px,5px 5px,5px 5px;
    }
    span{
      color: #313131;
    }
  }
}
</style>
