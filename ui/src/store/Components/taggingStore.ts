import { defineStore } from 'pinia'
import { TagNodesType, Tag } from '@/types/tags'

export const useTaggingStore = defineStore('useTaggingStore', () => {
  const selectedTags = ref<Tag[]>([])
  const tagNodesSelected = ref(TagNodesType.Unselected)

  const updateSelectedTags = (tags) => {
    selectedTags.value = tags
  }

  const selectTag = (tag) => {
    // if(selectedTags.filter(selTag => selTag.id === tag.id))
  }

  const selectTagNodes = (type: TagNodesType) => {
    // console.log(type)
    tagNodesSelected.value = type
  }

  return {
    selectedTags,
    tagNodesSelected,
    updateSelectedTags,
    selectTag,
    selectTagNodes
  }
})
