<template>
  <div class="search-container">
    <div class="search-content">
      <div class="top-section">
        <div class="title-section">
          <img src="../../../assets/images/logo.png" alt="logo">
          <span class="title">测绘地理线上服务平台</span>
        </div>
        <div class="search-section">
          <div class="input-area">
            <el-popover
              ref="popoverSH"
              placement="bottom"
              width="606"
              trigger="manual"
              :visible-arrow="false"
            >
              <div class="search-list">
                <ul>
                  <li
                    v-for="(item, index) in seachData"
                    :key="index"
                    @click="gotoSearch(item.value)"
                  >
                    {{ item.value }}
                  </li>
                </ul>
              </div>
              <input
                slot="reference"
                v-model="keyword"
                type="text"
                class="input"
                placeholder="请输入搜索内容"
                @input="throttle"
              >
            </el-popover>

            <div class="search-icon" @click="gotoSearch(keyword)">
              <img src="../../../assets/images/search.png" alt="搜索">
            </div>
            <!-- <el-input v-model="keyword" placeholder="请输入搜索内容" class="input-with-select" size="middle">
              <div slot="append" @click="gotoSearch(keyword)">
                <img src="../../../assets/images/icon-search.png" alt="搜索">
              </div>
            </el-input> -->
          </div>
          <div class="keyword">
            热门搜索：
            <span
              v-for="(item, index) in hotworddata.slice(0, 5)"
              :key="index"
              class="keyword-item"
              @click="gotoSearch(item.value)"
            >
              {{ item.value.slice(0, 3) }}</span>
          </div>
        </div>
      </div>
      <div v-if="weatherList && weatherList.length > 0" class="info-section">
        <div class="info-item date">
          <div>{{ weatherList[0].city }}</div>
          <div>{{ date }}</div>
        </div>
        <div class="info-item weather">
          <div class="up">
            <div class="temperature">{{ weatherList[0].currentTemperature }}</div>
            <div class="temperature-range">
              <div class="title">天气</div>
              <div class="range">{{ weatherList[0].temperature }}</div>
            </div>
          </div>
          <div class="down">
            <span>风力：{{ weatherList[0].windLevel }} </span>
            <span>风向：{{ weatherList[0].windAspect }}</span>
          </div>
        </div>
        <div class="info-item day-weather">
          <div v-for="(item, index) in weatherList" :key="index" class="day-weather-item">
            <div class="weather-text">{{ item.name }}</div>
            <div class="weather-temperature">{{ item.temperature }}</div>
            <div class="weather-icon">
              <img :src="item.weather" :alt="item.name">
            </div>
            <div class="weather-date">{{ item.date }}</div>
          </div>
        </div>
        <div class="info-item time">
          <div class="up">当前时间</div>
          <div class="down">{{ time }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { parseTime } from '@/utils/index'
import Logo from '@/assets/images/logo.png'
import { hotwordtopfive, gethotwordtopfive, getWeather } from '@/api/content'
import qintian from '@/assets/images/weather/qintian.png'
import xiaoyu from '@/assets/images/weather/xiaoyu.png'
import zhongyu from '@/assets/images/weather/zhongyu.png'
import dayu from '@/assets/images/weather/dayu.png'
import baoyu from '@/assets/images/weather/baoyu.png'
import xiaoxue from '@/assets/images/weather/xiaoxue.png'
import zhongxue from '@/assets/images/weather/zhongxue.png'
import daxue from '@/assets/images/weather/daxue.png'
import yintian from '@/assets/images/weather/yintian.png'
import leizhenyu from '@/assets/images/weather/leizhenyu.png'
import baoxue from '@/assets/images/weather/baoxue.png'
import fuchen from '@/assets/images/weather/fuchen.png'
import shachen from '@/assets/images/weather/shachen.png'
import wumai1 from '@/assets/images/weather/wumai1.png'
import wumai2 from '@/assets/images/weather/wumai2.png'
import wumai3 from '@/assets/images/weather/wumai3.png'
import wu from '@/assets/images/weather/wu.png'
import yujiaxue from '@/assets/images/weather/yujiaxue.png'
import duoyun from '@/assets/images/weather/duoyun.png'
import qiangduiliu from '@/assets/images/weather/qiangduiliu.png'
import dafeng from '@/assets/images/weather/dafeng.png'
import bingbao from '@/assets/images/weather/bingbao.png'
export default {
  data() {
    return {
      date: parseTime(new Date(), '{y}年{m}月{d}日'),
      time: parseTime(new Date(), '{h}:{i}:{s}'),
      keyword: '',
      logo: Logo,
      timeInterval: null,
      hotworddata: [],
      seachData: [],
      weatherList: []
    }
  },
  mounted() {
    this.timeInterval = setInterval(() => {
      this.time = parseTime(new Date(), '{h}:{i}:{s}')
    }, 1000)
    this.getWeather()
    this.showSeachList()
    this.keyword = this.$route.query.value
    const that = this
    document.addEventListener('click', function(e) {
      // 点击空白处让弹窗状态为显示
      if (that.$refs.popoverSH) {
        that.$refs.popoverSH.doClose()
      }
    })
  },
  beforeDestroy() {
    if (this.timeInterval) {
      clearInterval(this.timeInterval)
    }
  },
  methods: {
    gotoSearch(val) {
      if (val === '') {
        this.$message({
          message: '请输入搜索内容',
          type: 'warning'
        })
        return false
      }
      this.$router.push({
        path: '/search',
        query: { value: val || '', time: Date.now() }
      })
      this.keyword = this.$route.query.value
      this.$refs.popoverSH.doClose()
    },
    showSeachList() {
      hotwordtopfive().then((res) => {
        this.hotworddata = res.data
      })
    },
    // 节流
    throttle() {
      let timer = null
      const that = this
      if (timer) {
        return false
      }
      timer = setTimeout(() => {
        that.gethotwordtopfive()
      }, 1000)
    },
    gethotwordtopfive() {
      gethotwordtopfive(this.keyword).then((res) => {
        console.log(res)
        this.seachData = res.data
        if (this.keyword && this.seachData.length > 0) {
          setTimeout(() => {
            this.$refs.popoverSH.doShow()
          }, 100)
        } else {
          this.$refs.popoverSH.doClose()
        }
      })
    },
    getWeather() {
      getWeather(this.keyword).then((res) => {
        console.log(res)
        this.weatherList = res.data
        for (var i = 0; i < this.weatherList.length; i++) {
          switch (this.weatherList[i].weather) {
            case 'qintian':
              this.weatherList[i].weather = qintian
              break
            case 'xiaoxue':
              this.weatherList[i].weather = xiaoxue
              break
            case 'zhongxue':
              this.weatherList[i].weather = zhongxue
              break
            case 'daxue':
              this.weatherList[i].weather = daxue
              break
            case 'baoxue':
              this.weatherList[i].weather = baoxue
              break
            case 'xiaoyu':
              this.weatherList[i].weather = xiaoyu
              break
            case 'zhongyu':
              this.weatherList[i].weather = zhongyu
              break
            case 'dayu':
              this.weatherList[i].weather = dayu
              break
            case 'baoyu':
              this.weatherList[i].weather = baoyu
              break
            case 'duoyun':
              this.weatherList[i].weather = duoyun
              break
            case 'yintian':
              this.weatherList[i].weather = yintian
              break
            case 'leizhenyu':
              this.weatherList[i].weather = leizhenyu
              break
            case 'fuchen':
              this.weatherList[i].weather = fuchen
              break
            case 'shachen':
              this.weatherList[i].weather = shachen
              break
            case 'wu':
              this.weatherList[i].weather = wu
              break
            case 'wumai1':
              this.weatherList[i].weather = wumai1
              break
            case 'wumai2':
              this.weatherList[i].weather = wumai2
              break
            case 'wumai3':
              this.weatherList[i].weather = wumai3
              break
            case 'yujiaxue':
              this.weatherList[i].weather = yujiaxue
              break
            case 'qiangduiliu':
              this.weatherList[i].weather = qiangduiliu
              break
            case 'dafeng':
              this.weatherList[i].weather = dafeng
              break
            case 'bingbao':
              this.weatherList[i].weather = bingbao
              break
            default:
              this.weatherList[i].weather = qintian
              break
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.search-list {
  ul {
    padding: 0;
    margin: 0;
    li {
      list-style: none;
      height: 40px;
      line-height: 40px;
      cursor: pointer;
      border-bottom: 1px solid #d6d6d6;
      &:nth-last-child(1) {
        border: none;
      }
    }
  }
}
.search-container {
  width: 100%;
  height: 232px;
  .search-content {
    width: 1400px;
    height: 100%;
    margin: 0 auto;
    .top-section {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .title-section {
        padding: 20px 0;
        display: flex;
        align-items: center;
        .title {
          margin-left: 10px;
          font-size: 28px;
          font-weight: bold;
          color: #ffffff;
        }
      }
      .search-section {
        .input-area {
          position: relative;
          display: inline-block;
          margin-right: 20px;
          .input {
            width: 606px;
            height: 40px;
            line-height: 40px;
            background: #ffffff;
            border: 1px solid #d6d6d6;
            border-radius: 20px;
            outline: none;
            padding: 0 40px 0 20px;
            color: #535353;
            :focus {
              outline: none;
            }
          }
          .search-icon {
            cursor: pointer;
            position: absolute;
            top: 12px;
            right: 15px;
            width: 18px;
            height: 18px;
          }
        }
        .keyword {
          display: inline-block;
          margin-top: 5px;
          color: #ffffff;
          font-size: 14px;
          &-item {
            padding: 5px;
            cursor: pointer;
          }
        }
      }
    }
    .info-section {
      display: flex;
      justify-content: space-around;
      .info-item {
        height: 130px;
        background: rgba(255, 255, 255, 0.2);
        border-radius: 4px;
        padding: 20px 10px;
        color: #ffffff;
        text-align: center;
        font-size: 22px;
      }
    }
    .date {
      width: 200px;
      line-height: 40px;
    }
    .weather {
      width: 270px;
      line-height: 40px;
      .up {
        display: flex;
        align-items: center;
        justify-content: center;
        .temperature {
          font-size: 48px;
        }
        .temperature-range {
          .title {
            font-size: 22px;
            line-height: 24px;
          }
          .range {
            font-size: 16px;
            line-height: 20px;
          }
        }
      }
      .down {
        text-align: center;
      }
    }
    .day-weather {
      width: 630px;
      display: flex;
      &-item{
        width: 100px;
        margin: 0 10px;
        .weather-text{

        }
        .weather-temperature{
          font-size: 14px;
          padding: 6px 0;
        }
        .weather-icon{
          img{
            width: 20px;
            height: 20px;
          }
        }
        .weather-date{
          font-size: 14px;
          padding-top: 4px;
        }
      }
    }
    .time {
      width: 200px;
      line-height: 40px;
      .down {
        font-size: 30px;
      }
    }
  }
}
::v-deep .el-input-group__append {
  background: #0454cf;
}
>>> .el-popper .popper__arrow {
  display: none !important;
}
</style>
