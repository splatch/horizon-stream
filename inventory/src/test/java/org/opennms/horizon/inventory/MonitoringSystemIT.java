package org.opennms.horizon.inventory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opennms.horizon.inventory.dto.MonitoringLocationDTO;
import org.opennms.horizon.inventory.dto.MonitoringSystemDTO;
import org.opennms.horizon.inventory.dto.NodeDTO;
import org.opennms.horizon.inventory.repository.MonitoringSystemRepository;
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
class MonitoringSystemIT {

    public static final String SYS_ID = "SYS_ID";
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.5-alpine")
        .withDatabaseName("inventory").withUsername("inventory")
        .withPassword("password").withExposedPorts(5432);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MonitoringSystemRepository monitoringSystemRepository;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
            () -> String.format("jdbc:postgresql://localhost:%d/%s", postgres.getFirstMappedPort(), postgres.getDatabaseName()));
        registry.add("spring.datasource.username", () -> postgres.getUsername());
        registry.add("spring.datasource.password", () -> postgres.getPassword());
    }

    public static long savedMonitoringLocationId = -1;

    @BeforeEach
    public void setup() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());

        if (savedMonitoringLocationId == -1) {
            MonitoringLocationDTO dto = postMonitoringLocation("location");
            savedMonitoringLocationId = dto.getId();
        }
    }

    @AfterEach
    public void teardown() {
        monitoringSystemRepository.deleteAll();
    }

    private MonitoringLocationDTO postMonitoringLocation(String location) {
        UUID tenant = new UUID(10, 12);
        MonitoringLocationDTO ml = MonitoringLocationDTO.newBuilder()
            .setLocation(location)
            .setTenantId(tenant.toString())
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringLocationDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<MonitoringLocationDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/locations", request, MonitoringLocationDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        MonitoringLocationDTO saved = response.getBody();
        assertEquals(tenant.toString(), saved.getTenantId());
        assertEquals(location, saved.getLocation());
        return saved;
    }

    @Test
    void testGetAll() throws Exception {
        postMonitoringSystem();
        postMonitoringSystem();

        ResponseEntity<List> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/monitoringSystems", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List body = response.getBody();
        assertEquals(2, body.size());

        assertEquals(SYS_ID, ((Map)body.get(0)).get("systemId"));
        assertEquals(SYS_ID, ((Map)body.get(1)).get("systemId"));
    }

    @Test
    void testPost() throws Exception {
        postMonitoringSystem();
    }

    private MonitoringSystemDTO postMonitoringSystem() {
        UUID tenant = new UUID(10, 12);
        
        MonitoringSystemDTO ml = MonitoringSystemDTO.newBuilder()
            .setTenantId(tenant.toString())
            .setSystemId(SYS_ID)
            .setLabel("LABEL")
            .setMonitoringLocationId(savedMonitoringLocationId)
            .setLastCheckedIn("2022-11-03T14:34:05.542488")
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringSystemDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<MonitoringSystemDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/monitoringSystems", request, MonitoringSystemDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        MonitoringSystemDTO saved = response.getBody();
        assertEquals(tenant.toString(), saved.getTenantId());
        assertEquals(SYS_ID, saved.getSystemId());
        return saved;
    }

    @Test
    void testUpdate() throws Exception {
        MonitoringSystemDTO ml = postMonitoringSystem();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringSystemDTO> request = new HttpEntity<>(ml, headers);

        // Update
        ResponseEntity<MonitoringSystemDTO> putResponse = this.testRestTemplate
            .exchange("http://localhost:" + port + "/inventory/monitoringSystems", HttpMethod.PUT, request, MonitoringSystemDTO.class);
        assertEquals(HttpStatus.OK, putResponse.getStatusCode());

        // Check there is one database entry
        ResponseEntity<List> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/monitoringSystems", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List body = response.getBody();
        assertEquals(1, body.size());
    }

    @Test
    void testGet() throws Exception {
        MonitoringSystemDTO ml = postMonitoringSystem();

        ResponseEntity<MonitoringSystemDTO> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/monitoringSystems/" + ml.getId(), MonitoringSystemDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        MonitoringSystemDTO retrievedML = response.getBody();
        assertEquals(SYS_ID, retrievedML.getSystemId());
    }

    @Test
    void testGetNotFound() throws Exception {
        ResponseEntity<MonitoringSystemDTO> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/monitoringSystems/1", MonitoringSystemDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateBadId() throws Exception {
        MonitoringSystemDTO ml = postMonitoringSystem();

        MonitoringSystemDTO bad = MonitoringSystemDTO.newBuilder(ml)
                .setId(Long.MAX_VALUE)
                .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringSystemDTO> request = new HttpEntity<>(bad, headers);

        ResponseEntity<MonitoringSystemDTO> response = this.testRestTemplate
            .exchange("http://localhost:" + port + "/inventory/monitoringSystems", HttpMethod.PUT, request, MonitoringSystemDTO.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testPostExistingId() throws Exception {
        MonitoringSystemDTO ml = postMonitoringSystem();

        MonitoringSystemDTO bad = MonitoringSystemDTO.newBuilder(ml)
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringSystemDTO> request = new HttpEntity<>(bad, headers);

        ResponseEntity<MonitoringSystemDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/monitoringSystems", request, MonitoringSystemDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testPostBadMonitoringLocationId() throws Exception {
        UUID tenant = new UUID(10, 12);

        MonitoringSystemDTO ml = MonitoringSystemDTO.newBuilder()
            .setTenantId(tenant.toString())
            .setSystemId(SYS_ID)
            .setLabel("LABEL")
            .setMonitoringLocationId(Long.MAX_VALUE)
            .setLastCheckedIn("2022-11-03T14:34:05.542488")
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringSystemDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<MonitoringSystemDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/monitoringSystems", request, MonitoringSystemDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testPostBadTenantId() throws Exception {
        MonitoringSystemDTO ml = MonitoringSystemDTO.newBuilder()
            .setTenantId("0000")
            .setSystemId(SYS_ID)
            .setLabel("LABEL")
            .setMonitoringLocationId(savedMonitoringLocationId)
            .setLastCheckedIn("2022-11-03T14:34:05.542488")
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringSystemDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<MonitoringSystemDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/monitoringSystems", request, MonitoringSystemDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
