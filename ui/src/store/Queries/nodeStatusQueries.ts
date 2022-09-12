import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { ListDeviceStatusDocument } from '@/types/graphql'

export const useNodeStatusQueries = defineStore('nodeStatusQueries', () => {
  const fetchedData = ref()

  const { data } = useQuery({
    query: ListDeviceStatusDocument
  })

  watchEffect(() => {
    fetchedData.value = {
      events: data.value?.listEvents?.events || [],
      devices: data.value?.listDevices?.devices || [],
      deviceLatency: data.value?.deviceLatency?.data?.result || [],
      deviceUptime: data.value?.deviceUptime?.data?.result || []
    }
  })

  return {
    fetchedData
  }
})