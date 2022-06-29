import { defineStore } from 'pinia'

export const useLayoutStore = defineStore('layoutStore', {
  state: () => ({
    navRailOpen: false
  }),
  actions: {
    triggerNavRail() {
      this.navRailOpen = !this.navRailOpen
    }
  }
})
