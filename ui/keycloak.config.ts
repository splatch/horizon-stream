export default {
  realm: process.env.KEYCLOAK_REALM || import.meta.env.VITE_KEYCLOAK_REALM,
  url: process.env.KEYCLOAK_URL || import.meta.env.VITE_KEYCLOAK_URL,
  clientId: process.env.KEYCLOAK_CLIENT_ID || import.meta.env.VITE_KEYCLOAK_CLIENT_ID
}
