import { defineStore } from 'pinia'
import { NodeContent } from '@/types/inventory'

export const useInventoryStore = defineStore('inventoryStore', {
  state: () => ({
    isTagManagerOpen: false,
    isFilterOpen: false,
    nodeSelected: [] as NodeContent[],
    isEditMode: false
  }),
  actions: {
    toggleTagManager() {
      this.isTagManagerOpen = !this.isTagManagerOpen
    },
    toggleFilter() {
      this.isFilterOpen = !this.isFilterOpen
    },
    toggleNodeEditMode() {
      this.isEditMode = !this.isEditMode
    },
    resetNodeEditMode() {
      this.isEditMode = false
    },
    addRemoveNodeSelected(node: NodeContent, isSelected: boolean) {
      if (isSelected) this.nodeSelected.push(node)
      else {
        this.nodeSelected = this.nodeSelected.filter(({ id }) => id !== node.id)
      }
    },
    resetSelectedNode() {
      this.nodeSelected = []
    }
  }
})
