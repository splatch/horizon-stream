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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.common.VerificationException;
import org.opennms.horizon.inventory.SpringContextTestInitializer;
import org.opennms.horizon.inventory.discovery.ConfigResults;
import org.opennms.horizon.inventory.discovery.DiscoveryConfigDTO;
import org.opennms.horizon.inventory.discovery.DiscoveryConfigList;
import org.opennms.horizon.inventory.discovery.DiscoveryConfigOperationGrpc;
import org.opennms.horizon.inventory.discovery.DiscoveryConfigRequest;
import org.opennms.horizon.inventory.discovery.SNMPConfigDTO;
import org.opennms.horizon.inventory.discovery.SNMPConfigList;
import org.opennms.horizon.inventory.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.google.rpc.Code;
import com.google.rpc.Status;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.MetadataUtils;

@SpringBootTest
@ContextConfiguration(initializers = {SpringContextTestInitializer.class})
public class DiscoveryConfigGrpcItTest extends GrpcTestBase {
    @Autowired
    private ConfigurationRepository configRepo;
    private DiscoveryConfigOperationGrpc.DiscoveryConfigOperationBlockingStub serviceStub;

    @BeforeEach
    public void prepare() throws VerificationException {
        prepareServer();
        serviceStub = DiscoveryConfigOperationGrpc.newBlockingStub(channel);
    }

    @AfterEach
    public void cleanUp() throws InterruptedException {
        configRepo.deleteAll();
        afterTest();
    }

