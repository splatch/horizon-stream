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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.opennms.horizon.inventory.dto.MonitoringLocationDTO;
import org.opennms.horizon.inventory.mapper.MonitoringLocationMapper;
import org.opennms.horizon.inventory.model.MonitoringLocation;
import org.opennms.horizon.inventory.repository.MonitoringLocationRepository;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.internal.testing.StreamRecorder;

public class InventoryGrpcServiceTest {
    private InventoryGrpcService locationService;
    private MonitoringLocationRepository mockRepository;
    private final MonitoringLocationMapper mapper = Mappers.getMapper(MonitoringLocationMapper.class);
    @BeforeEach
    public void setUp(){
        mockRepository = mock(MonitoringLocationRepository.class);
        locationService = new InventoryGrpcService(mockRepository);
    }

    @AfterEach
    public void veryMock(){
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    public void testListLocations() throws Exception {
        MonitoringLocationDTO locationDTO = MonitoringLocationDTO.newBuilder()
            .setLocation("test-location")
            .setTenantId(new UUID(10, 10).toString())
            .setId(1L)
            .build();
        MonitoringLocation location = mapper.dtoToModel(locationDTO);
        MonitoringLocationDTO locationDTO2 = MonitoringLocationDTO.newBuilder()
            .setLocation("test-location2")
            .setTenantId(new UUID(10, 10).toString())
            .setId(2L)
            .build();
        MonitoringLocation location2 = mapper.dtoToModel(locationDTO2);
        doReturn(Arrays.asList(location, location2)).when(mockRepository).findAll();
        StreamRecorder<MonitoringLocationDTO> responseObserver = StreamRecorder.create();
        locationService.listLocations(Empty.newBuilder().build(), responseObserver);
        if(!responseObserver.awaitCompletion(3, TimeUnit.SECONDS)) {
            fail("The gRPC call did not complete in time");
        }
        assertNull(responseObserver.getError());
        List<MonitoringLocationDTO> results = responseObserver.getValues();
        assertEquals(2, results.size());
        verify(mockRepository).findAll();
    }

    @Test
    public void testFindLocationByName() throws Exception {
        MonitoringLocationDTO locationDTO = MonitoringLocationDTO.newBuilder()
            .setLocation("test-location")
            .setTenantId(new UUID(10, 10).toString())
            .setId(1L)
            .build();
        MonitoringLocation location = mapper.dtoToModel(locationDTO);
        doReturn(location).when(mockRepository).findByLocation(locationDTO.getLocation());
        StreamRecorder<MonitoringLocationDTO> responseObserver = StreamRecorder.create();
        locationService.getLocationByName(StringValue.of(locationDTO.getLocation()), responseObserver);
        if(!responseObserver.awaitCompletion(3, TimeUnit.SECONDS)) {
            fail("The gRPC call did not complete in time");
        }
        assertNull(responseObserver.getError());
        List<MonitoringLocationDTO> results = responseObserver.getValues();
        assertEquals(1, results.size());
        assertEquals(locationDTO, results.get(0));
        verify(mockRepository).findByLocation(locationDTO.getLocation());
    }

    @Test
    public void testFindLocationByNameNotExist() throws Exception {
        String locationName = "doesn't exist";
        doReturn(null).when(mockRepository).findByLocation(locationName);
        StreamRecorder<MonitoringLocationDTO> responseObserver = StreamRecorder.create();
        locationService.getLocationByName(StringValue.of(locationName), responseObserver);
        if(!responseObserver.awaitCompletion(3, TimeUnit.SECONDS)) {
            fail("The gRPC call did not complete in time");
        }
        assertNotNull(responseObserver.getError());
        StatusRuntimeException error = (StatusRuntimeException) responseObserver.getError();
        assertNotNull(error);
        assertEquals(Status.Code.NOT_FOUND, error.getStatus().getCode());
        verify(mockRepository).findByLocation(locationName);
    }
}
