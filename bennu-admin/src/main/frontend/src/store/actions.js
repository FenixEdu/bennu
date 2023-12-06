import * as types from './mutation-types'
import { get as getProfile } from '@/api/profile'
import { fetchSupportedCountries } from '@/api/connect'
import { EN_LOCALE_TAG, PT_LOCALE_TAG } from '@/mixins/TranslateApiString'

export const setTopMessage = ({ commit }, { active, msg, dismiss, type }) => {
  commit(types.SET_TOP_MESSAGE, { active, msg, dismiss, type })
}

export const setProgressBar = ({ commit }, progress) => {
  const clamped = Math.min(Math.max(progress, 0), 100) // clamps value to range 0-100
  commit(types.SET_PROGRESS_BAR_STATE, clamped)
}

export const completeProgressBar = ({ commit }) => {
  commit(types.SET_PROGRESS_BAR_STATE, 100)
}

export const fetchProfile = async ({ commit }) => {
  const profile = await getProfile()
  return commit(types.RECEIVE_PROFILE, { profile })
}

/**
 * Creates a map<country code, localized string> so that we can use TranslateApiString mixin on the countries
 */
export const fetchCountries = async ({ commit }) => {
  // eslint-disable-next-line camelcase
  const [pt, en] = await Promise.all([
    fetchSupportedCountries({ fullLocale: PT_LOCALE_TAG }),
    fetchSupportedCountries({ fullLocale: EN_LOCALE_TAG })
  ])
  const countries = {}
  pt.forEach(country => {
    countries[country.code] = { [PT_LOCALE_TAG]: country.name }
  })
  en.forEach(country => {
    countries[country.code] = { ...countries[country.code], [EN_LOCALE_TAG]: country.name }
  })
  return commit(types.RECEIVE_COUNTRIES, { countries })
}
