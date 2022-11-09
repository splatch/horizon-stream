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

import com.google.common.base.MoreObjects;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.opennms.horizon.alarmservice.db.api.EntityVisitor;

/**
 * <p>OnmsServiceType class.</p>
 *
 * @hibernate.class table="service"
 */
@XmlRootElement(name = "serviceType")
@Entity
@Table(name="service")
public class ServiceType extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -459218937667452586L;

    /** identifier field */
    private Integer m_id;

    /** persistent field */
    private String m_name;

    /**
     * full constructor
     *
     * @param servicename a {@link String} object.
     */
    public ServiceType(String servicename) {
        m_name = servicename;
    }

    public ServiceType(Integer id, String servicename) {
        m_id = id;
        m_name = servicename;
    }

    /**
     * default constructor
     */
    public ServiceType() {
    }

    /**
     * <p>getId</p>
     *
     * @return a {@link Integer} object.
     */
    @Id
    @XmlAttribute(name="id")
    @Column(name="serviceId")
    @SequenceGenerator(name="serviceTypeSequence", sequenceName="serviceNxtId", allocationSize = 1)
    @GeneratedValue(generator="serviceTypeSequence")
    public Integer getId() {
        return m_id;
    }

    /**
     * <p>setId</p>
     *
     * @param serviceid a {@link Integer} object.
     */
    public void setId(Integer serviceid) {
        m_id = serviceid;
    }

    /**
     * <p>getName</p>
     *
     * @return a {@link String} object.
     */
    @Column(name="serviceName", nullable=false, unique=true, length=255)
    public String getName() {
        return m_name;
    }

    /**
     * <p>setName</p>
     *
     * @param name a {@link String} object.
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * <p>toString</p>
     *
     * @return a {@link String} object.
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", getId())
            .add("name", getName())
            .toString();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ServiceType) {
            ServiceType t = (ServiceType)obj;
            return m_id.equals(t.m_id);
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
        return m_id.intValue();
    }

    @Override
    public void visit(EntityVisitor visitor) {

    }
}
