<template>
  <div id="app">
    <simple-menu/>
    <div class="container">
      <router-view/>
      <globalUploader></globalUploader>
    </div>
  </div>
</template>
<script>
import SimpleMenu from "@/components/menu/menu"
export default {
  components: { SimpleMenu }
}
</script>
<style>
body {
  margin: 0;
}
#app {
  height: 100vh;
}
.container {
  margin-left: 120px;
  height: 100%;
  background: #f5f7fa;
  padding: 10px;
  box-sizing: border-box;
}
</style>