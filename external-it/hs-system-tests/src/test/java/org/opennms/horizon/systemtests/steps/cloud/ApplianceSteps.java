/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2023 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2023 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.horizon.systemtests.steps.cloud;

import com.codeborne.selenide.Selenide;
import io.cucumber.java.en.Then;
import org.opennms.horizon.systemtests.pages.cloud.AppliancePage;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertTrue;
import static org.opennms.horizon.systemtests.CucumberHooks.MINIONS;

public class ApplianceSteps {

    @Then("check {string} minion in the list")
    public static void waitWhenMinionAppear(String minionName) {
        if (minionName.equals("DEFAULT")) {
            minionName = MINIONS.get(0).minionId;
        }

        for (int i = 0; i < 2; i++) {
            if (!$(byText(minionName.toUpperCase())).isDisplayed()) {
                waitHeartbeat(1);
            }
        }
    }

    @Then("wait for {long} heartbeats")
    public static void waitHeartbeat(long count) {
        Selenide.sleep(count * 30_000);
        Selenide.refresh();
    }

    @Then("check that the remove Minion button is displayed")
    public void checkIsRemoveMinionButtonShown() {
        assertTrue(AppliancePage.checkIsRemoveButtonShown());
    }

    @Then("check the status of the minion is {string}")
    public void checkTheStatusOfTheMinionIs(String status) {
        AppliancePage.waitMinionStatus(status);
    }

    @Then("click on the delete button for minion")
    public void removeMinionFromTheList() {
        AppliancePage.clickRemoveMinion();
    }
}
