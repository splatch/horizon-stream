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

package org.opennms.horizon.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opennms.horizon.shared.dto.device.LocationCollectionDTO;
import org.opennms.horizon.shared.dto.device.LocationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;

import io.leangen.graphql.execution.ResolutionEnvironment;
import reactor.core.publisher.Mono;

@SpringBootTest
public class LocationServiceTest {
    @MockBean
    private PlatformGateway mockGateway;
    @MockBean
    private ResolutionEnvironment mockEnv;
    @Autowired
    LocationService locationService;
    @Autowired
    CacheManager cacheManager;
    private LocationCollectionDTO collectionDTO;
    private LocationDTO location1, location2;
    private String authHeader = "Authorization: abdcd";

    @BeforeEach
    public void setUp() {
        location1 = new LocationDTO();
        location1.setLocationName("test-location1");
        location2 = new LocationDTO();
        location2.setLocationName("test-location2");
        collectionDTO = new LocationCollectionDTO(Arrays.asList(location1, location2));
        doReturn(authHeader).when(mockGateway).getAuthHeader(mockEnv);
        cacheManager.getCache("locations").clear();
    }

    @Test
    public void testListLocations() {
        doReturn(Mono.just(collectionDTO)).when(mockGateway).get(PlatformGateway.URL_PATH_LOCATIONS, authHeader, LocationCollectionDTO.class);
        locationService.listLocations(mockEnv).subscribe(r -> assertEquals(collectionDTO, r));
        locationService.listLocations(mockEnv).subscribe(r -> assertEquals(collectionDTO, r));
        verify(mockGateway, times(2)).getAuthHeader(mockEnv);
        verify(mockGateway, times(2)).get(PlatformGateway.URL_PATH_LOCATIONS, authHeader, LocationCollectionDTO.class);
        verifyNoMoreInteractions(mockGateway);
    }

    @Test
    public void testGetByIDAfterList() {
        doReturn(Mono.just(collectionDTO)).when(mockGateway).get(PlatformGateway.URL_PATH_LOCATIONS, authHeader, LocationCollectionDTO.class);
        locationService.listLocations(mockEnv).doOnSuccess (list -> {
            assertEquals(collectionDTO, list);
            locationService.getLocationById(location1.getLocationName(), mockEnv).subscribe(l -> assertEquals(location1, l));
            locationService.getLocationById(location2.getLocationName(), mockEnv).subscribe(l -> assertEquals(location2, l));
        });
        verify(mockGateway).getAuthHeader(mockEnv);
        verify(mockGateway).get(PlatformGateway.URL_PATH_LOCATIONS, authHeader, LocationCollectionDTO.class);
        verifyNoMoreInteractions(mockGateway);
    }

    @Test
    public void testGetByID() {
        doReturn(Mono.just(location1)).when(mockGateway).get(PlatformGateway.URL_PATH_LOCATIONS + "/" + location1.getLocationName(), authHeader, LocationDTO.class);
        locationService.getLocationById(location1.getLocationName(), mockEnv).subscribe(location -> assertEquals(location1, location));
        locationService.getLocationById(location1.getLocationName(), mockEnv).subscribe(location -> assertEquals(location1, location));
        verify(mockGateway).getAuthHeader(mockEnv);
        verify(mockGateway).get(PlatformGateway.URL_PATH_LOCATIONS + "/" + location1.getLocationName(), authHeader, LocationDTO.class);
        verifyNoMoreInteractions(mockGateway);
    }
}
