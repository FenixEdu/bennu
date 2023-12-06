import axios from 'axios'
import qs from 'qs'

const client = axios.create({
  baseURL: `/${process.env.VUE_APP_CTX ?? ''}`,
  paramsSerializer (params) {
    return qs.stringify(params, { encodeValuesOnly: true, arrayFormat: 'repeat' })
  }
})

client.interceptors.request.use(function (config) {
  if (!['get', 'head'].includes(config.method.toLowerCase())) {
    config.headers['X-Requested-With'] = 'bennu-admin-frontend'
  }
  return config
})

if (process.env.NODE_ENV === 'development') {
  const corsFix = (config) => {
    // force devServer proxy to avoid cors network errors
    if (config.url.startsWith('http://localhost:8080/')) {
      config.url = config.url.replace('http://localhost:8080', '')
    }
    return config
  }
  axios.interceptors.request.use(corsFix)
  client.interceptors.request.use(corsFix)
}
export async function sendErrorReport ({ email, subject, description, exceptionInfo }) {
  return axios.post(`${process.env.VUE_APP_CTX ?? ''}/api/fenix-ist/support-form`, {
    userAgent: navigator.userAgent,
    email,
    subject,
    description,
    exceptionInfo
  }, { headers: { 'X-Requested-With': 'bennu-admin-frontend' } })
}

export default client
