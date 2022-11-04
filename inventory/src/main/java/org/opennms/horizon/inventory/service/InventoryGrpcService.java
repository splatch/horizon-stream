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

import org.mapstruct.factory.Mappers;
import org.opennms.horizon.inventory.dto.InventoryServiceGrpc;
import org.opennms.horizon.inventory.dto.MonitoringLocationDTO;
import org.opennms.horizon.inventory.mapper.MonitoringLocationMapper;
import org.opennms.horizon.inventory.model.MonitoringLocation;
import org.opennms.horizon.inventory.repository.MonitoringLocationRepository;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.google.rpc.Code;
import com.google.rpc.Status;

import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InventoryGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase {
    private final MonitoringLocationRepository locationRepo;
    private final MonitoringLocationMapper mapper = Mappers.getMapper(MonitoringLocationMapper.class);

    public InventoryGrpcService(MonitoringLocationRepository locationRepo) {
        this.locationRepo = locationRepo;
    }

    @Override
    public void listLocations(Empty request, StreamObserver<MonitoringLocationDTO> responseObserver) {
        locationRepo.findAll().forEach(l -> responseObserver.onNext(mapper.modelToDTO(l)));
        responseObserver.onCompleted();
    }

    @Override
    public void getLocationByName(StringValue request, StreamObserver<MonitoringLocationDTO> responseObserver) {
        MonitoringLocation location = locationRepo.findByLocation(request.getValue());
        if(location !=null ){
            responseObserver.onNext(mapper.modelToDTO(location));
        } else {
            Status status = Status.newBuilder()
                .setCode(Code.NOT_FOUND_VALUE)
                .setMessage("Location with name: " + request.getValue() + " doesn't exist")
                .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        }
        responseObserver.onCompleted();
    }
}
