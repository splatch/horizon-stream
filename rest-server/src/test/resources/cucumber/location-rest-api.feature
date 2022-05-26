Feature: Location REST API endpoints integration tests

  Background:
    Given REST server url in system property "rest-server-url"
    Given Keycloak auth server url in system property "keycloak.url", realm "opennms" and client "admin-cli"
    Given Admin user "admin-user" with password "password123"


  Scenario: admin user can view/add/update/delete location
    Then Admin user can create an access token
    Then Admin user can create new location
    Then Admin user can list location
    Then Verify location ids in the list
    Then Admin user can get location by ID
    Then Admin user can update the location
    Then Admin user can delete the location by ID

  Scenario: Normal user only can view location
    Then Normal user "test-user" with password "password123" login to test location api
    Then Normal user can list location
    Then Normal user can get location by ID
    Then Normal user am not allowed to create new location
    Then Normal user am not allowed to update the location by ID
    Then Normal user am not allowed to delete the location

  Scenario: Not authorized user can't access the REST API
    Then Without correct token user can't access rest api


