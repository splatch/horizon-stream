import { defineStore } from 'pinia'
import { AlertSort, AlertType } from '@/components/Alerts/alerts.enum'

export const useAlertsStore = defineStore('alertsStore', () => {
  const severitiesSelected = ref([AlertType.CRITICAL]) // default
  const sortSelected = ref(<AlertSort[]>[])
  const alertList = ref([])
  const alertListSearched = ref([])

  const toggleSeverity = (selected: AlertType): void => {
    const exists = severitiesSelected.value.some((s) => s === selected)

    if (exists) {
      severitiesSelected.value = severitiesSelected.value.filter((s) => s !== selected)
    } else {
      severitiesSelected.value.push(selected)
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
    severitiesSelected,
    toggleSeverity,
    sortSelected,
    toggleSort
  }
})
