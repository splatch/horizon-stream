import { createTestingPinia } from '@pinia/testing'
import { setActivePinia } from 'pinia'
import { createClient, setActiveClient } from 'villus'

const setActive = (listDevices: any) => {
  setActivePinia(createTestingPinia({
    initialState: { 
      deviceQueries: { 
        listDevices
      }
    }
  }))

  setActiveClient(createClient({
    url: 'http://test/graphql'
  }))
}

export default setActive