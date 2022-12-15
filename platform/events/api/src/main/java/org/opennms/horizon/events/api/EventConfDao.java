/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2008-2014 The OpenNMS Group, Inc.
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

package org.opennms.horizon.events.api;

import java.util.List;
import java.util.Map;

import org.opennms.horizon.events.conf.xml.Event;
import org.opennms.horizon.events.conf.xml.Events;

/**
 * <p>EventConfDao interface.</p>
 *
 * @author ranger
 * @version $Id: $
 */
public interface EventConfDao {

    void reload();

    /**
     * <p>getEvents</p>
     *
     * @param uei a {@link String} object.
     * @return a {@link List} object.
     */
    List<Event> getEvents(String uei);

    /**
     * <p>getEventUEIs</p>
     *
     * @return a {@link List} object.
     */
    List<String> getEventUEIs();

    /**
     * <p>getEventLabels</p>
     *
     * @return a {@link Map} object.
     */
    Map<String, String> getEventLabels();

    /**
     * <p>getEventLabel</p>
     *
     * @param uei a {@link String} object.
     * @return a {@link String} object.
     */
    String getEventLabel(String uei);

    /**
     * <p>saveCurrent</p>
     */
    void saveCurrent();

    /**
     * <p>getEventsByLabel</p>
     *
     * @return a {@link List} object.
     */
    List<Event> getEventsByLabel();

    /**
     * Adds the event to the root level event config storage (file).
     * Does not save (you must save independently with saveCurrent)
     *
     * @param event The fully configured Event object to add.
     */
    void addEvent(Event event);

    /**
     * Adds the given event to the programmatic event store.  This store currently implemented as a file (referenced from eventconf.xml)
     * The programmatic store is a separate storage area, so that incidental programmatic editing of events (e.g. custom UEIs for thresholds, edited
     * through the Web-UI) does not clutter up the otherwise carefully maintained event files.  This method does not save (persist) the changes
     *
     * @param event The fully configured Event object to add.
     */
    void addEventToProgrammaticStore(Event event);

    /**
     * Removes the given event from the programmatic event store.  This store currently implemented as a file (referenced from eventconf.xml)
     * The programmatic store is a separate storage area, so that incidental programmatic editing of events (e.g. custom UEIs for thresholds, edited
     * through the Web-UI) does not clutter up the otherwise carefully maintained event files.  This method does not save (persist) the changes
     *
     * @param event The fully configured Event object to remove.
     * @returns true if the event was removed, false if it wasn't found (either not in the programmatic store, or the store didn't exist)
     * @return a boolean.
     */
    boolean removeEventFromProgrammaticStore(Event event);

    /**
     * <p>isSecureTag</p>
     *
     * @param tag a {@link String} object.
     * @return a boolean.
     */
    boolean isSecureTag(String tag);

    /**
     * <p>findByUei</p>
     *
     * @param uei a {@link String} object.
     */
    Event findByUei(String uei);

    /**
     * <p>findByEvent</p>
     */
    Event findByEvent(org.opennms.horizon.events.xml.Event matchingEvent);

    /**
     * <p>getRootEvents</p>
     * 
     * @return a {@link Events} object.
     */
    Events getRootEvents();
}
