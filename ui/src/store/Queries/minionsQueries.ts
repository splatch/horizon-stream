import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { MinionsQuery } from '@/types/minions'

export const useMinionsQueries = defineStore('minionsQueries', {
  state: () => {
    const { data, execute } = useQuery({
      query: MinionsQuery
    })

    const listMinions = computed(() => data.value?.listMinions?.items || [])

    return {
      listMinions,
      fetch: execute
    }
  }
})
