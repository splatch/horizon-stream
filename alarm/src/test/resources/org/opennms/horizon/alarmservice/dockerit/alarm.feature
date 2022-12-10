Feature: Alarm Service Basic Functionality

  Background: Configure base URLs
    Given Application Base URL in system property "application.base-url"
    Given Kafka Rest Server URL in system property "kafka-rest.url"

  Scenario: Verify when an event is received from Kafka, a new Alarm is created
    Then Send message to Kafka at topic "events-proto"
    Then Verify the HTTP response code is 200
    Then delay
    Then Send GET request to application at path "/alarms/list"
    Then DEBUG dump the response body
    Then parse the JSON response
    Then verify JSON path expressions match
      | totalCount == 1 |

  Scenario: Verify alarm can be deleted
    Then Send message to Kafka at topic "events-proto"
    Then Verify the HTTP response code is 200
    Then delay
    Then Send GET request to application at path "/alarms/list"
    Then parse the JSON response
    Then verify JSON path expressions match
      | totalCount == 2 |
    Then Remember alarm id
    Then Send DELETE request to application at path "/alarms/delete"
    Then Verify the HTTP response code is 200
    Then Send GET request to application at path "/alarms/list"
    Then parse the JSON response
    Then verify JSON path expressions match
      | totalCount == 1 |

