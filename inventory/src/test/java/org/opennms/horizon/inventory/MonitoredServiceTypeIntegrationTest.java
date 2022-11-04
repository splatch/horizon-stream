package org.opennms.horizon.inventory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.opennms.horizon.inventory.dto.MonitoredServiceTypeDTO;
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
class MonitoredServiceTypeIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.5-alpine")
        .withDatabaseName("inventory").withUsername("inventory")
        .withPassword("password").withExposedPorts(5432);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MonitoredServiceTypeRepository monitoredServiceTypeRepository;

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
        monitoredServiceTypeRepository.deleteAll();
    }

    @Test
    @Order(1)
    void testGetAll() throws Exception {
        String serviceName = "not here at all";
        postMonitoredServiceTypes(serviceName);
        postMonitoredServiceTypes(serviceName);

        ResponseEntity<List> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/serviceTypes", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List body = response.getBody();
        assertEquals(2, body.size());

        assertEquals(serviceName, ((Map)body.get(0)).get("serviceName"));
        assertEquals(serviceName, ((Map)body.get(1)).get("serviceName"));
    }

    @Test
    @Order(2)
    void testPost() throws Exception {
        String serviceName = "not here";
        postMonitoredServiceTypes(serviceName);
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
    @Order(4)
    void testUpdate() throws Exception {
        String serviceName = "not here";
        MonitoredServiceTypeDTO ml = postMonitoredServiceTypes(serviceName);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoredServiceTypeDTO> request = new HttpEntity<>(ml, headers);

        // Update
        ResponseEntity<MonitoredServiceTypeDTO> putResponse = this.testRestTemplate
            .exchange("http://localhost:" + port + "/inventory/serviceTypes", HttpMethod.PUT, request, MonitoredServiceTypeDTO.class);
        assertEquals(HttpStatus.OK, putResponse.getStatusCode());

        // Check there is one database entry
        ResponseEntity<List> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/serviceTypes", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List body = response.getBody();
        assertEquals(1, body.size());
    }

    @Test
    @Order(5)
    void testGet() throws Exception {
        String serviceName = "not here";
        MonitoredServiceTypeDTO ml = postMonitoredServiceTypes(serviceName);

        ResponseEntity<MonitoredServiceTypeDTO> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/serviceTypes/" + ml.getId(), MonitoredServiceTypeDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        MonitoredServiceTypeDTO retrievedML = response.getBody();
        assertEquals(serviceName, retrievedML.getServiceName());
    }

    @Test
    @Order(6)
    void testGetNotFound() throws Exception {
        ResponseEntity<MonitoredServiceTypeDTO> response = this.testRestTemplate
            .getForEntity("http://localhost:" + port + "/inventory/serviceTypes/1", MonitoredServiceTypeDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(7)
    void testUpdateBadId() throws Exception {
        String serviceName = "not here";
        MonitoredServiceTypeDTO ml = postMonitoredServiceTypes(serviceName);

        MonitoredServiceTypeDTO bad = MonitoredServiceTypeDTO.newBuilder(ml)
                .setId(Long.MAX_VALUE)
                .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoredServiceTypeDTO> request = new HttpEntity<>(bad, headers);

        ResponseEntity<MonitoredServiceTypeDTO> response = this.testRestTemplate
            .exchange("http://localhost:" + port + "/inventory/serviceTypes", HttpMethod.PUT, request, MonitoredServiceTypeDTO.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(8)
    void testPostExistingId() throws Exception {
        String serviceName = "not here";
        MonitoredServiceTypeDTO ml = postMonitoredServiceTypes(serviceName);

        MonitoredServiceTypeDTO bad = MonitoredServiceTypeDTO.newBuilder(ml)
            .setServiceName("something else")
            .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MonitoredServiceTypeDTO> request = new HttpEntity<>(bad, headers);

        ResponseEntity<MonitoredServiceTypeDTO> response = this.testRestTemplate
            .postForEntity("http://localhost:" + port + "/inventory/serviceTypes", request, MonitoredServiceTypeDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
