<template>
  <Paper :title="title">
    <template slot="content">
      <div class="list-section">
        <!-- <router-link v-for="(item,index) in infoList" :key="index" target="_blank" :to="{path:'/detail',query:{id: item.id}}"> -->
        <template v-if="infoList && infoList.length > 0">
          <div v-for="(item,index) in infoList" :key="index" @click="goToDetail(item)">
            <div class="list-item">
              <div class="image-section">
                <el-image
                  style="width: 100%; height: 100%"
                  :src="item.imageUrl"
                  fit="fit"
                />
              </div>
              <div class="info-section">
                <div class="title">{{ item.title }}</div>
                <div class="time">{{ item.createTime | formatTime }}</div>
              </div>
            </div>
          </div>
        </template>
        <div v-else class="no-data">
          暂无数据
        </div>
        <!-- </router-link> -->
      </div>
      <div class="pager-section">
        <el-pagination
          layout="prev, pager, next"
          :total="pager.total"
        />
      </div>
    </template>
  </Paper>
</template>

<script>
import * as API from '@/api/content'
export default {
  data() {
    return {
      title: '',
      moduleId: '',
      // 列表数据
      infoList: [],
      // 分页信息
      pager: {
        pageSize: 10,
        pageNumber: 1,
        total: 0
      }
    }
  },
  watch: {
    $route: {
      handler: function(val) {
        const { title, id } = val.meta
        this.title = title
        this.moduleId = id
      },
      // 深度观察监听
      deep: true,
      immediate: true
    }
  },
  created() {
    this.getInfoList()
  },
  methods: {
    // 获取信息列表
    getInfoList() {
      const params = {
        moduleId: this.moduleId,
        pageSize: this.pager.pageSize,
        pageNumber: this.pager.pageNumber
      }
      API.getInfoList(params).then(res => {
        const { code, data, msg } = res
        if (String(code) === '200') {
          if (data) {
            this.infoList = data.list
            this.pager.total = data.total
          }
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 跳转到详情页面
    goToDetail(data) {
      const routeName = this.$route.name
      this.$router.push({
        name: routeName + 'Detail',
        query: {
          id: data.id
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.list-section{
  .list-item{
    display: flex;
    cursor: pointer;
    border-bottom: 1px solid #D6D6D6;
    margin: 10px 0;
    .image-section{
      width: 80px;
      height: 53px;
      background: #E5E9EC;
      margin-right: 20px;
      margin-bottom: 20px;
    }
    .info-section{
      .title{
        height: 40px;
        width: 300px;
        color: #313131;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 18px;
      }
      .time{
        color: #7D7D7D;
        font-size: 14px;
      }
    }
  }
}
.pager-section{
  padding: 20px 0;
  text-align: right;
}
.no-data{
  padding: 20px;
  color: #717171;
  font-size: 14px;
}
</style>
