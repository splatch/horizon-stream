import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { ListLocationsForDiscoveryDocument, ListTagsByNodeIdDocument, Tag } from '@/types/graphql'

export const useDiscoveryQueries = defineStore('discoveryQueries', () => {
  const tagsByNodeId = ref([] as Tag[])

  const { data: locations, execute: getLocations } = useQuery({
    query: ListLocationsForDiscoveryDocument,
    fetchOnMount: false
  })

  const getTagsByNodeId = (nodeId: number) => {
    const { data, error } = useQuery({
      query: ListTagsByNodeIdDocument,
      variables: {
        nodeId
      },
      cachePolicy: 'network-only'
    })

    watchEffect(() => {
      // console.log('error', error)
      // console.log('data', data.value?.tagsByNodeId)
      if (data.value?.tagsByNodeId?.length) {
        tagsByNodeId.value = data.value?.tagsByNodeId
      }
      // } else {
      // tagsByNodeId.value = []
      // }
      // return data.value?.tagsByNodeId
    })
  }

  return {
    locations: computed(() => locations.value?.findAllLocations || []),
    getLocations,
    tagsByNodeId,
    getTagsByNodeId
  }
})
