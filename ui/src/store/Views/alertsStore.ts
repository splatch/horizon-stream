import { defineStore } from 'pinia'
import { TimeType } from '@/components/Alerts/alerts.constant'
import { useAlertsQueries } from '../Queries/alertsQueries'
import { useAlertsMutations } from '../Mutations/alertsMutations'
import { AlertsFilter } from '@/types/alerts'

export const useAlertsStore = defineStore('alertsStore', () => {
  const alertsList = ref()
  const alertsSelected = <number[]>[]
  const alertsFilter = <AlertsFilter>{}
  const severitiesSelected = ref<string[]>([])
  const timeSelected = ref()
  const alertsListSearched = ref([])

  const alertsQueries = useAlertsQueries()
  const alertsMutations = useAlertsMutations()

  const fetchAlerts = async () => {
    await alertsQueries.fetchAlerts()

    alertsList.value = alertsQueries.fetchAlertsData
  }

  const toggleSeverity = (selected: string): void => {
    const exists = severitiesSelected.value.some((s) => s === selected)

    if (exists) {
      severitiesSelected.value = severitiesSelected.value.filter((s) => s !== selected)
    } else {
      severitiesSelected.value.push(selected)
    }
  }

  const selectTime = (selected: TimeType | undefined): void => {
    alertsFilter.time = selected
  }

  const clearAllFilters = () => {
    severitiesSelected.value = []
    timeSelected.value = undefined
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
    alertsSelected,
    severitiesSelected,
    toggleSeverity,
    timeSelected,
    selectTime,
    clearAllFilters,
    clearSelectedAlerts,
    acknowledgeSelectedAlerts
  }
})
