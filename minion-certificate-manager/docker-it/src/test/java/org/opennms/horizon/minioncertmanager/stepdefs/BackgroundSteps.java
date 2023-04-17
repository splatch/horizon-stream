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

package org.opennms.horizon.minioncertmanager.stepdefs;

import io.cucumber.java.en.Given;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class BackgroundSteps {

    private static final Logger LOG = LoggerFactory.getLogger(BackgroundSteps.class);

    //Test configuration
    private String applicationBaseHttpUrl;
    private String applicationBaseGrpcUrl;

    @Given("Application base HTTP URL in system property {string}")
    public void applicationBaseHttpUrlInSystemProperty(String systemProperty) {
        this.applicationBaseHttpUrl = System.getProperty(systemProperty);

        LOG.info("Using base HTTP URL {}", this.applicationBaseHttpUrl);
    }

    @Given("Application base gRPC URL in system property {string}")
    public void applicationBaseGrpcUrlInSystemProperty(String systemProperty) {
        this.applicationBaseGrpcUrl = System.getProperty(systemProperty);

        LOG.info("Using base gRPC URL: {}", this.applicationBaseGrpcUrl);
    }
}
