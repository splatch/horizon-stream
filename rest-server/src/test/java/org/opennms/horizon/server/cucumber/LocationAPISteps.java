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

import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.opennms.horizon.server.security.KeyCloakUtils;
import org.opennms.horizon.server.security.KeycloakConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = {KeycloakConfig.class, KeyCloakUtils.class})
public class LocationAPISteps {
    private String realm = "opennms";
    private List<String> roles = Arrays.asList("admin", "user");
    private String username;
    private String password;
    private String baseUrl;
    private HttpClient client = HttpClientBuilder.create().build();
    private KeyCloakUtils keyCloakUtils;

    @Autowired
    public LocationAPISteps(KeyCloakUtils keyCloakUtils) {
        this.keyCloakUtils = keyCloakUtils;
        setUpKeyCloak();
        baseUrl = System.getProperty("application.base-url");
    }

    @Given("A admin user {string} with password {string}")
    public void adminUser(String username, String password) {
        //todo initial env vars and http client
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("Admin user can create an access token")
    public void adminUserCanCreateToken() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("Admin user can create new location")
    public void adminUserCreateLocation() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("Admin user can list location")
    public void adminUserListLocation() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("Admin user can get location by ID")
    public void adminUserGetLocation() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("Admin user can update the location")
    public void adminUserUpdateLocation() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("Admin user can delete the location by ID")
    public void adminUserDeleteLocation() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //test normal user
    @Given("A normal user with username {string} and password {string}")
    public void normalUser(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("Normal user can get location by ID")
    public void normalUserGetLocationById() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("Normal user am not allowed to create new location")
    public void normalUserCreateLocation() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("Normal user am not allowed to update the location by ID")
    public void normalUserUpdateLocation() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("Normal user am not allowed to delete the location")
    public void normalUserDeleteLocation() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //Invalid user
    @Then("Without token user can't access rest api")
    public void without_token_user_can_t_access_rest_api() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    private void setUpKeyCloak() {
        keyCloakUtils.createRealm(realm);
        roles.forEach(r -> keyCloakUtils.addRole(realm, r));
    }

    /*private boolean addKeycloakUser(String username, String password, String role) {
        UserDto user = new UserDto();
        user.setUsername(username);
        user.setRoles(Arrays.asList(role));
        return keyCloakUtils.addUser(realm, user, password);
    }*/

    private HttpResponse post(String path, String Data) {
        return null;
    }

    private HttpResponse put(String path, String data){
        return null;
    }

    private HttpResponse get(String path) {
        return null;
    }

    private HttpResponse delete(String path) {
        return null;
    }
}
