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
import static graphql.Assert.assertNull;
import static graphql.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.opennms.horizon.server.model.dto.MonitoringLocationDto;
import org.opennms.horizon.server.model.dto.NodeDto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

public class NodeIntegrationSteps extends IntegrationTestBase {

    private static final String foreignID1 = "asdfasdf";
    private static final String foreignID2 = "sdfhsdfhfasdf";
    private MonitoringLocationDto location;
    private NodeDto node1;
    private NodeDto node2;

    @Given("Prepare node test with REST server url in system property {string}")
    public void prepareNodeTestWithRESTServerUrlInSystemProperty(String property) {
        apiUrl = System.getProperty(property);
    }

    @Given("Prepare node test with auth server url in system property {string}, realm {string} and client {string}")
    public void prepareNodeTestWithAuthServerUrlInSystemPropertyRealmAndClient(String authUrl, String realm, String clientId) {
        this.keycloakAuthUrl = System.getProperty(authUrl);
        this.testRealm = realm;
        this.clientId = clientId;
    }

    @Given("Prepare node test with admin user {string} with password {string}")
    public void prepareNodeTestWithAdminUserWithPassword(String username, String password) {
        this.adminUsername = username;
        this.adminPassword = password;
    }

    @Then("Admin user can login and generate an access token")
    public void adminUserCanLoginAndGenerateAnAccessToken() {
        assertTrue(login(adminUsername, adminPassword));
    }

    @Then("Admin user create a location")
    public void adminUserCreateALocation() {
        ObjectNode data = mapper.createObjectNode();
        data.put("location", "Default");
        data.put("monitoringArea", "localhost");
        Response response = postRequest(PATH_LOCATIONS, data);
        assertEquals(200, response.statusCode());
        location = response.as(MonitoringLocationDto.class);
        assertNotNull(location);
    }

    @Then("Admin use can create new node")
    public void adminUseCanCreateNewNode() {
        JsonNode tmpNode1 = createNodeDto(foreignID1, null, null);
        Response response = postRequest(PATH_NODS, tmpNode1);
        assertEquals(200, response.statusCode());
        node1 = response.as(NodeDto.class);
        assertNotNull(node1);
        JsonNode tmpNode2 = createNodeDto(foreignID2, node1.getId(), location.getId());
        Response response2 = postRequest(PATH_NODS, tmpNode2);
        assertEquals(200, response.statusCode());
        node2 = response2.as(NodeDto.class);
        assertNotNull(node2);
    }

    @Then("Admin user can list nodes")
    public void adminUserCanListNodes() {
        Response response = getRequest(PATH_NODS);
        assertEquals(200, response.statusCode());
        List<NodeDto> result = response.jsonPath().getList(".", NodeDto.class);
        assertEquals(2, result.size());
        assertEquals(node1.getId(), result.get(0).getId());
        assertEquals(location.getId(), result.get(0).getLocationId());
        assertNull(result.get(0).getParentId());
        assertEquals(foreignID1, result.get(0).getForeignId());

        assertEquals(node2.getId(), result.get(1).getId());
        assertEquals(location.getId(), result.get(1).getLocationId());
        assertEquals(node1.getId(), result.get(1).getParentId());
        assertEquals(foreignID2, result.get(1).getForeignId());
    }

    @Then("Admin user can get node by ID")
    public void adminUserCanGetNodeByID() {
        Response response = getRequest(PATH_NODS + "/" + node2.getId());
        assertEquals(200, response.statusCode());
        NodeDto result = response.as(NodeDto.class);
        assertNotNull(result);
        assertEquals(node2.getId(), result.getId());
        assertEquals(foreignID2, result.getForeignId());
        assertEquals(location.getId(), result.getLocationId());
        assertEquals(node1.getId(), result.getParentId());
    }

    @Then("Admin user can update node")
    public void adminUserCanUpdateNode() {
        String newLabel = "updated_label";
        node2.setLabel(newLabel);
        Response response = putRequest(PATH_NODS + "/" + node2.getId(), mapper.valueToTree(node2));
        assertEquals(200, response.statusCode());
        NodeDto result = response.as(NodeDto.class);
        assertNotNull(result);
        assertEquals(newLabel, newLabel);
    }

    @Then("Admin user can delete a node")
    public void adminUserCanDeleteANode() {
        Response response = deleteRequest(PATH_NODS + "/" + node2.getId());
        assertEquals(204, response.statusCode());
        List<NodeDto> list = getRequest(PATH_NODS).jsonPath().getList(".", NodeDto.class);
        assertEquals(1, list.size());
    }

    @Then("Normal user {string} and password {string} login to test node api")
    public void normalAndPasswordLoginToTestNodeApi(String username, String password) {
        assertTrue(login(username, password));
    }

    @Then("Normal user can list nodes")
    public void normalUserCanListNodes() {
        Response response = getRequest(PATH_NODS);
        assertEquals(200, response.statusCode());
        List<NodeDto> result = response.jsonPath().getList(".", NodeDto.class);
        assertEquals(1, result.size());
        assertEquals(foreignID1, result.get(0).getForeignId());
        node1 = result.get(0);
    }

    @Then("Normal user can get node by ID")
    public void normalUserCanGetNodeByID() {
        Response response = getRequest(PATH_NODS + "/" + node1.getId());
        assertEquals(200, response.statusCode());
        NodeDto result = response.as(NodeDto.class);
        assertNotNull(result);
        assertEquals(foreignID1, result.getForeignId());
    }

    @Then("Normal user is not allowed to create new node")
    public void normalUserIsNotAllowedToCreateNewNode() {
        JsonNode tmpNode = createNodeDto(foreignID2, null, null);
        Response response = postRequest(PATH_NODS, tmpNode);
        assertEquals(403, response.statusCode());
    }

    @Then("Normal user is not allowed to update a node")
    public void normalUserIsNotAllowedToUpdateANode() {
        node1.setLabel("new_label");
        Response response = putRequest(PATH_NODS + "/" + node1.getId(), mapper.valueToTree(node1));
        assertEquals(403, response.statusCode());
    }

    @Then("Normal user is not allowed to delete a node")
    public void normalUserIsNotAllowedToDeleteANode() {
        Response response = deleteRequest(PATH_NODS + "/" + node1.getId());
        assertEquals(403, response.statusCode());
    }

    @Then("Without correct token user can't access node endpoint")
    public void withoutInCorrectTokenUserCanTAccessNodeEndpoint() {
        accessToken = "Bearer invalid_token";
        Response response = getRequest(PATH_NODS);
        assertEquals(401, response.statusCode());
    }

    private JsonNode createNodeDto(String foreignID, Long parentId, Long locationId) {
        NodeDto node = new NodeDto();
        node.setType("A");
        node.setForeignId(foreignID);
        node.setSysOid("os_id");
        node.setOperatingSystem("MacBook Pro");
        node.setSysDescription("MacbookPro with M1 Pro processor");
        node.setSysLocation("Kanata office");
        node.setLabel("YangLi_working_laptop");
        node.setLabelSource("H");
        node.setNetBiosName("BiosName");
        node.setDomainName("test.com");
        node.setOperatingSystem("Mac OS");
        node.setForeignSource("Opennms");
        node.setLocationId(locationId);
        node.setParentId(parentId);
        return mapper.valueToTree(node);
    }
}
