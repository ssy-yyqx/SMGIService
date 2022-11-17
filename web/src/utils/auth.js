import Cookies from 'js-cookie'

const TokenKey = 'token'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

const USER_NAME = 'username'

export function getUserName() {
  return Cookies.get(USER_NAME)
}

export function setUserName(username) {
  return Cookies.set(USER_NAME, username)
}

export function removeUserName() {
  return Cookies.remove(USER_NAME)
}
