import client from './client'

export async function findUsers ({ query, maxHits = 5 }) {
  const response = await client.post('/api/bennu-core/users/find', null, {
    params: {
      query,
      maxHits
    }
  })
  return response.data?.users
}
