// ROOT STATE 변수 정의 및 기본값 대입
const accessToken = localStorage.getItem('access_token') ? localStorage.getItem('access_token') : ''
const email = localStorage.getItem('email') ? localStorage.getItem('email') : ''
const mypageSeletedMenu = 'userInfoChange'
const username = ''
const profileImageURL = ''
const userId = ''
const myRoom = {}
const publisher = {}
const onVideo = true;
const onAudio = true;
const mafiaManager = {
  username : '', // 자기 이름
  theVoted : null, // 마피아가 죽이는애, 시민이 1차 투표 선정 유저
  isAgree : false, // 단두대 오른사람 찬반여부
  stage : 'default', // day1, day2, night
  players : null, // socket으로 생존 players 가져오는거
  secondVoteUsername : '', // 단두대 오른사람 이름
  myRole : '', // 자기 역할 Citizen or Mafia
  isAlive: true, // 내가 살았나 죽었나
}
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
  myRoom,
  userId,
  onVideo,
  onAudio,
  mafiaManager,
}
