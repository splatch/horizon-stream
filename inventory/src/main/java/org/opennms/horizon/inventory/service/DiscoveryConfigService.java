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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.opennms.horizon.inventory.discovery.ConfigResults;
import org.opennms.horizon.inventory.discovery.DiscoveryConfigDTO;
import org.opennms.horizon.inventory.discovery.DiscoveryConfigRequest;
import org.opennms.horizon.inventory.discovery.SNMPConfigDTO;
import org.opennms.horizon.inventory.discovery.SNMPParameters;
import org.opennms.horizon.inventory.discovery.SNMPVersion;
import org.opennms.horizon.inventory.dto.ConfigurationDTO;
import org.opennms.horizon.inventory.exception.InventoryRuntimeException;
import org.opennms.horizon.shared.protobuf.util.ProtobufUtil;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiscoveryConfigService {
    protected static final String CONFIG_PREFIX_DISCOVERY = "discovery-";
    protected static final String CONFIG_PREFIX_SNMP = "snmp-";
    protected static final String DEFAULT_COMMUNITY_STR = "public";
    private final DiscoveryConfigDTO.Builder discoveryConfigBuilder = DiscoveryConfigDTO.newBuilder()
        .setRetries(1)
        .setTimeout(300L);
    private final SNMPConfigDTO.Builder snmpConfigBuilder = SNMPConfigDTO.newBuilder()
        .setVersion(SNMPVersion.v2c)
        .setTimeout(3000)
        .setRetries(1)
        .setPort(161)
        .setMaxRequestSize(65535)
        .setMaxVarsPerPdu(10)
        .setMaxRepetitions(2)
        .setTtl(6000);

    private final ConfigurationService configService;

    public ConfigResults createConfigs(DiscoveryConfigRequest request, String tenantId) throws InvalidProtocolBufferException {
        if (StringUtils.isEmpty(request.getConfigName()) || StringUtils.isEmpty(request.getLocation()) || StringUtils.isEmpty(request.getIpAddresses())) {
            throw new InventoryRuntimeException("Invalid config request: " + request);
        }
        DiscoveryConfigDTO discoveryConfig = createDiscoveryConfig(request, tenantId);
        SNMPConfigDTO snmpConfigDTO = creatSNMPConfig(request, tenantId);
        return ConfigResults.newBuilder()
            .setDiscoveryConfig(discoveryConfig)
            .setSnmpConfig(snmpConfigDTO)
            .build();
    }

    public Optional<DiscoveryConfigDTO> getDiscoveryConfigByName(String name, String tenantId) {
        return getConfigByKey(CONFIG_PREFIX_DISCOVERY + name, tenantId, DiscoveryConfigDTO.class);
    }

    public Optional<SNMPConfigDTO> getSnmpConfigByName(String name, String tenantId) {
        return getConfigByKey(CONFIG_PREFIX_SNMP + name, tenantId, SNMPConfigDTO.class);
    }

    public List<DiscoveryConfigDTO> listDiscoveryConfigs(String tenantId) {
        return listConfigs(tenantId, CONFIG_PREFIX_DISCOVERY, DiscoveryConfigDTO.class);
    }

    public List<SNMPConfigDTO> listSnmpConfigs(String tenantId) {
        return listConfigs(tenantId, CONFIG_PREFIX_SNMP, SNMPConfigDTO.class);
    }

    public List<DiscoveryConfigDTO> listDiscoveryConfigByLocation(String tenantId, String location) {
        return listConfigByLocation(tenantId, location, CONFIG_PREFIX_DISCOVERY, DiscoveryConfigDTO.class);
    }

    public List<SNMPConfigDTO> listSNMPConfigByLocation(String tenantId, String location) {
        return listConfigByLocation(tenantId, location, CONFIG_PREFIX_SNMP, SNMPConfigDTO.class);
    }

    private <T extends GeneratedMessageV3> Optional<T> getConfigByKey(String key, String tenantId, Class<T> clazz) {
        return configService.findByKey(tenantId, key)
            .map(config -> {
                try {
                    return ProtobufUtil.fromJson(config.getValue(), clazz);
                } catch (InvalidProtocolBufferException e) {
                    throw new InventoryRuntimeException("Invalid config value: " + config.getValue(), e);
                }
            });
    }

    private <T extends GeneratedMessageV3> List<T> listConfigs(String tenantId, String prefix, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        configService.findByTenantId(tenantId)
            .stream().filter(c -> c.getKey().startsWith(prefix))
            .forEach(config -> {
                try {
                    list.add(ProtobufUtil.fromJson(config.getValue(), clazz));
                } catch (InvalidProtocolBufferException e) {
                    log.error("Invalid config value {}", config.getValue());
                }
            });
        return list;
    }

    private <T extends GeneratedMessageV3> List<T> listConfigByLocation(String tenantId, String location, String prefix, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        configService.findByLocation(tenantId, location)
            .stream().filter(c -> c.getKey().startsWith(prefix))
            .forEach(config -> {
                try {
                    list.add(ProtobufUtil.fromJson(config.getValue(), clazz));
                } catch (InvalidProtocolBufferException e) {
                    log.error("Invalid config value {}", config.getValue());
                }
            });
        return list;
    }


    private SNMPConfigDTO creatSNMPConfig(DiscoveryConfigRequest request, String tenantId) throws InvalidProtocolBufferException {
        String ipAddresses = request.getIpAddresses();
        String firstIp = ipAddresses.contains("-")? ipAddresses.substring(0, ipAddresses.indexOf("-")) : ipAddresses;
        String lastIp = ipAddresses.contains("-")? ipAddresses.substring(ipAddresses.indexOf("-") + 1) : null;
        String communityStr = StringUtils.isEmpty(request.getReadComStr()) ? DEFAULT_COMMUNITY_STR : request.getReadComStr();
        SNMPConfigDTO.Builder clonedBuilder = SNMPConfigDTO.newBuilder(snmpConfigBuilder.build());
        if(lastIp != null) {
            clonedBuilder.setLastIP(lastIp);
        }

        SNMPConfigDTO snmpConfigDTO = clonedBuilder.setConfigName(request.getConfigName())
            .setFirstIP(firstIp)
            .setParameters(SNMPParameters.newBuilder().setReadCmString(communityStr).build()).build();
        ConfigurationDTO configuration = ConfigurationDTO.newBuilder()
            .setKey(CONFIG_PREFIX_SNMP + request.getConfigName())
            .setTenantId(tenantId)
            .setLocation(request.getLocation())
            .setValue(ProtobufUtil.toJson(snmpConfigDTO)).build();
        return ProtobufUtil.fromJson(configService.createSingle(configuration).getValue().toString(), SNMPConfigDTO.class);
    }

    private DiscoveryConfigDTO createDiscoveryConfig(DiscoveryConfigRequest request, String tenantId) throws InvalidProtocolBufferException {
        DiscoveryConfigDTO discoveryConfig = DiscoveryConfigDTO.newBuilder(discoveryConfigBuilder.build())
            .setConfigName(request.getConfigName())
            .setIpAddresses(request.getIpAddresses()).build();

        ConfigurationDTO configuration = ConfigurationDTO.newBuilder()
            .setKey(CONFIG_PREFIX_DISCOVERY + request.getConfigName())
            .setTenantId(tenantId)
            .setValue(ProtobufUtil.toJson(discoveryConfig))
            .setLocation(request.getLocation()).build();
        return ProtobufUtil.fromJson(configService.createSingle(configuration).getValue().toString(), DiscoveryConfigDTO.class);
    }

    protected DiscoveryConfigDTO.Builder getDefaultDiscoveryConfigBuilder() {
        return discoveryConfigBuilder;
    }

    protected SNMPConfigDTO.Builder getDefaultSNMPConfigBuilder() {
        return snmpConfigBuilder;
    }
}
