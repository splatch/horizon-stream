import { defineStore } from 'pinia'
import { useMutation } from 'villus'
import { CreateEventDocument } from '@/types/graphql'

export const useEventMutations = defineStore('eventMutations', () => {
  // create event
  const {
    execute: createEvent
  } = useMutation(CreateEventDocument)

  return {
    createEvent
  }
})
