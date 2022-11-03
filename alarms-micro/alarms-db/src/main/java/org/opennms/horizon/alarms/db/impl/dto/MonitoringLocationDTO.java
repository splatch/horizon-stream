/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2014 The OpenNMS Group, Inc.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * This element contains the name of the location, the name of the
 * monitoring area (used to aggregate locations, example: Area San Francisco,
 * location name "SFO" which becomes SFO-1 or SFO-BuildingA, etc.)
 * Additionally, a geolocation can be provided (an address or other
 * identifying location that can be looked up with a geolocation
 *  API), as well as coordinates (latitude,longitude). Finally, a
 * priority can be assigned to the location, for purposes of sorting
 * (1 = highest, 100 = lowest).
 * </p>
 * <p>
 * The polling package name is used to associate with a polling
 * configuration found in the polling-configuration.xml file. 
 * </p>
 * <p>
 * The collection package name is used to associate with a collection
 * configuration found in the collectd-configuration.xml file.
 */
@Entity
@Table(name="monitoringLocations")
@Data
public class MonitoringLocationDTO implements Serializable {
    private static final long serialVersionUID = -7651610012389148818L;
    
    @Id
    @Column(name="id", nullable=false)
    private String locationName;

    @Column(nullable=false)
    private String monitoringArea;

    @Column
    private String geolocation;

    @Column
    private Float longitude;

    @Column
    private Float latitude;

    @Column
    private Long priority;

    @ElementCollection
    @JoinTable(name="monitoringLocationsTags", joinColumns = @JoinColumn(name="monitoringLocationId"))
    @Column(name="tag")
    private List<String> tags;

    public MonitoringLocationDTO() {
        super();
    }

    /**
     * This constructor is only used during unit testing.
     * 
     * @param locationName
     * @param monitoringArea
     */
    public MonitoringLocationDTO(final String locationName, final String monitoringArea) {
        this(locationName, monitoringArea, null, null, null, null, null, null);
    }

    public MonitoringLocationDTO(final String locationName, final String monitoringArea, final String geolocation, final Float latitude, final Float longitude, final Long priority, final String... tags) {
        this.locationName = locationName;
        this.monitoringArea = monitoringArea;
        this.geolocation = geolocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.priority = priority;
        // Because tags is a vararg, if you have no arguments for it, it comes in as String[0]
        this.tags = ((tags == null || tags.length == 0) ? Collections.emptyList() : Arrays.asList(tags));
    }
    

    public List<String> getTags() {
        if (tags == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(tags);
        }
    }

    public void setTags(final List<String> tags) {
        if (tags == null || tags.size() == 0) {
            this.tags = Collections.emptyList();
        } else {
            this.tags = new ArrayList<String>(tags);
        }
    }
}
