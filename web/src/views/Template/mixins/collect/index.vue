<script>
import { mapGetters } from 'vuex'
import * as API from '@/api/content'
export default {
  computed: {
    ...mapGetters(['token'])
  },
  methods: {
    // 收藏
    gotoCollect() {
      if (this.token) {
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
      } else {
        this.$store.dispatch('user/setLoginDialog', true)
      }
    },
    // 取消收藏
    gotoCancelCollect() {
      if (this.token) {
        API.cancelCollect({ router: this.routerName }).then(res => {
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
