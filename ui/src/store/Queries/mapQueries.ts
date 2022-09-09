import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { DeviceForMapDocument } from '@/types/graphql'

export const useMapQueries = defineStore('mapQueries', () => {
  const devices = ref()
  const areDevicesFetched = ref()

  const { data, execute } = useQuery({
    query: DeviceForMapDocument,
    cachePolicy: 'network-only' // always fetch and do not cache
  })

  watchEffect(() => {
    const devicesData = data.value?.listDevices?.devices
    devices.value = devicesData || []
    areDevicesFetched.value = devicesData ? true : false
  })

  return {
    devices,
    areDevicesFetched,
    fetch: execute
  }
})