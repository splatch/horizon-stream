import { DocumentNode } from 'graphql'
import { defineStore } from 'pinia'
import { useMutation } from 'villus'

export const useNotificationMutations = defineStore('notificationMutations', () => {
  // send pager duty routing key
  const {
    execute: sendPagerDutyRoutingKey
  } = useMutation({} as DocumentNode) // TODO: Use real mutation once available

  return {
    sendPagerDutyRoutingKey
  }
})
