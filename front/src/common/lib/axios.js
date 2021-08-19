import VueAxios from 'vue-axios'
import axios from 'axios'
// import config from '../config'

const DEFAULT_ACCEPT_TYPE = 'application/json; charset=utf-8; application/x-www-form-urlencoded;'
axios.defaults.headers.common['Content-Type'] = DEFAULT_ACCEPT_TYPE
export default {VueAxios, axios}
