import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { ListTagsDocument, Tag } from '@/types/graphql'
import { TagNodesType } from '@/types/tags'

export const useTagStore = defineStore('tagStore', () => {
  const tags = ref([] as Tag[])
  const selectedTags = ref([] as Tag[])
  const tagNodesSelected = ref(TagNodesType.Unselected)

  const fetchTags = () => {
    const { data, execute } = useQuery({
      query: ListTagsDocument,
      fetchOnMount: false,
      cachePolicy: 'network-only'
    })

    execute()

    watchEffect(() => {
      tags.value = data.value?.tags || []
    })
  }

  const resetTags = () => {
    tags.value = []
  }

  const updateSelectedTags = (tags) => {
    selectedTags.value = tags
  }

  const toggleTag = (tag) => {
    const isInSelectedList = selectedTags.value.some(({ name }) => name === tag.name)

    if (isInSelectedList) {
      selectedTags.value = selectedTags.value.filter(({ name }) => name !== tag.name)
    } else {
      selectedTags.value.push(tag)
    }
  }

  const selectAllTags = (selectAll: boolean) => {
    selectedTags.value = selectAll ? tags.value : []
  }

  const selectTagNodes = (type: TagNodesType) => {
    tagNodesSelected.value = type
  }

  return {
    fetchTags,
    tags,
    resetTags,
    selectAllTags,
    selectedTags,
    tagNodesSelected,
    updateSelectedTags,
    toggleTag,
    selectTagNodes
  }
})
