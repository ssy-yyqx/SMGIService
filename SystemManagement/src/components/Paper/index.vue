<template>
  <div class="paper-container">
    <div class="paper-header-section">
      <div class="title">
        <i class="icon" />
        <span>{{ title }}</span>
      </div>
      <div class="opera">
        <slot name="opera" />
      </div>
    </div>
    <div class="paper-content-section">
      <slot name="content" />
    </div>
  </div>
</template>

<script>
export default {
  props: {
    title: {
      type: String,
      default: '标题'
    }
  }
}
</script>

<style lang="scss" scoped>
  .paper-container{
    height: 100%;
    // background: #ffffff;
    border-radius: 5px;
    color: #24292E;
    .paper-header-section{
      height: 50px;
      line-height: 40px;
      display: flex;
      border-bottom: 1px solid #D6D6D6;
      justify-content: space-between;
      .title{
        font-size: 18px;
        position: relative;
        display: flex;
        align-items: center;
        i{
          height: 16px;
          width: 16px;
          margin-right: 5px;
          display: inline-block;
          background:linear-gradient(to left,#0454CF,#0454CF) no-repeat left top,
                    linear-gradient(to left,#DD180F,#DD180F) no-repeat right top,
                    linear-gradient(to left,#DD180F,#DD180F) no-repeat left bottom,
                    linear-gradient(to left,#0454CF,#0454CF) no-repeat right bottom,
                  #2E8B57;
          background-size:8px 8px,8px 8px,8px 8px,8px 8px;
        }
        &::after{
          position: absolute;
          content: ' ';
          height: 2px;
          width: 100%;
          background: #D8D8D8;
          bottom: 0px;
          left: 0px;
        }
      }
    }
    .paper-content-section{
      height: calc(100% - 50px);
      padding: 10px;
      font-size: 12px;
      overflow-y: auto;
    }
  }
</style>
