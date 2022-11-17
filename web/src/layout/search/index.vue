<template>
  <div>
    <Header />
    <searchPage />
    <Footer />
    <Login />
    <Register />
  </div>
</template>
<script>
import { Header, Footer } from '../components'
import Login from '../components/Login'
import Register from '../components/Register'
import searchPage from '@/views/searchPage'
export default {
  data() {
    return {}
  },
  components: {
    Header,
    Footer,
    Login,
    Register,
    searchPage
  }
}
</script>
