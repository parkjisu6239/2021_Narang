// Vuex 조립
import state from './state'
import * as getters from './getters'
import * as mutations from './mutations'
import * as actions from './actions'

const root = {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
}

export default root
