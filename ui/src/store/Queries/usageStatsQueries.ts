import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { UsageStatsReportDocument } from '@/types/graphql'

export const useUsageStatsQueries = defineStore('usageStatsQueries', () => {
  const { data, execute } = useQuery({
    query: UsageStatsReportDocument,
    cachePolicy: 'network-only'
  })

  return {
    usageStatsReport: computed(() => data.value?.usageStatsReport || {}),
    fetch: execute
  }
})
