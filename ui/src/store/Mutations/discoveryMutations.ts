import { defineStore } from 'pinia'
import { useMutation } from 'villus'

import { AddAzureCredentialDocument } from '@/types/graphql'

export const useDiscoveryMutations = defineStore('discoveryMutations', () => {
  const { execute: addAzureCreds, error, isFetching } = useMutation(AddAzureCredentialDocument)

  // mock
  let timeout = -1
  const saveSyslogSNMPTrapsError = ref()
  const savingSyslogSNMPTraps = ref(false)
  const saveSyslogSNMPTraps = async (values: any) => {
    savingSyslogSNMPTraps.value = true
    const errorsRes = {
      errors: [
        {
          message: 'Exception while fetching data (/saveDiscovery) : UNAVAILABLE: Service unavalable'
        }
      ]
    }
    const successRes = {
      data: {
        saveDiscovery: [
          {
            id: 1,
            name: 'MAD-001-Syslog-SNMP-Traps'
          }
        ]
      }
    }
    return new Promise((resolve) => {
      timeout = window.setTimeout(() => {
        savingSyslogSNMPTraps.value = false
        // success
        resolve(successRes)
        // fail
        // resolve(errorsRes)
        // saveSyslogSNMPTrapsError.value = errorsRes
        clearTimeout(timeout)
      }, 2000)
    })
  }

  return {
    addAzureCreds,
    azureError: computed(() => error),
    isFetching: computed(() => isFetching),
    saveSyslogSNMPTraps,
    saveSyslogSNMPTrapsErrors: computed(() => saveSyslogSNMPTrapsError.value),
    savingSyslogSNMPTraps: computed(() => savingSyslogSNMPTraps.value)
  }
})
