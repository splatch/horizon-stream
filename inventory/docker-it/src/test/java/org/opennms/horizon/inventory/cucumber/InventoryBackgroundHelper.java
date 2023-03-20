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

package org.opennms.horizon.inventory.cucumber;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.MetadataUtils;
import lombok.Getter;
import org.opennms.horizon.inventory.discovery.IcmpActiveDiscoveryServiceGrpc;
import org.opennms.horizon.inventory.dto.ActiveDiscoveryServiceGrpc;
import org.opennms.horizon.inventory.dto.AzureActiveDiscoveryServiceGrpc;
import org.opennms.horizon.inventory.dto.MonitoringLocationServiceGrpc;
import org.opennms.horizon.inventory.dto.MonitoringSystemServiceGrpc;
import org.opennms.horizon.inventory.dto.NodeServiceGrpc;
import org.opennms.horizon.inventory.dto.PassiveDiscoveryServiceGrpc;
import org.opennms.horizon.inventory.dto.TagServiceGrpc;
import org.opennms.horizon.shared.constants.GrpcConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@Getter
public class InventoryBackgroundHelper {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryBackgroundHelper.class);
    private static final int DEADLINE_DURATION = 30;
    private static final String LOCALHOST = "localhost";
    private Integer externalGrpcPort;
    private String kafkaBootstrapUrl;
    private String tenantId;
    private MonitoringSystemServiceGrpc.MonitoringSystemServiceBlockingStub monitoringSystemStub;
    private MonitoringLocationServiceGrpc.MonitoringLocationServiceBlockingStub monitoringLocationStub;
    private NodeServiceGrpc.NodeServiceBlockingStub nodeServiceBlockingStub;
    private TagServiceGrpc.TagServiceBlockingStub tagServiceBlockingStub;
    private ActiveDiscoveryServiceGrpc.ActiveDiscoveryServiceBlockingStub activeDiscoveryServiceBlockingStub;
    private IcmpActiveDiscoveryServiceGrpc.IcmpActiveDiscoveryServiceBlockingStub icmpActiveDiscoveryServiceBlockingStub;
    private AzureActiveDiscoveryServiceGrpc.AzureActiveDiscoveryServiceBlockingStub azureActiveDiscoveryServiceBlockingStub;
    private PassiveDiscoveryServiceGrpc.PassiveDiscoveryServiceBlockingStub passiveDiscoveryServiceBlockingStub;

    private final Map<String, String> grpcHeaders = new TreeMap<>();

    public void externalGRPCPortInSystemProperty(String propertyName) {
        String value = System.getProperty(propertyName);
        externalGrpcPort = Integer.parseInt(value);
        LOG.info("Using External gRPC port {}", externalGrpcPort);
    }

    public void kafkaBootstrapURLInSystemProperty(String systemPropertyName) {
        kafkaBootstrapUrl = System.getProperty(systemPropertyName);
        LOG.info("Using Kafka Bootstrap URL {}", kafkaBootstrapUrl);
    }

    public void grpcTenantId(String tenantId) {
        Objects.requireNonNull(tenantId);
        this.tenantId = tenantId;
        grpcHeaders.put(GrpcConstants.TENANT_ID_KEY, tenantId);
        LOG.info("Using Tenant Id {}", tenantId);
    }

    public void createGrpcConnectionForInventory() {
        NettyChannelBuilder channelBuilder =
            NettyChannelBuilder.forAddress(LOCALHOST, externalGrpcPort);

        ManagedChannel managedChannel = channelBuilder.usePlaintext().build();
        managedChannel.getState(true);
        monitoringSystemStub = MonitoringSystemServiceGrpc.newBlockingStub(managedChannel)
            .withInterceptors(prepareGrpcHeaderInterceptor()).withDeadlineAfter(DEADLINE_DURATION, TimeUnit.SECONDS);
        monitoringLocationStub = MonitoringLocationServiceGrpc.newBlockingStub(managedChannel)
            .withInterceptors(prepareGrpcHeaderInterceptor()).withDeadlineAfter(DEADLINE_DURATION, TimeUnit.SECONDS);
        nodeServiceBlockingStub = NodeServiceGrpc.newBlockingStub(managedChannel)
            .withInterceptors(prepareGrpcHeaderInterceptor()).withDeadlineAfter(DEADLINE_DURATION, TimeUnit.SECONDS);
        nodeServiceBlockingStub = NodeServiceGrpc.newBlockingStub(managedChannel)
            .withInterceptors(prepareGrpcHeaderInterceptor()).withDeadlineAfter(DEADLINE_DURATION, TimeUnit.SECONDS);
        tagServiceBlockingStub = TagServiceGrpc.newBlockingStub(managedChannel)
            .withInterceptors(prepareGrpcHeaderInterceptor()).withDeadlineAfter(DEADLINE_DURATION, TimeUnit.SECONDS);
        activeDiscoveryServiceBlockingStub = ActiveDiscoveryServiceGrpc.newBlockingStub(managedChannel)
            .withInterceptors(prepareGrpcHeaderInterceptor()).withDeadlineAfter(DEADLINE_DURATION, TimeUnit.SECONDS);
        icmpActiveDiscoveryServiceBlockingStub = IcmpActiveDiscoveryServiceGrpc.newBlockingStub(managedChannel)
            .withInterceptors(prepareGrpcHeaderInterceptor()).withDeadlineAfter(DEADLINE_DURATION, TimeUnit.SECONDS);
        azureActiveDiscoveryServiceBlockingStub = AzureActiveDiscoveryServiceGrpc.newBlockingStub(managedChannel)
            .withInterceptors(prepareGrpcHeaderInterceptor()).withDeadlineAfter(DEADLINE_DURATION, TimeUnit.SECONDS);
        passiveDiscoveryServiceBlockingStub = PassiveDiscoveryServiceGrpc.newBlockingStub(managedChannel)
            .withInterceptors(prepareGrpcHeaderInterceptor()).withDeadlineAfter(DEADLINE_DURATION, TimeUnit.SECONDS);
    }

    private ClientInterceptor prepareGrpcHeaderInterceptor() {
        return MetadataUtils.newAttachHeadersInterceptor(prepareGrpcHeaders());
    }

    private Metadata prepareGrpcHeaders() {
        Metadata result = new Metadata();
        result.put(GrpcConstants.AUTHORIZATION_BYPASS_KEY, String.valueOf(true));
        result.put(GrpcConstants.TENANT_ID_BYPASS_KEY, tenantId);
        return result;
    }
}
