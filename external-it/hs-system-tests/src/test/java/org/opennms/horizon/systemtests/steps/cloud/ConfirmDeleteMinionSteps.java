package org.opennms.horizon.systemtests.steps.cloud;

import io.cucumber.java.en.Then;
import org.opennms.horizon.systemtests.pages.cloud.ConfirmDeleteMinionPopup;

public class ConfirmDeleteMinionSteps {
    @Then("confirm the minion deletion")
    public void clickSubmitDeleteBtn() {
        ConfirmDeleteMinionPopup.clickDeleteBtn();
    }
}
