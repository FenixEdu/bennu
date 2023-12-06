import Vue from 'vue'
import translationsPt from '@/i18n/pt.json'
import translationsEn from '@/i18n/en.json'

import VueI18n from 'vue-i18n'
import axios from 'axios'

import dayjs from 'dayjs'
import 'dayjs/locale/en'
import 'dayjs/locale/pt'

import { setLocale as updateLocale } from '@/api/profile'
import { parseLocaleTag } from '@/mixins/TranslateApiString'

Vue.use(VueI18n)

/**
 * Sets the requested language
 *
 * @param {string} lang Language to set (eg. 'pt', 'en')
 * @param {object} auth Authentication data provided by the auth mixin (this.$auth)
 */
export async function setLocale (lang, auth) {
  // If the same language
  if (i18n.locale === lang) {
    return setI18nLanguage(lang)
  }

  // Update lang in profile
  await updateLocale({ locale: lang })

  return setI18nLanguage(lang)
}

/**
 * Does all required work to set the language, function does not handle translations async load
 *
 * @param {string} lang Language to set (eg. 'pt', 'en')
 */
function setI18nLanguage (lang) {
  i18n.locale = lang
  dayjs.locale(lang)
  window.localStorage.setItem('language', lang)
  axios.defaults.headers.common['Accept-Language'] = lang
  document.querySelector('html').setAttribute('lang', lang)
  return lang
}

const EuroCurrencyFormat = Object.freeze({
  style: 'currency',
  currency: 'EUR'
})

/** Vue-i18n plugin instance */
const i18n = new VueI18n({
  silentFallbackWarn: true,
  // default locale
  locale: 'pt',
  messages: {
    pt: translationsPt,
    en: translationsEn
  },
  numberFormats: {
    pt: {
      euro: EuroCurrencyFormat
    },
    en: {
      euro: EuroCurrencyFormat
    },
    [parseLocaleTag('pt')]: {
      euro: EuroCurrencyFormat
    },
    [parseLocaleTag('en')]: {
      euro: EuroCurrencyFormat
    }
  }
})

// Set starting language
let language = window.localStorage.getItem('language') || window.navigator.language.split('-')[0]
language = language === 'pt' ? 'pt' : 'en'
setLocale(language)

export default i18n

export function LocalizedString (key, params) {
  return {
    [parseLocaleTag('pt')]: i18n.t(key, 'pt', params),
    [parseLocaleTag('en')]: i18n.t(key, 'en', params)
  }
}
