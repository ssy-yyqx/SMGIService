<template>
  <div class="dashboard-paper" :class="{'white-bg': isWhiteBg}">
    <div class="dashboard-paper-container">
      <div v-if="title" class="dashboard-paper-header">
        <div class="dashboard-paper-header__image" />
        <div class="dashboard-paper-header__title">
          {{ title }}
        </div>
        <div class="dashboard-paper-header__seperate" />
      </div>
      <div class="dashboard-paper-content">
        <slot />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    title: {
      type: String,
      default: ''
    },
    isWhiteBg: {
      type: Boolean,
      default: false
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-paper{
  width: 100%;
  background: #F9F9F9;
  .dashboard-paper-container{
    width: 1400px;
    margin: 0 auto;
    .dashboard-paper-header{
      &__image{
        height: 60px;
        width: 100%;
        background: url(../../../assets/images/paper-header.png) no-repeat center center;
      }
      &__title{
        text-align: center;
        color: #0454CF;
        font-size: 24px;
        font-weight: bold;
        position: relative;
      }
      &__seperate{
        margin-top: 20px;
        width: 30px;
        height: 4px;
        background: #DD180F;
        margin-left: calc(50% - 15px);
      }
    }
    .dashboard-paper-content{
      width: 100%;
      padding: 20px 0;
    }
  }
}
.white-bg{
  background: #FFFFFF;
}
</style>
