package org.opennms.miniongateway.grpc.server.stub;

import io.grpc.stub.StreamObserver;
import org.opennms.cloud.grpc.minion.CloudToMinionMessage;
import org.opennms.cloud.grpc.minion.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

public class StubCloudToMinionMessageProcessor implements BiConsumer<Identity, StreamObserver<CloudToMinionMessage>> {
    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(StubCloudToMinionMessageProcessor.class);

    private Logger log = DEFAULT_LOGGER;

    @Override
    public void accept(Identity minionHeader, StreamObserver<CloudToMinionMessage> cloudToMinionMessageStreamObserver) {
        log.info("Have Message to send to Minion: system-id={}, location={}",
            minionHeader.getSystemId(),
            minionHeader.getLocation());
    }
}
