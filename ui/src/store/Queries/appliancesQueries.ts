import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { DevicesQuery } from '@/types/appliances'

export const useAppliancesQueries = defineStore('appliancesQueries', {
  state: () => {
    const { data, execute } = useQuery({
      query: DevicesQuery
    })

    const listDevices = computed(() => data.value?.listDevices?.devices || [])

    return {
      listDevices,
      fetch: execute
    }
  }
})
