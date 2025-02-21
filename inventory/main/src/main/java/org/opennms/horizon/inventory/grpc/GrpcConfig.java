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

package org.opennms.horizon.inventory.grpc;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.rotation.JWKPublicKeyLocator;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.opennms.horizon.inventory.grpc.discovery.ActiveDiscoveryGrpcService;
import org.opennms.horizon.inventory.grpc.discovery.IcmpActiveDiscoveryGrpcService;
import org.opennms.horizon.inventory.grpc.discovery.AzureActiveDiscoveryGrpcService;
import org.opennms.horizon.inventory.grpc.discovery.PassiveDiscoveryGrpcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class GrpcConfig {
    private static final int DEFAULT_GRPC_PORT = 8990;
    @Value("${grpc.server.port:" + DEFAULT_GRPC_PORT +"}")
    private int port;
    @Value("${keycloak.base-url}")
    private String keycloakAuthUrl;
    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Bean
    public TenantLookup createTenantLookup(){
        return new GrpcTenantLookupImpl();
    }

    @Bean
    public KeycloakDeployment createKeycloak() {
        AdapterConfig config = new AdapterConfig();
        config.setAllowAnyHostname(true);
        config.setAuthServerUrl(keycloakAuthUrl);
        config.setRealm(keycloakRealm);
        config.setUseResourceRoleMappings(false);
        config.setPrincipalAttribute("preferred_username");
        config.setSslRequired("false");

        KeycloakDeployment keycloak = new KeycloakDeployment();
        keycloak.setAuthServerBaseUrl(config);
        keycloak.setRealm(keycloakRealm);
        keycloak.setPublicKeyLocator(new JWKPublicKeyLocator());
        keycloak.setPublicKeyCacheTtl(3600);
        HttpClient client = HttpClientBuilder.create().build();
        keycloak.setClient(client);

        return keycloak;
    }

    @Bean(destroyMethod = "stopServer")
    public GrpcServerManager startServer(MonitoringLocationGrpcService locationGrpc, MonitoringSystemGrpcService systemGrpc,
                                         NodeGrpcService nodeGrpcService, AzureActiveDiscoveryGrpcService azureActiveDiscoveryGrpcService, TagGrpcService tagGrpcService,
                                         InventoryServerInterceptor interceptor,
                                         ActiveDiscoveryGrpcService activeDiscoveryGrpcService,
                                         IcmpActiveDiscoveryGrpcService icmpActiveDiscoveryGrpcService,
                                         PassiveDiscoveryGrpcService passiveDiscoveryGrpcService) {
        GrpcServerManager manager = new GrpcServerManager(port, interceptor);
        manager.startServer(locationGrpc, systemGrpc, nodeGrpcService, azureActiveDiscoveryGrpcService, tagGrpcService, activeDiscoveryGrpcService, icmpActiveDiscoveryGrpcService, passiveDiscoveryGrpcService);
        return manager;
    }
}
