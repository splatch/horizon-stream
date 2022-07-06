import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { DashboardDocument, DashboardQuery } from '@/graphql/operations'
import useSnackbar from '@/composables/useSnackbar'

const { showSnackbar } = useSnackbar()

export const useDashboardViewStore = defineStore('dashboardViewStore', () => {
  const { data, execute, error } = useQuery<DashboardQuery>({
    query: DashboardDocument
  })

  const alarms = computed(() => data.value?.listAlarms?.alarms || [])

  // handle error messages
  watchEffect(() => {
    if (error?.value?.message) {
      showSnackbar({
        msg: error.value.message
      })
    }
  })

  return {
    alarms,
    fetch: execute
  }
})
