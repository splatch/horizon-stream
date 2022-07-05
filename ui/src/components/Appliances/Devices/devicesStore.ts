import { defineStore } from 'pinia'
import { getDevices } from './devicesService'
import { Device } from './devicesTypes'

export const useDevicesStore = defineStore('devicesStore', {
  state: () => ({
    deviceList: <Device[]>[]
  }),
  getters: {
    getDeviceList: state => state.deviceList
  },
  actions: {
    async getDevices() {
      try {
        const list: Device[] = await getDevices()
        
        this.deviceList = list
      } catch (err) {
        this.deviceList = []
      }
    }
  }
})