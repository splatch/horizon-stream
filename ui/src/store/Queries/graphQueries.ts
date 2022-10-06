import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { GetMinionUptimeDocument, } from '@/types/graphql'

export const useGraphQueries = defineStore('graphQueries', {
  state: () => {

    const { data: minionUptime, execute: getMinionUptime, isDone: minionUptimeDataReady } = useQuery({
      query: GetMinionUptimeDocument,
      cachePolicy: 'network-only',
      fetchOnMount: false
    })

    const minionDataSets = computed(() => {
      const sets = []
      if (minionUptime.value) sets.push(minionUptime.value.minionUptime?.data?.result)

      return sets
    })
    
    return {
      getMinionUptime,
      minionUptime,
      minionDataSets,
      minionUptimeDataReady
    }
  }
})
