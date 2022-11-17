<template>
    <div class="menu">
        <template v-for="item in routes">
          <sub-menu
            v-if="item.children && item.children.length > 0"
            :item="item"
            :base-path="item.path"
            :key="item.path"
          />
          <menu-item
            v-else
            :path="item.path"
            :title="item.meta.title"
            :key="item.path + 1"
            />
        </template>
    </div>
</template>

<script>
import SubMenu from '@/components/menu/subMenu'
import MenuItem from '@/components/menu/menuItem'

export default {
  name: 'simple-menu',
  components: { MenuItem, SubMenu },
  props: {},
  data() {
    return{
        
    }
  },
  methods: {
  },
  computed: {
    routes() {
      return this.$router.options.routes
    }
  }
}
</script>

<style lang="scss" scoped>
.menu {
    user-select: none;
    width: 200px;
    height: 100%;
    position: fixed;
    top: 0;
    left: 0;
    box-shadow: 5px 5px 5px rgba(204, 204, 204, 0.23);
    background: white;
    overflow-x: hidden;
}
</style>