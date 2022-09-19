/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2002-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
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
package org.opennms.horizon.jmx.dao;


import java.io.IOException;
import java.net.URL;
import org.opennms.horizon.jmx.config.JmxConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation for config dao class.
 *
 * @author Christian Pape <Christian.Pape@informatik.hs-fulda.de>
 */
public class JmxConfigDaoJaxb implements JmxConfigDao {
    private static final Logger LOG = LoggerFactory.getLogger(JmxConfigDaoJaxb.class);

    private JmxConfig config;
    /**
     * Returns the loaded config object.
     *
     * @return the current config object
     */
    @Override
    public JmxConfig getConfig() {
        URL url = this.getClass().getClassLoader().getResource("jmx-config.json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            config = mapper.readValue(url, JmxConfig.class);
        } catch (IOException ex) {
            LOG.error("Failed to load JmxConfig from jmx-config.json file", ex);
        }
        return config;
    }

    //TODO: might be safe to remove this method
    public JmxConfig translateConfig(JmxConfig jmxConfig) {
        return jmxConfig;
    }
}
