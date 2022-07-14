import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { ListDevicesDocument } from '@/types/graphql-mocks'

export const useDeviceQueries = defineStore('deviceQueries', {
  state: () => {
    const { data, execute } = useQuery({
      query: ListDevicesDocument
    })

    const listDevices = computed(() => data.value?.listDevices?.items || [])

    return {
      listDevices,
      fetch: execute
    }
  }
})
