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
import static org.opennms.horizon.server.cucumber.APIClientSteps.PATH_LOCATIONS;

import java.util.List;

import org.opennms.horizon.server.model.dto.MonitoringLocationDto;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocationEndpointsIntegrationSteps {
    APIClientSteps apiClient;
    private MonitoringLocationDto location1;
    private MonitoringLocationDto location2;
    private List<MonitoringLocationDto> result;

    public LocationEndpointsIntegrationSteps(APIClientSteps apiClient) {
        this.apiClient = apiClient;
    }

    @Then("Admin user can create an access token")
    public void adminUserCanCreateAnAccessToken() {
        assertTrue(apiClient.login(apiClient.getAdminUsername(), apiClient.getAdminPassword()));
    }

    @Then("Admin user can create new location")
    public void adminUserCanCreateNewLocation() {

        ObjectNode data = apiClient.createJsonNode();
        data.put("location", "Default");
        data.put("monitoringArea", "localhost");

        ObjectNode data2 = apiClient.createJsonNode();
        data2.put("location", "test-location");
        data2.put("monitoringArea", "office-network");

        Response response = apiClient.postRequest(PATH_LOCATIONS, data);
        assertEquals(200, response.statusCode());
        location1 = response.as(MonitoringLocationDto.class);
        assertNotNull(location1);
        assertEquals("Default", location1.getLocation());
        assertEquals("localhost", location1.getMonitoringArea());
        Response response2 = apiClient.postRequest(PATH_LOCATIONS, data2);
        assertEquals(200, response2.statusCode());
        location2 = response2.as(MonitoringLocationDto.class);
        assertNotNull(location2);
        assertEquals("test-location", location2.getLocation());
        assertEquals("office-network", location2.getMonitoringArea());
    }

    @Then("Admin user can list location")
    public void adminUserCanListLocation() {
        Response response = apiClient.getRequest(PATH_LOCATIONS);
        assertEquals(200, response.statusCode());
        result = response.jsonPath().getList(".", MonitoringLocationDto.class);
        assertEquals(2, result.size());

    }

    @Then("Verify location ids in the list")
    public void verifyLocationIds() {
        assertEquals(location1.getId(), result.get(0).getId());
        assertEquals(location2.getId(), result.get(1).getId());
    }

    @Then("Admin user can get location by ID")
    public void adminUserCanGetLocationByID() {
        Response response = apiClient.getRequest(PATH_LOCATIONS + "/" + location1.getId());
        assertEquals(200, response.statusCode());
        MonitoringLocationDto result = response.as(MonitoringLocationDto.class);
        assertNotNull(result);
        assertEquals(location1.getLocation(), result.getLocation());
    }

    @Then("Admin user can update the location")
    public void adminUserCanUpdateTheLocation() {
        location1.setMonitoringArea("updated_network");
        Response response = apiClient.putRequest(PATH_LOCATIONS + "/" + location1.getId(), apiClient.objectToJson(location1));
        assertEquals(200, response.statusCode());
        assertEquals(location1.getMonitoringArea(), response.jsonPath().get("monitoringArea"));
    }

    @Then("Admin user can delete the location by ID")
    public void adminUserCanDeleteTheLocationByID() {
        Response response = apiClient.deleteRequest(PATH_LOCATIONS + "/" + location1.getId());
        assertEquals(204, response.statusCode());
        List <MonitoringLocationDto> list = apiClient.getRequest(PATH_LOCATIONS).jsonPath().getList(".", MonitoringLocationDto.class);
        assertEquals(1, list.size());
    }

    @Then("Normal user {string} with password {string} login to test location api")
    public void aNormalUserWithUsernameAndPassword(String username, String password) {
        assertTrue(apiClient.login(username, password));
    }

    @Then("Normal user can list location")
    public void normalUserCanListLocation() {
        Response response = apiClient.getRequest(PATH_LOCATIONS);
        assertEquals(200, response.statusCode());
        List<MonitoringLocationDto> result = response.jsonPath().getList(".", MonitoringLocationDto.class);
        assertEquals(1, result.size());
        location2 = result.get(0);
    }

    @Then("Normal user can get location by ID")
    public void normalUserCanGetLocationByID() {
        Response response = apiClient.getRequest(PATH_LOCATIONS + "/" + location2.getId());
        assertEquals(200, response.statusCode());
        MonitoringLocationDto result = response.as(MonitoringLocationDto.class);
        assertNotNull(result);
        assertEquals(location2.getLocation(), result.getLocation());
    }

    @Then("Normal user am not allowed to create new location")
    public void normalUserAmNotAllowedToCreateNewLocation() {
        ObjectNode data = apiClient.createJsonNode();
        data.put("location", "Default");
        data.put("monitoringArea", "localhost");
        Response response = apiClient.postRequest(PATH_LOCATIONS, data);
        assertEquals(403, response.statusCode());
    }

    @Then("Normal user am not allowed to update the location by ID")
    public void normalUserAmNotAllowedToUpdateTheLocationByID() {
        location2.setMonitoringArea("updated_network");
        Response response = apiClient.putRequest(PATH_LOCATIONS + "/" + location2.getId(), apiClient.objectToJson(location2));
        assertEquals(403, response.statusCode());
    }

    @Then("Normal user am not allowed to delete the location")
    public void normalUserAmNotAllowedToDeleteTheLocation() {
        Response response = apiClient.deleteRequest(PATH_LOCATIONS + "/" + location2.getId());
        assertEquals(403, response.statusCode());
    }

    @Then("Without correct token user can't access rest api")
    public void withoutInCorrectTokenUserCanTAccessRestApi() {
        apiClient.setAccessToken("Bearer invalid_token");
        Response response = apiClient.getRequest(PATH_LOCATIONS);
        assertEquals(401, response.statusCode());
        apiClient.cleanDB();
    }
}
