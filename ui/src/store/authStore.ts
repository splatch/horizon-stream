import { defineStore } from 'pinia'
import API from '@/services'
import useToken from '@/composables/useToken'
import { UserInfo } from '@/types'

const { setToken } = useToken()

interface State {
  userInfo: UserInfo
}

export const useAuthStore = defineStore('authStore', {
  state: () =>
  ({
    userInfo: {}
  } as State),
  actions: {
    async login(username: string, password: string) {
      const token = await API.login(username, password)

      if (token) {
        setToken(token)
      }
    },
    async logout() {
      await API.logout()
    },
    async getUserInfo() {
      const userInfo = await API.getUserInfo()
      if (userInfo) this.userInfo = userInfo
    }
  }
})
