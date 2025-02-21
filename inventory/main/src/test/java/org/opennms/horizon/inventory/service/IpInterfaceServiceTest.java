package org.opennms.horizon.inventory.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opennms.horizon.azure.api.AzureScanNetworkInterfaceItem;
import org.opennms.horizon.inventory.dto.IpInterfaceDTO;
import org.opennms.horizon.inventory.mapper.IpInterfaceMapper;
import org.opennms.horizon.inventory.model.IpInterface;
import org.opennms.horizon.inventory.model.Node;
import org.opennms.horizon.inventory.model.SnmpInterface;
import org.opennms.horizon.inventory.repository.IpInterfaceRepository;
import org.opennms.horizon.shared.utils.IPAddress;
import org.opennms.node.scan.contract.IpInterfaceResult;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class IpInterfaceServiceTest {

    public static final String TEST_TENANT_ID = "x-tenant-id-x";
    public static final long TEST_LOCATION = 1313L;
    public static final String TEST_LOCATION_TEXT = String.valueOf(TEST_LOCATION);

    private IpInterfaceRepository mockIpInterfaceRepository;
    private IpInterfaceMapper mockIpInterfaceMapper;

    private IpInterface testIpInterface;
    private IpInterfaceDTO testIpInterfaceDTO;

    private IpInterfaceService target;

    @BeforeEach
    public void setUp() {
        mockIpInterfaceRepository = Mockito.mock(IpInterfaceRepository.class);
        mockIpInterfaceMapper = Mockito.mock(IpInterfaceMapper.class);

        testIpInterface = new IpInterface();
        testIpInterfaceDTO =
            IpInterfaceDTO.newBuilder()
                .setId(1313)
                .build();

        target = new IpInterfaceService(mockIpInterfaceRepository, mockIpInterfaceMapper);
    }

    @Test
    void testGetByTenantId() {
        //
        // Setup Test Data and Interactions
        //
        var testIpInterface1 = new IpInterface();
        var testIpInterface2 = new IpInterface();
        var testIpInterfaceList = List.of(testIpInterface1, testIpInterface2);
        var testIpInterfaceDTO1 = IpInterfaceDTO.newBuilder().setId(1313).build();
        var testIpInterfaceDTO2 = IpInterfaceDTO.newBuilder().setId(1717).build();

        Mockito.when(mockIpInterfaceRepository.findByTenantId(TEST_TENANT_ID)).thenReturn(testIpInterfaceList);
        Mockito.when(mockIpInterfaceMapper.modelToDTO(testIpInterface1)).thenReturn(testIpInterfaceDTO1);
        Mockito.when(mockIpInterfaceMapper.modelToDTO(testIpInterface2)).thenReturn(testIpInterfaceDTO2);

        //
        // Execute
        //
        var result = target.findByTenantId(TEST_TENANT_ID);

        //
        // Verify the Results
        //
        assertEquals(2, result.size());
        assertSame(testIpInterfaceDTO1, result.get(0));
        assertSame(testIpInterfaceDTO2, result.get(1));
    }

    @Test
    void testGetByIdAndTenantId() {
        //
        // Setup Test Data and Interactions
        //
        Mockito.when(mockIpInterfaceRepository.findByIdAndTenantId(1313, TEST_TENANT_ID)).thenReturn(Optional.of(testIpInterface));
        Mockito.when(mockIpInterfaceMapper.modelToDTO(testIpInterface)).thenReturn(testIpInterfaceDTO);

        //
        // Execute
        //
        var result = target.getByIdAndTenantId(1313, TEST_TENANT_ID);

        //
        // Verify the Results
        //
        assertSame(testIpInterfaceDTO, result.get());
    }

    @Test
    void testCreateFromAzureScanResult() {
        //
        // Setup Test Data and Interactions
        //
        var testNode = new Node();
        var testAzureScanNetworkInterfaceItem =
            AzureScanNetworkInterfaceItem.newBuilder()
                .setIpAddress("11.11.11.11")
                .build();

        //
        // Execute
        //
        target.createFromAzureScanResult(TEST_TENANT_ID, testNode, testAzureScanNetworkInterfaceItem);

        //
        // Verify the Results
        //
        Mockito.verify(mockIpInterfaceRepository).save(
            Mockito.argThat(
                argument -> (
                    ( ! argument.getSnmpPrimary() ) &&
                    ( argument.getNode() == testNode ) &&
                    ( Objects.equals("11.11.11.11", argument.getIpAddress().getHostName()) )
                )
            )
        );
    }

    @Test
    void testFindByIpAddressAndLocationAndTenantId() {
        //
        // Setup Test Data and Interactions
        //
        Mockito.when(mockIpInterfaceRepository.findByIpAddressAndLocationIdAndTenantId(new IPAddress("11.11.11.11").toInetAddress(), TEST_LOCATION, TEST_TENANT_ID)).thenReturn(Optional.of(testIpInterface));
        Mockito.when(mockIpInterfaceMapper.modelToDTO(testIpInterface)).thenReturn(testIpInterfaceDTO);

        //
        // Execute
        //
        var result = target.findByIpAddressAndLocationAndTenantId("11.11.11.11", TEST_LOCATION_TEXT, TEST_TENANT_ID);

        //
        // Verify the Results
        //
        assertSame(testIpInterfaceDTO, result.orElse(null));
    }

    @Test
    void testFindByIpAddressAndLocationAndTenantIdNotFound() {
        //
        // Setup Test Data and Interactions
        //
        Mockito.when(mockIpInterfaceRepository.findByIpAddressAndLocationIdAndTenantId(new IPAddress("11.11.11.11").toInetAddress(), TEST_LOCATION, TEST_TENANT_ID)).thenReturn(Optional.empty());

        //
        // Execute
        //
        var result = target.findByIpAddressAndLocationAndTenantId("11.11.11.11", TEST_LOCATION_TEXT, TEST_TENANT_ID);

        //
        // Verify the Results
        //
        assertFalse(result.isPresent());
    }

    @Test
    void testCreateOrUpdateFromScanResult() {
        //
        // Setup Test Data and Interactions
        //
        var testNode = new Node();
        testNode.setId(1313);
        var testIpInterfaceResult =
            IpInterfaceResult.newBuilder()
                .setIfIndex(1)
                .setIpHostName("x-hostname-x")
                .setNetmask("x-netmask-x")
                .setIpAddress("11.11.11.11")
                .build();
        var testSnmpInterface1 = new SnmpInterface();
        var testSnmpInterface2 = new SnmpInterface();
        Map<Integer, SnmpInterface> snmpInterfaceMap =
            Map.of(
                1, testSnmpInterface1,
                2, testSnmpInterface2
            );
        Mockito.when(mockIpInterfaceRepository.findByNodeIdAndTenantIdAndIpAddress(1313, TEST_TENANT_ID, new IPAddress("11.11.11.11").toInetAddress())).thenReturn(Optional.of(testIpInterface));

        //
        // Execute
        //
        target.createOrUpdateFromScanResult(TEST_TENANT_ID, testNode, testIpInterfaceResult, snmpInterfaceMap);

        //
        // Verify the Results
        //
        Mockito.verify(mockIpInterfaceRepository).save(Mockito.argThat(
            argument ->
                (
                    ( Objects.equals("x-hostname-x", argument.getHostname()) ) &&
                    ( Objects.equals("x-netmask-x", argument.getNetmask()) )
                )
        ));
    }

    @Test
    void testCreateOrUpdateFromScanResultNotFound() {
        //
        // Setup Test Data and Interactions
        //
        var testNode = new Node();
        testNode.setId(1313);
        var testIpInterfaceResult =
            IpInterfaceResult.newBuilder()
                .setIfIndex(1)
                .setIpHostName("x-hostname-x")
                .setNetmask("x-netmask-x")
                .setIpAddress("11.11.11.11")
                .build();
        var testSnmpInterface1 = new SnmpInterface();
        var testSnmpInterface2 = new SnmpInterface();
        Map<Integer, SnmpInterface> snmpInterfaceMap =
            Map.of(
                1, testSnmpInterface1,
                2, testSnmpInterface2
            );
        Mockito.when(mockIpInterfaceRepository.findByNodeIdAndTenantIdAndIpAddress(1313, TEST_TENANT_ID, new IPAddress("11.11.11.11").toInetAddress())).thenReturn(Optional.empty());
        Mockito.when(mockIpInterfaceMapper.fromScanResult(testIpInterfaceResult)).thenReturn(testIpInterface);

        //
        // Execute
        //
        target.createOrUpdateFromScanResult(TEST_TENANT_ID, testNode, testIpInterfaceResult, snmpInterfaceMap);

        //
        // Verify the Results
        //
        Mockito.verify(mockIpInterfaceRepository).save(Mockito.same(testIpInterface));
        assertSame(testNode, testIpInterface.getNode());
        assertEquals(TEST_TENANT_ID, testIpInterface.getTenantId());
        assertFalse(testIpInterface.getSnmpPrimary());
        assertEquals("x-hostname-x", testIpInterface.getHostname());
        assertEquals(1, testIpInterface.getIfIndex());
        assertSame(testSnmpInterface1, testIpInterface.getSnmpInterface());
    }
}
