import { DiscoveryInput } from '@/types/discovery'
import { defineStore } from 'pinia'
import { useDiscoveryMutations } from '../Mutations/discoveryMutations'
import { cloneDeep } from 'lodash'

const defaultAzureForm = {
  name: '',
  clientId: '',
  clientSecret: '',
  subscriptionId: '',
  directoryId: ''
}

export const useDiscoveryStore = defineStore('discoveryStore', {
  state: () => ({
    selectedLocations: <string[]>[],
    selectedTags: [] as Record<string, string>[],
    ipAddresses: <string[]>[],
    ipRange: {
      cidr: '',
      fromIp: '',
      toIp: ''
    },
    activeDiscoveries: <DiscoveryInput[]>[],
    azure: cloneDeep(defaultAzureForm)
  }),
  actions: {
    selectLocation(location: string, single?: boolean) {
      if (single) {
        this.selectedLocations = [location]
        return
      }

      if (this.selectedLocations.includes(location)) {
        this.selectedLocations = this.selectedLocations.filter((x) => x !== location)
      } else {
        this.selectedLocations.push(location)
      }
    },
    selectTags(tags: Record<string, string>[]) {
      this.selectedTags = tags
    },
    async saveDiscoveryAzure() {
      const { addAzureCreds, azureError } = useDiscoveryMutations()

      await addAzureCreds({
        azureCredential: {
          location: this.selectedLocations[0],
          tags: this.selectedTags,
          ...this.azure
        }
      })

      return !azureError.value
    },
    saveDiscovery(discovery: DiscoveryInput) {
      if (!discovery.id) {
        discovery.id = new Date().getTime()
      } else {
        const exists = this.activeDiscoveries.find((d) => d.id == discovery.id)
        if (exists) {
          this.activeDiscoveries = this.activeDiscoveries.filter((d) => d.id !== discovery.id)
        }
      }
      this.activeDiscoveries.push(discovery)
    },
    clearAzureForm() {
      this.azure = cloneDeep(defaultAzureForm)
    }
  }
})
