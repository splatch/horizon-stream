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

package org.opennms.horizon.systemtests;

import com.codeborne.selenide.Selenide;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import org.opennms.horizon.systemtests.pages.cloud.CloudLoginPage;
import org.testcontainers.containers.GenericContainer;
import testcontainers.MinionContainer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CucumberHooks {
    public static final List<MinionContainer> MINIONS = new ArrayList<>();
    public static String instanceUrl;
    public static String gatewayHost;
    private static final String MINION_PREFIX = "Default_Minion-";
    private static final String LOCAL_INSTANCE_URL_DEFAULT = "https://onmshs.local:1443";
    private static final String LOCAL_MINION_GATEWAY_HOST_DEFAULT = "minion.onmshs.local";
    private static final String ADMIN_DEFAULT_USERNAME = "admin";
    private static final String ADMIN_DEFAULT_PASSWORD = "admin";

    @Before("@cloud")
    public static void setUp() {
        if (Selenide.webdriver().driver().hasWebDriverStarted()) {
            return;
        }

        long timeCode = Instant.now().toEpochMilli();

        instanceUrl = LOCAL_INSTANCE_URL_DEFAULT;
        gatewayHost = LOCAL_MINION_GATEWAY_HOST_DEFAULT;

        MinionContainer minionContainer = new MinionContainer(
            gatewayHost,
            MINION_PREFIX + timeCode,
            "location-" + timeCode
        );

        minionContainer.start();
        MINIONS.add(minionContainer);

        Selenide.open(instanceUrl);
        CloudLoginPage.checkPageTitle();
        CloudLoginPage.setUsername(ADMIN_DEFAULT_USERNAME);
        CloudLoginPage.setPassword(ADMIN_DEFAULT_PASSWORD);
        CloudLoginPage.clickSignInBtn();
    }

    @After("@cloud")
    public static void tearDownCloud() {
        Selenide.open(instanceUrl);

        Stream<MinionContainer> aDefault = MINIONS.stream().dropWhile(container -> !container.minionId.startsWith(MINION_PREFIX));
        aDefault.forEach(GenericContainer::stop);

        if (MINIONS.isEmpty()) {
            long timeCode = Instant.now().toEpochMilli();
            MinionContainer.createNewOne(
                MINION_PREFIX + timeCode,
                "location-" + timeCode
            );
        }
    }

    @AfterAll
    public static void tearDown() {
        if (!MINIONS.isEmpty()) {
            MINIONS.get(0).stop();
        }
    }
}
