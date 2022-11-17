<template>
  <Paper title="相关链接">
    <div class="relative-link-section">
      <div v-for="(item, index) in links" :key="index" class="relative-link-item">
        {{ item.name }}
      </div>
    </div>
  </Paper>
</template>

<script>
import Paper from './components/Paper.vue'
export default {
  components: { Paper },
  data() {
    return {
      links: [
        {
          name: '中华人民共和国国防部',
          url: ''
        },
        {
          name: '国防科技大学',
          url: ''
        },
        {
          name: '国家自然资源局',
          url: ''
        },
        {
          name: '西部战区',
          url: ''
        },
        {
          name: '东部战区',
          url: ''
        },
        {
          name: '北部战区',
          url: ''
        }
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
.relative-link-section{
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 20px;
  .relative-link-item{
    padding: 20px 30px;
    font-weight: bold;
    color: #313131;
    font-size: 24px;
    background: #FFFFFF;
    margin: 5px;
  }
}
</style>
