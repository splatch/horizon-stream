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

const setAppliancesStore = (tableMinions?: ComputedRef<any[]>, tableDevices?: ComputedRef<any[]>) => {
  const state = {
    appliancesQueries: { 
      tableMinions,
      tableDevices
    }
  }

  setActive(state)
}

export {
  setAppliancesStore
}