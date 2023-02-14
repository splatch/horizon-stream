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

package org.opennms.horizon.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opennms.horizon.inventory.discovery.ConfigResults;
import org.opennms.horizon.inventory.discovery.DiscoveryConfigDTO;
import org.opennms.horizon.inventory.discovery.DiscoveryConfigRequest;
import org.opennms.horizon.inventory.discovery.SNMPConfigDTO;
import org.opennms.horizon.inventory.discovery.SNMPParameters;
import org.opennms.horizon.inventory.dto.ConfigurationDTO;
import org.opennms.horizon.inventory.exception.InventoryRuntimeException;
import org.opennms.horizon.inventory.model.Configuration;
import org.opennms.horizon.shared.protobuf.util.ProtobufUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;

@ExtendWith(MockitoExtension.class)
public class DiscoveryConfigServiceTest {
    @Mock
    private ConfigurationService mockConfigurationService;
    @InjectMocks
    private DiscoveryConfigService configService;
    private final String configName = "testConfig";
    private final String tenantId = "testTenant";
    private final String location = "testLocation";
    private DiscoveryConfigDTO.Builder discoveryBuilder;
    private SNMPConfigDTO.Builder snmpConfigBuilder;
    private Configuration discoveryConfig;
    private Configuration snmpConfig;
    private ArgumentCaptor<ConfigurationDTO> argCaptor;

