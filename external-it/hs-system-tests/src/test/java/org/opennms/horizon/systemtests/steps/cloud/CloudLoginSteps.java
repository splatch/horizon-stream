package org.opennms.horizon.systemtests.steps.cloud;

import com.codeborne.selenide.Selenide;
import io.cucumber.java.en.Then;
import org.junit.Assert;
import org.opennms.horizon.systemtests.CucumberHooks;
import org.opennms.horizon.systemtests.keyvalue.SecretsStorage;
import org.opennms.horizon.systemtests.pages.cloud.CloudLoginPage;
import org.opennms.horizon.systemtests.utils.TestDataStorage;

public class CloudLoginSteps {

    @Then("Cloud login page appears")
    public void checkPopupIsVisible() {
        CloudLoginPage.checkPageTitle();
    }

    @Then("set email address as {string}")
    public void setEmail(String email) {
        String userEmail = TestDataStorage.mapUserToEmail(email);
        CloudLoginPage.setUsername(userEmail);
    }

    @Then("click on 'Next' button")
    public void clickNextBtn() {
        CloudLoginPage.clickNextBtn();
    }

    @Then("set password")
    public void setPassword() {
        CloudLoginPage.setPassword(SecretsStorage.adminUserPassword);
    }

    @Then("click on 'Sign in' button")
    public void clickSignIn() {
        CloudLoginPage.clickSignInBtn();
    }

    @Then("verify the instance url for {string} instance")
    public void checkInstanceUrl(String instanceName) {
        String expectedUrl = CucumberHooks.portalApi.getAllBtoInstancesByName(instanceName).pagedRecords.get(0).url;
        String actualUrl = Selenide.webdriver().driver().url();

        Assert.assertTrue(
            String.format("Expected url:\n%s\nactual url\n%s", expectedUrl, actualUrl),
            actualUrl.replace("https://", "").startsWith(expectedUrl));
    }
}

