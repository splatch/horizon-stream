import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { FeatherSortObject } from '@/types'
import { DeviceDto, AlarmDto, ListDevicesForMapDocument, LocationDto } from '@/types/graphql'
import { LatLngBounds, LatLngLiteral } from 'leaflet'
import { SORT } from '@featherds/table'
import { numericSeverityLevel } from '@/components/Map/utils'

export interface State {
  fetchedDevices: DeviceDto[]
  devicesWithCoordinates: DeviceDto[]
  alarms: AlarmDto[]
  interestedDevicesID: string[]
  mapCenter: LocationDto
  mapBounds: LatLngBounds | undefined
  selectedSeverity: string
  searchedDeviceLabels: string[]
  deviceSortObject: FeatherSortObject
  alarmSortObject: FeatherSortObject
}

export const useMapStore = defineStore('mapStore', {
  state: () => 
    <State>{
      fetchedDevices: [],
      devicesWithCoordinates: [],
      alarms: [],
      interestedDevicesID: [],
      mapCenter: { latitude: 37.776603506225115, longitude: -33.43824554266541 },
      mapBounds: undefined,
      selectedSeverity: 'NORMAL',
      searchedDeviceLabels: [],
      deviceSortObject: { property: 'label', value: SORT.ASCENDING },
      alarmSortObject: { property: 'id', value: SORT.DESCENDING }
    },
  actions: {
    async fetchDevices() {
      const { data: devices } = await useQuery({
        query: ListDevicesForMapDocument
      })
      
      this.fetchedDevices = devices?.value?.listDevices?.devices || []
      this.devicesWithCoordinates = this.fetchedDevices.filter(device => device.location?.latitude && device.location?.longitude)

    },
    async fetchAlarms() {
      const resp: AlarmDto[] = []
    
      if (resp) {
        this.alarms = resp
      }

      return this.alarms
    },
    async modifyAlarm(alarmQueryVariable: AlarmModificationQueryVariable) {
      // todo: add graphQL query
      const resp = {}
      return resp
    },
    filterNodesInBounds() {
      this.devicesWithCoordinates = this.fetchedDevices.filter((device: DeviceDto) => {
        const location: LatLngLiteral = {
          lat: device.location?.latitude || -9999999.99,
          lng: device.location?.longitude || -9999999.99
        }
        return this.mapBounds?.contains(location)
      })
    }
  },
  getters: {
    getNodeAlarmSeverityMap(state: State): Record<string, string | undefined>{
      const map: { [x: string]: string | undefined } = {}
    
      state.alarms.forEach((alarm: AlarmDto) => {
        if(alarm.nodeLabel && numericSeverityLevel(alarm.severity) > numericSeverityLevel(map[alarm.nodeLabel])) {
          map[alarm.nodeLabel] = alarm.severity?.toUpperCase()
        }
      })
    
      return map
    }
  }
})
