import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import {
  ListLocationsForDiscoveryDocument,
  Tag,
  ListTagsSearchDocument,
  ListDiscoveriesDocument
} from '@/types/graphql'

export const useDiscoveryQueries = defineStore('discoveryQueries', () => {
  const { data: locations, execute: getLocations } = useQuery({
    query: ListLocationsForDiscoveryDocument,
    fetchOnMount: false
  })

  const { data: listedDiscoveries, execute: getDiscoveries } = useQuery({
    query: ListDiscoveriesDocument
  })

  return {
    locations: computed(() => locations.value?.findAllLocations || []),
    getLocations,
    discoveries: computed(() => listDiscoveryConfig.value?.listDiscoveryConfig || []),
    tagsSearched: computed(() => tagsSearched.value || []),
    getTagsSearch,
    activeDiscoveries: computed(() => listedDiscoveries.value?.listActiveDiscovery || []),
    passiveDiscoveries: computed(() => listedDiscoveries.value?.passiveDiscoveries || []),
    getDiscoveries
  }
})
