import { defineStore } from 'pinia'
import API from '@/services'
import { UserInfo } from '@/types'

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
      await API.login(username, password)

      const userInfo = await API.getUserInfo()
      if (userInfo) this.userInfo = userInfo
    },
    async logout() {
      await API.logout()
    },
    async getUserInfo() {
      let userInfo = await API.getUserInfo()

      // if unable to access
      if (!userInfo) {
        // attempt to refresh the token
        const success = await API.refreshToken()
        // call for user data again
        if (success) userInfo = await API.getUserInfo()
      }
      if (userInfo) this.userInfo = userInfo
    }
  }
})
