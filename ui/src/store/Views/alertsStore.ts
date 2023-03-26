import { defineStore } from 'pinia'
import { TimeType } from '@/components/Alerts/alerts.constant'
import { useAlertsQueries } from '../Queries/alertsQueries'
import { useAlertsMutations } from '../Mutations/alertsMutations'
import { AlertsFilters } from '@/types/alerts'

const alertsFilterDefault: AlertsFilters = {
  filter: 'severity',
  filterValues: ['CRITICAL'],
  time: TimeType.ALL,
  search: '',
  pagination: {
    page: '0', // api base 0 (first page)
    pageSize: 10
  },
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
    const exists = alertsFilter.value.filterValues.some((s) => s === selected)

    if (exists) {
      alertsFilter.value = {
        ...alertsFilter.value,
        filterValues: alertsFilter.value.filterValues.filter((s) => s !== selected)
      }

      if (!alertsFilter.value.filterValues.length) alertsFilter.value = { ...alertsFilter.value, filter: '' }
    } else {
      alertsFilter.value = {
        ...alertsFilter.value,
        filter: 'severity',
        filterValues: [...alertsFilter.value.filterValues, selected]
      }
    }
  }

  const selectTime = (selected: TimeType): void => {
    alertsFilter.value = {
      ...alertsFilter.value,
      time: selected
    }
  }

  const setPage = (page: number): void => {
    const apiPage = page - 1 // pagination component base 1; hence first page is 1 - 1 = 0 for BE payload

    if (apiPage !== Number(alertsFilter.value.pagination.page)) {
      alertsFilter.value = {
        ...alertsFilter.value,
        pagination: {
          ...alertsFilter.value.pagination,
          page: apiPage.toString()
        }
      }
    }
  }

  const setPageSize = (pageSize: number): void => {
    if (pageSize !== alertsFilter.value.pagination.pageSize) {
      alertsFilter.value = {
        ...alertsFilter.value,
        pagination: {
          page: '0',
          pageSize
        }
      }
    }
  }

  const clearAllFilters = (): void => {
    alertsFilter.value = {
      filter: '',
      filterValues: [],
      time: TimeType.ALL,
      search: '',
      pagination: {
        page: '0',
        pageSize: 10
      },
      sortAscending: true,
      sortBy: 'alertId'
    }
  }

  const clearSelectedAlerts = async () => {
    // console.log('alertsSelected',alertsSelected)
    // await alertsMutations.clearAlerts(alertsSelected)
    await alertsMutations.clearAlerts({ alertId: 1 })

    fetchAlerts()
  }

  const acknowledgeSelectedAlerts = async () => {
    // console.log('alertsSelected',alertsSelected)
    // await alertsMutations.acknowledgeAlerts(alertsSelected)
    await alertsMutations.acknowledgeAlerts({ alertId: 1 })

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
