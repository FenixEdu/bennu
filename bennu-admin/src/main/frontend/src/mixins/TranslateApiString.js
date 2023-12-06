import isEmpty from 'underscore/modules/isEmpty'

const LOCALE_STRING_FALLBACK = 'pt'

export const PT_LOCALE_TAG = 'pt-PT'
export const EN_LOCALE_TAG = 'en-GB'

const LOCALE_LOCALIZED_STRING_FALLBACK = PT_LOCALE_TAG

export const parseLocaleTag = function (locale) {
  return locale === LOCALE_STRING_FALLBACK ? PT_LOCALE_TAG : EN_LOCALE_TAG
}

const parseLocale = (input, currentLocale) => {
  if (input instanceof Object && (Object.prototype.hasOwnProperty.call(input, PT_LOCALE_TAG) || Object.prototype.hasOwnProperty.call(input, EN_LOCALE_TAG))) {
    return parseLocaleTag(currentLocale)
  }
  return currentLocale
}

export const parseLocaleString = function (locales, input) {
  return Object.fromEntries(locales.map(locale => [locale, (input[parseLocaleTag(locale)] || input[LOCALE_STRING_FALLBACK] || input[LOCALE_LOCALIZED_STRING_FALLBACK] || input)]))
}

export default {
  methods: {
    translate (input) {
      if (input === undefined || input === null || isEmpty(input)) {
        return undefined
      }
      const locale = parseLocale(input, this.$i18n.locale)
      return input[locale] ||
             input[LOCALE_STRING_FALLBACK] ||
             input[LOCALE_LOCALIZED_STRING_FALLBACK] ||
             input
    },
    parseLocaleTag (locale = this.$i18n.locale) {
      return parseLocaleTag(locale)
    }
  }
}
