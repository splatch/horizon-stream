Feature: Testing login to the local environment

#  Background: Login to Cluster
#    Given Ingress base url in environment variable "INGRESS_BASE_URL"
#    Given Keycloak server base url in environment variable "KEYCLOAK_BASE_URL"
#    Given Keycloak realm in environment variable "KEYCLOAK_REALM"
#    Given Keycloak username in environment variable "KEYCLOAK_USERNAME"
#    Given Keycloak password in environment variable "KEYCLOAK_PASSWORD"
#    Given Keycloak client-id in environment variable "KEYCLOAK_CLIENT_ID"
#    Then login to Keycloak with timeout 120000ms

  @horizon-stream
  Scenario: Verify that we are able to login to the cluster
    Given Login to the web interface with provided "user" and "password"
    Then Verify that we logged in successfully
