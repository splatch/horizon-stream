package org.opennms.horizon.inventory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.opennms.horizon.inventory.dto.IpInterfaceDTO;
import org.opennms.horizon.inventory.dto.MonitoredServiceDTO;
import org.opennms.horizon.inventory.dto.MonitoredServiceTypeDTO;
import org.opennms.horizon.inventory.dto.NodeDTO;
import org.opennms.horizon.inventory.repository.MonitoredServiceRepository;
import org.opennms.horizon.inventory.repository.MonitoredServiceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = InventoryApplication.class)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MonitoredServiceIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.5-alpine")
        .withDatabaseName("inventory").withUsername("inventory")
        .withPassword("password").withExposedPorts(5432);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MonitoredServiceRepository monitoredServiceRepository;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
            () -> String.format("jdbc:postgresql://localhost:%d/%s", postgres.getFirstMappedPort(), postgres.getDatabaseName()));
        registry.add("spring.datasource.username", () -> postgres.getUsername());
        registry.add("spring.datasource.password", () -> postgres.getPassword());
    }

    public static long savedNodeId = -1;
    public static long savedIpInterfaceId = -1;
    public static long savedMonitorServiceTypeId = -1;

    @BeforeEach
    public void setup() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());

        if (savedNodeId == -1) {
            NodeDTO dto = postNode("label");
            savedNodeId = dto.getId();

            IpInterfaceDTO ipInterfaceDTO = postIpInterface("127.0.0.1");
            savedIpInterfaceId = ipInterfaceDTO.getId();

            MonitoredServiceTypeDTO monitoredServiceTypeDTO = postMonitoredServiceTypes("serviceName");
            savedMonitorServiceTypeId = monitoredServiceTypeDTO.getId();
        }
    }

    @AfterEach
    public void teardown() {
        monitoredServiceRepository.deleteAll();
    }

    private NodeDTO postNode(String nodeLabel) {
        UUID tenant = new UUID(10, 12);
        NodeDTO ml = NodeDTO.newBuilder()
            .setNodeLabel(nodeLabel)
            .setTenantId(tenant.toString())
            .setCreateTime("2022-11-03T14:34:05.542488")
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<NodeDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<NodeDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/nodes", request, NodeDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        NodeDTO saved = response.getBody();
        assertEquals(tenant.toString(), saved.getTenantId());
        assertEquals(nodeLabel, saved.getNodeLabel());
        return saved;
    }

    private IpInterfaceDTO postIpInterface(String ipAddress) {
        UUID tenant = new UUID(10, 12);

        IpInterfaceDTO ml = IpInterfaceDTO.newBuilder()
                .setIpAddress(ipAddress)
                .setTenantId(tenant.toString())
                .setNodeId(savedNodeId)
                .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<IpInterfaceDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<IpInterfaceDTO> response = this.testRestTemplate
                .postForEntity("http://localhost:" + port + "/inventory/ipInterfaces", request, IpInterfaceDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        IpInterfaceDTO saved = response.getBody();
        assertEquals(tenant.toString(), saved.getTenantId());
        assertEquals(ipAddress, saved.getIpAddress());
        return saved;
    }

    private MonitoredServiceTypeDTO postMonitoredServiceTypes(String serviceName) {
        UUID tenant = new UUID(10, 12);
        MonitoredServiceTypeDTO ml = MonitoredServiceTypeDTO.newBuilder()
                .setServiceName(serviceName)
                .setTenantId(tenant.toString())
                .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoredServiceTypeDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<MonitoredServiceTypeDTO> response = this.testRestTemplate
                .postForEntity("http://localhost:" + port + "/inventory/serviceTypes", request, MonitoredServiceTypeDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        MonitoredServiceTypeDTO saved = response.getBody();
        assertEquals(tenant.toString(), saved.getTenantId());
        assertEquals(serviceName, saved.getServiceName());
        return saved;
    }

    @Test
    void testGetAll() throws Exception {
        postMonitoredService();
        postMonitoredService();

        ResponseEntity<List> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/services", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List body = response.getBody();
        assertEquals(2, body.size());

        assertEquals(String.valueOf(savedIpInterfaceId), ((Map)body.get(0)).get("ipInterfaceId"));
        assertEquals(String.valueOf(savedIpInterfaceId), ((Map)body.get(1)).get("ipInterfaceId"));
    }

    @Test
    void testPost() throws Exception {
        postMonitoredService();
    }

    private MonitoredServiceDTO postMonitoredService() {
        UUID tenant = new UUID(10, 12);
        
        MonitoredServiceDTO ml = MonitoredServiceDTO.newBuilder()
            .setIpInterfaceId(savedIpInterfaceId)
            .setTenantId(tenant.toString())
            .setMonitoredServiceTypeId(savedMonitorServiceTypeId)
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoredServiceDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<MonitoredServiceDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/services", request, MonitoredServiceDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        MonitoredServiceDTO saved = response.getBody();
        assertEquals(tenant.toString(), saved.getTenantId());
        assertEquals(savedMonitorServiceTypeId, saved.getMonitoredServiceTypeId());
        return saved;
    }

    @Test
    void testUpdate() throws Exception {
        MonitoredServiceDTO ml = postMonitoredService();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoredServiceDTO> request = new HttpEntity<>(ml, headers);

        // Update
        ResponseEntity<MonitoredServiceDTO> putResponse = this.testRestTemplate
            .exchange("http://localhost:" + port + "/inventory/services", HttpMethod.PUT, request, MonitoredServiceDTO.class);
        assertEquals(HttpStatus.OK, putResponse.getStatusCode());

        // Check there is one database entry
        ResponseEntity<List> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/services", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List body = response.getBody();
        assertEquals(1, body.size());
    }

    @Test
    void testGet() throws Exception {
        MonitoredServiceDTO ml = postMonitoredService();

        ResponseEntity<MonitoredServiceDTO> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/services/" + ml.getId(), MonitoredServiceDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        MonitoredServiceDTO retrievedML = response.getBody();
        assertEquals(savedIpInterfaceId, retrievedML.getIpInterfaceId());
        assertEquals(savedMonitorServiceTypeId, retrievedML.getMonitoredServiceTypeId());
    }

    @Test
    void testGetNotFound() throws Exception {
        ResponseEntity<MonitoredServiceDTO> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/services/1", MonitoredServiceDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateBadId() throws Exception {
        MonitoredServiceDTO ml = postMonitoredService();

        MonitoredServiceDTO bad = MonitoredServiceDTO.newBuilder(ml)
                .setId(Long.MAX_VALUE)
                .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoredServiceDTO> request = new HttpEntity<>(bad, headers);

        ResponseEntity<MonitoredServiceDTO> response = this.testRestTemplate
            .exchange("http://localhost:" + port + "/inventory/services", HttpMethod.PUT, request, MonitoredServiceDTO.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testPostExistingId() throws Exception {
        MonitoredServiceDTO ml = postMonitoredService();

        MonitoredServiceDTO bad = MonitoredServiceDTO.newBuilder(ml)
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoredServiceDTO> request = new HttpEntity<>(bad, headers);

        ResponseEntity<MonitoredServiceDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/services", request, MonitoredServiceDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testPostBadIpInterfaceId() throws Exception {
        UUID tenant = new UUID(10, 12);

        MonitoredServiceDTO ml = MonitoredServiceDTO.newBuilder()
                .setIpInterfaceId(savedIpInterfaceId)
                .setTenantId(tenant.toString())
                .setMonitoredServiceTypeId(Long.MAX_VALUE)
                .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoredServiceDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<MonitoredServiceDTO> response = this.testRestTemplate
                .postForEntity("http://localhost:" + port + "/inventory/services", request, MonitoredServiceDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testPostBadMonitoredServiceTypeId() throws Exception {
        UUID tenant = new UUID(10, 12);

        MonitoredServiceDTO ml = MonitoredServiceDTO.newBuilder()
                .setIpInterfaceId(Long.MAX_VALUE)
                .setTenantId(tenant.toString())
                .setMonitoredServiceTypeId(savedMonitorServiceTypeId)
                .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoredServiceDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<MonitoredServiceDTO> response = this.testRestTemplate
                .postForEntity("http://localhost:" + port + "/inventory/services", request, MonitoredServiceDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testPostBadTenantId() throws Exception {
        MonitoredServiceDTO ml = MonitoredServiceDTO.newBuilder()
            .setIpInterfaceId(savedIpInterfaceId)
            .setTenantId("0000")
            .setMonitoredServiceTypeId(savedMonitorServiceTypeId)
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoredServiceDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<MonitoredServiceDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/services", request, MonitoredServiceDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
