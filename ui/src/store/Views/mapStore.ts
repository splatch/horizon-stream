import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { FeatherSortObject } from '@/types'
import { QueryParameters, AlarmModificationQueryVariable } from '@/types/map'
import { DeviceDto, AlarmDto, ListDevicesForMapDocument, LocationDto } from '@/types/graphql'
import { LatLngBounds, Map } from 'leaflet'
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

      this.fetchedDevices = this.devicesWithCoordinates = devices?.value?.listDevices?.devices || []
      this.fetchedDevices = this.devicesWithCoordinates = [
        {
          'id': 1,
          'label': 'Ottawa',
          'foreignSource': 'Foreign Source',
          'foreignId': 'ID',
          'labelSource': 'Label Source',
          'sysOid': 'SysO ID',
          'sysName': 'Sys Name',
          'sysDescription': 'Sys Description',
          'sysContact': 'Sys Contact',
          'sysLocation': 'Sys Location',
          'location': {
            'latitude': 45.41767120361328,
            'longitude': -75.6946105957031
          }
        },
        {
          'id': 2,
          'label': 'California',
          'foreignSource': 'Foreign Source',
          'foreignId': 'ID',
          'labelSource': 'Label Source',
          'sysOid': 'SysO ID',
          'sysName': 'Sys Name',
          'sysDescription': 'Sys Description',
          'sysContact': 'Sys Contact',
          'sysLocation': 'Sys Location',
          'location': {
            'latitude': 36.53391143881437,
            'longitude': -119.7084971887121
          }
        },
        {
          'id': 3,
          'label': 'France',
          'foreignSource': 'Foreign Source',
          'foreignId': 'ID',
          'labelSource': 'Label Source',
          'sysOid': 'SysO ID',
          'sysName': 'Sys Name',
          'sysDescription': 'Sys Description',
          'sysContact': 'Sys Contact',
          'sysLocation': 'Sys Location',
          'location': {
            'latitude': 46.691974355366646,
            'longitude': 2.3779300253682836
          }
        }
      ]
    },
    fetchAlarms (queryParameters?: QueryParameters) {
      const defaultParams = queryParameters || { limit: 5000, offset: 0 }
      // todo: add graphQL query
      const resp = [] as AlarmDto[]
    
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
        const location = {
          lat: device.location?.latitude || -9999999.99,
          lng: device.location?.longitude || -9999999.99
        }
        return this.mapBounds?.contains(location)
      })
    }
  },
  getters: {
    getNodeAlarmSeverityMap(state: State): Record<string, string | undefined>{
      const map: { [x: string ]: string | undefined} = {}
    
      state.alarms.forEach((alarm: AlarmDto) => {
        if(alarm.nodeLabel && numericSeverityLevel(alarm.severity) > numericSeverityLevel(map[alarm.nodeLabel])) {
          map[alarm.nodeLabel] = alarm.severity?.toUpperCase()
        }
      })
    
      return map
    }
  }
})
