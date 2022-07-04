import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import dashboardViewQuery from '@/graphql/dashboardViewQuery'
import { Query } from '@/graphql/generatedTypes'
import useSpinner from '@/composables/useSpinner'
import useSnackbar from '@/composables/useSnackbar'

const { startSpinner, stopSpinner } = useSpinner()
const { showSnackbar } = useSnackbar()

export const useDashboardViewStore = defineStore('dashboardViewStore', () => {
  const { data, isFetching, execute, error } = useQuery<Query>({
    query: dashboardViewQuery
  })

  const alarms = computed(() => data.value?.listAlarms?.alarm || [])

  // start / stop loading spinner
  watchEffect(() => {
    if (isFetching.value) {
      startSpinner()
    } else {
      stopSpinner()
    }
  })

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
    isFetching,
    fetch: execute
  }
})
