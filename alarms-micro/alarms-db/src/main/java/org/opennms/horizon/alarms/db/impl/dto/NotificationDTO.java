/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2014 The OpenNMS Group, Inc.
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

package org.opennms.horizon.alarms.db.impl.dto;

import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Entity
@Table(name="notifications")
@Getter
@Setter
@NoArgsConstructor
public class NotificationDTO implements Serializable {

    private static final long serialVersionUID = -1162549324168290004L;

    @Id
    @Column(nullable=false)
    @SequenceGenerator(name="notifySequence", sequenceName="notifyNxtId", allocationSize = 1)
    @GeneratedValue(generator="notifySequence")
    private Integer notifyId;

    @Column(length=4000, nullable=false)
    private String textMsg;

    @Column(length=256)
    private String subject;

    @Column(length=256)
    private String numericMsg;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date pageTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date respondTime;

    @Column(length=256)
    private String answeredBy;

    @Column(name="interfaceId")
    @Type(type="org.opennms.horizon.alarms.db.impl.dto.InetAddressUserType")
    private InetAddress ipAddress;

    @ManyToOne
    @JoinColumn(name="serviceId")
    private ServiceTypeDTO serviceType;

    @Column(length=256)
    private String queueId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="eventId", nullable=false)    
    private EventDTO event;

    /** persistent field */
    @OneToMany(mappedBy="notification", fetch=FetchType.LAZY)
    private Set<UserNotificationDTO> usersNotified = new HashSet<>();
    
    /**
     * persistent field representing the name of the configured notification from
     * notifications.xml
     */
    @Column(length=63 )
    private String notifConfigName;
    
    public NotificationDTO(Integer notifyId, String textMsg, EventDTO event,Set<UserNotificationDTO> usersNotified) {
        notifyId = notifyId;
        textMsg = textMsg;
        event = event;
        usersNotified = usersNotified;
    }
    
    @Transient
    public Integer getEventId() {
        return event == null ? null : event.getEventId();
    }

    /*
     * FIXME: HACK for some reason we put the eventUEI in the notifications table along with the eventId
     * so we have to HACK this so we can properly write the table
     */
    /**
     * <p>getEventUei</p>
     *
     * @return a {@link String} object.
     */
    @Column(name="eventUEI")
    public String getEventUei() {
        return event == null ? null : event.getEventUei();
    }
    
    /**
     * <p>setEventUei</p>
     *
     * @param eventUei a {@link String} object.
     */
    public void setEventUei(String eventUei) {
        // do nothing as this is a HACK
    }

    /**
     * <p>getSeverityLabel</p>
     *
     * @return a {@link String} object.
     */
    @Transient
    public String getSeverityLabel() {
        return event == null ? null : event.getSeverityLabel();
    }
    
    /**
     * <p>toString</p>
     *
     * @return a {@link String} object.
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("notifyid", getNotifyId())
            .toString();
    }
    
}
