import { defineStore } from 'pinia'
import { useMutation } from 'villus'
import useSpinner from '@/composables/useSpinner'

import saveRoutingKeyMockMutation from '@/graphql/Notifications/saveRoutingKeyMockMutation'

const { startSpinner, stopSpinner } = useSpinner()

export const useNotificationMutations = defineStore('notificationMutations', () => {
  // send pager duty routing key
  const {
    execute: sendPagerDutyRoutingKey,
    isFetching,
    error
  } = useMutation(saveRoutingKeyMockMutation) // TODO: Use real mutation once available

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
