import { defineStore } from 'pinia'

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

  const updateTagsSelected = (tags) => {
    tagsSelected.value = tags
  }

  const toggleTag = (tag) => {
    const isInSelectedList = tagsSelected.value.some(({ name }) => name === tag.name)

    if (isInSelectedList) {
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
    toggleTag
  }
})
