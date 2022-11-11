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

package org.opennms.horizon.alarmservice.db.impl.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

/**
 * <p>OnmsServiceType class.</p>
 *
 * @hibernate.class table="service"
 */
@Entity
@Table(name="service")
@Data
public class ServiceType extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -459218937667452586L;

    @Id
    @Column(name="service_id")
    @SequenceGenerator(name="serviceTypeSequence", sequenceName="serviceNxtId", allocationSize = 1)
    @GeneratedValue(generator="serviceTypeSequence")
    private Long id;

    @Column(name="service_name", nullable=false, unique=true, length=255)
    private String name;

    /**
     * full constructor
     *
     * @param servicename a {@link String} object.
     */
    public ServiceType(String servicename) {
        name = servicename;
    }

    public ServiceType(Long id, String servicename) {
        this.id = id;
        this.name = servicename;
    }

    /**
     * default constructor
     */
    public ServiceType() {
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ServiceType) {
            ServiceType t = (ServiceType)obj;
            return id.equals(t.id);
        }
        return false;
    }

    /**
     * <p>hashCode</p>
     *
     * @return a int.
     */
    @Override
    public int hashCode() {
        return id.intValue();
    }
}
