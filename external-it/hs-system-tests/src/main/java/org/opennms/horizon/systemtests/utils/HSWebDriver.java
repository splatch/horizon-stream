package org.opennms.horizon.systemtests.utils;

//import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.remote.CapabilityType;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.FluentWait;
//import org.openqa.selenium.support.ui.Wait;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.util.List;

public class HSWebDriver {

//    public static ChromeDriver driver;
//    private static final long TIME_OUT_SEC = 5;
//    private static final Logger LOG = LoggerFactory.getLogger(HSWebDriver.class);
//    private WebDriverWait wait = null;
//    private Wait<org.openqa.selenium.WebDriver> fluentWait = null;
//
//    public HSWebDriver() {
//        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.setHeadless(false);
//        chromeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
//        // path to the driver
//        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
//
//        driver = new ChromeDriver(chromeOptions);
//        wait = new WebDriverWait(driver, TIME_OUT_SEC);
//        fluentWait = new FluentWait<>(driver);
//    }
//
//    public WebElement getElement(By element) throws InterruptedException {
//        slowDown();
//        return wait.until(ExpectedConditions.elementToBeClickable(element));
//    }
//
//    public String getElementText(By element) throws InterruptedException {
//        slowDown();
//        return wait.until(ExpectedConditions.elementToBeClickable(element)).getText();
//    }
//
//    public List<WebElement> getElements(By element) throws InterruptedException {
//        slowDown();
//        return driver.findElements(element);
//    }
//
//    public boolean isElementVisible(By element) {
//        try {
//            wait.until(d -> driver.findElement(element)).isDisplayed();
//        } catch (Exception e) {
//            return false;
//        }
//        return true;
//    }
//
//    public void slowDown() throws InterruptedException {
//        Thread.sleep(1000);
//    }



}
