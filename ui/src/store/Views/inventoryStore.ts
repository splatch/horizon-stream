import { defineStore } from 'pinia'

export const useInventoryStore = defineStore('inventoryStore', {
  state: () => ({
    isTaggingBoxOpen: false,
    isFilterOpen: false
  }),
  actions: {
    toggleTaggingBox() {
      this.isTaggingBoxOpen = !this.isTaggingBoxOpen
    },
    toggleFilter() {
      this.isFilterOpen = !this.isFilterOpen
    }
  }
})
