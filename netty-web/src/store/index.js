import Vuex from 'vuex'
import Vue from 'vue'
Vue.use(Vuex)
const store = new Vuex.Store({
  state: {
    uploaderFunction: function(){}
  },
  getters: {
  },
  mutations: {
    setUploaderFunction(uploaderFunction){
      this.state.uploaderFunction = uploaderFunction
    },
    useUploaderFunction(){
      return this.state.uploaderFunction
    },
  },
  actions: {
  },
  modules: {
  }
})
export default store
