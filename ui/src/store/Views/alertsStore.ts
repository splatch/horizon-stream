import { defineStore } from 'pinia'
import { TimeRange } from '@/types/graphql'
import { useAlertsQueries } from '../Queries/alertsQueries'
import { useAlertsMutations } from '../Mutations/alertsMutations'
import { AlertsFilters } from '@/types/alerts'

const alertsFilterDefault: AlertsFilters = {
  timeRange: TimeRange.All,
  pagination: {
    page: 0, // api base 0 (first page)
    pageSize: 10
  },
  // search: '', // not avail for EAR
  severities: [],
  sortAscending: true,
  sortBy: 'alertId'
}

export const useAlertsStore = defineStore('alertsStore', () => {
  const alertsList = ref()
  const alertsFilter = ref(alertsFilterDefault)
  const alertsListSearched = ref([])

  const alertsQueries = useAlertsQueries()
  const alertsMutations = useAlertsMutations()

  const fetchAlerts = async () => {
    console.log('alertsFilter.value', alertsFilter.value)
    await alertsQueries.fetchAlerts(alertsFilter.value)

    alertsList.value = alertsQueries.fetchAlertsData
  }

  watch(
    alertsFilter,
    () => {
      fetchAlerts()
    },
    { deep: true }
  )

  const toggleSeverity = (selected: string): void => {
    const exists = alertsFilter.value.severities?.some((s) => s === selected)

    if (exists) {
      alertsFilter.value = {
        ...alertsFilter.value,
        severities: alertsFilter.value.severities?.filter((s) => s !== selected)
      }

      if (!alertsFilter.value.severities?.length) alertsFilter.value = { ...alertsFilter.value, severities: [] }
    } else {
      alertsFilter.value = {
        ...alertsFilter.value,
        severities: [...(alertsFilter.value.severities as string[]), selected]
      }
    }
  }

  const selectTime = (selected: TimeRange): void => {
    alertsFilter.value = {
      ...alertsFilter.value,
      timeRange: selected
    }
  }

  const setPage = (page: number): void => {
    const apiPage = page - 1 // pagination component base 1; hence first page is 1 - 1 = 0 for BE payload

    if (apiPage !== Number(alertsFilter.value.pagination.page)) {
      alertsFilter.value = {
        ...alertsFilter.value,
        pagination: {
          ...alertsFilter.value.pagination,
          page: apiPage
        }
      }
    }
  }

  const setPageSize = (pageSize: number): void => {
    if (pageSize !== alertsFilter.value.pagination.pageSize) {
      alertsFilter.value = {
        ...alertsFilter.value,
        pagination: {
          page: 0,
          pageSize
        }
      }
    }
  }

  const clearAllFilters = (): void => {
    alertsFilter.value = {
      timeRange: TimeRange.All,
      pagination: {
        page: 0,
        pageSize: 10
      },
      // search: '', // not avail for EAR
      severities: [],
      sortAscending: true,
      sortBy: 'alertId'
    }
  }

  const clearSelectedAlerts = async () => {
    // console.log('alertsSelected',alertsSelected)
    // await alertsMutations.clearAlerts(alertsSelected)
    await alertsMutations.clearAlerts({ ids: [1] })

    fetchAlerts()
  }

  const acknowledgeSelectedAlerts = async () => {
    // console.log('alertsSelected',alertsSelected)
    // await alertsMutations.acknowledgeAlerts(alertsSelected)
    await alertsMutations.acknowledgeAlerts({ ids: [1] })

    fetchAlerts()
  }

  return {
    alertsList,
    fetchAlerts,
    alertsFilter,
    toggleSeverity,
    selectTime,
    setPage,
    setPageSize,
    clearAllFilters,
    clearSelectedAlerts,
    acknowledgeSelectedAlerts
  }
})
