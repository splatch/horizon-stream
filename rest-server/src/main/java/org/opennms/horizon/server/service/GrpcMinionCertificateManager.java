package org.opennms.horizon.server.service;

import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.RequiredArgsConstructor;
import org.opennms.horizon.minioncertmanager.proto.GetMinionCertificateResponse;
import org.opennms.horizon.server.service.grpc.MinionCertificateManagerClient;
import org.opennms.horizon.server.utils.ServerHeaderUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@GraphQLApi
@Service
public class GrpcMinionCertificateManager {
    private final MinionCertificateManagerClient client;
    private final ServerHeaderUtil headerUtil;

    @GraphQLQuery(name = "getMinionCertificate")
    public Mono<byte[]> getMinionCertificate(Long locationId, @GraphQLEnvironment ResolutionEnvironment env) {
        String tenantId = headerUtil.extractTenant(env);

        GetMinionCertificateResponse minionCert = client.getMinionCert(tenantId, locationId, headerUtil.getAuthHeader(env));

        return Mono.just(minionCert.getZip().toByteArray());
    }
}
