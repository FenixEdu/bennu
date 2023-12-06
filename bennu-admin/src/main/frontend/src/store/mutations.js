import * as types from './mutation-types'
import Vue from 'vue'

export default {
  [types.SET_TOP_MESSAGE] (state, { active, msg, dismiss, type }) {
    state.topMessage = { active, msg, dismiss, type }
  },
  [types.SET_PROGRESS_BAR_STATE] (state, progress) {
    state.progressBar = progress
  },
  [types.RECEIVE_PROFILE] (state, { profile }) {
    Vue.set(state, 'profile', profile)
  },
  [types.RECEIVE_COUNTRIES] (state, { countries }) {
    state.countries = countries
  }
}
