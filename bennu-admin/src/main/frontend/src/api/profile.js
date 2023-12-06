import client from './client'
import { CONNECT_BASE_URL } from './connect'

export async function get () {
  const response = await client.get(`${CONNECT_BASE_URL}/profile`)
  return response.data
}

export async function setLocale ({ locale }) {
  const localeTag = locale === 'pt' ? 'pt-PT' : 'en-GB'
  await client.post(`/api/bennu-core/profile/locale/${localeTag}`)
}
