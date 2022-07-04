import { defineStore } from 'pinia'
import { useMutation } from 'villus'
import { ClearAlarmDocument } from '@/graphql/operations'
import useSpinner from '@/composables/useSpinner'
import useSnackbar from '@/composables/useSnackbar'

const { startSpinner, stopSpinner } = useSpinner()
const { showSnackbar } = useSnackbar()

export const useAlarmsStore = defineStore('alarmsStore', () => {
  // clear alarm
  const {
    execute: clearAlarm,
    error: clearAlarmError,
    isFetching: clearAlarmFetching
  } = useMutation(ClearAlarmDocument)

  // start / stop loading spinner
  watchEffect(() => {
    if (clearAlarmFetching.value) {
      startSpinner()
    } else {
      stopSpinner()
    }
  })

  // handle error messages
  watchEffect(() => {
    if (clearAlarmError?.value?.message) {
      showSnackbar({
        msg: clearAlarmError.value.message
      })
    }
  })

  return {
    clearAlarm
  }
})
