package org.opennms.horizon.flows;


import static org.awaitility.Awaitility.await;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opennms.dataplatform.flows.document.FlowDocument;
import org.opennms.dataplatform.flows.document.FlowDocumentLog;
import org.opennms.horizon.flows.grpc.client.GrpcIngesterMockServer;
import org.opennms.horizon.flows.grpc.client.GrpcInventoryMockServer;
import org.opennms.horizon.flows.grpc.client.IngestorApplicationConfigTest;
import org.opennms.horizon.flows.grpc.client.InventoryApplicationConfigTest;
import org.opennms.horizon.inventory.dto.NodeIdQuery;
import org.opennms.horizon.metrics.MetricsProcessorApplication;
import org.opennms.taskset.contract.TaskResult;
import org.opennms.taskset.contract.TaskSetResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import io.grpc.Server;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;

@SpringBootTest
@ContextConfiguration(classes = {KafkaProducer.class, KafkaConfig.class, MetricsProcessorApplication.class, IngestorApplicationConfigTest.class,
    InventoryApplicationConfigTest.class, FlowProcessorConfig.class})
@EmbeddedKafka(brokerProperties = {"listeners=PLAINTEXT://localhost:59092", "port=59092"}, topics = "flows")
@TestPropertySource(locations = "classpath:test-application.yml")
@DirtiesContext
@ActiveProfiles("test")
class FlowProcessorIntegrationTest {

    @Autowired
    private KafkaProducer producer;

    @Rule
    public final GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();

    @Autowired
    public GrpcIngesterMockServer grpcIngesterMockServer;

    @Autowired
    public GrpcInventoryMockServer grpcInventoryMockServer;

    @Value("${kafka.flow-topics}")
    private String flowTopic;

    private static final Logger LOG = LoggerFactory.getLogger(FlowProcessorIntegrationTest.class);

    private static final String LOCATION1 = "test-location1";
    private static final String LOCATION2 = "test-location2";

    @BeforeEach
    public void setUpMockServers() throws IOException {
        Server server = InProcessServerBuilder.forName(IngestorApplicationConfigTest.SERVER_NAME)
            .directExecutor()
            .addService(grpcIngesterMockServer)
            .build().start();
        grpcCleanupRule.register(server);

        Server inventoryServer = InProcessServerBuilder.forName(InventoryApplicationConfigTest.SERVER_NAME)
            .directExecutor()
            .addService(grpcInventoryMockServer)
            .build().start();
        grpcCleanupRule.register(inventoryServer);
    }

    @Test
    @DirtiesContext
    public void verifyKafkaReceivesMessagesFromProducerAndSendThemToInventoryAndIngester() {
        // Given
        String tenantId = "tenant-id";
        TaskResult testTaskResult = TaskResult.newBuilder().build();
        TaskSetResults testTaskSetResults = TaskSetResults.newBuilder().addResults(testTaskResult).build();

        FlowDocumentLog flows = FlowDocumentLog.newBuilder().addMessage(FlowDocument.newBuilder()
            .setSrcAddress("127.0.0.1")
            .setDstAddress("8.8.8.8")
            .setLocation(LOCATION1)
            .setExporterAddress("127.0.0.1")
        ).addMessage(FlowDocument.newBuilder()
            .setSrcAddress("192.168.0.1")
            .setDstAddress("1.1.1.1")
            .setLocation(LOCATION2)
            .setExporterAddress("127.0.0.1")
        ).build();

        // When
        CompletableFuture<SendResult<String, byte[]>> future = producer.sendByte(flowTopic, flows.toByteArray(), tenantId);
        future.whenComplete((result, ex) -> Optional.ofNullable(ex)
            .ifPresentOrElse(val -> LOG.error("Unable to send message=[{}] due to: {}", testTaskSetResults.toByteArray(), val.getMessage()),
                () -> LOG.info("Sent message=[{}] with offset=[{}]", testTaskSetResults.toByteArray(), result.getRecordMetadata().offset())));

        // Then
        // Flow Documents should be persisted
        await()
            .await()
            .atMost(Duration.ofSeconds(10))
            .until(() -> grpcIngesterMockServer.isFlowDocumentPersisted());
        Assertions.assertEquals(String.format("%s,%s", tenantId, tenantId), grpcIngesterMockServer.getSavedTenantId());

        // Flow Documents are sent to Inventory for Enrichment
        Map<String, NodeIdQuery> incomingNodeIdQueries = grpcInventoryMockServer.getIncomingNodeIdQueries();
        Assertions.assertEquals(2, incomingNodeIdQueries.size());
        Assertions.assertNotNull(incomingNodeIdQueries.get(LOCATION1));
        Assertions.assertNotNull(incomingNodeIdQueries.get(LOCATION2));
    }
}
