Feature: End to End test for REST API authentication

  Background:
    Given REST server url in system property "rest-server-url"
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

  Scenario: admin user can view/add/update/delete location
    #Given Admin user "admin-user" with password "password123"
    Then Admin user can create an access token
    Then Admin user can create new location
    Then Admin user can list location
    Then Admin user can get location by ID
    Then Admin user can update the location
    Then Admin user can delete the location by ID
#
#  Scenario: Normal user only can view location
#    Given A normal user with username "user" and password "password"
#    Then Normal user can create an access token
#    Then Normal user can list location
#    Then Normal user can get location by ID
#    Then Normal user am not allowed to create new location
#    Then Normal user am not allowed to update the location by ID
#    Then Normal user am not allowed to delete the location
#
#  Scenario: Not authorized user can't access the REST API
#    Then Without token user can't access rest api


