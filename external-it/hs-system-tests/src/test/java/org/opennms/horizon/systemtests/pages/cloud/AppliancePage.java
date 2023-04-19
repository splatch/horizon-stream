package org.opennms.horizon.systemtests.pages.cloud;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;

public class AppliancePage {

    private static final SelenideElement minionRemoveButton = $("[data-test='minion-item-delete-btn']");
    private static final SelenideElement minionLatency = $("[data-test='minion-item-latency']");
    private static final SelenideElement minionStatus = $("[data-test='minion-item-status']");
    private static final SelenideElement deviceStatus = $("[data-test='node-item']");
    private static final SelenideElement addDeviceButton = $("[data-test='add-node-btn']");

    private static final SelenideElement deviceNameInput = $("[data-test='name-input'] input");
    private static final SelenideElement deviceIpInput = $("[data-test='ip-input'] input");

    private static final SelenideElement saveButton = $("[data-test='save-btn']");
    private static final SelenideElement cancelButton = $("[data-test='cancel-btn']");

    public static void setDeviceNameInput(String name) {
        deviceNameInput.sendKeys(name);
    }

    public static void setDeviceIpInput(String ipAddress) {
        deviceIpInput.sendKeys(ipAddress);
    }

    public static void clickSaveButton() {
        saveButton.click();
    }

    public static void clickCancelButton() {
        cancelButton.click();
    }

    public static void clickAddDevice() {
        addDeviceButton.click();
    }

    public static String getMinionStatus() {
        return minionStatus.getText();
    }

    public static void waitMinionStatus(String status) {
        minionStatus.shouldHave(Condition.text(status), Duration.ofSeconds(31));
    }

    public static String getDeviceStatus() {
        return deviceStatus.getText();
    }

    public static String getMinionLatency() {
        return minionLatency.getText();
    }

    public static void clickRemoveMinion() {
        minionRemoveButton.click();
    }

    public static boolean checkIsRemoveButtonShown() {
        return minionRemoveButton.isDisplayed();
    }

}
