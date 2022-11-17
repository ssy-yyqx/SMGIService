<template>
  <div class="left-nav">
    <div class="left-title">
      <i class="icon" />
      <span class="title">{{ activeMenuName }}</span>
    </div>
    <el-menu
      :default-active="activeMenu"
      class="el-menu-vertical-demo"
      background-color="#FFFFFF"
      text-color="#333"
      active-text-color="#0454CF"
      @open="handleOpen"
      @close="handleClose"
      @select="handleSelect"
    >
      <LeftNavItem v-for="(item) in leftMenu" :key="item.routerName" :item="item" />
    </el-menu>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import LeftNavItem from './LeftNavItem'
export default {
  name: 'LeftNav',
  components: { LeftNavItem },
  data() {
    return {
    }
  },
  computed: {
    ...mapGetters([
      'leftMenu',
      'activeMenuName'
    ]),
    activeMenu() {
      return this.$route.name
    }
  },
  created() {
    // console.log('左侧菜单：', this.leftMenu)
  },
  methods: {
    handleOpen(key, keyPath) {
      console.log(key, keyPath)
    },
    handleClose(key, keyPath) {
      console.log(key, keyPath)
    },
    handleSelect(key, keyPath) {
      this.$router.push({ name: key })
    }
  }
}
</script>

<style lang="scss" scoped>
  .left-nav{
    width: 240px;
    height: 100%;
    background: #FFFFFF;
    border: 1px solid #D6D6D6;
    color: #313131;
    .left-title{
      height: 60px;
      line-height: 60px;
      font-size: 18px;
      padding: 0 20px;
      border-bottom: 1px solid #D6D6D6;
      .icon{
        width: 4px;
        height: 0px;
        border-top: 9px solid #0454CF;
        border-bottom: 9px solid #DD180F;
        display: inline-block;
        margin-bottom: -3px;
        border-radius: 2px;
      }
      .title{
        margin-left: 6px;
      }
    }
  }
  .el-menu{
    height: calc(100% - 60px);
    border-right: 0;
    overflow-y: auto;
  }
</style>