    @BeforeEach
    void prepare() throws InvalidProtocolBufferException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        discoveryBuilder = configService.getDefaultDiscoveryConfigBuilder();
        snmpConfigBuilder = configService.getDefaultSNMPConfigBuilder();
        discoveryConfig = new Configuration();
        discoveryConfig.setValue(objectMapper.readTree(ProtobufUtil.toJson(discoveryBuilder.build())));
        snmpConfig = new Configuration();
        snmpConfig.setValue(objectMapper.readTree(ProtobufUtil.toJson(snmpConfigBuilder.build())));
        argCaptor = ArgumentCaptor.forClass(ConfigurationDTO.class);
    }

    @AfterEach
    void afterTest() {
        verifyNoMoreInteractions(mockConfigurationService);
    }

    @Test
    void testCreateConfigSingleIp() throws InvalidProtocolBufferException {
        doReturn(discoveryConfig).doReturn(snmpConfig).when(mockConfigurationService).createSingle(any(ConfigurationDTO.class));
        DiscoveryConfigRequest request = DiscoveryConfigRequest.newBuilder()
            .setConfigName(configName)
            .setLocation(location)
            .setReadComStr("test-community")
            .setIpAddresses("127.0.0.1").build();
        ConfigResults results = configService.createConfigs(request, tenantId);

        assertThat(results).isNotNull()
            .extracting(ConfigResults::getDiscoveryConfig, ConfigResults::getSnmpConfig)
            .containsExactly(discoveryBuilder.build(), snmpConfigBuilder.build());
        verify(mockConfigurationService, times(2)).createSingle(argCaptor.capture());

        List<ConfigurationDTO> args = argCaptor.getAllValues();
        assertThat(args).hasSize(2)
            .first()
            .extracting(ConfigurationDTO::getKey, ConfigurationDTO::getLocation, ConfigurationDTO::getTenantId)
            .containsExactly(DiscoveryConfigService.CONFIG_PREFIX_DISCOVERY + configName, location, tenantId);
        assertThat(args)
            .last()
            .extracting(ConfigurationDTO::getKey, ConfigurationDTO::getLocation, ConfigurationDTO::getTenantId)
            .containsExactly(DiscoveryConfigService.CONFIG_PREFIX_SNMP + configName, location, tenantId);

        DiscoveryConfigDTO resultDiscoveryConfig = ProtobufUtil.fromJson(args.get(0).getValue(), DiscoveryConfigDTO.class);
        DiscoveryConfigDTO expectedDiscovery = discoveryBuilder.setConfigName(configName).setIpAddresses(request.getIpAddresses()).build();
        assertThat(resultDiscoveryConfig).isNotNull()
            .isEqualTo(expectedDiscovery);

        SNMPConfigDTO resultSnmpConfig = ProtobufUtil.fromJson(args.get(1).getValue(), SNMPConfigDTO.class);
        SNMPConfigDTO expectedSnmpConfig = snmpConfigBuilder.setConfigName(configName).setFirstIP(request.getIpAddresses())
            .setParameters(SNMPParameters.newBuilder().setReadCmString(request.getReadComStr()).build()).build();
        assertThat(resultSnmpConfig).isNotNull()
            .isEqualTo(expectedSnmpConfig);
    }

    @Test
    void testCreateConfigIpRangeAndDefaultCommunityStr() throws InvalidProtocolBufferException {
        doReturn(discoveryConfig).doReturn(snmpConfig).when(mockConfigurationService).createSingle(any(ConfigurationDTO.class));
        DiscoveryConfigRequest request = DiscoveryConfigRequest.newBuilder()
            .setConfigName(configName)
            .setLocation(location)
            .setIpAddresses("127.0.0.1-127.0.0.55").build();
        ConfigResults results = configService.createConfigs(request, tenantId);
        assertThat(results).isNotNull()
            .extracting(ConfigResults::getDiscoveryConfig, ConfigResults::getSnmpConfig)
            .containsExactly(discoveryBuilder.build(), snmpConfigBuilder.build());
        verify(mockConfigurationService, times(2)).createSingle(argCaptor.capture());
        List<ConfigurationDTO> args = argCaptor.getAllValues();
        assertThat(args).hasSize(2)
            .first()
            .extracting(ConfigurationDTO::getKey, ConfigurationDTO::getLocation, ConfigurationDTO::getTenantId)
            .containsExactly(DiscoveryConfigService.CONFIG_PREFIX_DISCOVERY + configName, location, tenantId);
        assertThat(args)
            .last()
            .extracting(ConfigurationDTO::getKey, ConfigurationDTO::getLocation, ConfigurationDTO::getTenantId)
            .containsExactly(DiscoveryConfigService.CONFIG_PREFIX_SNMP + configName, location, tenantId);

        DiscoveryConfigDTO resultDiscoveryConfig = ProtobufUtil.fromJson(args.get(0).getValue(), DiscoveryConfigDTO.class);
        DiscoveryConfigDTO expectedDiscovery = discoveryBuilder.setConfigName(configName).setIpAddresses(request.getIpAddresses()).build();
        assertThat(resultDiscoveryConfig).isNotNull()
            .isEqualTo(expectedDiscovery);

        SNMPConfigDTO resultSnmpConfig = ProtobufUtil.fromJson(args.get(1).getValue(), SNMPConfigDTO.class);
        SNMPConfigDTO expectedSnmpConfig = snmpConfigBuilder.setConfigName(configName).setFirstIP("127.0.0.1").setLastIP("127.0.0.55")
            .setParameters(SNMPParameters.newBuilder().setReadCmString(DiscoveryConfigService.DEFAULT_COMMUNITY_STR).build()).build();
        assertThat(resultSnmpConfig).isNotNull()
            .isEqualTo(expectedSnmpConfig);
    }

    @Test
    void testMissingConfigName() {
        DiscoveryConfigRequest request = DiscoveryConfigRequest.newBuilder()
            .setIpAddresses("127.0.0.1").setLocation("testLocation").build();
        assertThatExceptionOfType(InventoryRuntimeException.class)
            .isThrownBy(() -> configService.createConfigs(request, tenantId))
            .withMessageStartingWith("Invalid config request");
    }

    @Test
    void testMissingLocation() {
        DiscoveryConfigRequest request = DiscoveryConfigRequest.newBuilder()
            .setIpAddresses("127.0.0.1").setConfigName("testName").build();
        assertThatExceptionOfType(InventoryRuntimeException.class)
            .isThrownBy(() -> configService.createConfigs(request, tenantId))
            .withMessageStartingWith("Invalid config request");
    }

    @Test
    void testMissingIpAddresses() {
        DiscoveryConfigRequest request = DiscoveryConfigRequest.newBuilder()
            .setLocation("testLocation").setConfigName("testName").build();
        assertThatExceptionOfType(InventoryRuntimeException.class)
            .isThrownBy(() -> configService.createConfigs(request, tenantId))
            .withMessageStartingWith("Invalid config request");
    }

    @Test
    void testListDiscoveryConfig() throws InvalidProtocolBufferException {
        DiscoveryConfigDTO discoveryConfigDTO = discoveryBuilder.build();
        ConfigurationDTO configuration = ConfigurationDTO.newBuilder()
            .setValue(ProtobufUtil.toJson(discoveryConfigDTO)).build();
        ConfigurationDTO configuration2 = ConfigurationDTO.newBuilder()
                .setValue("something").build();
        doReturn(List.of(configuration, configuration2)).when(mockConfigurationService).findByTenantId(tenantId);
        List<DiscoveryConfigDTO> resultList = configService.listDiscoveryConfigs(tenantId);
        assertThat(resultList).hasSize(1)
            .first().isEqualTo(discoveryConfigDTO);
    }

    @Test
    void testGetDiscoveryConfig() throws InvalidProtocolBufferException {
        DiscoveryConfigDTO discoveryConfigDTO = discoveryBuilder.build();
        ConfigurationDTO configuration = ConfigurationDTO.newBuilder()
            .setValue(ProtobufUtil.toJson(discoveryConfigDTO)).build();

        doReturn(Optional.of(configuration)).when(mockConfigurationService).findByKey(tenantId, DiscoveryConfigService.CONFIG_PREFIX_DISCOVERY + configName);
        Optional<DiscoveryConfigDTO> result = configService.getDiscoveryConfigByName(configName, tenantId);
        assertThat(result).isPresent()
            .contains(discoveryConfigDTO);
    }

    @Test
    void testGetDiscoveryConfigException() {
        ConfigurationDTO configuration = ConfigurationDTO.newBuilder()
            .setValue("something wrong").build();
        doReturn(Optional.of(configuration)).when(mockConfigurationService).findByKey(tenantId, DiscoveryConfigService.CONFIG_PREFIX_DISCOVERY + configName);
        assertThatExceptionOfType(InventoryRuntimeException.class)
            .isThrownBy(() -> configService.getDiscoveryConfigByName(configName, tenantId))
            .withMessageStartingWith("Invalid config value");
    }
}
