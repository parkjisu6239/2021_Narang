export function setPlatform(state, isDesktop) {
  state.isDesktopPlatform = isDesktop
}

export function setAccessToken(state) {
  state.accessToken = localStorage.getItem('access_token')
}

export function setUserEmail(state) {
  state.email = localStorage.getItem('email')
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
  state.userId = userInfo.userId
}

export function setRoomInfo(state, roomInfo) {
  state.myRoom = {
    ...roomInfo
  }
}
