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

package org.opennms.horizon.server.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.opennms.horizon.server.model.entity.MonitoringLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

@DataJpaTest
public class MonitoringLocationRepositoryTest {
    @Autowired
    private MonitoringLocationRepository repo;
    @Autowired
    private DataSource dataSource;

    private String location1="test location1";
    private String location2="test location2";
    private String getLocation = "test geo location";

    @Test
    public void testCrudNotFound() {
        List<MonitoringLocation> list = repo.findAll();
        assertThat(list).isEmpty();

        Optional<MonitoringLocation> location = repo.findById(100L);
        assertThat(location).isEmpty();
        assertThrows(EmptyResultDataAccessException.class,
                () -> repo.deleteById(100L), "Entity not found");
    }

    @Test
    public void testAddAndGet() {
        MonitoringLocation first = createLocation(location1, 3, "first");
        MonitoringLocation savedLocation = repo.save(first);
        assertThat(savedLocation).isNotNull();
        assertThat(savedLocation.getId()).isNotNull();
        assertThat(savedLocation.getLocation()).isEqualTo(location1);
        assertThat(savedLocation.getGeolocation()).isEqualTo(first.getGeolocation());
        assertThat(savedLocation.getTags().size()).isEqualTo(3);

        MonitoringLocation dbLocation = repo.getById(savedLocation.getId());
        assertThat(dbLocation).isNotNull();
        assertThat(dbLocation.getId()).isEqualTo(savedLocation.getId());
        assertThat(dbLocation.getLocation()).isEqualTo(location1);
        dbLocation.getTags().forEach(t->assertThat(t).startsWith("first_"));
    }

    @Test
    public void testListAndDelete() {
        MonitoringLocation first = createLocation(location1, 1, "first");
        repo.save(first);
        MonitoringLocation second = createLocation(location2, 2,  "second");
        repo.save(second);
        List<MonitoringLocation> list = repo.findAll();
        assertThat(list.size()).isEqualTo(2);
        List<String> locationData = list.stream().map(l->l.getLocation()).collect(Collectors.toList());
        assertThat(locationData).containsAll(Arrays.asList(location1, location2));
        repo.delete(first);
        repo.deleteById(second.getId());
        List<MonitoringLocation> emptyList = repo.findAll();
        assertThat(emptyList).isEmpty();
    }

    @Test
    public void testUpdate() {
        String newGeoLocation = "spring.jpa.show-sql";
        String newTag ="extra_tag";
        MonitoringLocation location = repo.save(createLocation(location1, 2, "test"));
        assertThat(location).isNotNull();
        assertThat(location.getGeolocation()).isEqualTo(getLocation);
        assertThat(location.getTags().size()).isEqualTo(2);
        location.getTags().forEach(t->t.startsWith("test_"));

        location.setGeolocation(newGeoLocation);
        location.getTags().add(newTag);
        repo.save(location);
        MonitoringLocation updatedLocation = repo.findById(location.getId()).orElse(null);
        assertThat(updatedLocation).isNotNull();
        assertThat(updatedLocation.getGeolocation()).isEqualTo(newGeoLocation);
        assertThat(updatedLocation.getTags().size()).isEqualTo(3);
        assertThat(updatedLocation.getTags()).containsAll(Arrays.asList("test_0", "test_1", newTag));
    }

    private MonitoringLocation createLocation(String locationId, int tagCount, String tagPrefix){
        MonitoringLocation location = new MonitoringLocation();
        location.setLocation(locationId);
        location.setLongitude(1245.56);
        location.setLatitude(-235.49);
        location.setGeolocation(getLocation);
        location.setPriority(1);
        location.setMonitoringArea("test area");
        List<String> tags = new ArrayList<>();
        for(int i=0; i<tagCount; i++) {
            tags.add(tagPrefix +"_"+i);
        }
        location.setTags(tags);
        return location;
    }
}
