<template>
  <InnerPaper>
    <div class="news-container">
      <div class="news-container__left">
        <el-carousel height="350px" @change="onCarouselChange">
          <el-carousel-item v-for="item in list" :key="item.id">
            <el-image
              style="width: 100%; height: 100%"
              :src="item.imageUrl"
              fit="fit"
              direction="vertical"
              @click="goToDetail(item)"
            />
          </el-carousel-item>
          <div class="picture-title">
            {{ title }}
          </div>
          <div class="more" @click="gotoMore">
            更多
          </div>
        </el-carousel>
      </div>
      <div class="news-container__right">
        <Paper title="新闻聚焦">
          <div slot="opera">
            <span class="more" @click="gotoMore">更多<i>>></i></span>
          </div>
          <div slot="content">
            <div class="content-section">
              <!-- <router-link v-for="(item,index) in list" :key="index" target="_blank" :to="{path:'/detail', query:{id: item.id}}"> -->
              <div v-for="(item,index) in list" :key="index" @click="goToDetail(item)">
                <div class="content-item">
                  <div class="title-section">
                    <i class="icon" />
                    <span class="title">{{ item.title }}</span>
                  </div>
                  <span class="time">[{{ item.createTime | formatTime }}]</span>
                </div>
              </div>
            <!-- </router-link> -->
            </div>
          </div>
        </paper>
      </div>
    </div>
  </InnerPaper>
</template>

<script>
import InnerPaper from './components/Paper.vue'
export default {
  components: { InnerPaper },
  props: {
    list: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  data() {
    return {
      title: ''
    }
  },
  watch: {
    list: {
      handler(val) {
        this.title = val[0] ? val[0].title : ''
      },
      immediate: true
    }
  },
  methods: {
    // 点击更多
    gotoMore() {
      this.$router.push({ name: 'InternationalNews' })
    },
    // 切换
    onCarouselChange(index) {
      this.title = this.list[index].title
    },
    // 跳转到详情页面
    goToDetail(data) {
      this.$router.push({
        name: data.router + 'Detail',
        query: {
          id: data.id
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
  .news-container{
    width: 100%;
    display: flex;
    margin-bottom: 20px;
    &__left{
      width: 690px;
      height: 350px;
      position: relative;
      .picture-title{
        position: absolute;
        bottom: 0px;
        left: 0px;
        height: 30px;
        line-height: 30px;
        background: rgba(52, 56, 79, 0.8);
        width: 486px;
        z-index: 100;
        padding: 0 10px;
        font-size: 16px;
        color: #FFFFFF;
      }
      .more{
        position: absolute;
        bottom: 0px;
        right: 0px;
        height: 30px;
        line-height: 30px;
        background: rgba(52, 56, 79, 0.8);
        width: 54px;
        z-index: 100;
        padding: 0 10px;
        font-size: 12px;
        color: #FFFFFF;
        text-align: center;
        cursor: pointer;
      }
    }
    &__right{
      margin-left: 20px;
      flex: 1;
      .content-section{
        .content-item{
          font-size: 16px;
          height: 30px;
          line-height: 30px;
          display: flex;
          justify-content: space-between;
          color: #313131;
          cursor: pointer;
          align-items: center;
          .title-section{
            display: flex;
            align-items: center;
            .icon{
              width: 6px;
              height: 6px;
              background: #E31000;
              transform:rotate(45deg);
              margin-right: 10px;
            }
            .title{
              display: inline-block;
              width: 505px;
              white-space: nowrap;
              text-overflow: ellipsis;
              overflow: hidden;
              word-break: break-all;
            }
          }
          .time{
            color: #7D7D7D;
          }
        }
      }
      .more{
        display: inline-block;
        width: 106px;
        height: 32px;
        line-height: 32px;
        background: #E3E3E3;
        border-radius: 16px;
        text-align: center;
        font-size: 14px;
        cursor: pointer;
        i{
          padding-left: 5px;
        }
      }
    }
  }
</style>
