import { createTestingPinia } from '@pinia/testing'
import { setActivePinia } from 'pinia'
import { createClient, setActiveClient } from 'villus'
import { ComputedRef } from 'vue'

const setActive = (state: any) => {
  setActivePinia(createTestingPinia({
    initialState: state
  }))

  setActiveClient(createClient({
    url: 'http://test/graphql'
  }))
}

const setAppliancesStore = (tableDevices?: ComputedRef<any[]>, tableMinions?: ComputedRef<any[]>) => {
  const state = {
    appliancesQueries: { 
      tableDevices,
      tableMinions
    }
  }

  setActive(state)
}

export {
  setAppliancesStore
}