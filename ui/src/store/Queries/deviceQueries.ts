import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { DevicesQuery } from '@/types/device'

export const useDeviceQueries = defineStore('deviceQueries', {
  state: () => {
    const { data, execute } = useQuery({
      query: DevicesQuery
    })

    const listDevices = computed(() => data.value?.listDevices?.items || [])

    return {
      listDevices,
      fetch: execute
    }
  }
})
