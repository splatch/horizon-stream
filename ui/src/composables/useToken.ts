import { TokenResponse } from '@/types'

const storedToken = JSON.parse(window.localStorage.getItem('token') || '{}')
const token = useStorage('token', storedToken)

const useToken = () => {
  const isAuthenticated = computed<boolean>(() => token.value.access_token)

  const setToken = (obj: TokenResponse | null) => {
    token.value = obj || {}
  }

  return { token, setToken, isAuthenticated }
}

export default useToken
