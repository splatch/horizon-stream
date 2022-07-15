import { defineStore } from 'pinia'
import { useMutation } from 'villus'
import useSpinner from '@/composables/useSpinner'

import { SaveDeviceDocument } from '@/types/graphql-mocks'

const { startSpinner, stopSpinner } = useSpinner()

export const useDeviceMutations = defineStore('deviceMutations', () => {
  const {
    execute: saveDevice,
    isFetching,
    error
  } = useMutation(SaveDeviceDocument)

  watchEffect(() => {
    if (isFetching.value) {
      startSpinner()
    } else {
      stopSpinner()
    }
  })

  return {
    saveDevice,
    error
  }
})
