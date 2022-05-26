import axios from 'axios'
import useToken from '@/composables/useToken'
import { refreshToken } from './authService'

const api = axios.create({
  baseURL: process.env.API_BASE_URL || import.meta.env.VITE_BASE_URL?.toString()
})

api.interceptors.request.use(
  async (config) => {
    const { token, isExpired } = useToken()

    // if token expired, attempt refresh before call
    if (isExpired()) await refreshToken()

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

export { api }
