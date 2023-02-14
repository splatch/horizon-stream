/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2023 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2023 The OpenNMS Group, Inc.
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

package org.opennms.horizon.inventory.grpc;

import org.opennms.horizon.inventory.discovery.ConfigResults;
import org.opennms.horizon.inventory.discovery.DiscoveryConfigDTO;
import org.opennms.horizon.inventory.discovery.DiscoveryConfigList;
import org.opennms.horizon.inventory.discovery.DiscoveryConfigOperationGrpc;
import org.opennms.horizon.inventory.discovery.DiscoveryConfigRequest;
import org.opennms.horizon.inventory.discovery.SNMPConfigDTO;
import org.opennms.horizon.inventory.discovery.SNMPConfigList;
import org.opennms.horizon.inventory.exception.InventoryRuntimeException;
import org.opennms.horizon.inventory.service.DiscoveryConfigService;
import org.springframework.stereotype.Component;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.google.rpc.Code;
import com.google.rpc.Status;

import io.grpc.Context;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscoveryConfigGrpcService extends DiscoveryConfigOperationGrpc.DiscoveryConfigOperationImplBase {
    private final TenantLookup tenantLookup;
    private final DiscoveryConfigService configService;

    @Override
    public void createConfig(DiscoveryConfigRequest request, StreamObserver<ConfigResults> responseObserver) {
        tenantLookup.lookupTenantId(Context.current())
            .ifPresentOrElse(tenantId -> {
                try {
                    ConfigResults results = configService.createConfigs(request, tenantId);
                    responseObserver.onNext(results);
                    responseObserver.onCompleted();
                } catch (Exception e) {
                    responseObserver.onError(StatusProto.toStatusRuntimeException(createStatus(Code.INVALID_ARGUMENT_VALUE, "Invalid request " + request)));
                }
            }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(createStatus(Code.INVALID_ARGUMENT_VALUE, "Missing tenantId"))));
    }

    @Override
    public void listDiscoveryConfig(Empty request, StreamObserver<DiscoveryConfigList> responseObserver) {
        tenantLookup.lookupTenantId(Context.current())
            .ifPresentOrElse(tenantId -> {
                DiscoveryConfigList list = DiscoveryConfigList.newBuilder()
                        .addAllDiscoverConfigs(configService.listDiscoveryConfigs(tenantId)).build();
                responseObserver.onNext(list);
                responseObserver.onCompleted();
            }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(createStatus(Code.INVALID_ARGUMENT_VALUE, "Missing tenantId"))));
    }

    @Override
    public void listSnmpConfig(Empty request, StreamObserver<SNMPConfigList> responseObserver) {
        tenantLookup.lookupTenantId(Context.current())
            .ifPresentOrElse(tenantId -> {
                SNMPConfigList list = SNMPConfigList.newBuilder()
                    .addAllSnmpConfigs(configService.listSnmpConfigs(tenantId)).build();
                responseObserver.onNext(list);
                responseObserver.onCompleted();
            }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(createStatus(Code.INVALID_ARGUMENT_VALUE, "Missing tenantId"))));
    }

    @Override
    public void listDiscoveryConfigByLocation(StringValue request, StreamObserver<DiscoveryConfigList> responseObserver) {
        tenantLookup.lookupTenantId(Context.current())
            .ifPresentOrElse(tenantId -> {
                DiscoveryConfigList list = DiscoveryConfigList.newBuilder()
                    .addAllDiscoverConfigs(configService.listDiscoveryConfigByLocation(tenantId, request.getValue())).build();
                responseObserver.onNext(list);
                responseObserver.onCompleted();
            }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(createStatus(Code.INVALID_ARGUMENT_VALUE, "Missing tenantId"))));
    }

    @Override
    public void listSnmpConfigByLocation(StringValue request, StreamObserver<SNMPConfigList> responseObserver) {
        tenantLookup.lookupTenantId(Context.current())
            .ifPresentOrElse(tenantId -> {
                SNMPConfigList list = SNMPConfigList.newBuilder()
                    .addAllSnmpConfigs(configService.listSNMPConfigByLocation(tenantId, request.getValue())).build();
                responseObserver.onNext(list);
                responseObserver.onCompleted();
            }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(createStatus(Code.INVALID_ARGUMENT_VALUE, "Missing tenantId"))));
    }

    @Override
    public void getDiscoveryConfigByName(StringValue request, StreamObserver<DiscoveryConfigDTO> responseObserver) {
        tenantLookup.lookupTenantId(Context.current())
            .ifPresentOrElse(tenantId -> {
                try{
                    configService.getDiscoveryConfigByName(request.getValue(), tenantId).ifPresentOrElse(config -> {
                            responseObserver.onNext(config);
                            responseObserver.onCompleted();
                        }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(createStatus(Code.NOT_FOUND_VALUE, String.format("Discovery config with name: %s doesn't exist", request.getValue())))));
                } catch (InventoryRuntimeException e) {
                    responseObserver.onError(StatusProto.toStatusRuntimeException(createStatus(Code.INTERNAL_VALUE, "Error while get discovery config with name " + request.getValue())));
                }
            }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(createStatus(Code.INVALID_ARGUMENT_VALUE, "Missing tenantId"))));
    }

    @Override
    public void getSnmpConfigByName(StringValue request, StreamObserver<SNMPConfigDTO> responseObserver) {
        tenantLookup.lookupTenantId(Context.current())
            .ifPresentOrElse(tenantId -> {
                try{
                    configService.getSnmpConfigByName(request.getValue(), tenantId).ifPresentOrElse(config -> {
                        responseObserver.onNext(config);
                        responseObserver.onCompleted();
                    }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(createStatus(Code.NOT_FOUND_VALUE, String.format("SNMP config with name: %s doesn't exist", request.getValue())))));

                } catch (InventoryRuntimeException e) {
                    responseObserver.onError(StatusProto.toStatusRuntimeException(createStatus(Code.INTERNAL_VALUE, "Error while get discovery config with name " + request.getValue())));
                }
            }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(createStatus(Code.INVALID_ARGUMENT_VALUE, "Missing tenantId"))));
    }

    private Status createStatus(int code, String message) {
        return Status.newBuilder().setCode(code).setMessage(message).build();
    }
}
