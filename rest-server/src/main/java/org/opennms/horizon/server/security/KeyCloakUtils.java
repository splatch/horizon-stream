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

package org.opennms.horizon.server.security;

import java.util.Arrays;

import javax.ws.rs.core.Response;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class KeyCloakUtils {
    private final Keycloak keycloak;

    @Autowired
    public KeyCloakUtils(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void createRealm(String realm) {
        RealmRepresentation realmRp = new RealmRepresentation();
        realmRp.setId(realm);
        realmRp.setRealm(realm);
        realmRp.setEnabled(true);
        keycloak.realms().create(realmRp);
    }

    public void addRole(String realm, String role) {
        RoleRepresentation rr = new RoleRepresentation();
        rr.setName(role);
        rr.setName(role);
        keycloak.realm(realm).roles().create(rr);
    }

    /*public void addUser(String realm, UserDto user) {
        addUser(realm, user, null);

    }

    public boolean addUser(String realm, UserDto user, String password) {
        UserRepresentation ur = new UserRepresentation();
        ur.setUsername(user.getUsername());
        ur.setFirstName(user.getFirstname());
        ur.setLastName(user.getLastname());
        ur.setEmail(user.getEmail());
        ur.setEnabled(true);
        if(StringUtils.hasLength(password)) {
            CredentialRepresentation cr = new CredentialRepresentation();
            cr.setType(CredentialRepresentation.PASSWORD);
            cr.setValue(password);
            ur.setCredentials(Arrays.asList(cr));
        }
        ur.setRealmRoles(user.getRoles());


        Response response = keycloak.realm(realm).users().create(ur);
        return response.getStatus()==200;
    }*/

    /*public void updateRoleMapperForClient(String realm, String client, String mapperName) {
        ClientRepresentation cr = keycloak.realm(realm).clients().findByClientId(client).get(0);

        ProtocolMapperRepresentation pr = keycloak.realm(realm).

        cr.setProtocolMappers();
    }*/
}
