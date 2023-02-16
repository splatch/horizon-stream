import { IDiscovery } from '@/types/discovery'
import { filter } from 'lodash'
import { defineStore } from 'pinia'
import { useDiscoveryMutations } from '../Mutations/discoveryMutations'

export const useDiscoveryStore = defineStore('discoveryStore', {
  state: () => ({
    selectedLocations: <string[]>[],
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
    },
    activeDiscoveries: <IDiscovery[]>[]
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
    async saveDiscoveryAzure() {
      const { addAzureCreds, azureError } = useDiscoveryMutations()

      await addAzureCreds({
        azureCredential: {
          location: this.selectedLocations[0],
          ...this.azure
        }
      })

      return !azureError.value
    },
    saveDiscovery(discovery: IDiscovery) {
      const exists = this.activeDiscoveries.find((d) => d.id == discovery.id)
      if (exists) {
        this.activeDiscoveries = this.activeDiscoveries.filter((d) => d.id !== discovery.id)
      } else {
        discovery.id = new Date().getTime()
      }
      this.activeDiscoveries.push(discovery)
    }
  }
})
