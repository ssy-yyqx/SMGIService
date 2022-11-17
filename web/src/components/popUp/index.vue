<template>
  <div>
    <el-dialog
      :visible.sync="dialogVisible"
      :width="dialogWidth"
      :close-on-press-escape="false"
      :show-close="false"
      :close-on-click-modal="false"
    >
      <div class="content">
        <slot name="content" />
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="takeAway">取 消</el-button>
        <el-button type="primary" @click="determine">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
export default {
  props: {
    dialogVisible: {
      type: Boolean,
      default: false
    },
    dialogWidth: {
      type: String,
      default: '30%'
    }
  },
  methods: {
    //取 消
    takeAway() {
      this.$emit('takeAway', 'takeAway')
    },
    //确 定
    determine() {
      this.$emit('determine', 'determine')
    }
  }
}
</script>