package org.opennms.horizon.inventory;

import org.junit.jupiter.api.*;
import org.opennms.horizon.inventory.dto.MonitoringLocationDTO;
import org.opennms.horizon.inventory.repository.MonitoringLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
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
class MonitoringLocationIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.5-alpine")
        .withDatabaseName("inventory").withUsername("inventory")
        .withPassword("password").withExposedPorts(5432);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MonitoringLocationRepository monitoringLocationRepository;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
            () -> String.format("jdbc:postgresql://localhost:%d/%s", postgres.getFirstMappedPort(), postgres.getDatabaseName()));
        registry.add("spring.datasource.username", () -> postgres.getUsername());
        registry.add("spring.datasource.password", () -> postgres.getPassword());
    }

    @BeforeEach
    public void setup() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @AfterEach
    public void teardown() {
        monitoringLocationRepository.deleteAll();
    }

    @Test
    @Order(1)
    void testGetAll() throws Exception {
        String firstLocation = "not here at all";
        postMonitoringLocations(firstLocation);
        String secondLocation = "not there";
        postMonitoringLocations(secondLocation);

        ResponseEntity<List> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/monitoring_location", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List body = response.getBody();
        assertEquals(2, body.size());

        assertEquals(firstLocation, ((Map)body.get(0)).get("location"));
        assertEquals(secondLocation, ((Map)body.get(1)).get("location"));
    }

    @Test
    @Order(2)
    void testPost() throws Exception {
        String location = "not here";
        postMonitoringLocations(location);
    }

    private MonitoringLocationDTO postMonitoringLocations(String location) {
        UUID tenant = new UUID(10, 12);
        MonitoringLocationDTO ml = MonitoringLocationDTO.newBuilder()
            .setLocation(location)
            .setTenantId(tenant.toString())
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringLocationDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<MonitoringLocationDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/monitoring_location", request, MonitoringLocationDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        MonitoringLocationDTO saved = response.getBody();
        assertEquals(tenant.toString(), saved.getTenantId());
        assertEquals(location, saved.getLocation());
        return saved;
    }

    @Test
    @Order(3)
    void testViolateUniqueConstraint() throws Exception {
        String location = "not anywhere";
        postMonitoringLocations(location);

        // location must be unique
        UUID tenant = new UUID(10, 12);
        MonitoringLocationDTO ml = MonitoringLocationDTO.newBuilder()
            .setLocation(location)
            .setTenantId(tenant.toString())
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringLocationDTO> request = new HttpEntity<>(ml, headers);

        ResponseEntity<MonitoringLocationDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/monitoring_location", request, MonitoringLocationDTO.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(4)
    void testUpdate() throws Exception {
        String location = "not here";
        MonitoringLocationDTO ml = postMonitoringLocations(location);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringLocationDTO> request = new HttpEntity<>(ml, headers);

        // Update
        ResponseEntity<MonitoringLocationDTO> putResponse = this.testRestTemplate
            .exchange("http://localhost:" + port + "/inventory/monitoring_location", HttpMethod.PUT, request, MonitoringLocationDTO.class);
        assertEquals(HttpStatus.OK, putResponse.getStatusCode());

        // Check there is one database entry
        ResponseEntity<List> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/monitoring_location", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List body = response.getBody();
        assertEquals(1, body.size());
    }

    @Test
    @Order(5)
    void testGet() throws Exception {
        String location = "not here";
        MonitoringLocationDTO ml = postMonitoringLocations(location);

        ResponseEntity<MonitoringLocationDTO> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/monitoring_location/" + ml.getId(), MonitoringLocationDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        MonitoringLocationDTO retrievedML = response.getBody();
        assertEquals(location, retrievedML.getLocation());
    }

    @Test
    @Order(6)
    void testGetNotFound() throws Exception {
        ResponseEntity<MonitoringLocationDTO> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/monitoring_location/1", MonitoringLocationDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(7)
    void testUpdateBadId() throws Exception {
        String location = "not here";
        MonitoringLocationDTO ml = postMonitoringLocations(location);

        MonitoringLocationDTO bad = MonitoringLocationDTO.newBuilder(ml)
                .setId(Long.MAX_VALUE)
                .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringLocationDTO> request = new HttpEntity<>(bad, headers);

        ResponseEntity<MonitoringLocationDTO> response = this.testRestTemplate
            .exchange("http://localhost:" + port + "/inventory/monitoring_location", HttpMethod.PUT, request, MonitoringLocationDTO.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(8)
    void testPostExistingId() throws Exception {
        String location = "not here";
        MonitoringLocationDTO ml = postMonitoringLocations(location);

        MonitoringLocationDTO bad = MonitoringLocationDTO.newBuilder(ml)
            .setLocation("something else")
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringLocationDTO> request = new HttpEntity<>(bad, headers);

        ResponseEntity<MonitoringLocationDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/monitoring_location", request, MonitoringLocationDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
