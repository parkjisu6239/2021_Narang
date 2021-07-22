export function setPlatform(state, isDesktop) {
  state.isDesktopPlatform = isDesktop
}

export function setAccessToken(state) {
  state.accessToken = localStorage.getItem('access_token')
  console.log(state.accessToken)
}

export function setUserId(state) {
  state.userId = localStorage.getItem('user_id')
  console.log(state.userId)
}

export function deleteToken(state) {
  state.accessToken = ''
  state.user_id = ''
}
