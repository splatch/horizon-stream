import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { ListLocationsDocument, Location } from '@/types/graphql'

export const useLocationsQueries = defineStore('locationsQueries', () => {
  const locations = ref<Location[]>([])

  const { data, execute, isFetching, error } = useQuery({
    query: ListLocationsDocument,
    fetchOnMount: false,
    cachePolicy: 'network-only'
  })

  watchEffect(() => {
    if (!error.value) {
      if (!isFetching.value) {
        locations.value = data.value?.findAllLocations || []
      }
    } else {
      // TODO: what kind of errors and how to handle them
    }
  })

  return {
    fetchLocations: execute,
    locations: computed(() => locations.value)
  }
})
