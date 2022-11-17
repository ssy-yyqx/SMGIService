<template>
  <Paper title="单位概况" :is-white-bg="true">
    <div class="button-section">
      <button class="button-more" @click="gotoMore">更多</button>
    </div>
    <!-- <p class="introduce-section">测绘及地理信息中心是国家测绘地理信息局直属事业单位。主要负责管理全国测绘成果资料和档案资料、国家级基础地理信息系统建设、维护、更新、开发以及有关研究、专题数据库建库及基础测绘和重大测绘项目。具备空间基准建立维持、精密武器测绘保障、战场环境信息资源建设、空间地理信息服务保障等能力。</p> -->
    <p class="introduce-section">本单位主要负责管理成果资料和档案资料、基础地理信息系统建设、维护、更新、开发以及有关研究、专题数据库建库及基础测绘和重大测绘项目。具备空间基准建立维持、精密武器测绘保障、战场环境信息资源建设、空间地理信息服务保障等能力。</p>
    <div class="picture-section">
      <div v-for="(item,index) in list" :key="index" class="picture-item" @click="gotoPage(item)">
        <el-image
          class="image"
          :src="item.imageUrl"
          fit="cover"
        />
        <div class="title">{{ item.moduleName }}</div>
      </div>
    </div>
  </Paper>
</template>

<script>
import Paper from './components/Paper.vue'
import OneImage from '@/assets/images/one.png'
import TwoImage from '@/assets/images/two.jpg'
export default {
  components: { Paper },
  props: {
    list: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      images: [
        {
          name: '一室',
          url: OneImage,
          room: '1'
        },
        {
          name: '二室',
          url: TwoImage,
          room: '2'
        },
        {
          name: '三室',
          url: OneImage,
          room: '3'
        },
        {
          name: '四室',
          url: TwoImage,
          room: '4'
        },
        {
          name: '五室',
          url: OneImage,
          room: '5'
        },
        {
          name: '六室',
          url: TwoImage,
          room: '6'
        },
        {
          name: '七室',
          url: OneImage,
          room: '7'
        }
      ]
    }
  },
  methods: {
    gotoMore() {
      this.$router.push({ name: 'CenterIntroduce' })
    },
    // 跳转至具体的页面
    gotoPage(data) {
      this.$router.push({
        name: data.router
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.button-section{
  padding: 20px 0;
  text-align: center;
  .button-more{
    width: 106px;
    height: 32px;
    line-height: 32px;
    border: none;
    background: #E31000;
    border-radius: 16px;
    font-size: 14px;
    color: #FFF;
    cursor: pointer;
  }
}
.introduce-section{
  padding: 20px 40px;
  text-align: left;
  line-height: 24px;
  font-size: 16px;
  color: #313131;
  text-indent: 30px;
}
.picture-section{
  .picture-item {
    display: inline-block;
    width: 330px;
    height: 270px;
    text-align: center;
    margin: 5px;
    .image{
      width: 330px;
      height: 230px;
    }
    .title{
      font-weight: bold;
      color: #313131;
      font-size: 18px;
      line-height: 30px;
    }
  }
}
</style>
