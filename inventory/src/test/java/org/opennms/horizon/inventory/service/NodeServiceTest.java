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

package org.opennms.horizon.inventory.service;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opennms.cloud.grpc.minion.Identity;
import org.opennms.horizon.grpc.heartbeat.contract.HeartbeatMessage;
import org.opennms.horizon.inventory.dto.DeviceCreateDTO;
import org.opennms.horizon.inventory.mapper.MonitoringSystemMapper;
import org.opennms.horizon.inventory.mapper.NodeMapper;
import org.opennms.horizon.inventory.model.IpInterface;
import org.opennms.horizon.inventory.model.MonitoringLocation;
import org.opennms.horizon.inventory.model.MonitoringSystem;
import org.opennms.horizon.inventory.repository.IpInterfaceRepository;
import org.opennms.horizon.inventory.repository.MonitoringLocationRepository;
import org.opennms.horizon.inventory.repository.MonitoringSystemRepository;
import org.opennms.horizon.inventory.repository.NodeRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NodeServiceTest {

    @InjectMocks
    NodeService nodeService;

    @Mock
    NodeRepository nodeRepository;

    @Mock
    MonitoringLocationRepository monitoringLocationRepository;

    @Mock
    IpInterfaceRepository ipInterfaceRepository;

    @Mock
    NodeMapper mapper;

    @Test
    public void createDevice() {
        DeviceCreateDTO deviceCreateDTO = DeviceCreateDTO.newBuilder()
            .setLabel("Label")
            .setLocation("loc")
            .setManagementIp("127.0.0.1")
            .build();

        nodeService.createDevice(deviceCreateDTO, "ANY");

        verify(ipInterfaceRepository).save(any(IpInterface.class));
        verify(monitoringLocationRepository).save(any(MonitoringLocation.class));
    }

    @Test
    public void createDeviceExistingLocation() {
        String location = "loc";
        String tenantId = "ANY";

        DeviceCreateDTO deviceCreateDTO = DeviceCreateDTO.newBuilder()
            .setLabel("Label")
            .setLocation(location)
            .setManagementIp("127.0.0.1")
            .build();

        doReturn(Optional.of(new MonitoringLocation())).when(monitoringLocationRepository).findByLocationAndTenantId(location, tenantId);

        nodeService.createDevice(deviceCreateDTO, tenantId);

        verify(ipInterfaceRepository).save(any(IpInterface.class));
        verify(monitoringLocationRepository, times(0)).save(any(MonitoringLocation.class));
    }

    @Test
    public void createDeviceNoIp() {
        DeviceCreateDTO deviceCreateDTO = DeviceCreateDTO.newBuilder()
            .setLabel("Label")
            .setLocation("loc")
            .build();

        nodeService.createDevice(deviceCreateDTO, "ANY");

        verify(ipInterfaceRepository, times(0)).save(any(IpInterface.class));
    }
}
