Feature: Location GraphQL Integration tests

  Background:
    Given REST server url in system property "rest-server-url"
    Given Keycloak auth server url in system property "keycloak.url", realm "opennms" and client "admin-cli"
    Given Admin user "admin-user" with password "password123"

  Scenario: Admin user has full access of locations via graphql endpoint
    Then Admin user can loging and create access token
    Then Admin user can create new locations
    Then Admin user can query locations
    Then Admin user can query a location by ID
    Then Admin user can update a location
    Then Admin user can delete a location

  Scenario: Normal user only can view location view locations via graphql endpoint
    Then Normal user "test-user" with password "password123" login to test location graphql api
    Then Normal user can query locations
    Then Normal user can query a location by ID
    Then Normal user am not allowed to create a location
    Then Normal user not allowed to update a location
    Then Normal user not allowed to delete a location

  Scenario: Not authorized user can't access the graphql API
    Then Without correct token user can't access graphql api



