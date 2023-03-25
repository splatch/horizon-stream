import { defineStore } from 'pinia'
import { useQuery } from 'villus'
// import { AlertsListDocument } from '@/types/graphql-mocks'
import { AlertsDocument } from '@/types/graphql'

export const useAlertsQueries = defineStore('alertsQueries', () => {
  const { data: fetchAlertsData, execute: fetchAlerts } = useQuery({
    query: AlertsDocument,
    variables: {
      filter: '',
      filterValues: [],
      page: '0',
      pageSize: 10,
      sortAscending: true,
      sortBy: ''
    },
    fetchOnMount: false,
    cachePolicy: 'network-only'
  })

  // fetch search alert list

  return {
    fetchAlerts,
    fetchAlertsData: computed(() => fetchAlertsData.value?.findAllAlerts?.alerts || [])
  }
})
