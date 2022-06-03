package org.opennms.horizon.server.cucumber;

import static graphql.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.opennms.horizon.server.cucumber.APIClientSteps.PATH_GRAPHQL;

import java.util.List;

import org.opennms.horizon.server.model.dto.MonitoringLocationDto;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
public class LocationGraphQLSteps {

    private MonitoringLocationDto location1;
    private MonitoringLocationDto location2;
    private APIClientSteps apiClient;

    public LocationGraphQLSteps(APIClientSteps apiClient) {
        this.apiClient = apiClient;
    }

    private MonitoringLocationDto generateLocation(Response response) {
        MonitoringLocationDto location = new MonitoringLocationDto();
        location.setId(response.jsonPath().getLong("data.addLocation.id"));
        location.setLocation(response.jsonPath().getString("data.addLocation.location"));
        location.setMonitoringArea(response.jsonPath().get("data.addLocation.monitoringArea"));
        return location;
    }

    @Then("Admin user can loging and create access token")
    public void adminUserCanLogingAndCreateAccessToken() {
        assertTrue(apiClient.login(apiClient.getAdminUsername(), apiClient.getAdminPassword()));
    }

    @Then("Admin user can create new locations")
    public void adminUserCanCreateNewLocations() {
        ObjectNode data = apiClient.createJsonNode();
        data.put("query", "mutation {addLocation(input: {location: \"graphql-test\", monitoringArea: \"localhost\"}) " +
                "{id  location monitoringArea}}");
        Response response = apiClient.postRequest(PATH_GRAPHQL, data);
        assertEquals(200, response.statusCode());
        location1 = generateLocation(response);
        assertEquals(1, location1.getId());
        assertEquals("graphql-test", location1.getLocation());
        assertEquals("localhost", location1.getMonitoringArea());

        ObjectNode data2 = apiClient.createJsonNode();
        data2.put("query", "mutation {addLocation(input: {location: \"graphql-test2\", monitoringArea: \"office-network\"}) " +
                "{id   location monitoringArea} }");
        Response response2 = apiClient.postRequest(PATH_GRAPHQL, data2);
        assertEquals(200, response2.statusCode());
        location2 = generateLocation(response2);
        assertEquals(2, location2.getId());
        assertEquals("graphql-test2", location2.getLocation());
        assertEquals("office-network", location2.getMonitoringArea());
    }

    @Then("Admin user can query locations")
    public void adminUserCanQueryLocations() {
        ObjectNode data = apiClient.createJsonNode();
        data.put("query", "{getAllLocations {id location monitoringArea}}");
        Response response = apiClient.postRequest(PATH_GRAPHQL, data);
        assertEquals(200, response.statusCode());
        assertEquals(2, response.jsonPath().getList("data.getAllLocations").size());
    }

    @Then("Admin user can query a location by ID")
    public void adminUserCanQueryALocationByID() {
        ObjectNode data = apiClient.createJsonNode();
        data.put("query", String.format("{getLocationById(id: %d){id location monitoringArea tags}}", location2.getId()));
        Response response = apiClient.postRequest(PATH_GRAPHQL, data);
        assertEquals(200, response.statusCode());
        assertEquals(location2.getId(), response.jsonPath().getLong("data.getLocationById.id"));
        assertEquals(location2.getLocation(), response.jsonPath().getString("data.getLocationById.location"));
        assertEquals(location2.getMonitoringArea(), response.jsonPath().getString("data.getLocationById.monitoringArea"));
    }

    @Then("Admin user can update a location")
    public void adminUserCanUpdateALocation() {
        ObjectNode data = apiClient.createJsonNode();
        data.put("query", String.format("mutation {updateLocation(input: {location: \"updated-location\"}, id: %d) ", location2.getId()) +
                "{id, location, monitoringArea}}");
        Response response = apiClient.postRequest(PATH_GRAPHQL, data);
        assertEquals(200, response.statusCode());
        assertEquals(location2.getId(), response.jsonPath().getLong("data.updateLocation.id"));
        assertEquals("updated-location", response.jsonPath().getString("data.updateLocation.location"));
        assertEquals(location2.getMonitoringArea(), response.jsonPath().getString("data.updateLocation.monitoringArea"));
    }

