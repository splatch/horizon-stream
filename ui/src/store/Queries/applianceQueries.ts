import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { ListDevicesForTableDocument, ListMinionsForTableDocument, ListMinionsAndDevicesForTablesDocument, ListMinionsForTableQuery } from '@/types/graphql'
import { ExtendedMinionDTO } from '@/types/minion'
import { Ref } from 'vue'

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

      tableMinions.value = addMetricsToMinions(minionsData)
    }


    // minions AND devices table
    const { data: minionsAndDevices } = useQuery({
      query: ListMinionsAndDevicesForTablesDocument
    })

    const addMetricsToMinions = (data: Ref<ListMinionsForTableQuery | null>)=> {
      const minions = data.value?.listMinions?.minions as ExtendedMinionDTO[] || []
      const minionLatency = data.value?.minionLatency?.data?.result?.[0]?.value?.[1] || 0
      const minionUptime = data.value?.minionUptime?.data?.result?.[0]?.value?.[1] || 0

      if(minions[0]) {
        minions[0].icmp_latency = minionLatency
        minions[0].snmp_uptime = minionUptime
      }

      return minions
    }

    watchEffect(() => {
      tableMinions.value = addMetricsToMinions(minionsAndDevices)
      tableDevices.value = minionsAndDevices.value?.listDevices?.devices || []
    })

    return {
      tableDevices,
      tableMinions,
      fetchDevicesForTable,
      fetchMinionsForTable
    }
  }
})
