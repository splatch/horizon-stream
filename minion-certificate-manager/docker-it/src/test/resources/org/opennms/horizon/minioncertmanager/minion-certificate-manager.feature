Feature: Minion Certificate Manager RPC Request Processing

  Background: Configure base URLs
    Given Application base HTTP URL in system property "application.base-http-url"
    Given Application base gRPC URL in system property "application.base-grpc-url"

  Scenario: Send a get minion certificate request
    Given Get Minion Certificate Request TenantId "x-tenant-x"
    Given Get Minion Certificate Request Location "x-location-001-x"
    Then send Get Minion Certificate Request with timeout 10000ms and verify success

  Scenario: Send two get minion certificate request
    Given Get Minion Certificate Request TenantId "x-tenant-x"
    Given Get Minion Certificate Request Location "x-location-001-x"
    Then send Get Minion Certificate Request with timeout 10000ms and verify success
    Then send Get Minion Certificate Request with timeout 10000ms and verify success

  Scenario: Send two get minion certificate request with different tenant id
    Given Get Minion Certificate Request TenantId "x-tenant-x"
    Given Get Minion Certificate Request Location "x-location-001-x"
    Then send Get Minion Certificate Request with timeout 10000ms and verify success
    Given Get Minion Certificate Request TenantId "x-tenant-y"
    Then send Get Minion Certificate Request with timeout 10000ms and verify success
