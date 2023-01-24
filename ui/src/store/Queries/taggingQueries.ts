import { defineStore } from 'pinia'
import { useQuery } from 'villus'

const mockTags = [
  'tag1',
  'tag2',
  'tag3',
  'tag4',
  'tag5',
  'tag6',
  'tag7',
  'tag8',
  'tag9',
  'tag10',
  'tag11',
  'tag12',
  'tag13'
]

export const useTaggingQueries = defineStore('taggingQueries', () => {
  const tags = ref([])

  const fetchTags = () => {
    // TODO: add gql query
    tags.value = mockTags
  }

  const searchTag = (searchTag: string) => {
    // TODO: add gql query
    // tags.value = tags.value.filter((tag) => tag === searchTag)
  }

  const resetTags = () => {
    tags.value = []
  }

  return {
    tags,
    fetchTags,
    searchTag,
    resetTags
  }
})
