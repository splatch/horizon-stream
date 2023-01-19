import { defineStore } from 'pinia'
import { without } from 'lodash'
import { IPolicy, IRule } from '@/types/policies'

type TState = {
  selectedPolicy: IPolicy
  selectedRule: IRule
}

export const useMonitoringPoliciesStore = defineStore('monitoringPoliciesStore', {
  state: (): TState => ({
    selectedPolicy: {
      id: '',
      name: '',
      tags: [],
      rules: []
    },
    selectedRule: {
      id: '',
      name: '',
      componentType: '',
      detectionMethod: '',
      metricName: '',
      conditions: []
    }
  }),
  actions: {
    removeTag(tag: string) {
      this.selectedPolicy.tags = without(this.selectedPolicy.tags, tag)
    },
    setMetricName(name: string) {
      this.selectedRule.metricName = name
    }
  }
})
