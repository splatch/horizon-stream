Feature: Alarm Service Basic Functionality

  Background: Configure base URLs
    Given Application Base URL in system property "application.base-url"
#    Given Kafka Boot Servers in system property "kafka.bootstrap-servers"
#    Given Kafka producer is setup

#  Scenario: Verify when kick rest call, a new Alarm is created
#    Then Send POST request to application at path "/alarms/kick"
#    Then DEBUG dump the response body
#    Then Remember response body for later comparison

  Scenario: Verify when an event is received from Kafka, a new Alarm is created
#    Then Send message to Kafka at topic "events-proto"
    Then Send POST request to application at path "/alarms/kick"
    Then delay
    Then Send GET request to application at path "/alarms/list"
    Then DEBUG dump the response body
    Then parse the JSON response
    Then verify JSON path expressions match
      | totalCount == 1 |

