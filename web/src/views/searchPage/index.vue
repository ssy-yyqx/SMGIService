<template>
  <div class="seach-list">
    <Paper v-for="(item, key, index) of listData" :key="index" :title="key">
      <div
        slot="opera"
        class="seach-button"
        @click="getListmore(item[0].firstRateId, key,item[0].serviceTempaleType)"
      >
        查看更多>>
      </div>
      <div slot="content">
        <ul class="seach-ul">
          <li v-for="(itemLi, indexLi) in item" :key="indexLi">

            {{ itemLi.title }}
            <span v-if="itemLi.router">
              <router-link :to="{ name: itemLi.router }">
                查看详情
              </router-link>
            </span>
            <span v-else>
              <router-link :to="{ path: '/Detail',query:{id:itemLi.id} }">
                查看详情
              </router-link>
            </span>
          </li>
        </ul>
      </div>
    </Paper>
    <div v-if="total" class="pagination">
      <el-pagination
        :current-page.sync="currentPage"
        :page-size="pageSize"
        layout="total, prev, pager, next"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    <p style="clear: both" />
    <!-- <Paper title="办事服务">
      <div slot="opera" class="seach-button">查看更多>></div>
      <div slot="content">
        <ul class="seach-ul">
          <li>1111 <span>查看详情</span></li>
          <li>2222 <span>查看详情</span></li>
        </ul>
      </div>
    </Paper> -->
  </div>
</template>
<script>
import Paper from '@/components/Paper'
import { getQueryLike, getListmore } from '@/api/content'
export default {
  components: {
    Paper
  },
  data() {
    return {
      listData: {},
      pageSize: 10,
      currentPage: 1,
      total: 0,
      firstkey: '',
      firstRateId: ''
    }
  },
  watch: {
    $route: {
      handler() {
        const { value } = this.$route.query
        this.getQueryLike(value)
      },

      immediate: true
    }
  },
  methods: {
    // 搜索
    getQueryLike(value) {
      const params = {
        value: value
      }
      getQueryLike(params).then((res) => {
        this.listData = res.data
        this.total = 0
      })
    },
    // 搜索更多
    getListmore(moduleId, key, serviceTempaleType) {
      this.firstkey = key
      this.firstRateId = moduleId
      this.serviceTempaleType = serviceTempaleType
      const data = {
        moduleId: moduleId,
        pageNumber: this.currentPage,
        pageSize: this.pageSize,
        serviceTemplateType: serviceTempaleType
      }
      getListmore(data).then((res) => {
        this.listData = {}
        this.listData[key] = res.data.list
        this.total = res.data.total
      })
    },
    handleSizeChange(val) {
      console.log(val)
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.getListmore(this.firstRateId, this.firstkey, this.serviceTempaleType)
    }
  }
}
</script>
<style lang="scss" scoped>
.seach-list {
  width: 1200px;
  margin: 0 auto;
  .seach-button {
    font-size: 14px;
    margin-top: 10px;
    cursor: pointer;
    color: #515a6e;
  }
  .seach-ul {
    li {
      height: 40px;
      line-height: 40px;
      list-style: none;
      width: 100%;
      margin-bottom: 5px;
      border-bottom: 1px solid #d6d6d6;
      font-size: 16px;
      span {
        padding: 0 5px;
        height: 30px;
        line-height: 30px;
        margin-top: 5px;
        border-radius: 5px;
        background: #969696;
        color: #fff;
        font-size: 12px;
        float: right;
        cursor: pointer;
      }
    }
  }
}
.pagination {
  height: 40px;
  float: right;
}
</style>
