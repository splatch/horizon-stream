import { defineStore } from 'pinia'

export const useInventoryStore = defineStore('inventoryStore', {
  state: () => ({
    isTagsOpen: false,
    isFilterOpen: false
  }),
  actions: {
    triggerTagsBox() {
      this.isTagsOpen = !this.isTagsOpen
    },
    toggleFilter() {
      console.log(this.isFilterOpen)
      this.isFilterOpen = !this.isFilterOpen
    }
  }
})
