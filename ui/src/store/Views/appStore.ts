import { defineStore } from 'pinia'

export interface State {
  navRailIsOpen: boolean
}

export const useAppStore = defineStore('appStore', {
  state: () => 
    <State>{
      navRailIsOpen: false
    },
  actions: {
    setNavRailOpen(navRailOpen: boolean) {
      this.navRailIsOpen = navRailOpen
    }
  }
})

