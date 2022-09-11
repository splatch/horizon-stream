import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { ListDeviceStatusDocument } from '@/types/graphql'

export const useNodeStatusQueries = defineStore('nodeStatusQueries', () => {
  const fetchedEvents = ref()

  const { data, execute } = useQuery({
    query: ListDeviceStatusDocument,
    cachePolicy: 'network-only' // always fetch and do not cache
  })

  watchEffect(() => {
    fetchedEvents.value = {
      events: data.value?.listEvents?.events || [],
      devices: data.value?.listDevices?.devices || [],
      deviceLatency: data.value?.deviceLatency?.data?.result || [],
      deviceUptime: data.value?.deviceUptime?.data?.result || []
    }
  })

  return {
    fetchedEvents,
    fetch: execute
  }
})