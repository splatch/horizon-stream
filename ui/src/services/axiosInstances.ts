import axios, { AxiosError } from 'axios'
import useKeycloak from '@/composables/useKeycloak'

const api = axios.create({
  baseURL: import.meta.env.VITE_BASE_URL?.toString() || '/opennms/api',
  withCredentials: true
})

api.interceptors.request.use((config) => {
  const { keycloak } = useKeycloak()

  const defaultHeaders = {
    'Authorization': `Bearer ${keycloak.value?.token}`,
    'Content-Type': 'application/json'
  }

  config.headers = defaultHeaders

  return config
}, (error) => {
  return Promise.reject(error)
})

api.interceptors.response.use((config) => { return config }, 
async (err: AxiosError) => {
  const { keycloak } = useKeycloak()
  if (err.response?.status === 401) {
    await keycloak.value?.logout()
  }
})

export { api }
