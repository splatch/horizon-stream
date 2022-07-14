import { defineStore } from 'pinia'
import { useMutation } from 'villus'
import useSpinner from '@/composables/useSpinner'

import { SaveRoutingKeyDocument } from '@/types/graphql-mocks'

const { startSpinner, stopSpinner } = useSpinner()

export const useNotificationMutations = defineStore('notificationMutations', () => {
  // send pager duty routing key
  const {
    execute: sendPagerDutyRoutingKey,
    isFetching,
    error
  } = useMutation(SaveRoutingKeyDocument)

  watchEffect(() => {
    if (isFetching.value) {
      startSpinner()
    } else {
      stopSpinner()
    }
  })

  return {
    sendPagerDutyRoutingKey,
    error
  }
})
