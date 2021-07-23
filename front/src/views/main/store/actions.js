// API
import $axios from 'axios'

// 로그인
export function requestLogin({state}, payload) {
  console.log('requestLogin', state, payload)
  const url = '/auth/login'
  let body = payload
  return $axios.post(url, body)
}

// 로그아웃
export function requestLogout({state}) {
  console.log('requestLogout', state)
  localStorage.removeItem('access_token')
  localStorage.removeItem('email')
  return
}

// 회원가입
export function requestSignup({state}, payload) {
  console.log('requestSignup', state, payload)
  const url = '/user'
  let body = payload
  return $axios.post(url, body)
}

// 유저 정보 : 이메일 중복 체크
export function requestEmailCheck({state}, payload) {
  console.log('requestEmailCheck', state, payload)
  const url = `/user/chkemail/${payload.email}`
  return $axios.get(url)
}

// 유저 정보 : 이름 중복 체크
export function requestNameCheck({state}, payload) {
  console.log('requestNameCheck', state, payload)
  const url = `/user/chkusername/${payload.username}`
  return $axios.get(url)
}

// 내 프로필
export function requestReadMyInfo({state}) {
  console.log('requestMyInfo', state)
  const headers = {'Authorization': state.accessToken ? `Bearer ${state.accessToken}` : ''} // 토큰
  const url = '/user'
  return $axios.get(url, {headers: headers})
}

// 유저 정보 수정
export function requestUpdateMyInfo({state}, payload) {
  console.log('requestMyInfo', state, payload)
  const headers = {'Authorization': state.accessToken ? `Bearer ${state.accessToken}` : ''} // 토큰
  const url = '/user'
  let body = payload
  console.log(body)
  return $axios.patch(url, body, {headers: headers})
}

// 유저 정보 삭제
export function requestDeleteMyInfo({state}) {
  console.log('requestMyInfo', state)
  const headers = {'Authorization': state.accessToken ? `Bearer ${state.accessToken}` : ''} // 토큰
  const url = '/user'
  return $axios.delete(url, {headers: headers})
}

