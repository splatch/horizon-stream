import { DiscoveryInput } from '@/types/discovery'
import { defineStore } from 'pinia'
import { useDiscoveryMutations } from '../Mutations/discoveryMutations'
import { cloneDeep } from 'lodash'
import { DiscoveryType } from '@/components/Discovery/discovery.constants'
import { AzureCredential } from '@/types/graphql'
const defaultAzureForm = {
  name: '',
  clientId: '',
  clientSecret: '',
  subscriptionId: '',
  directoryId: ''
  // tags: [] to be done later
}

const defaultSnmpForm: DiscoveryInput = {
  id: 0,
  name: '',
  location: [],
  tags: [],
  type: DiscoveryType.ICMP,
  IPRange: '',
  communityString: '',
  UDPPort: ''
}

export const useDiscoveryStore = defineStore('discoveryStore', {
  state: () => ({
    selectedLocations: <string[]>[],
    ipAddresses: <string[]>[],
    ipRange: {
      cidr: '',
      fromIp: '',
      toIp: ''
    },
    activeDiscoveries: <DiscoveryInput[]>[],
    azure: cloneDeep(defaultAzureForm),
    snmp: cloneDeep(defaultSnmpForm),
    selectedDiscovery: {}
  }),
  actions: {
    selectLocation(location: string, single?: boolean) {
      if (single) {
        this.selectedLocations = location ? [location] : []
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
    async saveDiscoverySnmp() {
      const { createDiscoveryConfig, errorSnmp } = useDiscoveryMutations()
      await createDiscoveryConfig({
        snmpInfo: {
          configName: this.snmp.name,
          ipAddresses: ['127.0.0.1'],
          location: this.selectedLocations[0],
          snmpConfig: { readCommunities: ['test-community'], ports: [161] }
        }
      })
      return !errorSnmp.value
    },
    clearAzureForm() {
      this.azure = cloneDeep(defaultAzureForm)
    },
    clearSnmpForm() {
      this.snmp = cloneDeep(defaultSnmpForm)
    },
    setSelectedDiscovery(selected: any) {
      if (!selected) {
        this.selectedDiscovery = Object.assign({ type: DiscoveryType.None })
        this.clearAzureForm()
        this.clearSnmpForm()
      } else {
        if (selected.type === DiscoveryType.ICMP) {
          this.snmp = cloneDeep(selected)
          this.selectedLocations = [selected.location]
        } else {
          this.azure = selected
        }
      }
    }
  }
})
