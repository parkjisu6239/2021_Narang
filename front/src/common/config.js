
// 설정 관련 공용 모듈

// 본 모듈에서 전역적으로 모든 라이브러리에 언어 설정을 공통으로 적용할 경우,
// 아래와 같이 함수를 선언하고 본 모듈에서 common/lib에 정의된 모듈들을 Import(참조) 및 접근하여 언어 Locale을 설정하는 API를 개발할 수 있다.
function changeLocale (locale) {
  console.error(locale)
}

export default {
  changeLocale
}
