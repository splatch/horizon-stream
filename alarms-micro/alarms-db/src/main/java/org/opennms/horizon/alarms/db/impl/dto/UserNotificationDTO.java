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

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

@Entity
/**
 * <p>OnmsUserNotification class.</p>
 */
@Table(name="usersNotified")
@Data
public class UserNotificationDTO implements Serializable {

    private static final long serialVersionUID = -1750912427062821742L;

    @Id
    @Column(nullable=false)
    @SequenceGenerator(name="userNotificationSequence", sequenceName="userNotifNxtId", allocationSize = 1)
    @GeneratedValue(generator="userNotificationSequence")
    private Integer id;

    @Column(length=256)
    private String userId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date notifyTime;

    @Column(length=32)
    private String media;

    @Column(length=64)
    private String contactInfo;

    @Column(length=1)
    private String autoNotify;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="notifyId")
    private NotificationDTO notification;
}
