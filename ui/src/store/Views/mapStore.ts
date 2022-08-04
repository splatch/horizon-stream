import { defineStore } from 'pinia'
import { FeatherSortObject } from '@/types'
import { Node, Alarm, Coordinates, QueryParameters } from '@/types/map'
import { LatLngBounds } from 'leaflet'
import { SORT } from '@featherds/table'
import { numericSeverityLevel } from '@/components/Map/utils'
import { orderBy } from 'lodash'
import { latLng } from 'leaflet'


export interface State {
  nodesWithCoordinates: Node[]
  alarms: Alarm[]
  interestedNodesID: string[]
  mapCenter: Coordinates
  mapBounds: LatLngBounds | undefined
  selectedSeverity: string
  searchedNodeLabels: string[]
  nodeSortObject: FeatherSortObject
  alarmSortObject: FeatherSortObject
}

export const useMapStore = defineStore('mapStore', {
  state: () => 
    <State>{
      nodesWithCoordinates: [],
      alarms: [],
      interestedNodesID: [],
      mapCenter: { latitude: 37.776603506225115, longitude: -33.43824554266541 },
      mapBounds: undefined,
      selectedSeverity: 'NORMAL',
      searchedNodeLabels: [],
      nodeSortObject: { property: 'label', value: SORT.ASCENDING },
      alarmSortObject: { property: 'id', value: SORT.DESCENDING }
    },
  actions: {
    async getNodes(queryParameters?: QueryParameters) {
      const defaultParams = queryParameters || { limit: 5000, offset: 0 }
      // todo: graphQL request
      
      const resp = [] as Node[]
      if (resp) {
        this.nodesWithCoordinates = resp
      }
    },
    async getAlarms (queryParameters?: QueryParameters) {
      const defaultParams = queryParameters || { limit: 5000, offset: 0 }
      // todo: graphQL request
      
      const resp = [] as Alarm[]
      if (resp) {
        this.alarms = resp
      }
    }
  }
})