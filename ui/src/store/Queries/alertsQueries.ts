import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { AlertsDocument, CountAlertsDocument, Alert, TimeRange } from '@/types/graphql'
import { AlertsFilters } from '@/types/alerts'

export const useAlertsQueries = defineStore('alertsQueries', () => {
  const fetchAlertsData = ref(<Alert[]>[])

  const fetchAlerts = async (alertsFilters: AlertsFilters) => {
    const { data, execute, isFetching } = useQuery({
      query: AlertsDocument,
      variables: {
        timeRange: alertsFilters.timeRange,
        page: alertsFilters.pagination.page,
        pageSize: alertsFilters.pagination.pageSize,
        search: alertsFilters.search,
        severities: alertsFilters.severities,
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

  const fetchCountAlerts = async (timeRange = TimeRange.All, severityFilters = [] as string[]) =>
    useQuery({
      query: CountAlertsDocument,
      variables: {
        timeRange,
        severityFilters
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
