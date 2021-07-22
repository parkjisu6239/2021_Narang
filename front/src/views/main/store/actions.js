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
  localStorage.removeItem('user_id')
  return
}

// 회원가입
export function requestSignup({state}, payload) {
  console.log('requestSignup', state, payload)
  const url = '/users'
  let body = payload
  return $axios.post(url, body)
}

// 유저 정보 : 아이디 중복 체크
export function requestCheckDuplicate({state}, payload) {
  console.log('requestCheckDuplicate', state, payload)
  const url = `/users/${payload.id}`
  return $axios.get(url)
}

// 내 프로필
export function requestReadMyInfo({state}) {
  console.log('requestMyInfo', state)
  const headers = {'Authorization': state.accessToken ? `Bearer ${state.accessToken}` : ''} // 토큰
  const url = '/users/me'
  return $axios.get(url, {headers: headers})
}

// 유저 정보 수정
export function requestUpdateMyInfo({state}, payload) {
  console.log('requestMyInfo', state, payload)
  const headers = {'Authorization': state.accessToken ? `Bearer ${state.accessToken}` : ''} // 토큰
  const url = `/users/${state.userId}`
  let body = payload
  console.log(body)
  return $axios.patch(url, body, {headers: headers})
}

// 유저 정보 삭제
export function requestDeleteMyInfo({state}) {
  console.log('requestMyInfo', state)
  const headers = {'Authorization': state.accessToken ? `Bearer ${state.accessToken}` : ''} // 토큰
  const url = `/users/${state.userId}`
  return $axios.delete(url, {headers: headers})
}

