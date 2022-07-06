import { defineStore } from 'pinia'
import { useMutation } from 'villus'
import { ClearAlarmDocument } from '@/graphql/operations'

export const useAlarmsStore = defineStore('alarmsStore', () => {
  // clear alarm
  const {
    execute: clearAlarm
  } = useMutation(ClearAlarmDocument)

  return {
    clearAlarm
  }
})
