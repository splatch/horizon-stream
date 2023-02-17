import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { ListLocationsForDiscoveryDocument, Tag } from '@/types/graphql'
import useSpinner from '@/composables/useSpinner'
import useSnackbar from '@/composables/useSnackbar'

export const useDiscoveryQueries = defineStore('discoveryQueries', () => {
  const tagsUponTyping = ref([] as Tag[])
  const syslogSNMPTrapsFormSaved = ref()
  const isSyslogSNMPTrapsFormSaveSuccess = ref(false)

  const { startSpinner, stopSpinner } = useSpinner()
  const { showSnackbar } = useSnackbar()

  const afterFormSaved = () => {
    stopSpinner()

    if (syslogSNMPTrapsFormSaved.value.data) {
      showSnackbar({
        msg: 'Saved successfully!'
      })
      isSyslogSNMPTrapsFormSaveSuccess.value = true
    } else {
      showSnackbar({
        msg: syslogSNMPTrapsFormSaved.value.errors[0].message,
        error: true
      })
      isSyslogSNMPTrapsFormSaveSuccess.value = false
    }
  }

  const { data: locations, execute: getLocations } = useQuery({
    query: ListLocationsForDiscoveryDocument,
    fetchOnMount: false
  })

  let timeout = -1
  const getTagsUponTyping = (s: string) => {
    /* const { data, error } = useQuery({
      query: ListTagsByNodeIdDocument,
      variables: {
        string: s
      },
      cachePolicy: 'network-only'
    }) */

    // mock
    const success = [
      {
        id: 1,
        name: 'local',
        tenantId: 'opennms-prime'
      },
      {
        id: 2,
        name: 'localhost',
        tenantId: 'opennms-prime'
      }
    ]
    clearTimeout(timeout)
    timeout = window.setTimeout(() => {
      tagsUponTyping.value = success
    }, 1000)
  }

  const saveSyslogSNMPTrapsForm = (values: unknown) => {
    console.log('values', values)
    startSpinner()

    // mock
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
            id: 1
          }
        ]
      }
    }

    timeout = window.setTimeout(() => {
      syslogSNMPTrapsFormSaved.value = successRes
      // syslogSNMPTrapsFormSaved.value = errorsRes
      afterFormSaved()

      clearTimeout(timeout)
    }, 2000)
  }

  return {
    locations: computed(() => locations.value?.findAllLocations || []),
    getLocations,
    tagsUponTyping,
    getTagsUponTyping,
    saveSyslogSNMPTrapsForm,
    isSyslogSNMPTrapsFormSaveSuccess
  }
})
