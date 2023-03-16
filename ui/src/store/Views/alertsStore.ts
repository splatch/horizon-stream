import { defineStore } from 'pinia'
import { TimeType, AlertType } from '@/components/Alerts/alerts.constant'
import { useAlertsQueries } from '../Queries/alertsQueries'
import { IAlert } from '@/types/alerts'

export const useAlertsStore = defineStore('alertsStore', () => {
  const severitiesSelected = ref<string[]>([])
  const timeSelected = ref(TimeType.ALL)
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

  const selectTimeFilter = (selected: TimeType): void => {
    timeSelected.value = selected
  }

  return {
    alertList: computed(() => alertList.value),
    allAlertsList: computed(() => allAlertsList.value),
    fetchAlerts,
    severitiesSelected,
    toggleSeverity,
    timeSelected,
    selectTimeFilter
  }
})
