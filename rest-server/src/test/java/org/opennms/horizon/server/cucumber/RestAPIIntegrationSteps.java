/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2022 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2022 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.horizon.server.cucumber;

import static graphql.Assert.assertNotNull;
import static graphql.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import org.opennms.horizon.server.model.dto.MonitoringLocationDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestAPIIntegrationSteps extends IntegrationTestBase{
    private ObjectMapper mapper = new ObjectMapper();
    private MonitoringLocationDto location1;
    private MonitoringLocationDto location2;
    @Given("REST server url in system property {string}")
    public void restServerUrlInSystemProperty(String apiUrl) {
        this.apiUrl = System.getProperty(apiUrl);
    }

    @Given("Keycloak auth server url in system property {string}")
    public void keycloakAuthServerUrlInSystemProperty(String authUrl) {
        this.keycloakAuthUrl = System.getProperty(authUrl);
    }

    @Given("Keycloak admin user {string} with password {string}")
    public void keycloakAdminUserWithPassword(String user, String password) {
        this.keycloakAdminUser = user;
        this.keycloakAdminPassword = password;
    }

    @Given("Keycloak master realm client-id {string}")
    public void keycloakMasterRealmClientId(String clientId) {
        adminClientId = clientId;
    }

    @Then("Initial Keycloak utils")
    public void initialKeycloakAdminClient() {
        log.info("Initial keycloak utils, {}", this);
        initKeycloakUtils();
    }

    @Then("Create Keycloak realm {string}")
    public void createKeycloakRealm(String realm) {
        this.testRealm = realm;
        keyCloakUtils.createRealm(realm, keycloakAuthUrl);
    }

    @Then("Add roles")
    public void addRoles(List<String> roles) {
        keyCloakUtils.addRoles(testRealm, roles);
    }


    @Then("Add admin user {string} with password {string} and role {string}")
    public void addAdminUserWithPassword(String username, String password, String role) {
        keyCloakUtils.addUser(testRealm, username, password, role);
        adminUser = new User(username, password);
    }

    @Then("Add regular user {string} with password {string} and role {string}")
    public void addRegularUserWithPassword(String user, String pwd, String role) {
        keyCloakUtils.addUser(testRealm, user, pwd, role);
        testUser = new User(user, pwd);
        keyCloakUtils.close();
    }
    @Then("Admin user can create an access token")
    public void adminUserCanCreateAnAccessToken() throws MalformedURLException {
        assertTrue(login(adminUser.getUsername(), adminUser.getPassword()));
    }

    @Then("Admin user can create new location")
    public void adminUserCanCreateNewLocation() {

        ObjectNode data = mapper.createObjectNode();
        data.put("location", "Default");
        data.put("monitoringArea", "localhost");

        ObjectNode data2 = mapper.createObjectNode();
        data2.put("location", "test-location");
        data2.put("monitoringArea", "office-network");

        Response response = postRequest(PATH_LOCATIONS, data);
        assertEquals(200, response.statusCode());
        location1 = response.as(MonitoringLocationDto.class);
        assertNotNull(location1);
        assertEquals("Default", location1.getLocation());
        assertEquals("localhost", location1.getMonitoringArea());
        Response response2 = postRequest(PATH_LOCATIONS, data2);
        assertEquals(200, response2.statusCode());
        location2 = response2.as(MonitoringLocationDto.class);
        assertNotNull(location2);
        assertEquals("test-location", location2.getLocation());
        assertEquals("office-network", location2.getMonitoringArea());
    }

    @Then("Admin user can list location")
    public void adminUserCanListLocation() {
        Response response = getRequest(PATH_LOCATIONS);
        assertEquals(200, response.statusCode());
        List<MonitoringLocationDto> result = response.jsonPath().getList(".", MonitoringLocationDto.class);
        assertEquals(2, result.size());
    }

    @Then("Admin user can get location by ID")
    public void adminUserCanGetLocationByID() {
        Response response = getRequest(PATH_LOCATIONS + "/" + location1.getId());
        assertEquals(200, response.statusCode());
        MonitoringLocationDto result = response.as(MonitoringLocationDto.class);
        assertNotNull(result);
        assertEquals("Default", result.getLocation());
    }

    @Then("Admin user can update the location")
    public void adminUserCanUpdateTheLocation() {
        location1.setMonitoringArea("updated_network");
        Response response = putRequest(PATH_LOCATIONS + "/" + location1.getId(), mapper.valueToTree(location1));
        assertEquals(200, response.statusCode());
        assertEquals(location1.getMonitoringArea(), response.jsonPath().get("monitoringArea"));
    }

    @Then("Admin user can delete the location by ID")
    public void adminUserCanDeleteTheLocationByID() {
        Response response = deleteRequest(PATH_LOCATIONS + "/" + location1.getId());
        assertEquals(204, response.statusCode());
        List <MonitoringLocationDto> list = getRequest(PATH_LOCATIONS).jsonPath().getList(".", MonitoringLocationDto.class);
        assertEquals(1, list.size());
    }
}
