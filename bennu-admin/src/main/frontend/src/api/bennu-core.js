import client from './client'

const BASE_URL = '/api/bennu-core/domain-browser'

export async function getRelation ({ objectId, relationName }) {
  const response = await client.get(`${BASE_URL}/${objectId}/${relationName}`)
  return response.data
}

export async function getRelationCount ({ objectId, relationName }) {
  const response = await client.get(`${BASE_URL}/${objectId}/${relationName}/count`)
  return response.data
}

export async function getDomainObject ({ objectId }) {
  const response = await client.get(`${BASE_URL}/${objectId}`)
  return response.data
}
