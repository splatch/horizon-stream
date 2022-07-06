import { defineStore } from 'pinia'
import { useMutation } from 'villus'
import { CreateEventDocument } from '@/graphql/operations'
import useSnackbar from '@/composables/useSnackbar'

const { showSnackbar } = useSnackbar()

export const useEventsStore = defineStore('eventsStore', () => {
  // create event
  const {
    execute: createEvent,
    error: createEventError
  } = useMutation(CreateEventDocument)

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
