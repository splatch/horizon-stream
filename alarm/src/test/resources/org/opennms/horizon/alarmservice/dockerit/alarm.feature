Feature: Alarm Service Basic Functionality

  Background: Configure base URLs
    Given Application Base URL in system property "application.base-url"
    Given Kafka Rest Server URL in system property "kafka-rest.url"

  Scenario: Verify when an event is received from Kafka, a new Alarm is created
    Then Send message to Kafka at topic "events-proto"
    Then Verify the HTTP response code is 200
    Then Send GET request to application at path "/alarms/list"
    Then DEBUG dump the response body
    Then parse the JSON response
    Then Verify JSON path expressions match
      | totalCount == 1 |

  Scenario: Verify alarm can be deleted
    Then Send message to Kafka at topic "events-proto"
    Then Verify the HTTP response code is 200
    Then Send GET request to application at path "/alarms/list"
    Then DEBUG dump the response body
    Then parse the JSON response
    Then Verify JSON path expressions match
      | totalCount == 1 |
    Then Remember alarm id
    Then Send DELETE request to application at path "/alarms/delete"
    Then Verify the HTTP response code is 200
    Then Send GET request to application at path "/alarms/list"
    Then DEBUG dump the response body
    Then parse the JSON response
    Then Verify JSON path expressions match
      | totalCount == 0 |

  Scenario: Verify alarm can be cleared
    Then Send message to Kafka at topic "events-proto"
    Then Verify the HTTP response code is 200
    Then delay
    Then Send GET request to application at path "/alarms/list"
    Then DEBUG dump the response body
    Then Send POST request to clear alarm at path "/alarms/clear"
    Then Verify the HTTP response code is 200
    Then Send GET request to application at path "/alarms/list"
    Then Verify alarm was cleared
    Then Send POST request to clear alarm at path "/alarms/unclear"
    Then Verify the HTTP response code is 200
    Then Send GET request to application at path "/alarms/list"
    Then Verify alarm was uncleared

  Scenario: Verify alarm can be acknowledged and unacknowledged
    Then Send message to Kafka at topic "events-proto"
    Then Verify the HTTP response code is 200
    Then delay
    Then Send GET request to application at path "/alarms/list"
    Then DEBUG dump the response body
    Then Send POST request to acknowledge alarm at path "/alarms/ack"
    Then Verify the HTTP response code is 200
    Then Send GET request to application at path "/alarms/list"
    Then Verify alarm was acknowledged
    Then Send POST request to unacknowledge alarm at path "/alarms/unAck"
    Then Verify the HTTP response code is 200
    Then Send GET request to application at path "/alarms/list"
    Then Verify alarm was unacknowledged

  Scenario: Verify alarm severity can be set and escalated
    Then Send message to Kafka at topic "events-proto"
    Then Verify the HTTP response code is 200
    Then delay
    Then Send GET request to application at path "/alarms/list"
    Then DEBUG dump the response body
    Then Send POST request to set alarm severity at path "/alarms/severity"
    Then Verify the HTTP response code is 200
    Then Send GET request to application at path "/alarms/list"
#    Then Verify alarm was acknowledged
    Then Send POST request to escalate alarm severity at path "/alarms/escalate"
    Then Verify the HTTP response code is 200
    Then Send GET request to application at path "/alarms/list"
    Then Verify alarm severity was escalated

  Scenario: Verify alarm reduction for duplicate events
    Then Send message to Kafka at topic "events-proto"
    Then Verify the HTTP response code is 200
    Then delay
    Then Send GET request to application at path "/alarms/list"
    Then DEBUG dump the response body
    Then parse the JSON response
    Then Verify JSON path expressions match
      | totalCount == 1 |
    Then Send message to Kafka at topic "events-proto"
    Then Verify the HTTP response code is 200
    Then delay
    Then Send GET request to application at path "/alarms/list"
    Then DEBUG dump the response body
    Then parse the JSON response
    Then Verify JSON path expressions match
      | totalCount == 1 |

