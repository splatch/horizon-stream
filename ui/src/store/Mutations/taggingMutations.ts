import { defineStore } from 'pinia'

export const useTaggingMutations = defineStore('useTaggingMutations', () => {
  const editTagsInNode = (id: number, toAddTags: boolean) => {
    console.log('editTagsInNode', id, 'toAdd?', toAddTags)
    if (toAddTags) {
      // TODO: gql query to add tags to node
    } else {
      // TODO: gql query to remove tags from node
    }

    // display toaster
  }

  const addTagsToAllNodes = () => {
    console.log('addTagsToAllNodes')
    // TODO: add gql query to add tags to node, with seleted tags, and the node list with the filter options
    // display toaster
  }

  return {
    editTagsInNode,
    addTagsToAllNodes
  }
})
