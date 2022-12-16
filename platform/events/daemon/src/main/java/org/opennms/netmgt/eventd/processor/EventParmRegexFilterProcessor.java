/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2014 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.eventd.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opennms.horizon.events.api.EventConfDao;
import org.opennms.horizon.events.api.EventProcessor;
import org.opennms.horizon.events.api.EventProcessorException;
import org.opennms.horizon.events.conf.xml.Filter;
import org.opennms.horizon.events.xml.Event;
import org.opennms.horizon.events.xml.Log;
import org.opennms.horizon.events.xml.Parm;
import org.opennms.netmgt.eventd.EventExpander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Seth
 * @author <a href="mailto:tfalzone@doubleclick.com">Tim Falzone</a>
 */
public final class EventParmRegexFilterProcessor implements EventProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(EventParmRegexFilterProcessor.class);

    private EventConfDao m_eventConfDao;
    private Map<String, Filter> m_filterMap = new HashMap<>();

    /**
     * This processor is always synchronous so this method just 
     * delegates to {@link #process(Log)}.
     */
    @Override
    public void process(Log eventLog, boolean synchronous) throws EventProcessorException {
        process(eventLog);
    }

    @Override
    public void process(Log eventLog) throws EventProcessorException {
        if (eventLog != null && eventLog.getEvents() != null && eventLog.getEvents().getEvent() != null) {
            for (Event eachEvent : eventLog.getEvents().getEvent()) {
                process(eachEvent);
            }
        }
    }

    private void process(Event event) throws EventProcessorException {

        org.opennms.horizon.events.conf.xml.Event econf = EventExpander.lookup(m_eventConfDao, event);
        if (econf.getFilters() != null) {

            for (Filter fConf : econf.getFilters()) {
                if (!m_filterMap.containsKey(fConf.getEventparm() + "|" + event.getUei())) {
                    m_filterMap.put(fConf.getEventparm() + "|" + event.getUei(), fConf);
                    LOG.debug("adding [{}|{}] to filter map", fConf.getEventparm(), event.getUei());
                }
            }

            for (Parm parm : event.getParmCollection()) {
                if ((parm.getParmName() != null)
                        && (parm.getValue().getContent() != null)
                        && (m_filterMap.containsKey(parm.getParmName() + "|" + event.getUei()))
                ) {
                    Filter f = m_filterMap.get(parm.getParmName() + "|" + event.getUei());
                    LOG.debug("filtering {} with {}", parm.getParmName(), f.getPattern());
                    final Pattern pattern = Pattern.compile( f.getPattern() );
                    Matcher matcher = pattern.matcher( parm.getValue().getContent().trim() );
                    parm.getValue().setContent( matcher.replaceAll(f.getReplacement()) );
                }
            }
        }
    }

    /**
     * <p>getEventConfDao</p>
     *
     * @return a {@link EventConfDao} object.
     */
    public EventConfDao getEventConfDao() {
        return m_eventConfDao;
    }

    /**
     * <p>setEventConfDao</p>
     *
     * @param eventConfDao a {@link EventConfDao} object.
     */
    public void setEventConfDao(EventConfDao eventConfDao) {
        m_eventConfDao = eventConfDao;
    }
}
