import { defineStore } from 'pinia'
import { useQuery } from 'villus'

const mockTags = [
  { id: 1, label: 'tag1' },
  { id: 2, label: 'tag2' },
  { id: 3, label: 'tag3' },
  { id: 4, label: 'tag4' },
  { id: 5, label: 'tag5' },
  { id: 6, label: 'tag6' },
  { id: 7, label: 'tag7' },
  { id: 8, label: 'tag8' },
  { id: 9, label: 'tag9' },
  { id: 10, label: 'tag10' },
  { id: 11, label: 'tag11' },
  { id: 12, label: 'tag12' },
  { id: 13, label: 'tag13' }
]

export const useTaggingQueries = defineStore('taggingQueries', () => {
  const tags = ref()

  const fetchTags = () => {
    console.log('fetchTags')
    // TODO: add gql query

    // mock
    tags.value = mockTags
  }

  const searchTag = (searchTag: string) => {
    console.log('searchTag', searchTag)
    // TODO: add gql query

    // mock
    // tags.value = mockTags.filter((tag) => tag.id === searchTag.id)
  }

  const resetTags = () => {
    console.log('resetTags')
    tags.value = []
  }

  return {
    tags,
    fetchTags,
    searchTag,
    resetTags
  }
})
