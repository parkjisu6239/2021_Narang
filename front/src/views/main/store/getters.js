// 플랫폼 관련 정보 가져오기
export function getIsDesktopPlatform (state) {
  return state.isDesktopPlatform
}

// 로그인 여부 반환
export function isLoggedIn (state) {
	return state.accessToken ? true : false
}

export function userId (state) {
	return state.userId
}
