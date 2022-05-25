import { TokenResponse } from '@/types'
import { isAfter, add } from 'date-fns'

const storedToken = JSON.parse(window.localStorage.getItem('token') || '{}')
const token = useStorage('token', storedToken)
let expiry = new Date()

const useToken = () => {
  const isAuthenticated = computed<boolean>(() => token.value.access_token)

  const setToken = (obj: TokenResponse | null) => {
    token.value = obj || {}
    expiry = add(new Date(), { seconds: token.value.expires_in })
  }

  const isExpired = () => isAfter(new Date(), expiry)

  return { token, setToken, isExpired, isAuthenticated }
}

export default useToken
