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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.opennms.horizon.inventory.dto.MonitoringLocationDTO;
import org.opennms.horizon.inventory.mapper.MonitoringLocationMapper;
import org.opennms.horizon.inventory.model.MonitoringLocation;
import org.opennms.horizon.inventory.repository.MonitoringLocationRepository;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.internal.testing.StreamRecorder;

public class LocationGrpcServiceTest {
    private LocationGrpcService locationService;
    private MonitoringLocationRepository mockRepository;
    private final MonitoringLocationMapper mapper = Mappers.getMapper(MonitoringLocationMapper.class);
    @BeforeEach
    public void setUp(){
        mockRepository = mock(MonitoringLocationRepository.class);
        locationService = new LocationGrpcService(mockRepository);
    }

    @AfterEach
    public void veryMock(){
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    public void testCreateLocation() throws Exception {
        MonitoringLocationDTO locationDTO = MonitoringLocationDTO.newBuilder()
            .setLocation("test-location")
            .setTenantId(new UUID(10, 10).toString())
            .build();
        MonitoringLocation location = mapper.dtoToModel(locationDTO);
        location.setId(1L);
        doReturn(null).when(mockRepository).findByLocation(locationDTO.getLocation());
        doReturn(location).when(mockRepository).save(any(MonitoringLocation.class));
        StreamRecorder<MonitoringLocationDTO> responseObserver = StreamRecorder.create();
        locationService.createLocation(locationDTO, responseObserver);
        if(!responseObserver.awaitCompletion(3, TimeUnit.SECONDS)) {
            Assertions.fail("The gRPC call did not complete in time");
        }
        assertNull(responseObserver.getError());
        List<MonitoringLocationDTO> results = responseObserver.getValues();
        assertEquals(1, results.size());
        MonitoringLocationDTO savedLocation = results.get(0);
        assertEquals(mapper.modelToDTO(location), savedLocation);
        verify(mockRepository).findByLocation(locationDTO.getLocation());
        verify(mockRepository).save(any(MonitoringLocation.class));
    }

    @Test
    public void testCreateLocationExist() throws Exception {
        MonitoringLocationDTO locationDTO = MonitoringLocationDTO.newBuilder()
            .setLocation("test-location")
            .setTenantId(new UUID(10, 10).toString())
            .build();
        MonitoringLocation location = mapper.dtoToModel(locationDTO);
        location.setId(1L);
        doReturn(location).when(mockRepository).findByLocation(locationDTO.getLocation());
        StreamRecorder<MonitoringLocationDTO> responseObserver = StreamRecorder.create();
        locationService.createLocation(locationDTO, responseObserver);
        if(!responseObserver.awaitCompletion(3, TimeUnit.SECONDS)) {
            Assertions.fail("The gRPC call did not complete in time");
        }
        StatusRuntimeException error = (StatusRuntimeException) responseObserver.getError();
        assertNotNull(error);
        assertEquals(Status.Code.ALREADY_EXISTS, error.getStatus().getCode());
        verify(mockRepository).findByLocation(locationDTO.getLocation());
    }

    @Test
    public void testUpdateLocation() throws Exception {
        MonitoringLocationDTO locationDTO = MonitoringLocationDTO.newBuilder()
            .setLocation("test-location")
            .setTenantId(new UUID(10, 10).toString())
            .setId(1L)
            .build();
        MonitoringLocation location = mapper.dtoToModel(locationDTO);
        doReturn(Optional.of(location)).when(mockRepository).findById(locationDTO.getId());
        doReturn(location).when(mockRepository).save(any(MonitoringLocation.class));
        StreamRecorder<MonitoringLocationDTO> responseObserver = StreamRecorder.create();
        locationService.updateLocation(locationDTO, responseObserver);
        if(!responseObserver.awaitCompletion(3, TimeUnit.SECONDS)) {
            Assertions.fail("The gRPC call did not complete in time");
        }
        assertNull(responseObserver.getError());
        List<MonitoringLocationDTO> results = responseObserver.getValues();
        assertEquals(1, results.size());
        MonitoringLocationDTO savedLocation = results.get(0);
        assertEquals(locationDTO, savedLocation);
        verify(mockRepository).findById(locationDTO.getId());
        verify(mockRepository).save(any(MonitoringLocation.class));
    }

    @Test
    public void testUpdateLocationNotExist() throws Exception {
        MonitoringLocationDTO locationDTO = MonitoringLocationDTO.newBuilder()
            .setLocation("test-location")
            .setTenantId(new UUID(10, 10).toString())
            .setId(1L)
            .build();
        doReturn(Optional.empty()).when(mockRepository).findById(locationDTO.getId());
        StreamRecorder<MonitoringLocationDTO> responseObserver = StreamRecorder.create();
        locationService.updateLocation(locationDTO, responseObserver);
        if(!responseObserver.awaitCompletion(3, TimeUnit.SECONDS)) {
            Assertions.fail("The gRPC call did not complete in time");
        }
        StatusRuntimeException error = (StatusRuntimeException) responseObserver.getError();
        assertNotNull(error);
        assertEquals(Status.Code.NOT_FOUND, error.getStatus().getCode());
        verify(mockRepository).findById(locationDTO.getId());
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
            .setId(2l)
            .build();
        MonitoringLocation location2 = mapper.dtoToModel(locationDTO);
        doReturn(Arrays.asList(location, location2)).when(mockRepository).findAll();
        StreamRecorder<MonitoringLocationDTO> responseObserver = StreamRecorder.create();
        locationService.listLocations(Empty.newBuilder().build(), responseObserver);
        if(!responseObserver.awaitCompletion(3, TimeUnit.SECONDS)) {
            Assertions.fail("The gRPC call did not complete in time");
        }
        assertNull(responseObserver.getError());
        List<MonitoringLocationDTO> results = responseObserver.getValues();
        assertEquals(2, results.size());
        verify(mockRepository).findAll();
    }

    @Test
    public void testFindLocationById() throws Exception {
        MonitoringLocationDTO locationDTO = MonitoringLocationDTO.newBuilder()
            .setLocation("test-location")
            .setTenantId(new UUID(10, 10).toString())
            .setId(1L)
            .build();
        MonitoringLocation location = mapper.dtoToModel(locationDTO);
        doReturn(Optional.of(location)).when(mockRepository).findById(locationDTO.getId());
        StreamRecorder<MonitoringLocationDTO> responseObserver = StreamRecorder.create();
        locationService.getLocationById(Int64Value.of(locationDTO.getId()), responseObserver);
        if(!responseObserver.awaitCompletion(3, TimeUnit.SECONDS)) {
            Assertions.fail("The gRPC call did not complete in time");
        }
        assertNull(responseObserver.getError());
        List<MonitoringLocationDTO> results = responseObserver.getValues();
        assertEquals(1, results.size());
        assertEquals(locationDTO, results.get(0));
        verify(mockRepository).findById(locationDTO.getId());
    }

    @Test
    public void testFindLocationByIdNotExist() throws Exception {
        long id = 1L;
        doReturn(Optional.empty()).when(mockRepository).findById(id);
        StreamRecorder<MonitoringLocationDTO> responseObserver = StreamRecorder.create();
        locationService.getLocationById(Int64Value.of(id), responseObserver);
        if(!responseObserver.awaitCompletion(3, TimeUnit.SECONDS)) {
            Assertions.fail("The gRPC call did not complete in time");
        }
        assertNotNull(responseObserver.getError());
        StatusRuntimeException error = (StatusRuntimeException) responseObserver.getError();
        assertNotNull(error);
        assertEquals(Status.Code.NOT_FOUND, error.getStatus().getCode());
        verify(mockRepository).findById(id);
    }

    @Test
    public void testDeleteLocation() throws Exception {
        long id = 1L;
        MonitoringLocation location = new MonitoringLocation();
        doReturn(Optional.of(location)).when(mockRepository).findById(id);
        doNothing().when(mockRepository).deleteById(id);
        StreamRecorder<BoolValue> responseObserver = StreamRecorder.create();
        locationService.deleteLocation(Int64Value.of(id), responseObserver);
        if(!responseObserver.awaitCompletion(3, TimeUnit.SECONDS)) {
            Assertions.fail("The gRPC call did not complete in time");
        }
        assertNull(responseObserver.getError());
        List<BoolValue> results = responseObserver.getValues();
        assertEquals(1, results.size());
        assertTrue(results.get(0).getValue());
        verify(mockRepository).findById(id);
        verify(mockRepository).deleteById(id);
    }

    @Test
    public void testDeleteLocationNotExist() throws Exception {
        long id = 1L;
        doReturn(Optional.empty()).when(mockRepository).findById(id);
        StreamRecorder<BoolValue> responseObserver = StreamRecorder.create();
        locationService.deleteLocation(Int64Value.of(id), responseObserver);
        if(!responseObserver.awaitCompletion(3, TimeUnit.SECONDS)) {
            Assertions.fail("The gRPC call did not complete in time");
        }
        assertNotNull(responseObserver.getError());
        StatusRuntimeException error = (StatusRuntimeException) responseObserver.getError();
        assertNotNull(error);
        assertEquals(Status.Code.NOT_FOUND, error.getStatus().getCode());
        verify(mockRepository).findById(id);

    }
}
