import client from './client'

const BASE_URL = '/bennu-admin'

export async function listDomainClasses ({ query, page, perPage } = {}) {
  const params = {
    ...(query && { query }),
    ...(perPage !== undefined && page !== undefined && { skip: (page - 1) * perPage, limit: perPage })
  }
  const response = await client.get(`${BASE_URL}/domain-objects/classes`, { params })
  return response.data
}

export async function getDomainObject ({ objectId }) {
  const response = await client.get(`${BASE_URL}/domain-objects/${objectId}`)
  return response.data
}

export async function deleteDomainObject ({ objectId }) {
  const response = await client.delete(`${BASE_URL}/domain-objects/${objectId}`)
  return response.data
}

export async function editDomainObject ({ objectId, data }) {
  const response = await client.post(`${BASE_URL}/domain-objects/${objectId}/edit`, data)
  return response.data
}

export async function getDomainObjectMeta ({ objectId }) {
  const response = await client.get(`${BASE_URL}/domain-objects/${objectId}/meta`)
  return response.data
}

export async function getDomainObjectForm ({ objectId }) {
  const response = await client.get(`${BASE_URL}/domain-objects/${objectId}/form`)
  return response.data
}

export async function listDomainObjectSlots ({ objectId, query, page, perPage } = {}) {
  const params = {
    ...(query && { query }),
    ...(perPage !== undefined && page !== undefined && { skip: (page - 1) * perPage, limit: perPage })
  }
  const response = await client.get(`${BASE_URL}/domain-objects/${objectId}/slots`, { params })
  return response.data
}

export async function listDomainObjectRoles ({ objectId, query, page, perPage } = {}) {
  const params = {
    ...(query && { query }),
    ...(perPage !== undefined && page !== undefined && { skip: (page - 1) * perPage, limit: perPage })
  }
  const response = await client.get(`${BASE_URL}/domain-objects/${objectId}/roles`, { params })
  return response.data
}

export async function listDomainObjectRoleSets ({ objectId, query, page, perPage } = {}) {
  const params = {
    ...(query && { query }),
    ...(perPage !== undefined && page !== undefined && { skip: (page - 1) * perPage, limit: perPage })
  }
  const response = await client.get(`${BASE_URL}/domain-objects/${objectId}/role-sets`, { params })
  return response.data
}

export async function getDomainObjectRoleSet ({ objectId, roleSetName, query, page, perPage } = {}) {
  const params = {
    ...(query && { query }),
    ...(perPage !== undefined && page !== undefined && { skip: (page - 1) * perPage, limit: perPage })
  }
  const response = await client.get(`${BASE_URL}/domain-objects/${objectId}/role-sets/${roleSetName}`, { params })
  return response.data
}

export async function getDomainObjectRoleSetCount ({ objectId, roleSetName } = {}) {
  const response = await client.get(`${BASE_URL}/domain-objects/${objectId}/role-sets/${roleSetName}/count`)
  return response.data
}
