/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2017-2020 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2020 The OpenNMS Group, Inc.
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

package org.opennms.horizon.events.api;

import java.util.function.Consumer;

import org.opennms.horizon.events.model.IEvent;
import org.opennms.horizon.events.model.IParm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaemonTools {

    public static final Logger LOG = LoggerFactory.getLogger(DaemonTools.class);

    public static void handleReloadEvent(IEvent e, String daemonName, Consumer<IEvent> handleConfigurationChanged, EventForwarder eventForwarder) {
        final IParm daemonNameParm = e.getParm(EventConstants.PARM_DAEMON_NAME);
        if (daemonNameParm == null || daemonNameParm.getValue() == null) {
            LOG.warn("The {} parameter has no value. Ignoring.", EventConstants.PARM_DAEMON_NAME);
            return;
        }

        if (daemonName.equalsIgnoreCase(daemonNameParm.getValue().getContent())) {
            LOG.info("Reloading {}.", daemonName);

            EventBuilder ebldr = null;
            try {
                handleConfigurationChanged.accept(e);

                LOG.info("Reload successful.");

                ebldr = new EventBuilder(EventConstants.RELOAD_DAEMON_CONFIG_SUCCESSFUL_UEI, daemonName);

            } catch (Exception t) {
                LOG.error("Reload failed.", t);

                ebldr = new EventBuilder(EventConstants.RELOAD_DAEMON_CONFIG_FAILED_UEI, daemonName);
                ebldr.addParam(EventConstants.PARM_REASON, t.getLocalizedMessage().substring(0, 128));
            }

            ebldr.addParam(EventConstants.PARM_DAEMON_NAME, daemonName);
            eventForwarder.sendNow(ebldr.getEvent());
        }
    }

}
