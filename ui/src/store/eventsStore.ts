import { defineStore } from 'pinia'
import { useMutation } from 'villus'
import { Mutation } from '@/graphql/generatedTypes'
import createEventMutation from '@/graphql/Events/createEventMutation'
import useSpinner from '@/composables/useSpinner'
import useSnackbar from '@/composables/useSnackbar'

const { startSpinner, stopSpinner } = useSpinner()
const { showSnackbar } = useSnackbar()

export const useEventsStore = defineStore('eventsStore', () => {
  // create event
  const {
    execute: createEvent,
    error: createEventError,
    isFetching: createEventFetching
  } = useMutation<Mutation>(createEventMutation)

  // start / stop loading spinner
  watchEffect(() => {
    if (createEventFetching.value) {
      startSpinner()
    } else {
      stopSpinner()
    }
  })

  // handle error messages
  watchEffect(() => {
    if (createEventError?.value?.message) {
      showSnackbar({
        msg: createEventError.value.message
      })
    }
  })

  return {
    createEvent
  }
})
