<template>
  <div
    class="footer-container"
    :class="{ 'footer-position-bottom': hasLeftMenu }"
  >
    <span class="left-info">
      <span class="info-item link" @click="gotoCopyRight">版权声明</span> |
      <span class="info-item link" @click="gotoOurIntroduce">关于我们</span>
    </span>
    <span class="right-info">
      <span class="info-item">电话: 010-66720068 </span>
      <span class="info-item">邮箱：xwzbz@sina.com</span>
    </span>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  computed: {
    ...mapGetters(['leftMenu']),
    hasLeftMenu() {
      return this.leftMenu.length > 0
    }
  },
  methods: {
    // 跳转至版权声明
    gotoCopyRight() {
      this.$message.warning('建设中。。。')
    },
    // 跳转至关于我们
    gotoOurIntroduce() {
      this.$message.warning('建设中。。。')
    }
  }
}
</script>

<style lang="scss" scoped>
.footer-container {
  margin-top: 20px;
  width: 100%;
  height: 60px;
  line-height: 60px;
  background: linear-gradient(90deg, #113d97, #4cbbee);
  text-align: center;
  color: #ffffff;
  font-size: 18px;
  .left-info {
    margin-right: 40px;
    .link {
      cursor: pointer;
    }
  }
  .info-item {
    padding: 0 5px;
  }
}
.footer-position-bottom {
  // position: fixed;
  // bottom: 0px;
}
</style>
