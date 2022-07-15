import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { ListMinionsDocument } from '@/types/graphql-mocks'

export const useMinionsQueries = defineStore('minionsQueries', {
  state: () => {
    const { data, execute } = useQuery({
      query: ListMinionsDocument
    })

    const listMinions = computed(() => data.value?.listMinions?.items || [])

    return {
      listMinions,
      fetch: execute
    }
  }
})
