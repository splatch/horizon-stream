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
    addSelectedNode(node: NodeContent) {
      this.nodeSelected.push(node)
    },
    resetSelectedNode() {
      this.nodeSelected = []
    },
    toggleNodeEditMode() {
      this.isEditMode = !this.isEditMode
    },
    resetNodeEditMode() {
      this.isEditMode = false
    },
    setNodeSelection(node: NodeContent, isSelected: boolean) {
      if (isSelected) this.nodeSelected.push(node)
      else {
        this.nodeSelected = this.nodeSelected.filter(({ id }) => id !== node.id)
      }
    }
  }
})
