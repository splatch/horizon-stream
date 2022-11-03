/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2002-2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
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
package org.opennms.horizon.alarms.db.impl.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.opennms.horizon.events.api.EventDatabaseConstants;
//import org.opennms.horizon.events.api.EventDatabaseConstants;
//import org.opennms.horizon.events.xml.Parm;

/**
 * The Class OnmsEventParameter.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@Entity
@IdClass(EventParameterDTO.OnmsEventParameterId.class)
@Table(name="event_parameters")
@Getter
@Setter
@NoArgsConstructor
public class EventParameterDTO implements Serializable {

    private static final long serialVersionUID = 4530678411898489175L;

    @Id
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="eventID")
    private EventDTO event;

    /** The name. */
    @Id
    private String name;

    /** The value. */
    private String value;

    /** The type. */
    private String type;

    /** helper attribute to maintain the right order of event parameters when saving and retrieving to/from database. */
    private int position;

    /**
     * Instantiates a new OpenNMS event parameter.
     *
     * param parm the Event parameter object
     */
    //TODO:MMF do we need this for events?
//    public EventParameterDTO(EventDTO event, Parm parm) {
//        this.event = event;
//        name = parm.getParmName();
//        value = EventDatabaseConstants.escape(parm.getValue().getContent() == null ? "" : parm.getValue().getContent(), EventDatabaseConstants.NAME_VAL_DELIM);
//        type = parm.getValue().getType();
//    }
//
//    public EventParameterDTO(final EventDTO event,
//                              final String name,
//                              final String value,
//                              final String type) {
//        this.event = event;
//        this.name = name;
//        this.value = EventDatabaseConstants.escape(value == null ? "" : value, EventDatabaseConstants.NAME_VAL_DELIM);
//        this.type = type;
//    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
        return EventDatabaseConstants.escape(value, EventDatabaseConstants.NAME_VAL_DELIM);
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(String value) {
        this.value = EventDatabaseConstants.escape(value, EventDatabaseConstants.NAME_VAL_DELIM);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class OnmsEventParameterId implements Serializable {
        private EventDTO event;
        private String name;

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) { return false; }
            if (!(obj instanceof OnmsEventParameterId)) { return false; }

            return Objects.equals(this.event, ((OnmsEventParameterId) obj).event) &&
                Objects.equals(this.name, ((OnmsEventParameterId) obj).name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.event, this.name);
        }
    }

}
