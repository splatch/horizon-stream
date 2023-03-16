import { defineStore } from 'pinia'
import { AlertSort, AlertType } from '@/components/Alerts/alerts.constant'
import { useAlertsQueries } from '../Queries/alertsQueries'
import { IAlert } from '@/types/alerts'

export const useAlertsStore = defineStore('alertsStore', () => {
  const severitiesSelected = ref<string[]>([])
  const sortSelected = ref(<AlertSort[]>[])
  const alertList = ref()
  const allAlertsList = ref()
  const alertListSearched = ref([])

  const alertsQueries = useAlertsQueries()

  const fetchAlerts = async () => {
    await alertsQueries.fetchAlerts()

    alertList.value = alertsQueries.fetchAlertsData
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
      alertList.value = allAlertsList.value
    } else {
      alertList.value = allAlertsList.value.filter((a: IAlert) => severitiesSelected.value.includes(a.severity))
    }
  }

  const toggleSort = (selected: AlertSort): void => {
    const exists = sortSelected.value.some((s) => s === selected)

    if (exists) {
      sortSelected.value = sortSelected.value.filter((s) => s !== selected)
    } else {
      sortSelected.value.push(selected)
    }
  }

  return {
    alertList: computed(() => alertList.value),
    allAlertsList: computed(() => allAlertsList.value),
    fetchAlerts,
    severitiesSelected,
    toggleSeverity,
    sortSelected,
    toggleSort
  }
})
