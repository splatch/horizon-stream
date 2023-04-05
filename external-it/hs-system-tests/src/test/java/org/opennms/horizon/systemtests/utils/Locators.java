package org.opennms.horizon.systemtests.utils;

import org.openqa.selenium.By;

public final class Locators {


    // Login Page
    public static final By LOGIN_USERNAME = By.id("idp-discovery-username");
    public static final By LOGIN_PASSWORD = By.id("okta-signin-password");
    public static final By LOGIN_NEXT = By.id("idp-discovery-submit");
    public static final By LOGIN_SUBMIT = By.id("okta-signin-submit");


    // Banners
    public static final By OPENNMS_BANNER = By.className("icon-animate");

    // Buttons


}
