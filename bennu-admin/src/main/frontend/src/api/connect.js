import { EN_LOCALE_TAG, PT_LOCALE_TAG } from '@/mixins/TranslateApiString'
import client from './client'

export const CONNECT_APP_BASE_URL = '/fenixedu-connect'
export const CONNECT_BASE_URL = '/connect'

export async function fetchSupportedCountries ({ locale, fullLocale }) {
  const localeTag = fullLocale ?? (locale === 'pt' ? PT_LOCALE_TAG : EN_LOCALE_TAG)
  const response = await client.get(`${CONNECT_BASE_URL}/resources/supported-countries/${localeTag}`)
  return response.data
}

export async function fetchSupportEmail () {
  const response = await client.get(`${CONNECT_BASE_URL}/resources/support-email`)
  return response.data
}

export async function findAccounts ({ query, maxHits = 5 }) {
  const response = await client.get(`${CONNECT_BASE_URL}/accounts/find`, {
    params: {
      query,
      maxHits
    }
  })
  return response.data
}
