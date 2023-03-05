import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { ListTagsDocument, Tag, ListTagsSearchDocument } from '@/types/graphql'
import { useTagStore } from '@/store/Components/tagStore'

export const useTagQueries = defineStore('tagQueries', () => {
  const tagsSearched = ref([] as Tag[])

  const tagStore = useTagStore()

  const fetchTags = async () => {
    const { data, execute } = useQuery({
      query: ListTagsDocument,
      fetchOnMount: false,
      cachePolicy: 'network-only'
    })

    await execute()

    tagStore.setTags(data.value?.tags || [])
  }

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

  const searchTag = (searchTag: string) => {
    // TODO: add gql query
  }

  return {
    fetchTags,
    tagsSearched: computed(() => tagsSearched.value || []),
    getTagsSearch,
    searchTag
  }
})
