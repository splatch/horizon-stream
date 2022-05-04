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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeycloakRoleProvider implements UserRoleProvider{
    private String kcRealm;
    private Keycloak keycloak;

    public KeycloakRoleProvider(Keycloak keycloak, String userRealm) {
        this.keycloak = keycloak;
        this.kcRealm = userRealm;
    }

    @Override
    public Set<String> lookupUserRoles(String userId) {
        //TODO add cache
        return getRolesFromServer(userId);
    }

    private Set<String> getRolesFromServer(String userId) {
        RealmResource realmResource = keycloak.realm(kcRealm);
        UserResource user = realmResource.users().get(userId);
        if(user != null) {
            return user.roles().getAll().getRealmMappings().stream().map(r->r.getName()).collect(Collectors.toSet());
        }
        return new HashSet<>();
    }
}
