import { createStore } from 'vuex'

// store modules
import spinnerModule from './spinner'

export default createStore({
  modules: {
    spinnerModule
  }
})
