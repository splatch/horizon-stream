import { defineStore } from 'pinia'
import { sDeviceItems } from './devicesService'
import { Device } from './devicesTypes'

export const useDevicesStore = defineStore('devicesStore', {
  state: () => ({
    deviceItems: <Device[]>[]
  }),
  getters: {
    gDeviceItems: state => state.deviceItems
  },
  actions: {
    async aGetDevices() {
      try {
        const items: Device[] = await sDeviceItems()
        
        this.deviceItems = items
      } catch (err) {
        this.deviceItems = []
      }
    }
  }
})