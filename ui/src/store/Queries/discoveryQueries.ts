import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { ListLocationsForDiscoveryDocument, Tag } from '@/types/graphql'

export const useDiscoveryQueries = defineStore('discoveryQueries', () => {
  const tagsUponTyping = ref([] as Tag[])

  const { data: locations, execute: getLocations } = useQuery({
    query: ListLocationsForDiscoveryDocument,
    fetchOnMount: false
  })

  let timeout = -1
  const getTagsUponTyping = (s: string) => {
    /* const { data, error } = useQuery({
      query: ListTagsByNodeIdDocument,
      variables: {
        string: s
      },
      cachePolicy: 'network-only'
    }) */

    // mock
    clearTimeout(timeout)

    timeout = window.setTimeout(() => {
      tagsUponTyping.value = [
        {
          id: 1,
          name: 'local',
          tenantId: 'opennms-prime'
        },
        {
          id: 2,
          name: 'localhost',
          tenantId: 'opennms-prime'
        }
      ]
    }, 1000)
  }

  return {
    locations: computed(() => locations.value?.findAllLocations || []),
    getLocations,
    tagsUponTyping,
    getTagsUponTyping
  }
})
