<template>
  <div>
    <template v-if="item.childrenList && item.childrenList.length > 0">
      <el-submenu :index="item.router">
        <template slot="title">
          <span>{{ item.moduleName }}</span>
        </template>
        <left-nav-item
          v-for="child in item.childrenList"
          :key="child.id"
          :item="child"
        />
      </el-submenu>
    </template>
    <template v-else>
      <el-menu-item :index="item.router">
        <span slot="title">{{ item.moduleName }}</span>
      </el-menu-item>
    </template>
  </div>
</template>

<script>
export default {
  name: 'LeftNavItem',
  props: {
    item: {
      type: Object,
      required: true
    }
  }
}
</script>

<style>
/* .el-menu-item{
  font-size: 16px;
  padding-left: 30px !important;
} */
.el-menu-item.is-active{
  background: #E5E9EC !important;
}
</style>
