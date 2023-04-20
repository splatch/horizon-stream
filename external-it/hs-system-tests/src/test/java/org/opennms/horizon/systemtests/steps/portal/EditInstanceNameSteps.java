package org.opennms.horizon.systemtests.steps.portal;

import io.cucumber.java.en.Then;
import org.opennms.horizon.systemtests.pages.portal.EditInstanceNamePopup;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.disappear;

public class EditInstanceNameSteps {
    @Then("set new name {string} in 'Instance Name' field")
    public void setNewInstanceName(String instanceName) {
        EditInstanceNamePopup.setNewInstanceName(instanceName);
    }

    @Then("click on 'UPDATE' button")
    public void clickUpdateBtn() {
        EditInstanceNamePopup.clickUpdateBtn();
    }

    @Then("'Edit Instance' popup appears")
    public void waitPopup() {
        EditInstanceNamePopup.isPopupVisible(appear);
    }

    @Then("'Edit Instance' popup disappear")
    public void waitPopupDisappear() {
        EditInstanceNamePopup.isPopupVisible(disappear);
    }
}
