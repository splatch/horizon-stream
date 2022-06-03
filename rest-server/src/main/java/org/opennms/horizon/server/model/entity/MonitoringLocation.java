/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2022 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2022 The OpenNMS Group, Inc.
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

package org.opennms.horizon.server.model.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "monitoring_locations", uniqueConstraints = {@UniqueConstraint(name = "uk_monitoring_location", columnNames = {"location"})})
public class MonitoringLocation {
    public static String DEFAULT_LOCATION = "Default";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String location;
    @Column(nullable = false)
    private String monitoringArea = "localhost";
    private String geolocation;
    private Double latitude;
    private Double longitude;
    private Integer priority = 100;
    @ElementCollection
    @CollectionTable(name = "location_tags", joinColumns = @JoinColumn(name = "location_id"), foreignKey = @ForeignKey(name = "fk_location_tag_locationId"))
    private List<String> tags = new ArrayList<>();

    public void addTag(String tag) {
        tags.add(tag);
    }

    public static MonitoringLocation defaultLocation() {
        MonitoringLocation defaultLocation = new MonitoringLocation();
        defaultLocation.setLocation(DEFAULT_LOCATION);
        return defaultLocation;
    }
}
