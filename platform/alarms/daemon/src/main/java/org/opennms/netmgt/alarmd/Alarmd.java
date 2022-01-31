/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2009-2020 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.alarmd;

import org.opennms.horizon.core.lib.SystemProperties;
import org.opennms.horizon.events.api.DaemonTools;
import org.opennms.horizon.events.api.EventForwarder;
import org.opennms.horizon.events.api.ThreadAwareEventListener;
import org.opennms.horizon.events.model.IEvent;
import org.opennms.horizon.events.xml.Event;
import org.opennms.netmgt.alarmd.drools.DroolsAlarmContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Alarm management Daemon
 *
 * @author jwhite
 * @author <a href="mailto:david@opennms.org">David Hustace</a>
 */

public class Alarmd implements ThreadAwareEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(Alarmd.class);

    /** Constant <code>NAME="alarmd"</code> */
    public static final String NAME = "alarmd";

    protected static final Integer THREADS = SystemProperties.getInteger("org.opennms.alarmd.threads", 4);

    private AlarmPersister m_persister;

    @Autowired
    private AlarmLifecycleListenerManager m_alm;

    @Autowired
    private DroolsAlarmContext m_droolsAlarmContext;

    @Autowired
    private EventForwarder m_eventForwarder;

    /**
     * Listens for all events.
     *
     * This method is thread-safe.
     *
     * @param e a {@link org.opennms.netmgt.events.api.model.IEvent} object.
     */
   // @EventHandler(uei = EventHandler.ALL_UEIS)
    public void onEvent(IEvent e) {
    	if (e.getUei().equals("uei.opennms.org/internal/reloadDaemonConfig")) {
           handleReloadEvent(e);
           return;
    	}
    	m_persister.persist(Event.copyFrom(e));
    }

    private synchronized void handleReloadEvent(IEvent e) {
        DaemonTools.handleReloadEvent(e, Alarmd.NAME, (event) -> onAlarmReload(), m_eventForwarder);
    }

    private void onAlarmReload() {
        m_droolsAlarmContext.reload();
    }

	/**
     * <p>setPersister</p>
     *
     * @param persister a {@link org.opennms.netmgt.alarmd.AlarmPersister} object.
     */
    public void setPersister(AlarmPersister persister) {
        this.m_persister = persister;
    }

    /**
     * <p>getPersister</p>
     *
     * @return a {@link org.opennms.netmgt.alarmd.AlarmPersister} object.
     */
    public AlarmPersister getPersister() {
        return m_persister;
    }

    protected synchronized void onInit() {
        // pass
    }

    public synchronized void onStart() {
        // Start the Drools context
        m_droolsAlarmContext.start();
    }

    public synchronized void onStop() {
        // Stop the Drools context
        m_droolsAlarmContext.stop();
    }

    @Override
    public int getNumThreads() {
        return THREADS;
    }


    public void setAlarmLifecycleListenerManager(AlarmLifecycleListenerManager allm) {
        this.m_alm = allm;
    }

    public void setDroolsAlarmContext(DroolsAlarmContext droolsAlarmContext) {
        this.m_droolsAlarmContext = droolsAlarmContext;
    }

    public void setEventForwarder(EventForwarder eventForwarder) {
        this.m_eventForwarder = m_eventForwarder;
    }
}
