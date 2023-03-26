import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { AlertsDocument, CountAlertsDocument } from '@/types/graphql'

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

  const fetchCountAlerts = async (filter: string, filterValues: [string]) =>
    useQuery({
      query: CountAlertsDocument,
      variables: {
        filter,
        filterValues
      },
      fetchOnMount: false,
      cachePolicy: 'network-only'
    })

  return {
    fetchAlerts,
    fetchAlertsData: computed(() => fetchAlertsData.value?.findAllAlerts?.alerts || []),
    fetchCountAlerts
  }
})
