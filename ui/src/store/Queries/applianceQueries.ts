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
      const minionLatencies = data.value?.minionLatency?.data?.result || []
      const minionUptimes = data.value?.minionUptime?.data?.result || []

      const latenciesMap: Record<string, number> = {}
      const uptimesMap: Record<string, number> = {}

      for (const latency of minionLatencies) {
        latenciesMap[latency?.metric?.instance] = latency?.value?.[1] || 0
      }

      for (const uptime of minionUptimes) {
        uptimesMap[uptime?.metric?.instance] = uptime?.value?.[1] || 0
      }

      for (const minion of minions) {
        minion.icmp_latency = latenciesMap[minion.id as string]
        minion.snmp_uptime = uptimesMap[minion.id as string]
      }

      return minions
    }

    watchEffect(() => {
      tableMinions.value = addMetricsToMinions(minionsAndDevices)
      tableDevices.value = minionsAndDevices.value?.listDevices?.devices || []
    })

    const locations = computed(() => minionsAndDevices.value?.listLocations?.locations?.map((item, index) => ({ id: index, name: item.locationName })) || [])
    
    return {
      tableDevices,
      tableMinions,
      locations,
      fetchDevicesForTable,
      fetchMinionsForTable
    }
  }
})
