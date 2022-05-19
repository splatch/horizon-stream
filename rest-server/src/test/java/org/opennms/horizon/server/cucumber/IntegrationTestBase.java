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

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.opennms.horizon.server.security.KeyCloakUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

abstract class IntegrationTestBase {

    private static final String LOGIN_DATA_TEMPLATE = "client_id=%s&username=%s&password=%s&grant_type=password";
    private static final String LOGIN_URL_TEMPLATE = "%s/realms/%s/protocol/openid-connect/token";
    protected static final String PATH_LOCATIONS = "/locations";

    protected String apiUrl;
    protected String keycloakAuthUrl;
    protected KeyCloakUtils keyCloakUtils;
    protected String keycloakAdminUser;
    protected String keycloakAdminPassword;
    protected String adminClientId;
    protected String testRealm;
    protected User adminUser;
    protected User testUser;
    protected String accessToken;

    protected void initKeycloakUtils() {
        ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
        clientBuilder.connectionPoolSize(5);
        clientBuilder.connectionCheckoutTimeout(5, TimeUnit.MINUTES);
        KeycloakBuilder kb = KeycloakBuilder.builder();
        kb.serverUrl(keycloakAuthUrl)
                .grantType(OAuth2Constants.PASSWORD)
                .realm("master")
                .clientId(adminClientId)
                .username(keycloakAdminUser)
                .password(keycloakAdminPassword)
                .resteasyClient(clientBuilder.build());
        Keycloak keycloak = kb.build();
        keyCloakUtils = new KeyCloakUtils(keycloak);
    }

    protected void closeKeycloak() {
        keyCloakUtils.close();
    }

    protected static class User {
        private String username;
        private String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    protected boolean login(String user, String password) throws MalformedURLException {
       String postData = String.format(LOGIN_DATA_TEMPLATE, adminClientId, user, password);

        Response response = given()
                .header("Accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(postData)
                .when()
                .post(String.format(LOGIN_URL_TEMPLATE, keycloakAuthUrl, testRealm))
                .then().extract().response();

        assertEquals(200, response.statusCode());
        accessToken = "Bearer " + response.jsonPath().get("access_token");
        return StringUtils.hasLength(accessToken);
    }

    protected Response postRequest(String path, JsonNode data) {
        return given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .baseUri(apiUrl)
                .body(data)
                .when()
                .post(path).then().extract().response();
    }

    protected Response getRequest(String path) {
        return given()
                .accept(ContentType.JSON)
                .header("Authorization", accessToken)
                .baseUri(apiUrl)
                .when()
                .get(path).then().extract().response();
    }

    protected Response putRequest(String path, JsonNode data) {
        return given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .baseUri(apiUrl)
                .body(data)
                .when()
                .put(path).then().extract().response();
    }

    protected Response deleteRequest(String path) {
        return given()
                .accept(ContentType.JSON)
                .header("Authorization", accessToken)
                .baseUri(apiUrl)
                .when()
                .delete(path).then().extract().response();
    }
}
