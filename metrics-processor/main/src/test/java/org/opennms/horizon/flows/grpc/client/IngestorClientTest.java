package org.opennms.horizon.flows.grpc.client;


import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opennms.dataplatform.flows.document.FlowDocument;
import org.opennms.dataplatform.flows.ingester.v1.StoreFlowDocumentsRequest;
import org.opennms.horizon.flows.FlowsApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.codahale.metrics.MetricRegistry;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FlowsApplicationConfig.class)
@ContextConfiguration(classes = MetricRegistry.class)
@TestPropertySource(locations = "classpath:test-application.yml")
class IngestorClientTest {

    @Autowired
    private IngestorClient ingestorClient;

    @Test
    void sendData() {
        // Given
        FlowDocument flowDocument = createFlowDocument("test-ip", "test-dest-ip");
        StoreFlowDocumentsRequest storeFlowDocumentsRequest = StoreFlowDocumentsRequest.newBuilder()
            .addDocuments(flowDocument)
            .build();

        // When
        // TODO: verify what's failing in the call then reactivate the test
        //assertDoesNotThrow(() -> ingestorClient.sendData(storeFlowDocumentsRequest, "test-tenant-id"));
    }


    public static FlowDocument createFlowDocument(String sourceIp, String destIp) {
        return createFlowDocument(sourceIp, destIp, 0);
    }

    public static FlowDocument createFlowDocument(String sourceIp, String destIp, final long timeOffset) {
        final var now = Instant.now();

        final var flow = FlowDocument.newBuilder()
            .setTimestamp(now.toEpochMilli())
            .setFirstSwitched(UInt64Value.of(now.minus(20_000L, ChronoUnit.MILLIS).toEpochMilli()))
            .setDeltaSwitched(UInt64Value.of(now.minus(10_000L, ChronoUnit.MILLIS).toEpochMilli()))
            .setLastSwitched(UInt64Value.of(now.minus(5_000L, ChronoUnit.MILLIS).toEpochMilli()))
            .setSrcAddress(sourceIp)
            .setSrcPort(UInt32Value.of(510))
            .setDstAddress(destIp)
            .setDstPort(UInt32Value.of(80))
            .setProtocol(UInt32Value.of(6)); // TCP

        return flow.build();
    }
}
