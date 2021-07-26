export function setPlatform(state, isDesktop) {
  state.isDesktopPlatform = isDesktop
}

export function setAccessToken(state) {
  state.accessToken = localStorage.getItem('access_token')
  console.log(state.accessToken)
}

export function setUserEmail(state) {
  state.email = localStorage.getItem('email')
  console.log(state.userId)
}

export function deleteToken(state) {
  state.accessToken = ''
  state.user_id = ''
}

export function setSeletedMenu(state, menu) {
  state.mypageSeletedMenu = menu
}

export function setUserInfo(state, userInfo) {
  state.username = userInfo.username
  state.profileImageURL = userInfo.profileImageURL
  state.email = userInfo.email
}
