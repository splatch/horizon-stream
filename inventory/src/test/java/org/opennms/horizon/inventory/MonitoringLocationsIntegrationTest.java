package org.opennms.horizon.inventory;

import org.junit.jupiter.api.*;
import org.opennms.horizon.inventory.model.MonitoringLocations;
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
class MonitoringLocationsIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.5-alpine")
        .withDatabaseName("inventory").withUsername("inventory")
        .withPassword("password").withExposedPorts(5432);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TestRestTemplate testRestTemplate;

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
        this.testRestTemplate
            .delete("http://localhost:" + port + "/inventory/monitoring_locations");
    }

    @Test
    @Order(1)
    void testGetAll() throws Exception {
        String firstLocation = "not here at all";
        postMonitoringLocations(firstLocation);
        String secondLocation = "not there";
        postMonitoringLocations(secondLocation);

        ResponseEntity<List> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/monitoring_locations", List.class);

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

    private MonitoringLocations postMonitoringLocations(String location) {
        MonitoringLocations ml = new MonitoringLocations();
        ml.setLocation(location);
        UUID tenant = new UUID(10, 12);
        ml.setTenant_id(tenant);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringLocations> request = new HttpEntity<>(ml, headers);

        ResponseEntity<MonitoringLocations> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/monitoring_locations", request, MonitoringLocations.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        MonitoringLocations saved = response.getBody();
        assertEquals(tenant, saved.getTenant_id());
        assertEquals(location, saved.getLocation());
        return saved;
    }

    @Test
    @Order(3)
    void testViolateUniqueConstraint() throws Exception {
        String location = "not anywhere";
        postMonitoringLocations(location);

        // location must be unique
        MonitoringLocations ml = new MonitoringLocations();
        ml.setLocation(location);
        UUID tenant = new UUID(10, 12);
        ml.setTenant_id(tenant);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringLocations> request = new HttpEntity<>(ml, headers);

        ResponseEntity<MonitoringLocations> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/monitoring_locations", request, MonitoringLocations.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(4)
    void testUpdate() throws Exception {
        String location = "not here";
        MonitoringLocations ml = postMonitoringLocations(location);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringLocations> request = new HttpEntity<>(ml, headers);

        // Update
        ResponseEntity<MonitoringLocations> putResponse = this.testRestTemplate
            .exchange("http://localhost:" + port + "/inventory/monitoring_locations", HttpMethod.PUT, request, MonitoringLocations.class);
        assertEquals(HttpStatus.OK, putResponse.getStatusCode());

        // Check there is one database entry
        ResponseEntity<List> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/monitoring_locations", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List body = response.getBody();
        assertEquals(1, body.size());
    }

    @Test
    @Order(5)
    void testGet() throws Exception {
        String location = "not here";
        MonitoringLocations ml = postMonitoringLocations(location);

        ResponseEntity<MonitoringLocations> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/monitoring_locations/" + ml.getId(), MonitoringLocations.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        MonitoringLocations retrievedML = response.getBody();
        assertEquals(location, retrievedML.getLocation());
    }

    @Test
    @Order(6)
    void testGetNotFound() throws Exception {
        ResponseEntity<MonitoringLocations> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/monitoring_locations/1", MonitoringLocations.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(7)
    void testUpdateBadId() throws Exception {
        String location = "not here";
        MonitoringLocations ml = postMonitoringLocations(location);
        ml.setId(Long.MAX_VALUE);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringLocations> request = new HttpEntity<>(ml, headers);

        ResponseEntity<MonitoringLocations> response = this.testRestTemplate
            .exchange("http://localhost:" + port + "/inventory/monitoring_locations", HttpMethod.PUT, request, MonitoringLocations.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(8)
    void testPostExistingId() throws Exception {
        String location = "not here";
        MonitoringLocations ml = postMonitoringLocations(location);

        ml.setLocation("something else");

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoringLocations> request = new HttpEntity<>(ml, headers);

        ResponseEntity<MonitoringLocations> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/monitoring_locations", request, MonitoringLocations.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