    @Test
    void testCreateConfig() throws VerificationException {
        DiscoveryConfigRequest request = DiscoveryConfigRequest.newBuilder()
            .setConfigName("test-config")
            .setLocation("test-location")
            .setReadComStr("test-community")
            .setIpAddresses("127.0.0.1-127.0.0.10").build();
        ConfigResults results = serviceStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createAuthHeader(authHeader)))
            .createConfig(request);
        assertThat(results).isNotNull()
            .extracting(ConfigResults::getDiscoveryConfig).isNotNull()
            .extracting(DiscoveryConfigDTO::getConfigName, DiscoveryConfigDTO::getIpAddresses)
            .containsExactly(request.getConfigName(), request.getIpAddresses());
        assertThat(results).extracting(ConfigResults::getSnmpConfig).isNotNull()
                .extracting(SNMPConfigDTO::getConfigName, SNMPConfigDTO::getFirstIP, SNMPConfigDTO::getLastIP, c -> c.getParameters().getReadCmString())
                .containsExactly(request.getConfigName(), "127.0.0.1", "127.0.0.10", request.getReadComStr());
        verify(spyInterceptor).verifyAccessToken(authHeader);
        verify(spyInterceptor).interceptCall(any(ServerCall.class), any(Metadata.class), any(ServerCallHandler.class));
    }

    @Test
    void testGetConfigByName() throws VerificationException {
        DiscoveryConfigRequest request = DiscoveryConfigRequest.newBuilder()
            .setConfigName("test-config")
            .setLocation("test-location")
            .setReadComStr("test-community")
            .setIpAddresses("127.0.0.1-127.0.0.10").build();
        serviceStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createAuthHeader(authHeader)))
            .createConfig(request);
        DiscoveryConfigDTO discoveryConfig = serviceStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createAuthHeader(authHeader)))
                .getDiscoveryConfigByName(StringValue.of(request.getConfigName()));

        assertThat(discoveryConfig).isNotNull()
            .extracting(DiscoveryConfigDTO::getConfigName, DiscoveryConfigDTO::getIpAddresses)
            .containsExactly(request.getConfigName(), request.getIpAddresses());

        SNMPConfigDTO snmpConfig = serviceStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createAuthHeader(authHeader)))
            .getSnmpConfigByName(StringValue.of(request.getConfigName()));
        assertThat(snmpConfig).isNotNull()
            .extracting(SNMPConfigDTO::getConfigName, SNMPConfigDTO::getFirstIP, SNMPConfigDTO::getLastIP, c -> c.getParameters().getReadCmString())
            .containsExactly(request.getConfigName(), "127.0.0.1", "127.0.0.10", request.getReadComStr());
        verify(spyInterceptor, times(3)).verifyAccessToken(authHeader);
        verify(spyInterceptor, times(3)).interceptCall(any(ServerCall.class), any(Metadata.class), any(ServerCallHandler.class));
    }

    @Test
    void testGetConfigByNameNotExist() throws VerificationException {
        String badName = "invalidName";
        DiscoveryConfigRequest request = DiscoveryConfigRequest.newBuilder()
            .setConfigName("test-config")
            .setLocation("test-location")
            .setReadComStr("test-community")
            .setIpAddresses("127.0.0.1-127.0.0.10").build();
        serviceStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createAuthHeader(authHeader)))
            .createConfig(request);

        assertThatThrownBy(() ->serviceStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createAuthHeader(authHeader)))
            .getDiscoveryConfigByName(StringValue.of(badName)))
            .isInstanceOf(StatusRuntimeException.class)
            .extracting(StatusProto::fromThrowable)
            .extracting(Status::getCode).isEqualTo(Code.NOT_FOUND_VALUE);

        assertThatThrownBy(() ->serviceStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createAuthHeader(authHeader)))
            .getSnmpConfigByName(StringValue.of(badName)))
            .isInstanceOf(StatusRuntimeException.class)
            .extracting(StatusProto::fromThrowable)
            .extracting(Status::getCode).isEqualTo(Code.NOT_FOUND_VALUE);
        verify(spyInterceptor, times(3)).verifyAccessToken(authHeader);
        verify(spyInterceptor, times(3)).interceptCall(any(ServerCall.class), any(Metadata.class), any(ServerCallHandler.class));
    }

    @Test
    void testListConfig() throws VerificationException {
        DiscoveryConfigRequest request = DiscoveryConfigRequest.newBuilder()
            .setConfigName("test-config")
            .setLocation("test-location")
            .setReadComStr("test-community")
            .setIpAddresses("127.0.0.1-127.0.0.10").build();
        DiscoveryConfigRequest request2 = DiscoveryConfigRequest.newBuilder()
            .setConfigName("test-config-2")
            .setLocation("test-location-2")
            .setReadComStr("test-community")
            .setIpAddresses("127.0.0.15-127.0.0.20").build();
        serviceStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createAuthHeader(authHeader)))
            .createConfig(request);
        serviceStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createAuthHeader(authHeader)))
            .createConfig(request2);

        DiscoveryConfigList discoveryList = serviceStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createAuthHeader(authHeader)))
            .listDiscoveryConfig(Empty.getDefaultInstance());

        assertThat(discoveryList).isNotNull()
            .extracting(DiscoveryConfigList::getDiscoverConfigsList).asList().hasSize(2)
            .extracting("configName")
            .contains(request.getConfigName(), request2.getConfigName());

        SNMPConfigList snmpList = serviceStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createAuthHeader(authHeader)))
            .listSnmpConfig(Empty.getDefaultInstance());

        assertThat(snmpList).isNotNull()
            .extracting(SNMPConfigList::getSnmpConfigsList).asList().hasSize(2)
            .extracting("configName")
            .contains(request.getConfigName(), request2.getConfigName());

        verify(spyInterceptor, times(4)).verifyAccessToken(authHeader);
        verify(spyInterceptor, times(4)).interceptCall(any(ServerCall.class), any(Metadata.class), any(ServerCallHandler.class));
    }

    @Test
    void testListConfigByLocation() throws VerificationException {
        DiscoveryConfigRequest request = DiscoveryConfigRequest.newBuilder()
            .setConfigName("test-config")
            .setLocation("test-location")
            .setReadComStr("test-community")
            .setIpAddresses("127.0.0.1-127.0.0.10").build();
        DiscoveryConfigRequest request2 = DiscoveryConfigRequest.newBuilder()
            .setConfigName("test-config-2")
            .setLocation("test-location-2")
            .setReadComStr("test-community")
            .setIpAddresses("127.0.0.15-127.0.0.20").build();
        serviceStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createAuthHeader(authHeader)))
            .createConfig(request);
        serviceStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createAuthHeader(authHeader)))
            .createConfig(request2);

        DiscoveryConfigList discoveryList = serviceStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createAuthHeader(authHeader)))
            .listDiscoveryConfigByLocation(StringValue.of(request.getLocation()));

        assertThat(discoveryList).isNotNull()
            .extracting(DiscoveryConfigList::getDiscoverConfigsList).asList().hasSize(1)
            .extracting("configName")
            .containsExactly(request.getConfigName());

        SNMPConfigList snmpList = serviceStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createAuthHeader(authHeader)))
            .listSnmpConfigByLocation(StringValue.of(request.getLocation()));

        assertThat(snmpList).isNotNull()
            .extracting(SNMPConfigList::getSnmpConfigsList).asList().hasSize(1)
            .extracting("configName")
            .containsExactly(request.getConfigName());

        verify(spyInterceptor, times(4)).verifyAccessToken(authHeader);
        verify(spyInterceptor, times(4)).interceptCall(any(ServerCall.class), any(Metadata.class), any(ServerCallHandler.class));
    }
}
