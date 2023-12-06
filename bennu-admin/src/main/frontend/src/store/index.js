import Vue from 'vue'
import Vuex from 'vuex'

import * as actions from './actions'
import mutations from './mutations'

Vue.use(Vuex)

const state = {
  topMessage: { active: false, msg: { pt: '', en: '' }, dismiss: false, type: '' },
  progressBar: 0,
  profile: {},
  countries: {}
}

const store = new Vuex.Store({
  state,
  actions,
  mutations
})

export default store
