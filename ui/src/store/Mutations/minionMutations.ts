// const foo = 2 + 2

// console.log(foo)

/* import { defineStore } from 'pinia'
import { useMutation } from 'villus'
import useSpinner from '@/composables/useSpinner'

import { DeleteMinionDocument } from '@/types/graphql'

const { startSpinner, stopSpinner } = useSpinner()

export const useMinionMutations = defineStore('minionMutations', () => {
  const {
    execute: deleteMinion,
    isFetching,
    error
  } = useMutation(DeleteMinionDocument)

  watchEffect(() => {
    if (isFetching.value) {
      startSpinner()
    } else {
      stopSpinner()
    }
  })

  return {
    deleteMinion,
    error
  }
})
 */
