import VueAxios from 'vue-axios'
import axios from 'axios'
// import config from '../config'

const BASE_URL = '/api/v1'
const DEFAULT_ACCEPT_TYPE = 'application/json; charset=utf-8'

axios.defaults.baseURL = BASE_URL
axios.defaults.headers.common['Content-Type'] = DEFAULT_ACCEPT_TYPE

export default { VueAxios, axios }
