import { defineStore } from 'pinia'
import { useMutation } from 'villus'
import { ClearAlarmDocument } from '@/graphql/operations'
import useSnackbar from '@/composables/useSnackbar'

const { showSnackbar } = useSnackbar()

export const useAlarmsStore = defineStore('alarmsStore', () => {
  // clear alarm
  const {
    execute: clearAlarm,
    error: clearAlarmError
  } = useMutation(ClearAlarmDocument)

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
