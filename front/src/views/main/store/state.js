// ROOT STATE 변수 정의 및 기본값 대입
const accessToken = localStorage.getItem('access_token') ? localStorage.getItem('access_token') : ''
const email = localStorage.getItem('user_id') ? localStorage.getItem('email') : ''
const mypageSeletedMenu = 'userInfoChange'
const username = ''
const profileImageURL = ''

/**
 * 플랫폼 관련 정보로 데스크탑인지, 모바일인지 판별 - 하이브리드 앱 대비
 */
function getIsDesktop() {
  var userAgent = window.navigator.userAgent,
    platform = window.navigator.platform,
    macosPlatforms = ['Macintosh', 'MacIntel', 'MacPPC', 'Mac68K'],
    windowsPlatforms = ['Win32', 'Win64', 'Windows', 'WinCE'],
    iosPlatforms = ['iPhone', 'iPad', 'iPod'],
    os = null;

  if (macosPlatforms.indexOf(platform) !== -1) { //Desktop - Mac
    return true
  } else if (iosPlatforms.indexOf(platform) !== -1) { // IOS
    return false
  } else if (windowsPlatforms.indexOf(platform) !== -1) { //Desktop - window
    return true
  } else if (/Android/.test(userAgent)) { //Android
    return false
  } else if (!os && /Linux/.test(platform)) { //Linux
    return true
  }

  return os;
}

const IsDesktop = getIsDesktop()

export default {
  isDesktopPlatform: IsDesktop,
  accessToken: accessToken,
  email,
  mypageSeletedMenu: mypageSeletedMenu,
  username,
  profileImageURL: profileImageURL,
}
