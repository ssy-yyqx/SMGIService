import { getMenuList } from '@/api/user'
import { setUserName, setToken } from '@/utils/auth'

const state = {
  token: '',
  name: '',
  menu: [],
  leftMenu: [],
  activeMenuName: '',
  // 是否打开登录窗口
  isLoginDialog: false,
  // 是否打开注册窗口
  isRegisterDialog: false,
  // 是否已生成路由
  isGenerateRoute: false
}

const mutations = {
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_TOKEN: (state, token) => {
    setToken(token)
    state.token = token
    if (!token) {
      const name = '游客' + new Date().getTime()
      setUserName(name)
      state.name = name
    }
  },
  SET_MENU: (state, menu) => {
    state.menu = menu
  },
  SET_LEFT_MENU: (state, leftMenu) => {
    state.leftMenu = leftMenu
  },
  SET_ACTIVE_MENU_NAME: (state, activeMenuName) => {
    state.activeMenuName = activeMenuName
  },
  SET_IS_LOGIN_DIALOG: (state, isOpen) => {
    state.isLoginDialog = isOpen
  },
  SET_IS_REGISTER_DIALOG: (state, isOpen) => {
    state.isRegisterDialog = isOpen
  },
  SET_IS_GENERATE_ROUTE: (state, isGenerate) => {
    state.isGenerateRoute = isGenerate
  }
}

const actions = {
  setName({ commit }, name) {
    commit('SET_NAME', name)
  },
  setToken({ commit }, token) {
    commit('SET_TOKEN', token)
  },
  // 设置登录窗口状态
  setLoginDialog({ commit }, state) {
    commit('SET_IS_LOGIN_DIALOG', state)
  },
  // 设置注册窗口状态
  setRegisterDialog({ commit }, state) {
    commit('SET_IS_REGISTER_DIALOG', state)
  },
  // 获取菜单列表
  getMenuList({ commit }, state) {
    return new Promise((resolve, reject) => {
      getMenuList(state.token).then(response => {
        const { data } = response
        commit('SET_MENU', data)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  // 设置左侧菜单
  setLeftMenu({ commit }, leftMenu) {
    commit('SET_LEFT_MENU', leftMenu)
  },
  // 设置当前菜单
  activeMenuName({ commit }, activeMenuName) {
    commit('SET_ACTIVE_MENU_NAME', activeMenuName)
  },
  // 设置是否已生成路由
  setIsGenerateRoute({ commit }, isGenerateRoute) {
    commit('SET_IS_GENERATE_ROUTE', isGenerateRoute)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

