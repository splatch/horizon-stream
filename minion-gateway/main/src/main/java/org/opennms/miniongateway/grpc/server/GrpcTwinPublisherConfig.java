package org.opennms.miniongateway.grpc.server;

import org.opennms.miniongateway.grpc.twin.GrpcTwinPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcTwinPublisherConfig {

    @Bean(initMethod = "start", destroyMethod = "close")
    public GrpcTwinPublisher grpcTwinPublisher() {
        return new GrpcTwinPublisher();
    }

}
