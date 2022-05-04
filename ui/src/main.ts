import { createApp, h } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import { createPinia } from 'pinia'

// keycloak
import VueKeyCloak from '@dsb-norge/vue-keycloak-js'
import keycloakConfig from '../keycloak.config'
import useKeycloak from './composables/useKeycloak'
import { KeycloakInstance } from '@dsb-norge/vue-keycloak-js/dist/types'

// feather styles
import '@featherds/styles'
import '@featherds/styles/themes/open-light.css'

const { setKeycloak } = useKeycloak()

createApp({
  render: () => h(App)
})
  .use(router)
  .use(createPinia())
  .use(store)
  .use(VueKeyCloak, { 
    config: keycloakConfig,
    onReady: (keycloak: KeycloakInstance) => {
      setKeycloak(keycloak)
      if (!keycloak.authenticated) {
        router.push('/login')
      }
    },
    init: { onLoad: 'check-sso' }
  })
  .mount('#app')
