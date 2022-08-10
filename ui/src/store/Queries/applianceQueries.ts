import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { ListDevicesForTableDocument, ListMinionsForTableDocument, ListMinionsAndDevicesForTablesDocument } from '@/types/graphql'

export const useApplianceQueries = defineStore('applianceQueries', {
  state: () => {
    const tableDevices = ref()
    const tableMinions = ref()
    
    // fetch appliance devices table data
    const fetchDevicesForTable = async () => {
      const { data: deviceData } = await useQuery({
        cachePolicy: 'network-only',
        query: ListDevicesForTableDocument
      })

      tableDevices.value = deviceData?.value?.listDevices?.devices || []
    }

    // fetch appliance minions table data
    const fetchMinionsForTable = () => {
      const { data: minionsData } = useQuery({
        cachePolicy: 'network-only',
        query: ListMinionsForTableDocument
      })

      tableMinions.value = minionsData.value?.listMinions?.minions || []
    }


    // minions AND devices table
    const { data: minionsAndDevices } = useQuery({
      query: ListMinionsAndDevicesForTablesDocument
    })

    watchEffect(() => {
      tableDevices.value = minionsAndDevices.value?.listDevices?.devices || []
      tableMinions.value = minionsAndDevices.value?.listMinions?.minions || []
    })

    return {
      tableDevices,
      tableMinions,
      fetchDevicesForTable,
      fetchMinionsForTable
    }
  }
})
