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

package org.opennms.horizon.server.service.grpc;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.opennms.horizon.minioncertmanager.proto.GetMinionCertificateRequest;
import org.opennms.horizon.minioncertmanager.proto.GetMinionCertificateResponse;
import org.opennms.horizon.minioncertmanager.proto.MinionCertificateManagerGrpc;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MinionCertificateManagerClientTest {

    private MinionCertificateManagerClient client;
    private MinionCertificateManagerGrpc.MinionCertificateManagerBlockingStub mockCertStub1;
    private MinionCertificateManagerGrpc.MinionCertificateManagerBlockingStub mockCertStub2;
    private ManagedChannel mockManagedChannel;
    private GetMinionCertificateResponse testGetMinionCertificateResponse;

    @BeforeEach
    public void setUp() {
        mockCertStub1 = mock(MinionCertificateManagerGrpc.MinionCertificateManagerBlockingStub.class);
        mockCertStub2 = mock(MinionCertificateManagerGrpc.MinionCertificateManagerBlockingStub.class);
        mockManagedChannel = mock(ManagedChannel.class);

        testGetMinionCertificateResponse = GetMinionCertificateResponse.newBuilder().build();

        client = new MinionCertificateManagerClient(mockManagedChannel);
        client.setMinionCertStubFactory(this::testMinionCertFactory);

        ArgumentMatcher<GetMinionCertificateRequest> requestMatcher = createGetMinionCertificateRequestArgumentMatcher("tenantId", "location");

        Mockito.when(mockCertStub1.withInterceptors(Mockito.any(ClientInterceptor.class))).thenReturn(mockCertStub2);
        Mockito.when(mockCertStub2.getMinionCert(Mockito.argThat(requestMatcher))).thenReturn(testGetMinionCertificateResponse);
    }

    @Test
    void testGetMinionCert() {
        client.initialStubs();

        GetMinionCertificateResponse response = client.getMinionCert("tenantId", "location", "accessToken");

        assertThat(response).isSameAs(testGetMinionCertificateResponse);

        //
        // Shutdown
        //
        Mockito.when(mockManagedChannel.isShutdown()).thenReturn(false);

        client.shutdown();

        verify(mockManagedChannel).shutdown();
    }

//========================================
//
//----------------------------------------

    private MinionCertificateManagerGrpc.MinionCertificateManagerBlockingStub testMinionCertFactory(ManagedChannel channel) {
        assertThat(channel).isSameAs(mockManagedChannel);
        return mockCertStub1;
    }

    private ArgumentMatcher<GetMinionCertificateRequest> createGetMinionCertificateRequestArgumentMatcher(String expectedTenantId, String expectedLocation) {
        return new ArgumentMatcher<GetMinionCertificateRequest>() {
            @Override
            public boolean matches(GetMinionCertificateRequest argument) {
                if (Objects.equals(argument.getTenantId(), expectedTenantId)) {
                    if (Objects.equals(argument.getLocation(), expectedLocation)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
