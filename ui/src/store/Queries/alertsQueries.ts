import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { AlertListDocument } from '@/types/graphql-mocks'

export const useAlertsQueries = defineStore('alertsQueries', () => {
  const { data: fetchAlertsData, execute: fetchAlerts } = useQuery({
    query: AlertListDocument,
    fetchOnMount: false,
    cachePolicy: 'network-only'
  })

  // fetch search alert list

  return {
    fetchAlerts,
    fetchAlertsData: computed(() => fetchAlertsData.value?.alertList || [])
  }
})
