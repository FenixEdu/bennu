import client from './client'

const BASE_URL = '/bennu-admin'

export async function getDomainObject ({ objectId }) {
  const response = await client.get(`${BASE_URL}/domain-objects/${objectId}`)
  return response.data
}
