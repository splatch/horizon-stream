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

import java.util.Optional;

import org.mapstruct.factory.Mappers;
import org.opennms.horizon.inventory.dto.LocationServiceGrpc;
import org.opennms.horizon.inventory.dto.MonitoringLocationDTO;
import org.opennms.horizon.inventory.mapper.MonitoringLocationMapper;
import org.opennms.horizon.inventory.model.MonitoringLocation;
import org.opennms.horizon.inventory.repository.MonitoringLocationRepository;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import com.google.rpc.Code;
import com.google.rpc.Status;

import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
public class LocationGrpcService extends LocationServiceGrpc.LocationServiceImplBase {
    private final MonitoringLocationRepository locationRepo;
    private final MonitoringLocationMapper mapper = Mappers.getMapper(MonitoringLocationMapper.class);

    public LocationGrpcService(MonitoringLocationRepository locationRepo) {
        this.locationRepo = locationRepo;
    }

    @Override
    public void createLocation(MonitoringLocationDTO request, StreamObserver<MonitoringLocationDTO> responseObserver) {
        MonitoringLocation location = locationRepo.findByLocation(request.getLocation());
        if(location != null) {
            Status status = Status.newBuilder()
                .setCode(Code.ALREADY_EXISTS.getNumber())
                .setMessage("The location already exist")
                .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        } else {
            //if id exist it will update the location
            responseObserver.onNext(mapper.modelToDTO(locationRepo.save(mapper.dtoToModel(request))));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void updateLocation(MonitoringLocationDTO request, StreamObserver<MonitoringLocationDTO> responseObserver) {
        Optional<MonitoringLocation> locationOp = locationRepo.findById(request.getId());
        if(locationOp.isPresent()) {
            responseObserver.onNext(mapper.modelToDTO(locationRepo.save(mapper.dtoToModel(request))));
        } else {
            Status status = Status.newBuilder()
                .setCode(Code.NOT_FOUND_VALUE)
                .setMessage("Location with id " + request.getId() + " doesn't exist")
                .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void listLocations(Empty request, StreamObserver<MonitoringLocationDTO> responseObserver) {
        locationRepo.findAll().forEach(l -> responseObserver.onNext(mapper.modelToDTO(l)));
        responseObserver.onCompleted();
    }

    @Override
    public void getLocationById(Int64Value request, StreamObserver<MonitoringLocationDTO> responseObserver) {
        Optional<MonitoringLocation> locationOp = locationRepo.findById(request.getValue());
        if(locationOp.isPresent()) {
            responseObserver.onNext(mapper.modelToDTO(locationOp.get()));
        } else {
            Status status = Status.newBuilder()
                .setCode(Code.NOT_FOUND_VALUE)
                .setMessage("Location with ID: " + request.getValue() + " doesn't exist").build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void deleteLocation(Int64Value request, StreamObserver<BoolValue> responseObserver) {
        Optional<MonitoringLocation> locationOp = locationRepo.findById(request.getValue());
        if(locationOp.isPresent()) {
            locationRepo.deleteById(request.getValue());
            responseObserver.onNext(BoolValue.of(true));
        } else {
            Status status = Status.newBuilder()
                .setCode(Code.NOT_FOUND_VALUE)
                .setMessage("Location with ID: " + request.getValue() + " doesn't exist")
                .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        }
        responseObserver.onCompleted();
    }
}
