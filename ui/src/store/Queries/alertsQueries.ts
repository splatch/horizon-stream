import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { AlertsDocument, CountAlertsDocument, Alert } from '@/types/graphql'
import { AlertsFilters } from '@/types/alerts'

export const useAlertsQueries = defineStore('alertsQueries', () => {
  const fetchAlertsData = ref(<Alert[]>[])

  const fetchAlerts = async (alertsFilters: AlertsFilters) => {
    const { data, execute, isFetching } = useQuery({
      query: AlertsDocument,
      variables: {
        filter: alertsFilters.filter,
        filterValues: alertsFilters.filterValues,
        page: alertsFilters.pagination.page,
        pageSize: alertsFilters.pagination.pageSize,
        sortAscending: alertsFilters.sortAscending,
        sortBy: alertsFilters.sortBy
      },
      fetchOnMount: false,
      cachePolicy: 'network-only'
    })

    await execute()

    if (!isFetching.value) {
      fetchAlertsData.value = data.value?.findAllAlerts?.alerts || []
    }
  }

  const fetchCountAlerts = async (filter = '', filterValues = [] as string[]) =>
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
    fetchAlertsData,
    fetchCountAlerts
  }
})
