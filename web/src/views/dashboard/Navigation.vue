<template>
  <Paper title="站内导航">
    <div class="button-section">
      <button class="button-more" @click="gotoMore">更多</button>
    </div>
    <div class="tab-bar-section">
      <div v-for="item in tabs" :key="item.value" :class="{'tab-bar-item-active': item.value === currentTab}" class="tab-bar-item" @click="onHandleClickTab(item)">
        {{ item.title }}
      </div>
    </div>
    <div class="menu-section">
      <div v-for="item in menus" :key="item.id" class="menu-item">
        <div class="menu-item__left" @click="onHandleClickMenu(item)">
          {{ item.name }}
        </div>
        <div class="menu-item__right">
          <div v-for="child in item.children" :key="child.id" class="menu-item-child">
            <span class="text" :class="{'text-active': child.id === activeSubMenu}" @click="onHandleClickSubMenu(child)">{{ child.name }}</span>
          </div>
        </div>
      </div>
    </div>
    <div class="info-section">
      <div v-if="!isSubMenu" class="introduce-section">
        <div class="left">
          <img src="../../assets/images/one.png" alt="">
        </div>
        <div class="right">
          <p>空间基准导航数据是在惯性空间坐标系下表示的导航数据。在地球表面附近运动的载体不论是飞机、舰船、还是车辆，最重要的是要知道它们相对地球的地理位置和相对于地理坐标系的方向及水平姿态角，因此必须在运动物体上获得一个地理坐标系或一个惯性坐标系。陀螺仪最重要的功用之一就是用它在载体上模拟地理坐标系或惯性坐标系。包括：大地基准、高程基准、深度基准、重力基准建立维持及应用。</p>
        </div>
      </div>
      <div v-else class="menu-introduce-section">
        <div v-for="item in menuInfos" :key="item.id" class="menu-introduce-item">
          <div class="header">
            <div class="header-left" @click="onClickSubItem(item)">
              <i />
              <span>{{ item.name }}</span>
            </div>
            <div class="header-right">
              <button class="header-right-button" @click="gotoHandlingGuide(item)">
                <i class="icon-deal" /><span>办理指南</span>
              </button>
              <button class="header-right-button" @click="gotoSpecial(item)">
                <i class="icon-online" /><span>在线办理</span>
              </button>
              <button class="header-right-button">
                <i class="icon-vant" /><span>点赞</span>
              </button>
              <button class="header-right-button">
                <i class="icon-collect" /><span>收藏</span>
              </button>
              <button class="header-right-button">
                <i class="icon-comment" /><span>评论</span>
              </button>
            </div>
          </div>
          <div class="content" :class="{'content-show': item.isShow}">
            <p>空间基准导航数据是在惯性空间坐标系下表示的导航数据。在地球表面附近运动的载体不论是飞机、舰船、还是车辆，最重要的是要知道它们相对地球的地理位置和相对于地理坐标系的方向及水平姿态角，因此必须在运动物体上获得一个地理坐标系或一个惯性坐标系。陀螺仪最重要的功用之一就是用它在载体上模拟地理坐标系或惯性坐标系。包括：大地基准、高程基准、深度基准、重力基准建立维持及应用。</p>
          </div>
        </div>
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
      currentTab: 'GeneralCapability',
      tabs: [
        {
          title: '通用能力',
          value: 'GeneralCapability'
        },
        {
          title: '各科室能力',
          value: 'DepartmentCapability'
        }
      ],
      // 是否点击子菜单
      isSubMenu: false,
      // 当前活跃的子菜单
      activeSubMenu: '',
      // 点击子菜单后的数据
      menuInfos: []
    }
  },
  computed: {
    menus() {
      return menuJson.find(item => item.id === '5').children
    }
  },
  methods: {
    gotoMore() {
      this.$router.push({ name: 'BusinessNavigation5_1_1_1' })
    },
    // 点击tab
    onHandleClickTab(data) {
      this.currentTab = data.value
    },
    // 点击左侧菜单
    onHandleClickMenu(data) {
      this.isSubMenu = false
      this.activeSubMenu = ''
      this.$message.warning('建设中。。。')
      console.log(data)
    },
    // 点击子菜单
    onHandleClickSubMenu(data) {
      this.isSubMenu = true
      this.activeSubMenu = data.id
      this.menuInfos = data.children
      this.menuInfos.forEach((item, index) => {
        if (index === 0) {
          item.isShow = true
        } else {
          item.isShow = false
        }
      })
      console.log(data)
    },
    // 点击子菜单显示或者隐藏内容
    onClickSubItem(data) {
      data.isShow = !data.isShow
      if (data.isShow) {
        this.menuInfos.forEach(item => {
          if (item.id !== data.id) {
            item.isShow = false
          }
        })
      }
      this.menuInfos = [...this.menuInfos]
    },
    // 跳转到办理指南
    gotoHandlingGuide(data) {
      const { routerName } = data
      this.$router.push({
        name: routerName,
        params: {
          transferTab: 'handlingGuide'
        }
      })
    },
    // 跳转到特色服务
    gotoSpecial(data) {
      const { specialRouter } = data
      if (specialRouter) {
        this.$router.push({ name: specialRouter })
      }
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
.tab-bar-section{
  width: 100%;
  height: 44px;
  border-bottom: 2px solid #0454CF;
  background: #FFFFFF;
  .tab-bar-item{
    cursor: pointer;
    height: 41px;
    line-height: 41px;
    padding: 0 12px;
    display: inline-block;
    color: #313131;
    background: #E6E6E6;
    &-active{
      background: #0454CF;
      color: #FFFFFF;
    }
  }
}
.menu-section{
  width: 100%;
  border: 1px solid #DFDFDF;
  padding: 20px 20px 0 20px;
  background: #FFFFFF;
  .menu-item{
    display: flex;
    margin-bottom: 5px;
    border-bottom: 1px solid #F0F0F0;
    padding: 20px 0;
    &:last-child{
      border-bottom: none;
    }
    &__left{
      width: 220px;
      font-size: 18px;
      color: #313131;
      margin-right: 20px;
      font-weight: bold;
      cursor: pointer;
      line-height: 30px;
    }
    &__right{
      flex: 1;
      .menu-item-child{
        display: inline-block;
        height: 30px;
        width: 215px;
        line-height: 30px;
        font-size: 16px;
        font-weight: bold;
        color: #434343;
        cursor: pointer;
        .text{
          padding: 5px 10px;
          &:hover, &-active{
            background: #0454CF;
            color: #ffffff;
            border-radius: 4px;
          }
        }

      }
    }
  }
}
.info-section{
  .introduce-section{
    display: flex;
    padding: 20px;
    margin-top: 20px;
    border: 1px solid #DFDFDF;
    background: #FFF;
    .left{
      width: 335px;
    }
    .right{
      p{
        text-indent: 30px;
        padding: 0 0 10px 10px;
        line-height: 26px;
      }
    }
  }
  .menu-introduce-section{
    padding: 20px;
    margin-top: 20px;
    border: 1px solid #DFDFDF;
    background: #FFF;
    .menu-introduce-item{
      border: 1px solid#F0F0F0;
      margin-bottom: 10px;
      .header{
        width: 100%;
        height: 70px;
        line-height: 70px;
        padding: 0 20px;
        background: #F0F0F0;
        color: #313131;
        font-size: 16px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        &-left{
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
            background-size:5px 5px,5px 5px,5px 5px,8px 8px;
          }
        }
        &-right{
          color: #313131;
          font-size: 14px;
          display: flex;
          &-button{
            background: #FFF;
            border: 1px solid #C8C8C8;
            border-radius: 4px;
            padding: 8px 16px;
            display: flex;
            cursor: pointer;
            margin-left: 10px;
            i{
              display: inline-block;
              width: 15px;
              height: 15px;
              margin-right: 5px;
            }
            .icon-deal{
              width: 25px;
              background: url('../../assets/images/icon-deal.png') no-repeat;
            }
            .icon-online{
              width: 20px;
              background: url('../../assets/images/icon-online.png') no-repeat;
            }
            .icon-vant{
              background: url('../../assets/images/icon-comment.png') no-repeat;
            }
            .icon-collect{
              background: url('../../assets/images/icon-collect.png') no-repeat;
            }
            .icon-comment{
              background: url('../../assets/images/icon-comment.png') no-repeat;
            }
          }
        }
      }
      .content{
        text-indent: 30px;
        padding: 0 10px;
        line-height: 26px;
        display: none;
      }
      .content-show{
        display: block;
      }
    }
  }
}
</style>
