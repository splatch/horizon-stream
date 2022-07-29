import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { ListDevicesDocument } from '@/types/graphql'

export const useDeviceQueries = defineStore('deviceQueries', {
  state: () => {
    const { data, execute } = useQuery({
      query: ListDevicesDocument
    })

    const listDevices = computed(() => data.value?.listDevices?.devices || [])

    return {
      listDevices,
      fetch: execute
    }
  }
})
