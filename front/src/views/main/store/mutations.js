export function setPlatform(state, isDesktop) {
  state.isDesktopPlatform = isDesktop
}

export function setAccessToken(state) {
  state.accessToken = localStorage.getItem('access_token')
  console.log(state.accessToken)
}

export function setUserEmail(state) {
  state.userId = localStorage.getItem('email')
  console.log(state.userId)
}

export function deleteToken(state) {
  state.accessToken = ''
  state.user_id = ''
}

export function setSeletedMenu(state, menu) {
  state.mypageSeletedMenu = menu
}
