<script>
import { mapGetters } from 'vuex'
import * as API from '@/api/content'
export default {
  computed: {
    ...mapGetters(['token'])
  },
  methods: {
    // 点赞
    gotoPraise() {
      if (this.token) {
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
      } else {
        this.$store.dispatch('user/setLoginDialog', true)
      }
    },
    // 取消点赞
    gotoCancelPraise() {
      if (this.token) {
        API.cancelPraise({ router: this.routerName }).then(res => {
          const { code, msg } = res
          if (String(code) === '200') {
            this.getPraiseAndCollectInfo()
          } else {
            this.$message.error(msg)
          }
        })
      } else {
        this.$store.dispatch('user/setLoginDialog', true)
      }
    }
  }
}
</script>
