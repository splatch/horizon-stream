import { defineStore } from 'pinia'
import { Tag } from '@/types/graphql'

export const useTagStore = defineStore('tagStore', () => {
  const tags = ref([] as Tag[])
  const tagsSelected = ref([] as Tag[])
  const isTagEditMode = ref(false)

  const setTags = (tagList: Tag[]) => {
    tags.value = tagList
  }

  const setTagEditMode = (isEdit: boolean) => {
    isTagEditMode.value = isEdit
  }

  const updateTagsSelected = (tags: Tag[]) => {
    tagsSelected.value = tags
  }

  const toggleTagsSelected = (tag: Tag) => {
    const isTagAlreadySelected = tagsSelected.value.some(({ name }) => name === tag.name)

    if (isTagAlreadySelected) {
      tagsSelected.value = tagsSelected.value.filter(({ name }) => name !== tag.name)
    } else {
      tagsSelected.value.push(tag)
    }
  }

  const selectAllTags = (selectAll: boolean) => {
    tagsSelected.value = selectAll ? tags.value : []
  }

  return {
    tags,
    setTags,
    isTagEditMode,
    setTagEditMode,
    selectAllTags,
    tagsSelected,
    updateTagsSelected,
    toggleTagsSelected
  }
})