    @Then("Admin user can delete a location")
    public void adminUserCanDeleteALocation() {
        ObjectNode data = apiClient.createJsonNode();
        data.put("query", String.format("mutation {deleteLocation(id: %d)}", location2.getId()));
        Response response = apiClient.postRequest(PATH_GRAPHQL, data);
        assertEquals(200, response.statusCode());
        assertTrue(response.jsonPath().getBoolean("data.deleteLocation"));
    }

    @Then("Normal user {string} with password {string} login to test location graphql api")
    public void normalUserWithPasswordForLocationGraphqlTest(String user, String password) {
        assertTrue(apiClient.login(user, password));
    }

    @Then("Normal user can query locations")
    public void normalUserCanQueryLocations() {
        ObjectNode data = apiClient.createJsonNode();
        data.put("query", "{getAllLocations {id location monitoringArea}}");
        Response response =apiClient. postRequest(PATH_GRAPHQL, data);
        assertEquals(200, response.statusCode());
        List<MonitoringLocationDto> list = response.jsonPath().getList("data.getAllLocations", MonitoringLocationDto.class);
        assertEquals(1, list.size());
        location1 = list.get(0);
    }

    @Then("Normal user can query a location by ID")
    public void normalUserCanQueryALocationByID() {
        ObjectNode data =apiClient.createJsonNode();
        data.put("query", String.format("{getLocationById(id: %d){id location monitoringArea tags}}", location1.getId()));
        Response response = apiClient.postRequest(PATH_GRAPHQL, data);
        assertEquals(200, response.statusCode());
        assertEquals(location1.getId(), response.jsonPath().getLong("data.getLocationById.id"));
        assertEquals(location1.getLocation(), response.jsonPath().getString("data.getLocationById.location"));
        assertEquals(location1.getMonitoringArea(), response.jsonPath().getString("data.getLocationById.monitoringArea"));
    }

    @Then("Normal user am not allowed to create a location")
    public void normalUserAmNotAllowedToCreateALocation() {
        ObjectNode data = apiClient.createJsonNode();
        data.put("query", "mutation {addLocation(input: {location: \"graphql-test\", monitoringArea: \"localhost\"}) " +
                "{id  location monitoringArea}}");
        Response response = apiClient.postRequest(PATH_GRAPHQL, data);
        assertTrue(response.jsonPath().getString("errors[0].message").contains("Access is denied"));
    }

    @Then("Normal user not allowed to update a location")
    public void normalUserNotAllowedToUpdateALocation() {
        ObjectNode data = apiClient.createJsonNode();
        data.put("query", String.format("mutation {updateLocation(input: {location: \"updated-location\"}, id: %d) ", location1.getId()) +
                "{id, location, monitoringArea}}");
        Response response = apiClient.postRequest(PATH_GRAPHQL, data);
        assertTrue(response.jsonPath().getString("errors[0].message").contains("Access is denied"));
    }

    @Then("Normal user not allowed to delete a location")
    public void normalUserNotAllowedToDeleteALocation() {
        ObjectNode data = apiClient.createJsonNode();
        data.put("query", String.format("mutation {deleteLocation(id: %d)}", location1.getId()));
        Response response = apiClient.postRequest(PATH_GRAPHQL, data);
        assertTrue(response.jsonPath().getString("errors[0].message").contains("Access is denied"));
    }

    @Then("Without correct token user can't access graphql api")
    public void withoutCorrectTokenUserNotAllowedAccessGraphqlApi() {
        apiClient.setAccessToken("Bearer invalid_token");
        ObjectNode data = apiClient.createJsonNode();
        data.put("query", "{getAllLocations {id location monitoringArea}}");
        Response response = apiClient.postRequest(PATH_GRAPHQL, data);
        assertEquals(401, response.statusCode());
        apiClient.cleanDB();
    }
}
