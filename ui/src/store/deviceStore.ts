import { defineStore } from 'pinia'
import deviceService from '@/services/deviceService'
import { Device } from '@/types/appliances'

export const useDeviceStore = defineStore('deviceStore', {
  state: () => ({
    deviceItems: <Device[]>[]
  }),
  getters: {
    gDeviceItems: state => state.deviceItems
  },
  actions: {
    async aGetDevices() {
      try {
        const items: Device[] = await deviceService.sDeviceItems()
        this.deviceItems = items
      } catch (err) {
        this.deviceItems = []
      }
    }
  }
})