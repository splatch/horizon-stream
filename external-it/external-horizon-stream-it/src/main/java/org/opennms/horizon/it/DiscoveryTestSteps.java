/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2023 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2023 The OpenNMS Group, Inc.
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
package org.opennms.horizon.it;

import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;
import org.opennms.horizon.it.gqlmodels.GQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.HttpHeaders;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.opennms.horizon.it.InventoryTestSteps.DEFAULT_HTTP_SOCKET_TIMEOUT;

public class DiscoveryTestSteps {

    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryTestSteps.class);

//========================================
// Variables
//----------------------------------------
    private Supplier<String> userAccessTokenSupplier;
    private Supplier<String> ingressUrlSupplier;


    //========================================
// Getters and Setters
//----------------------------------------
    public Supplier<String> getUserAccessTokenSupplier() {
        return userAccessTokenSupplier;
    }

    public void setUserAccessTokenSupplier(Supplier<String> userAccessTokenSupplier) {
        this.userAccessTokenSupplier = userAccessTokenSupplier;
    }

    public Supplier<String> getIngressUrlSupplier() {
        return ingressUrlSupplier;
    }

    public void setIngressUrlSupplier(Supplier<String> ingressUrlSupplier) {
        this.ingressUrlSupplier = ingressUrlSupplier;
    }

    //========================================
// Additional methods
//----------------------------------------
    public Response executePost(URL url, String accessToken, Object body) {
        RestAssuredConfig restAssuredConfig = createRestAssuredTestConfig();

        RequestSpecification requestSpecification =
            RestAssured
                .given()
                .config(restAssuredConfig)
            ;

        Response restAssuredResponse =
            requestSpecification
                .header(HttpHeaders.AUTHORIZATION, formatAuthorizationHeader(accessToken))
                .header(HTTP.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .body(body)
                .post(url)
                .thenReturn()
            ;

        return restAssuredResponse;
    }

    public String formatAuthorizationHeader(String token) {
        return "Bearer " + token;
    }

    public RestAssuredConfig createRestAssuredTestConfig() {
        return RestAssuredConfig.config()
            .sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation("SSL"))
            .httpClient(HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", DEFAULT_HTTP_SOCKET_TIMEOUT)
                .setParam("http.socket.timeout", DEFAULT_HTTP_SOCKET_TIMEOUT)
            );
    }

    public URL formatIngressUrl(String path) throws MalformedURLException {
        String baseUrl = ingressUrlSupplier.get();

        return new URL(new URL(baseUrl), path);
    }

    /**
     * Method to get the first node ID from DB
     * @return Node ID as Int first in the list
     * @throws MalformedURLException
     */
    public int getFirstNodeId() throws MalformedURLException {
        URL url = formatIngressUrl("/api/graphql");
        String accessToken = userAccessTokenSupplier.get();

        GQLQuery gqlQuery = new GQLQuery();
        gqlQuery.setQuery(GQLQueryConstants.GET_NODE_ID);

        Response response = executePost(url, accessToken, gqlQuery);

        JsonPath jsonPathEvaluator = response.jsonPath();
        LinkedHashMap lhm = jsonPathEvaluator.get("data");
        ArrayList map = (ArrayList) lhm.get("findAllNodes");
        LinkedHashMap nodesData = (LinkedHashMap) map.get(0);
        int id = (int) nodesData.get("id");

        return id;
    }

    /**
     * Method to check the expected status of the first node during the test
     * @param expectedStatus Expected status of the node
     * @return If the status is equals tot eh expected one
     * @throws MalformedURLException
     */
    public boolean checkTheStatusOfTheFirstNode(String expectedStatus) throws MalformedURLException {
        LOG.info("checkTheStatusOfTheNode");
        URL url = formatIngressUrl("/api/graphql");
        String accessToken = userAccessTokenSupplier.get();

        String queryList = GQLQueryConstants.LIST_NODE_METRICS;

        int nodeId = getFirstNodeId();

        Map<String, Object> queryVariables = Map.of("id", nodeId);

        GQLQuery gqlQuery = new GQLQuery();
        gqlQuery.setQuery(queryList);
        gqlQuery.setVariables(queryVariables);

        Response response = executePost(url, accessToken, gqlQuery);

        JsonPath jsonPathEvaluator = response.jsonPath();
        LinkedHashMap lhm = jsonPathEvaluator.get("data");
        LinkedHashMap map = (LinkedHashMap) lhm.get("nodeStatus");
        String currentStatus = (String) map.get("status");
        return currentStatus.equals(expectedStatus);
    }

//========================================
// Test Step Definitions
//----------------------------------------

    /**
     * This test step is to create a new discovery
     * @param name Name of the discovery
     * @param location Rather Default or behind the Minion
     * @param ipaddress Ip address or range of addresses separated by -
     * @param port port or array of ports separated by comma
     * @param communities Community string
     * @throws MalformedURLException
     */
    @Then("Add a new active discovery for the name {string} at location {string} with ip address {string} and port {int}, readCommunities {string}")
    public void addANewActiveDiscovery(String name, String location, String ipaddress, int port, String communities) throws MalformedURLException {
        LOG.info("Add a new discovery query execution steps");
        URL url = formatIngressUrl("/api/graphql");
        String accessToken = getUserAccessTokenSupplier().get();

        String query = String.format(GQLQueryConstants.ADD_DISCOVERY_QUERY, name, location, ipaddress, communities, port);

        GQLQuery gqlQuery = new GQLQuery();
        gqlQuery.setQuery(query);

        Response response = executePost(url, accessToken, gqlQuery);

        assertEquals("add-discovery query failed: status=" + response.getStatusCode() + "; body=" + response.getBody().asString(),
            200, response.getStatusCode());
    }

}
