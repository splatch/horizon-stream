Feature: End to End test for REST API authentication
  Scenario: admin user can view/add/update/delete location
    Given A admin user "admin" with password "password"
    Then Admin user can create an access token
    Then Admin user can create new location
    Then Admin user can list location
    Then Admin user can get location by ID
    Then Admin user can update the location
    Then Admin user can delete the location by ID

  Scenario: Normal user only can view location
    Given A normal user with username "user" and password "password"
    Then Normal user can create an access token
    Then Normal user can list location
    Then Normal user can get location by ID
    Then Normal user am not allowed to create new location
    Then Normal user am not allowed to update the location by ID
    Then Normal user am not allowed to delete the location

  Scenario: Not authorized user can't access the REST API
    Then Without token user can't access rest api


