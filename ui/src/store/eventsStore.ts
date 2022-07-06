import { defineStore } from 'pinia'
import { useMutation } from 'villus'
import { CreateEventDocument } from '@/graphql/operations'

export const useEventsStore = defineStore('eventsStore', () => {
  // create event
  const {
    execute: createEvent
  } = useMutation(CreateEventDocument)

  return {
    createEvent
  }
})
