Feature: Node REST API endpoints integration tests

  Background:
    Given Prepare node test with REST server url in system property "rest-server-url"
    Given Prepare node test with auth server url in system property "keycloak.url", realm "opennms" and client "admin-cli"
    Given Prepare node test with admin user "admin-user" with password "password123"

  Scenario: admin user have full access to all endpoints
    Then Admin user can login and generate an access token
    Then Admin user create a location
    Then Admin use can create new node
    Then Admin user can list nodes
    Then Admin user can get node by ID
    Then Admin user can update node
    Then Admin user can delete a node

  Scenario: Normal user only can view node
    Given Prepare node test with with normal "test-user" and password "password123"
    Then Normal user can list nodes
    Then Normal user can get node by ID
    Then Normal user is not allowed to create new node
    Then Normal user is not allowed to update a node
    Then Normal user is not allowed to delete a node

  Scenario: Not authorized user can't access the node endpoint
    Given Prepare node test with REST server url in system property "rest-server-url"
    Then Without correct token user can't access node endpoint

