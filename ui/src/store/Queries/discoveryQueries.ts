import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { ListLocationsForDiscoveryDocument, Tag, ListTagsSearchDocument } from '@/types/graphql'

export const useDiscoveryQueries = defineStore('discoveryQueries', () => {
  const tagsSearched = ref([] as Tag[])

  const { data: locations, execute: getLocations } = useQuery({
    query: ListLocationsForDiscoveryDocument,
    fetchOnMount: false
  })

  const getTagsSearch = (searchTerm: string) => {
    const { data, error } = useQuery({
      query: ListTagsSearchDocument,
      variables: {
        searchTerm
      }
    })

    watchEffect(() => {
      if (data.value?.tags) {
        tagsSearched.value = data.value.tags
      } else {
        // TODO: what kind of errors and how to manage them
      }
    })
  }

  return {
    locations: computed(() => locations.value?.findAllLocations || []),
    getLocations,
    tagsSearched: computed(() => tagsSearched.value || []),
    getTagsSearch
  }
})
