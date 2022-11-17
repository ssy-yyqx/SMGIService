<template>
  <div class="carousel-section">
    <el-carousel trigger="click" height="150px">
      <el-carousel-item v-for="(item,index) in items" :key="index">
        <img :src="item.img" alt="新闻图片">
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script>
import newsImage from '@/assets/images/news1.jpg'
export default {
  data() {
    return {
      items: [
        {
          img: newsImage
        }
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
.carousel-section{
  width: 100%;
  height: 600px;
  background: #FFF;
  ::v-deep .el-carousel__container{
    height: 600px !important;
  }
}
</style>
