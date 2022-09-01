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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.opennms.horizon.shared.dto.device.DeviceCollectionDTO;
import org.opennms.horizon.shared.dto.device.DeviceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;

import io.leangen.graphql.execution.ResolutionEnvironment;
import reactor.core.publisher.Mono;

@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
public class DeviceServiceTest {
    @MockBean
    private PlatformGateway mockGateway;
    @MockBean
    private ResolutionEnvironment mockEnv;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private CacheManager cacheManager;
    private DeviceDTO device1;
    private DeviceDTO device2;
    private DeviceCollectionDTO collectionDTO;
    private String authHeader = "Authorization: abdcd";

    @BeforeEach
    public void setup() {
        device1 = new DeviceDTO();
        device1.setId(1);
        device2 = new DeviceDTO();
        device2.setId(2);
        collectionDTO = new DeviceCollectionDTO(Arrays.asList(device1, device2));
        doReturn(authHeader).when(mockGateway).getAuthHeader(mockEnv);
        cacheManager.getCache("devices").clear();
    }

    @Test
    public void testListDevices() {
        doReturn(Mono.just(collectionDTO)).when(mockGateway).get(PlatformGateway.URL_PATH_DEVICES, authHeader, DeviceCollectionDTO.class);
        deviceService.listDevices(mockEnv).subscribe(r -> assertEquals(collectionDTO, r));
        deviceService.listDevices(mockEnv).subscribe(r -> assertEquals(collectionDTO, r));
        verify(mockGateway, times(2)).getAuthHeader(mockEnv);
        verify(mockGateway, times(2)).get(PlatformGateway.URL_PATH_DEVICES, authHeader, DeviceCollectionDTO.class);
        verifyNoMoreInteractions(mockGateway);
    }

    @Test
    public void testGetByIDAfterList() throws InterruptedException {
        doReturn(Mono.just(collectionDTO)).when(mockGateway).get(PlatformGateway.URL_PATH_DEVICES, authHeader, DeviceCollectionDTO.class);
        deviceService.listDevices(mockEnv).doOnSuccess(result -> {
            assertEquals(collectionDTO, result);
            deviceService.getDeviceById(device1.getId(), mockEnv).subscribe(device -> assertEquals(device1, device));
            deviceService.getDeviceById(device2.getId(), mockEnv).subscribe(device -> assertEquals(device2, device));
        });
        verify(mockGateway).getAuthHeader(mockEnv);
        verify(mockGateway).get(PlatformGateway.URL_PATH_DEVICES, authHeader, DeviceCollectionDTO.class);
        verifyNoMoreInteractions(mockGateway);
    }

    @Test
    public void testGetByIDTwice() {
        doReturn(Mono.just(device1)).when(mockGateway).get(PlatformGateway.URL_PATH_DEVICES + "/" + device1.getId(), authHeader, DeviceDTO.class);
        deviceService.getDeviceById(device1.getId(), mockEnv).subscribe(result -> assertEquals(device1, result));
        deviceService.getDeviceById(device1.getId(), mockEnv).subscribe(result -> assertEquals(device1, result));
        verify(mockGateway).getAuthHeader(mockEnv);
        verify(mockGateway).get(PlatformGateway.URL_PATH_DEVICES + "/" + device1.getId(), authHeader, DeviceDTO.class);
        verifyNoMoreInteractions(mockGateway);
    }
}
