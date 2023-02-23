package org.opennms.horizon.flows.integration;

import static org.opennms.horizon.flows.processing.DocumentEnricherTest.createFlowDocument;
import static org.opennms.horizon.flows.processing.DocumentEnricherTest.createNodeDTO;
import static org.opennms.horizon.flows.processing.DocumentEnricherTest.getFlowSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.opennms.dataplatform.flows.ingester.v1.IngesterGrpc;
import org.opennms.dataplatform.flows.ingester.v1.StoreFlowDocumentsResponse;
import org.opennms.horizon.flows.dao.InterfaceToNodeCache;
import org.opennms.horizon.flows.grpc.client.IngestorClient;
import org.opennms.horizon.flows.processing.DocumentEnricherImpl;
import org.opennms.horizon.flows.processing.EnrichedFlow;
import org.opennms.horizon.flows.processing.MockDocumentEnricherFactory;
import org.opennms.horizon.grpc.flows.contract.FlowDocument;
import org.opennms.horizon.inventory.dto.NodeDTO;
import org.opennms.horizon.shared.utils.InetAddressUtils;
import org.springframework.retry.support.RetryTemplate;

import com.google.common.collect.Lists;

import io.grpc.ManagedChannel;


class FlowRepositoryImplTest {

    private FlowRepositoryImpl flowRepository;
    private final IngesterGrpc.IngesterBlockingStub ingesterBlockingStub = Mockito.mock(IngesterGrpc.IngesterBlockingStub.class);
    private final ManagedChannel managedChannel = Mockito.mock(ManagedChannel.class);
    private final IngestorClient ingestorClient = new IngestorClient(managedChannel, 1000, new RetryTemplate());


    @BeforeEach
    public void setUp() {
        flowRepository = new FlowRepositoryImpl(ingestorClient);
        ingestorClient.setIngesterBlockingStub(ingesterBlockingStub);
        Mockito.when(ingesterBlockingStub.withDeadlineAfter(Mockito.anyLong(), Mockito.any())).thenReturn(ingesterBlockingStub);
        Mockito.when(ingesterBlockingStub.withInterceptors(Mockito.any())).thenReturn(ingesterBlockingStub);

        try (MockedStatic<IngesterGrpc> mockedIngester = Mockito.mockStatic(IngesterGrpc.class)) {
            mockedIngester.when(() -> IngesterGrpc.newBlockingStub(managedChannel)).thenReturn(ingesterBlockingStub);
        }
        Mockito.when(ingesterBlockingStub
                .storeFlowDocuments(Mockito.any()))
            .thenThrow(RuntimeException.class).thenThrow(RuntimeException.class)
            .thenReturn(StoreFlowDocumentsResponse.newBuilder().build());
    }

    @Test
    void testCorrectNumberOfInteractionsWithIngesterStub() throws InterruptedException {
        // Given
        final MockDocumentEnricherFactory factory = new MockDocumentEnricherFactory(createNodeDto());

        List<FlowDocument> flows = fillUpAndGetFlows(factory);
        List<EnrichedFlow> enrichedFlows = fillUpAndGetEnrichedFlows(factory, flows);

        // When
        flowRepository.persist(enrichedFlows, "test-tenant-id");

        // Then
        Mockito.verify(ingesterBlockingStub, Mockito.times(3));
    }

    private Map<Long, NodeDTO> createNodeDto() {
        Map<Long, NodeDTO> idToNodeDTO = new HashMap<>();
        idToNodeDTO.put(1L, createNodeDTO(1, "my-requisition"));
        idToNodeDTO.put(2L, createNodeDTO(2, "my-requisition"));
        idToNodeDTO.put(3L, createNodeDTO(3, "my-requisition"));
        return idToNodeDTO;
    }

    private List<FlowDocument> fillUpAndGetFlows(MockDocumentEnricherFactory factory) {
        final FlowDocument flow1 = createFlowDocument("10.0.0.1", "10.0.0.3");
        final FlowDocument flow2 = createFlowDocument("10.0.0.1", "10.0.0.3", -3600_000L);
        final FlowDocument flow3 = createFlowDocument("10.0.0.1", "10.0.0.3", +3600_000L);

        final InterfaceToNodeCache interfaceToNodeCache = factory.getInterfaceToNodeCache();

        interfaceToNodeCache.setNodeId("Default", InetAddressUtils.addr("10.0.0.1"), 1, "tenantId");
        interfaceToNodeCache.setNodeId("Default", InetAddressUtils.addr("10.0.0.2"), 2, "tenantId");
        interfaceToNodeCache.setNodeId("Default", InetAddressUtils.addr("10.0.0.3"), 3, "tenantId");

        return Lists.newArrayList(flow1, flow2, flow3);
    }

    private List<EnrichedFlow> fillUpAndGetEnrichedFlows(MockDocumentEnricherFactory factory, List<FlowDocument> flows) {
        final DocumentEnricherImpl enricher = factory.getEnricher();
        return enricher.enrich(flows, getFlowSource(), "tenantId");
    }
}
