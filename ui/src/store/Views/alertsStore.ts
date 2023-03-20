import { defineStore } from 'pinia'
import { TimeType, AlertType } from '@/components/Alerts/alerts.constant'
import { useAlertsQueries } from '../Queries/alertsQueries'
import { IAlert } from '@/types/alerts'

export const useAlertsStore = defineStore('alertsStore', () => {
  const severitiesSelected = ref<string[]>([])
  const timeSelected = ref()
  const alertsList = ref()
  const allAlertsList = ref()
  const alertsListSearched = ref([])

  const alertsQueries = useAlertsQueries()

  const fetchAlerts = async () => {
    await alertsQueries.fetchAlerts()

    alertsList.value = alertsQueries.fetchAlertsData
    allAlertsList.value = alertsQueries.fetchAlertsData
  }

  const toggleSeverity = (selected: string): void => {
    const exists = severitiesSelected.value.some((s) => s === selected)

    if (exists) {
      severitiesSelected.value = severitiesSelected.value.filter((s) => s !== selected)
    } else {
      severitiesSelected.value.push(selected)
    }
    if (!severitiesSelected.value.length) {
      alertsList.value = allAlertsList.value
    } else {
      alertsList.value = allAlertsList.value.filter((a: IAlert) => severitiesSelected.value.includes(a.severity))
    }
  }

  const selectTime = (selected: TimeType | undefined): void => {
    timeSelected.value = selected
  }

  const clearAllFilters = () => {
    severitiesSelected.value = []
    timeSelected.value = undefined
  }

  const clearAlerts = () => {
    // send query
  }

  const acknowledgedSelectedAlerts = () => {
    // send query
  }

  return {
    alertsList,
    allAlertsList: computed(() => allAlertsList.value),
    fetchAlerts,
    severitiesSelected,
    toggleSeverity,
    timeSelected,
    selectTime,
    clearAllFilters,
    clearAlerts,
    acknowledgedSelectedAlerts
  }
})
