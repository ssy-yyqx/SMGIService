<script>
import * as API from '@/api/content'
export default {
  methods: {
    // 点赞
    gotoPraise() {
      const params = {
        moduleId: this.moduleId,
        name: this.title,
        routerURI: this.routerName
      }
      API.savePraise(params).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.getPraiseAndCollectInfo()
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 取消点赞
    gotoCancelPraise() {
      API.cancelPraise({ router: this.routerName }).then(res => {
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
