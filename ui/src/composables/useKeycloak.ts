import { KeycloakInstance } from "@dsb-norge/vue-keycloak-js/dist/types"

const keycloak = ref<KeycloakInstance>()

const useKeycloak = () => {

  const setKeycloak = (obj: KeycloakInstance) => {
    keycloak.value = obj
  }

  return { keycloak, setKeycloak }
}

export default useKeycloak
