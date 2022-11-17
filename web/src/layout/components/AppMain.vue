<template>
  <div
    class="main-container"
    :class="{ 'main-container-dashboard': !hasLeftMenu }"
  >
    <section :class="hasLeftMenu ? 'app-main' : 'app-main-dashboard'">
      <LeftNav v-if="hasLeftMenu" />
      <div
        id="RightSection"
        class="right-section"
        :class="{ 'right-section-padding': hasLeftMenu }"
      >
        <transition name="fade-transform" mode="out-in">
          <router-view :key="key" />
        </transition>
        <OnlineDialog />
      </div>
    </section>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import LeftNav from './LeftNav/index'
import OnlineDialog from '@/views/OnlineDialog/index.vue'
export default {
  name: 'AppMain',
  components: { LeftNav, OnlineDialog },
  computed: {
    ...mapGetters(['leftMenu']),
    key() {
      return this.$route.path
    },
    hasLeftMenu() {
      return this.leftMenu.length > 0
    }
  }
}
</script>

<style lang="scss" scoped>
.main-container {
  width: 100%;
  overflow-y: auto;
  width: 1400px;
  margin: 0 auto;
  min-height: calc(100% - 332px);
}
.main-container-dashboard {
  width: 100%;
}
.app-main {
  width: 1400px;
  display: flex;
  margin-top: 20px;
}
.app-main-dashboard {
  width: 100%;
}
.right-section {
  flex: 1;
  overflow-x: hidden;
}
.right-section-padding {
  padding-left: 20px;
  height: 100%;
  overflow-y: auto;
}
</style>
