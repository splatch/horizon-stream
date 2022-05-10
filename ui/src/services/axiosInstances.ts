import axios, { AxiosError } from 'axios'
import useToken from '@/composables/useToken'
import { refreshToken } from './authService'

const api = axios.create({
  baseURL: import.meta.env.VITE_BASE_URL?.toString() || '/opennms/api'
})

api.interceptors.request.use(
  (config) => {
    const { token } = useToken()

    const defaultHeaders = {
      Authorization: `Bearer ${token.value.access_token}`,
      'Content-Type': 'application/json'
    }

    config.headers = defaultHeaders

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  (config) => {
    return config
  },
  async (err: AxiosError) => {
    if (err.response?.status === 401) {
      // attempts refresh, logs out if err
      refreshToken()
    }
  }
)

export { api }
