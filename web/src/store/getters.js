const getters = {
  name: state => state.user.name,
  token: state => state.user.token,
  sidebar: state => state.app.sidebar,
  menu: state => state.user.menu,
  leftMenu: state => state.user.leftMenu,
  activeMenuName: state => state.user.activeMenuName,
  isLoginDialog: state => state.user.isLoginDialog,
  isRegisterDialog: state => state.user.isRegisterDialog
}
export default getters
