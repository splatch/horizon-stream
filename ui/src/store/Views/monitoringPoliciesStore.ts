import { defineStore } from 'pinia'
import { without } from 'lodash'

type TState = {
  policy: {
    id: string
    name: string
    tags: string[]
    rules: string[]
  }
}

export const useMonitoringPoliciesStore = defineStore(
  'monitoringPoliciesStore',
  {
    state: (): TState => ({
      policy: {
        id: '',
        name: '',
        tags: [],
        rules: []
      }
    }),
    actions: {
      removeTag(tag: string) {
        this.policy.tags = without(this.policy.tags, tag)
      }
    }
  }
)
