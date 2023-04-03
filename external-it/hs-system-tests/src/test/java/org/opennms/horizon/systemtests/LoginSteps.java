package org.opennms.horizon.systemtests;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;

public class LoginSteps {

    private static final Logger LOG = LoggerFactory.getLogger(LoginSteps.class);

    private String url = "https://f280581c-8ff7-4658-a476-0778f0b6dd16.tnnt.onms-fb-dev.dev.nonprod.dataservice.opennms.com";
    private String user = "chadfieldqa@gmail.com";
    private String password = "HorizonStreamPassword!@#";


    @Given("Login to the web interface with provided {string} and {string}")
    public void loginToTheWebInterfaceWithProvidedAnd(String username, String password) {
        doLogin();
    }

    @Then("Verify that we logged in successfully")
    public void verifyThatWeLoggedInSuccessfully() {

    }


    private void doLogin() {
        LOG.info("doLogin method started");
        open(url);
        sleep(1500);
        //driver.get(url);

        // Wait if Cookie Banner appears
        if ($(By.id("idp-discovery-username")).isDisplayed()) {
            WebElement usernameField = $(By.id("idp-discovery-username"));
            usernameField.sendKeys(user);
            $(By.id("idp-discovery-submit")).click();
        }

        WebElement passwordField = $(By.id("okta-signin-password"));
        passwordField.sendKeys(password);
        $(By.id("okta-signin-submit")).click();

        sleep(10_000);

    }
}
