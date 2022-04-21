/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2017-2017 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2017 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.eventd.kafkastreams;

import org.mapstruct.*;
import org.opennms.horizon.core.lib.InetAddressUtils;
import org.opennms.horizon.events.xml.Event;

import java.net.InetAddress;
import java.util.Date;
import java.util.Locale;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class EventMapper {
    public abstract OpennmsEventModelProtos.Event eventToEventProto(Event event);

    public abstract Event eventProtoToEvent(OpennmsEventModelProtos.Event eventProto);

    public String inetAddressToString(InetAddress inetAddress) {
        return inetAddress.getHostAddress();
    }
    public InetAddress stringToInetAddress(String inetAddress) {
        return InetAddressUtils.addr(inetAddress);
    }

    public OpennmsEventModelProtos.AlarmData.AlarmType integerToAlarmType(Integer value) {
        return OpennmsEventModelProtos.AlarmData.AlarmType.forNumber(value - 1);
    }

    public Integer integerToAlarmType(OpennmsEventModelProtos.AlarmData.AlarmType value) {
        return value.getNumber() + 1;
    }

    public OpennmsEventModelProtos.Severity stringToSeverity(String severity) {
        return OpennmsEventModelProtos.Severity.valueOf(severity.toUpperCase());
    }

    public String severityToString(OpennmsEventModelProtos.Severity severity) {
        return severity.name();
    }

    public long dateToLong(Date date) {
        return date.getTime();
    }

    public Date longToDate(long time) {
        return new Date(time);
    }

}
