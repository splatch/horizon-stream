package org.opennms.horizon.inventory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.opennms.horizon.inventory.dto.SnmpInterfaceDTO;
import org.opennms.horizon.inventory.dto.NodeDTO;
import org.opennms.horizon.inventory.repository.SnmpInterfaceRepository;
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
class SnmpInterfaceIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.5-alpine")
        .withDatabaseName("inventory").withUsername("inventory")
        .withPassword("password").withExposedPorts(5432);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private SnmpInterfaceRepository snmpInterfaceRepository;

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

    @BeforeEach
    public void setup() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());

        if (savedNodeId == -1) {
            NodeDTO dto = postNode("label");
            savedNodeId = dto.getId();
        }
    }

    @AfterEach
    public void teardown() {
        snmpInterfaceRepository.deleteAll();
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

    @Test
    void testGetAll() throws Exception {
        String ipAddress = "127.0.0.2";
        postSnmpInterface(ipAddress);
        postSnmpInterface(ipAddress);

        ResponseEntity<List> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/snmpInterfaces", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List body = response.getBody();
        assertEquals(2, body.size());

        assertEquals(ipAddress, ((Map)body.get(0)).get("ipAddress"));
        assertEquals(ipAddress, ((Map)body.get(1)).get("ipAddress"));
    }

    @Test
    void testPost() throws Exception {
        String ipAddress = "127.0.0.1";
        postSnmpInterface(ipAddress);
    }

    private SnmpInterfaceDTO postSnmpInterface(String ipAddress) {
        UUID tenant = new UUID(10, 12);
        
        SnmpInterfaceDTO ml = SnmpInterfaceDTO.newBuilder()
            .setIpAddress(ipAddress)
            .setTenantId(tenant.toString())
            .setNodeId(savedNodeId)
            .setIfIndex(4)
            .setIfDescr("Desc")
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<SnmpInterfaceDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<SnmpInterfaceDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/snmpInterfaces", request, SnmpInterfaceDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        SnmpInterfaceDTO saved = response.getBody();
        assertEquals(tenant.toString(), saved.getTenantId());
        assertEquals(ipAddress, saved.getIpAddress());
        return saved;
    }

    @Test
    void testUpdate() throws Exception {
        String ipAddress = "127.0.0.1";
        SnmpInterfaceDTO ml = postSnmpInterface(ipAddress);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<SnmpInterfaceDTO> request = new HttpEntity<>(ml, headers);

        // Update
        ResponseEntity<SnmpInterfaceDTO> putResponse = this.testRestTemplate
            .exchange("http://localhost:" + port + "/inventory/snmpInterfaces", HttpMethod.PUT, request, SnmpInterfaceDTO.class);
        assertEquals(HttpStatus.OK, putResponse.getStatusCode());

        // Check there is one database entry
        ResponseEntity<List> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/snmpInterfaces", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List body = response.getBody();
        assertEquals(1, body.size());
    }

    @Test
    void testGet() throws Exception {
        String ipAddress = "127.0.0.1";
        SnmpInterfaceDTO ml = postSnmpInterface(ipAddress);

        ResponseEntity<SnmpInterfaceDTO> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/snmpInterfaces/" + ml.getId(), SnmpInterfaceDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        SnmpInterfaceDTO retrievedML = response.getBody();
        assertEquals(ipAddress, retrievedML.getIpAddress());
    }

    @Test
    void testGetNotFound() throws Exception {
        ResponseEntity<SnmpInterfaceDTO> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/snmpInterfaces/1", SnmpInterfaceDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateBadId() throws Exception {
        String ipAddress = "127.0.0.1";
        SnmpInterfaceDTO ml = postSnmpInterface(ipAddress);

        SnmpInterfaceDTO bad = SnmpInterfaceDTO.newBuilder(ml)
                .setId(Long.MAX_VALUE)
                .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<SnmpInterfaceDTO> request = new HttpEntity<>(bad, headers);

        ResponseEntity<SnmpInterfaceDTO> response = this.testRestTemplate
            .exchange("http://localhost:" + port + "/inventory/snmpInterfaces", HttpMethod.PUT, request, SnmpInterfaceDTO.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testPostExistingId() throws Exception {
        String ipAddress = "127.0.0.1";
        SnmpInterfaceDTO ml = postSnmpInterface(ipAddress);

        SnmpInterfaceDTO bad = SnmpInterfaceDTO.newBuilder(ml)
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<SnmpInterfaceDTO> request = new HttpEntity<>(bad, headers);

        ResponseEntity<SnmpInterfaceDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/snmpInterfaces", request, SnmpInterfaceDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testPostBadNodeId() throws Exception {
        String ipAddress = "127.0.0.1";
        UUID tenant = new UUID(10, 12);

        SnmpInterfaceDTO ml = SnmpInterfaceDTO.newBuilder()
            .setIpAddress(ipAddress)
            .setTenantId(tenant.toString())
            .setNodeId(Long.MAX_VALUE)
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<SnmpInterfaceDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<SnmpInterfaceDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/snmpInterfaces", request, SnmpInterfaceDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testPostBadTenantId() throws Exception {
        String ipAddress = "127.0.0.1";

        SnmpInterfaceDTO ml = SnmpInterfaceDTO.newBuilder()
            .setIpAddress(ipAddress)
            .setTenantId("0000")
            .setNodeId(savedNodeId)
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<SnmpInterfaceDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<SnmpInterfaceDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/snmpInterfaces", request, SnmpInterfaceDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testPostBadIPAddress() throws Exception {
        String ipAddress = "bad";
        UUID tenant = new UUID(10, 12);

        SnmpInterfaceDTO ml = SnmpInterfaceDTO.newBuilder()
            .setIpAddress(ipAddress)
            .setTenantId(tenant.toString())
            .setNodeId(savedNodeId)
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<SnmpInterfaceDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<SnmpInterfaceDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/snmpInterfaces", request, SnmpInterfaceDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void postWithNullFields() {
        UUID tenant = new UUID(10, 12);

        SnmpInterfaceDTO ml = SnmpInterfaceDTO.newBuilder()
            .setTenantId(tenant.toString())
            .setNodeId(savedNodeId)
            .setIfIndex(4)
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<SnmpInterfaceDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<SnmpInterfaceDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/snmpInterfaces", request, SnmpInterfaceDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        SnmpInterfaceDTO saved = response.getBody();
        assertEquals(tenant.toString(), saved.getTenantId());
        assertEquals("", saved.getIpAddress());
        assertEquals("", saved.getIfDescr());
    }
}
