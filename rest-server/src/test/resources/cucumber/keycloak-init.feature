Feature: Initial Keycloak auth server

  Scenario: create new realm, roles and users
    Given Keycloak auth server url in system property "keycloak.url"
    Given Keycloak admin user "keycloak-admin" with password "admin"
    Given Keycloak master realm client-id "admin-cli"
    Then Initial Keycloak utils
    Then Create Keycloak realm "opennms"
    Then Add roles
      | admin |
      | user  |
    Then Add admin user "admin-user" with password "password123" and role "admin"
    Then Add regular user "test-user" with password "password123" and role "user"