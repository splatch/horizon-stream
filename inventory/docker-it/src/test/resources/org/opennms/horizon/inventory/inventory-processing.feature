Feature: Inventory Processing

  Background: Common Test Setup
    Given External GRPC Port in system property "application-external-grpc-port"
    Given Kafka Bootstrap URL in system property "kafka.bootstrap-servers"
    Given Grpc TenantId "tenant-stream"
    #Given Grpc location named "MINION"
    Given Create Grpc Connection for Inventory
    Given [Common] Create "MINION" Location
    Given [Common] Create "MINION-2" Location

  Scenario: Send an Heartbeat Message to Inventory and verify Minion and location are added
    Given Minion at location named "MINION" with system ID "MINION-TEST-1"
    Then send heartbeat message to Kafka topic "heartbeat"
    Then verify Monitoring system is created with system id "MINION-TEST-1"
    Then verify Monitoring location is created with location "MINION"

  Scenario: Add a device with existing location and verify Device and Associated Task creation
    Given Label "test-label"
    Given Device IP Address "192.168.10.1" in location named "MINION"
    Given Device Task IP address = "192.168.10.1"
    Given Subscribe to kafka topic "task-set-publisher"
    Then add a new device
    Then verify the device has an interface with the given IP address
    Then verify the new node return fields match
    Then retrieve the list of nodes from Inventory
    Then verify that the new node is in the list returned from inventory
    Then verify the task set update is published for device with nodeScan within 30000ms
    Then shutdown kafka consumer


  Scenario: Add a device with new location and verify that Device and location gets created
    Given add a new device with label "test-label-2" and ip address "192.168.20.1" and location named "MINION-2"
    Then verify that a new node is created with location named "MINION-2" and ip address "192.168.20.1"
    Then verify Monitoring location is created with location "MINION-2"

  Scenario: Add a device with existing ip address for a given location and verify that creation fails
    Then verify adding existing device with label "test-label-2" and ip address "192.168.20.1" and location "MINION-2" will fail


  Scenario: Detection of a Device causes Monitoring and Collector Task Definitions to be Published
    Given Device IP Address "192.168.30.1" in location named "MINION"
    Given Minion at location named "MINION" with system ID "MINION-TEST-1"
    Given Device detected indicator = "true"
    Given Device Task IP address = "192.168.30.1"
    Given Device detected reason = "useful detection reason - maybe responded to ICMP"
    # SNMP has both monitor and collector tasks
    Given Monitor Type "SNMP"
    Given Subscribe to kafka topic "task-set-publisher"
    Then add a new device with label "test-label" and ip address "192.168.30.1" and location named "MINION"
    Then lookup node with location "MINION" and ip address "192.168.30.1"
    Then send Device Detection to Kafka topic "task-set.results" for an ip address "192.168.30.1" at location "MINION"
    Then verify the task set update is published for device with task suffix "icmp-monitor" within 30000ms
    Then verify the task set update is published for device with task suffix "snmp-monitor" within 30000ms
    Then shutdown kafka consumer


  Scenario: Deletion of a device causes Task Definitions Removals to be Requested
    Given Device IP Address "192.168.30.1" in location named "MINION"
    Given Device Task IP address = "192.168.30.1"
    Given Subscribe to kafka topic "task-set-publisher"
    Then remove the device
    Then verify the task set update is published with removal of task with suffix "icmp-monitor" within 30000ms
    Then verify the task set update is published with removal of task with suffix "snmp-monitor" within 30000ms
    Then shutdown kafka consumer
# TBD888 - Test multi-tenancy
# TBD888 - Test Flows and Traps Configs published
