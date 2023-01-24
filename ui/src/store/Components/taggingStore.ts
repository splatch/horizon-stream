import { defineStore } from 'pinia'
import { TagNodesType } from '@/types/tags'

export const useTaggingStore = defineStore('taggingStore', () => {
  const selectedTags = ref([])
  const tagNodesSelected = ref(TagNodesType.Unselected)

  const updateSelectedTags = (tags) => {
    selectedTags.value = tags
  }

  const toggleTag = (tag) => {
    if (selectedTags.value.includes(tag)) {
      selectedTags.value = selectedTags.value.filter((t) => t !== tag)
    } else {
      selectedTags.value.push(tag)
    }
  }

  const selectTagNodes = (type: TagNodesType) => {
    tagNodesSelected.value = type
  }

  return {
    selectedTags,
    tagNodesSelected,
    updateSelectedTags,
    toggleTag,
    selectTagNodes
  }
})
