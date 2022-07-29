import { createStore } from 'vuex'

// store modules
import mapModule from './map'
import topologyModule from './topology'

export default createStore({
  modules: {
    mapModule,
    topologyModule
  }
})
