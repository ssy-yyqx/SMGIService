<script>
import * as API from '@/api/content'
export default {
  methods: {
    // 收藏
    gotoCollect() {
      const params = {
        moduleId: this.moduleId,
        name: this.title,
        routerURI: this.routerName
      }
      API.saveCollect(params).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.getPraiseAndCollectInfo()
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 取消收藏
    gotoCancelCollect() {
      API.cancelCollect({ router: this.routerName }).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.getPraiseAndCollectInfo()
        } else {
          this.$message.error(msg)
        }
      })
    }
  }
}
</script>
