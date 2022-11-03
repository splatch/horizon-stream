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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DiscriminatorOptions;

/**
 * <p>Represents an OpenNMS monitoring system that can poll status of nodes
 * and report events that occur on the network. Examples of monitoring systems
 * include:</p>
 * 
 * <ul>
 * <li>OpenNMS</li>
 * <li>OpenNMS Remote Poller</li>
 * <li>OpenNMS Minion</li>
 * </ul>
 * 
 * <p>CAUTION: Don't add final modifiers to methods here because they need to be
 * proxyable to the child classes and Javassist doesn't override final methods.
 * 
 * @author Seth
 */
@Entity
@Table(name="monitoringSystems")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="type",
    discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue("System")
// Require all objects to have a discriminator type
@DiscriminatorOptions(force=true)
@Getter
@Setter
@NoArgsConstructor
public class MonitoringSystemDTO implements Serializable {

    private static final long serialVersionUID = -5095710111103727832L;

    public static final String TYPE_OPENNMS = "OpenNMS";
    public static final String TYPE_MINION = "Minion";

    @Id
    @Column(name="id", nullable=false)
    private String id;

    @Column(name="label")
    private String label;

    @Column(name="location", nullable=false)
    private String location;

    @Column(name="type", nullable=false, insertable=false, updatable=false)
    private String type;

    @Column(name="last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    //TODO:MMF check with jesse on this, no accessors currently!
    private Date lastCheckedIn;

    @ElementCollection
    @JoinTable(name="monitoringSystemsProperties", joinColumns = @JoinColumn(name="monitoringSystemId"))
    @MapKeyColumn(name="property", nullable=false)
    @Column(name="propertyValue")
    private Map<String,String> properties = new HashMap<String,String>();

    public MonitoringSystemDTO(String id, String location) {
        id = id;
        location = location;
    }
    

    public void setProperty(String property, String value) {
        properties.put(property, value);
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
            .add("label", getLabel())
            .add("location", getLocation())
            .add("type", getType())
            .toString();
    }
}
