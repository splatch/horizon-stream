package org.opennms.horizon.inventory.grpc;

import com.google.rpc.Code;
import com.google.rpc.Status;
import com.vladmihalcea.hibernate.type.basic.Inet;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opennms.horizon.inventory.InventoryApplication;
import org.opennms.horizon.inventory.PostgresInitializer;
import org.opennms.horizon.inventory.dto.DeviceCreateDTO;
import org.opennms.horizon.inventory.dto.DeviceServiceGrpc;
import org.opennms.horizon.inventory.dto.NodeDTO;
import org.opennms.horizon.inventory.model.IpInterface;
import org.opennms.horizon.inventory.model.MonitoringLocation;
import org.opennms.horizon.inventory.model.Node;
import org.opennms.horizon.inventory.repository.IpInterfaceRepository;
import org.opennms.horizon.inventory.repository.MonitoringLocationRepository;
import org.opennms.horizon.inventory.repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = InventoryApplication.class)
@ContextConfiguration(initializers = {PostgresInitializer.class})
class DeviceGrpcIT extends GrpcTestBase {
    private DeviceServiceGrpc.DeviceServiceBlockingStub serviceStub;

    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private MonitoringLocationRepository monitoringLocationRepository;
    @Autowired
    private IpInterfaceRepository ipInterfaceRepository;

    public void initStub() {
        serviceStub = DeviceServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    public void cleanUp(){
        ipInterfaceRepository.deleteAll();
        nodeRepository.deleteAll();
        monitoringLocationRepository.deleteAll();

        channel.shutdown();
    }

    @Test
    void testCreateDevice() throws Exception {
        setupGrpc();
        initStub();

        String label = "label";

        DeviceCreateDTO createDTO = DeviceCreateDTO.newBuilder()
            .setLocation("location")
            .setLabel(label)
            .setManagementIp("127.0.0.1")
            .build();

        NodeDTO node = serviceStub.createDevice(createDTO);

        assertEquals(label, node.getNodeLabel());
    }

    @Test
    void testCreateDeviceExistingIpAddress() throws Exception {
        String location = "location";
        String ip = "127.0.0.1";
        String label = "label";

        setupGrpc();
        populateTables(location, ip);
        initStub();

        DeviceCreateDTO createDTO = DeviceCreateDTO.newBuilder()
            .setLocation(location)
            .setLabel(label)
            .setManagementIp(ip)
            .build();

        StatusRuntimeException exception = Assertions.assertThrows(StatusRuntimeException.class, ()->serviceStub.createDevice(createDTO));
        Status status = StatusProto.fromThrowable(exception);
        assertThat(status.getCode()).isEqualTo(Code.ALREADY_EXISTS_VALUE);
        assertThat(status.getMessage()).isEqualTo("Ip address already exists for location");
    }

    @Test
    void testCreateDeviceExistingIpAddressDifferentTenantId() throws Exception {
        String location = "location";
        String ip = "127.0.0.1";
        String label = "label";

        setupGrpcWithDifferentTenantID();
        populateTables(location, ip);
        initStub();

        DeviceCreateDTO createDTO = DeviceCreateDTO.newBuilder()
            .setLocation(location)
            .setLabel(label)
            .setManagementIp(ip)
            .build();

        NodeDTO node = serviceStub.createDevice(createDTO);

        assertEquals(label, node.getNodeLabel());
    }

    @Test
    void testCreateDeviceExistingIpAddressDifferentLocation() throws Exception {
        String location = "location";
        String ip = "127.0.0.1";
        String label = "label";

        setupGrpc();
        populateTables(location, ip);
        initStub();

        DeviceCreateDTO createDTO = DeviceCreateDTO.newBuilder()
            .setLocation("different")
            .setLabel(label)
            .setManagementIp(ip)
            .build();

        NodeDTO node = serviceStub.createDevice(createDTO);

        assertEquals(label, node.getNodeLabel());
    }

    private void populateTables(String location, String ip) {
        MonitoringLocation ml = new MonitoringLocation();
        ml.setLocation(location);
        ml.setTenantId(tenantId);
        MonitoringLocation savedML = monitoringLocationRepository.save(ml);

        Node node = new Node();
        node.setTenantId(tenantId);
        node.setNodeLabel("label");
        node.setMonitoringLocation(savedML);
        node.setCreateTime(LocalDateTime.now());
        Node savedNode = nodeRepository.save(node);

        IpInterface ipInterface = new IpInterface();
        ipInterface.setTenantId(tenantId);
        ipInterface.setIpAddress(new Inet(ip));
        ipInterface.setNode(savedNode);
        IpInterface savedIpInterface = ipInterfaceRepository.save(ipInterface);
    }

    @Test
    void testCreateDeviceMissingTenantId() throws Exception {
        setupGrpcWithOutTenantID();
        initStub();

        String label = "label";

        DeviceCreateDTO createDTO = DeviceCreateDTO.newBuilder()
            .setLocation("location")
            .setLabel(label)
            .setManagementIp("127.0.0.1")
            .build();

        StatusRuntimeException exception = Assertions.assertThrows(StatusRuntimeException.class, ()->serviceStub.createDevice(createDTO));
        Status status = StatusProto.fromThrowable(exception);
        assertThat(status.getCode()).isEqualTo(Code.UNAUTHENTICATED_VALUE);
        assertThat(status.getMessage()).isEqualTo("Missing tenant id");
    }

    @Test
    void testCreateDeviceBadIPAddress() throws Exception {
        setupGrpc();
        initStub();

        String label = "label";

        DeviceCreateDTO createDTO = DeviceCreateDTO.newBuilder()
            .setLocation("location")
            .setLabel(label)
            .setManagementIp("BAD")
            .build();

        StatusRuntimeException exception = Assertions.assertThrows(StatusRuntimeException.class, ()->serviceStub.createDevice(createDTO));
        Status status = StatusProto.fromThrowable(exception);
        assertThat(status.getCode()).isEqualTo(Code.INVALID_ARGUMENT_VALUE);
        assertThat(status.getMessage()).isEqualTo("Bad management_ip: BAD");
    }
}
