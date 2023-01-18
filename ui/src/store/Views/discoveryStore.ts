import { defineStore } from 'pinia'
import { useDiscoveryMutations } from '../Mutations/discoveryMutations'

export const useDiscoveryStore = defineStore('discoveryStore', {
  state: () => ({
    selectedLocationIds: <string[]>[],
    ipAddresses: <string[]>[],
    ipRange: {
      cidr: '',
      fromIp: '',
      toIp: ''
    },
    azure: {
      clientId: '',
      clientSecret: '',
      subscriptionId: '',
      directoryId: ''
    }
  }),
  actions: {
    selectLocation(id: string, single?: boolean) {
      if (single) {
        this.selectedLocationIds = [id]
        return
      }

      if (this.selectedLocationIds.includes(id)) {
        this.selectedLocationIds = this.selectedLocationIds.filter((x) => x !== id)
      } else {
        this.selectedLocationIds.push(id)
      }
    },
    async saveDiscoveryAzure() {
      const { addAzureCreds, azureError } = useDiscoveryMutations()

      await addAzureCreds({
        azureCredential: {
          location: this.selectedLocationIds[0],
          ...this.azure
        }
      })

      return !azureError
    }
  }
})
