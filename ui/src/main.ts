import { createApp, h } from 'vue'
import { createClient, defaultPlugins } from 'villus'
import App from './App.vue'
import router from './router'
import store from './store'
import { createPinia } from 'pinia'
import VueKeycloak from '@dsb-norge/vue-keycloak-js'
import { KeycloakInstance } from '@dsb-norge/vue-keycloak-js/dist/types'
import keycloakConfig from '../keycloak.config'
import useKeycloak from './composables/useKeycloak'
import '@featherds/styles'
import '@featherds/styles/themes/open-light.css'
import dateFormatDirective from './directives/v-date'

// dark / light mode
const { setKeycloak } = useKeycloak()
const dark = useDark()

// creates a villus gql client instance
const getGqlClient = (kc: KeycloakInstance) => {
  const authPlugin = ({ opContext }: any) => {
    opContext.headers.Authorization = `Bearer ${kc.token}`
  }
  return createClient({
    url: `${process.env.API_BASE_URL || import.meta.env.VITE_BASE_URL?.toString()}/graphql`,
    use: [authPlugin, ...defaultPlugins()],
    cachePolicy: 'cache-and-network'
  })
}

const app = createApp({
  render: () => h(App)
})
  .use(router)
  .use(createPinia())
  .use(store)
  .use(VueKeycloak, {
    init: {
      onLoad: 'login-required',
      redirectUri: `${window.location.href}${dark.value ? '?theme=dark' : '?theme=light'}`
    },
    config: {
      realm: keycloakConfig.realm,
      url: keycloakConfig.url,
      clientId: keycloakConfig.clientId
    },
    onReady: (kc: KeycloakInstance) => {
      setKeycloak(kc)
      const gqlClient = getGqlClient(kc)
      app.use(gqlClient)
      app.mount('#app')
    }
  })
  .directive('date', dateFormatDirective)
