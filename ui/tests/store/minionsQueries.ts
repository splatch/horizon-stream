import { createTestingPinia } from '@pinia/testing'
import { setActivePinia } from 'pinia'
import { createClient, setActiveClient } from 'villus'

const setActive = (listMinions: any) => {
  setActivePinia(createTestingPinia({
    initialState: { 
      minionsQueries: { 
        listMinions
      }
    }
  }))

  setActiveClient(createClient({
    url: 'http://test/graphql'
  }))
}

export default setActive