package org.opennms.netmgt.provision.scan;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opennms.netmgt.provision.LocationAwareDetectorClient;
import org.opennms.netmgt.provision.persistence.dto.ForeignSourceDTO;
import org.opennms.netmgt.provision.persistence.dto.RequisitionNodeDTO;
import org.opennms.netmgt.provision.persistence.model.ForeignSourceRepository;

@Slf4j
public class NodeScannerTest extends CamelTestSupport {
    NodeScanner nodeScanner;

    @Mock
    LocationAwareDetectorClient locationAwareDetectorClient;
    @Mock
    ForeignSourceRepository foreignSourceRepository;

    RequisitionNodeDTO requisitionNodeDTO;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.openMocks(this);
        requisitionNodeDTO = new RequisitionNodeDTO();
        requisitionNodeDTO.setLocation("blahLocation");
        requisitionNodeDTO.setNodeLabel("blahLabel");
        requisitionNodeDTO.setForeignId("Default");
        nodeScanner = new NodeScanner(context(), locationAwareDetectorClient, foreignSourceRepository);

        ForeignSourceDTO foreignSourceDTO = new ForeignSourceDTO();
        foreignSourceDTO.setScanInterval(new Duration(6000));

        when(foreignSourceRepository.getForeignSource(anyString())).thenReturn(foreignSourceDTO);
    }

    @Test
    public void scanNode() throws Exception {
        
        nodeScanner.scanNode(requisitionNodeDTO);

        context().getRoutes().forEach(route -> log.info("######### ROUTE: "+route.getId()));

        assertTrue(context().getRoutes().stream().filter(route -> route.getId().startsWith("SCHEDULED-SCANNER")).collect(Collectors.toList()).size() > 0);
    }
}