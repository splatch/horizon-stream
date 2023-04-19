@cloud
Feature: User can see a minion connected to the instance

  Scenario: Verify that user can delete a minion with status DOWN
    Given Navigate to the "appliances" through the left panel
    Then check "DEFAULT" minion in the list
    Then check the status of the minion is "UP"
    When Stop running minion connected to the cloud instance
    Then wait for 2 heartbeats
    Then check the status of the minion is "DOWN"
    Then check that the remove Minion button is displayed
    Then click on the delete button for minion
    And confirm the minion deletion

  Scenario: Create and run a new Minion
    Given Navigate to the "appliances" through the left panel
    When Run a minion "Automation Minion" as name, "Ottawa" as location and connect to the cloud instance
    Then check "Automation Minion" minion in the list
    Then check the status of the minion is "UP"


