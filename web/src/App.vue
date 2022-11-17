<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
  created() {
    if (window.websocket) {
      window.websocket.close()
    }
  }
}
</script>

