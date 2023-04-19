@cloud
Feature: User can see a minion connected to the instance

  Scenario: Verify that we are able to login to the cloud instance
    Given Navigate to the "appliances" through the left panel
    Then check minion in the list
    Then check the status of the minion is "UP"
    When Stop running minion connected to the cloud instance
    Then check the status of the minion is "DOWN"
    Then check that the remove Minion button is displayed
    Then remove Minion from the list

 Scenario: Create and run a new Minion
   Given Navigate to the "appliances" through the left panel
   When Run a minion "Automation Minion" as name, "Ottawa" as location and connect to the cloud instance
   Then check minion in the list
   Then check the status of the minion is "UP"


