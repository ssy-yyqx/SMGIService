<template>
  <div class="header-container">
    <Info />
    <Search />
    <Nav />
  </div>
</template>

<script>
import Info from './Info.vue'
import Search from './Search.vue'
import Nav from './Nav.vue'
export default {
  components: { Info, Search, Nav }
}
</script>

<style lang="scss" scoped>
.header-container{
  width: 100%;
  height: 332px;
  background: url('../../../assets/images/header-bg.png') no-repeat center;
}
</style>
