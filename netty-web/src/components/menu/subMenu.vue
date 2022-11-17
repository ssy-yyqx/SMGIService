<template>
  <div>
    <div class="title">
      <!-- 标题 -->
      <a href="javascript:void(0)">{{ item.meta.title }}</a>
      <!-- 箭头符号 -->
      
    </div>
    <div
      class="children"
    >
      <template v-for="childItem in item.children">
        <sub-menu
          v-if="childItem.children && childItem.children.length>0"
          :item="childItem"
          :base-path="resolvePath(childItem.path)"
          :key="childItem.path"
        />
        <menu-item
          v-else
          :path="resolvePath(childItem.path)"
          :title="childItem.meta.title"
          :key="childItem.path + 1"
          />
      </template>
    </div>
  </div>
</template>

<script>
import path from 'path'
import MenuItem from "@/components/menu/menuItem"
export default {
  components: { MenuItem },
  name: 'sub-menu', // 一定要写 name，不然递归调用自己会报错
  props: {
    item: { // route object
      type: Object,
      required: true
    },
    basePath: { // 上级 path
      type: String,
      default: ''
    }
  },
  data() {
    return {}
  },
  methods: {
    // 将 routePath 和 basePath 拼接起来
    resolvePath(routePath) {
      return path.resolve(this.basePath, routePath)
    }
  }
}
</script>

<style lang="scss" scoped>
.title {
  padding: 20px;
  color: rgb(153, 153, 153);
}
.title:hover {
  background: rgb(244, 244, 244);
}
.children {
  padding-left: 20px;
  background: rgb(244, 244, 244);
}
.icon {
  float: right;
  color: #999999;
}
</style>