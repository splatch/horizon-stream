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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.opennms.horizon.server.exception.UserManagementException;
import org.opennms.horizon.server.model.dto.ResetPasswordDTO;
import org.opennms.horizon.server.model.dto.UserDTO;
import org.opennms.horizon.server.model.dto.UserSearchDTO;
import org.opennms.horizon.server.model.mapper.KeycloakUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import liquibase.repackaged.org.apache.commons.lang3.RandomStringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KeyCloakUtils {
    private final String pwdChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
    private final int pwdLen = 10;
    @Value("${keycloak.realm}")
    private String appRealm;
    private final Keycloak keycloak;
    private final KeycloakUserMapper userMapper;

    @Autowired
    public KeyCloakUtils(Keycloak keycloak, KeycloakUserMapper userMapper) {
        this.keycloak = keycloak;
        this.userMapper = userMapper;
    }

    public void setAppRealm(String appRealm) {
        this.appRealm = appRealm;
    }

    public void createRealm(String frontendUrl) {
        RealmRepresentation realmRp = new RealmRepresentation();
        realmRp.setId(appRealm);
        realmRp.setRealm(appRealm);
        realmRp.setEnabled(true);
        if(StringUtils.hasLength(frontendUrl)) {
            Map<String, String> attr = new HashMap<>();
            attr.put("frontendUrl", frontendUrl);
            realmRp.setAttributes(attr);
        }
        keycloak.realms().create(realmRp);
    }

    public void addRole(String role) {
        RoleRepresentation rr = new RoleRepresentation();
        rr.setName(role);
        rr.setName(role);
        keycloak.realm(appRealm).roles().create(rr);
    }

    public UserDTO addUser(String username, String password, String role) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setRoles(Arrays.asList(role));
        try {
            if (StringUtils.hasLength(password)) {
                CredentialRepresentation cr = new CredentialRepresentation();
                cr.setType(CredentialRepresentation.PASSWORD);
                cr.setValue(password);
                return addNewUser(userDTO, cr);
            } else {
                return addNewUser(userDTO, null);
            }
        } catch (UserManagementException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public void addRoles(List<String> roles) {
        roles.forEach(r -> addRole(r));
    }

    /**
     *
     * @param usrId the keycloak user id
     * @return set of realm roles assigned to the user
     */
    public Set<String> listUserRoles(String usrId) {
        log.info("list roles for user {} with realm {}", usrId, appRealm);
        UserResource userResource = keycloak.realm(appRealm).users().get(usrId);
        try {
            return userResource.roles().getAll().getRealmMappings().stream().map(r->r.getName()).collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("failed list user roles: {}", e.getMessage());
            return new HashSet<>();
        }
    }

    public void close() {
        if(!keycloak.isClosed()) {
            keycloak.close();
        }
    }

    private UserResource getUserResourceById(String userId) {
        return keycloak.realm(appRealm).users().get(userId);
    }

    private Set<String> getRolesFromUserResource(UserResource userResource) {
        return userResource.roles().getAll().getRealmMappings().stream().map(r->r.getName()).collect(Collectors.toSet());
    }

    public List<UserDTO> searchUser(UserSearchDTO searchDTO) {
        List<UserRepresentation> list =keycloak.realm(appRealm).users().search(searchDTO.getUsername(), searchDTO.getFirstName(),
                searchDTO.getLastName(), searchDTO.getEmail(), searchDTO.getFirst(), searchDTO.getMax(),
                searchDTO.getEnabled(), null);
        list.stream().map(urs -> {
            urs.setRealmRoles(new ArrayList<>(listUserRoles(urs.getId())));
            return urs;
        }).collect(Collectors.toList());
        return  userMapper.listToDto(list);
    }

    public UserDTO getUserById(String userId) {
        UserResource userResource = getUserResourceById(userId);
        UserRepresentation user = userResource.toRepresentation();
        user.setRealmRoles(new ArrayList<>(getRolesFromUserResource(userResource)));
        return userMapper.toDto(user);
    }

    public UserDTO createUser(UserDTO userDTO) throws UserManagementException {
        String tmpPwd = RandomStringUtils.random(pwdLen, pwdChars);
        CredentialRepresentation crP = new CredentialRepresentation();
        crP.setType(CredentialRepresentation.PASSWORD);
        crP.setValue(tmpPwd);
        crP.setTemporary(true);
        return addNewUser(userDTO, crP);
    }

    private UserDTO addNewUser(UserDTO userDTO, CredentialRepresentation crp) throws UserManagementException {
        UserRepresentation userRep = userMapper.fromDto(userDTO);
        if(crp!=null) {
            log.info("create user {} with temp password {}", userDTO.getUsername(), crp.getValue());
            userRep.setCredentials(Arrays.asList(crp));
        }
        if(userDTO.getEnabled()==null) { //enabled by default
            userRep.setEnabled(true);
        }
        RealmResource realmResource = keycloak.realm(appRealm);
        Response response = realmResource.users().create(userRep);
        if(response.getStatus()!= Response.Status.CREATED.getStatusCode()) {
            throw new UserManagementException(String.format("Failed on creating new user with username: %s and email: %s. The error is: %s",
                    userDTO.getUsername(), userDTO.getEmail(), ((ClientResponse) response).getReasonPhrase()));
        }
        String userId = CreatedResponseUtil.getCreatedId(response);
        if(userDTO.getRoles()!=null&&userDTO.getRoles().size()>0) {
            assignRoles(realmResource, userId, userDTO.getRoles());
        }
        UserResource userResource = realmResource.users().get(userId);
        UserRepresentation newUser = userResource.toRepresentation();
        newUser.setRealmRoles(new ArrayList<>(getRolesFromUserResource(userResource)));
        return userMapper.toDto(newUser);
    }

    public void assignRoles(RealmResource realmResource, String userId, List<String> roles){
        if(roles !=null) {
            UserResource userResource = realmResource.users().get(userId);
            List<RoleRepresentation> oldRoles = userResource.roles().realmLevel().listAll();
            userResource.roles().realmLevel().remove(oldRoles);
            List<RoleRepresentation> newRoles = roles.stream().map(r->realmResource.roles().get(r).toRepresentation())
                    .collect(Collectors.toList());
            userResource.roles().realmLevel().add(newRoles);
        }
    }

    public UserDTO updateUser(String userId, UserDTO userDTO) {
        RealmResource realmResource = keycloak.realm(appRealm);
        UserResource userRes = realmResource.users().get(userId);
        if(userRes != null) {
            UserRepresentation userRep = userRes.toRepresentation();
            userMapper.updateUserFromDto(userDTO, userRep);
            userRes.update(userRep);
            if(userDTO.getRoles() != null) {
                assignRoles(realmResource, userId, userDTO.getRoles());
            }
            UserRepresentation updatedUser = userRes.toRepresentation();
            updatedUser.setRealmRoles(new ArrayList<>(getRolesFromUserResource(userRes)));
            return userMapper.toDto(updatedUser);
        }
        return null;
    }

    public boolean deleteUser(String userId) {
        boolean deleted = false;
        UserResource user = keycloak.realm(appRealm).users().get(userId);
        if (user != null) {
            user.remove();
            deleted = true;
        }
        return deleted;
    }

    public boolean resetPassword(String userId, ResetPasswordDTO passwordDto) throws UserManagementException {
        if(!StringUtils.hasLength(passwordDto.getNewPassword()) || passwordDto.getNewPassword().length() < pwdLen) {
            throw new UserManagementException(String.format("Invalid password: d%s, password must have at least %d characters.", passwordDto.getNewPassword(), pwdLen));
        }
        boolean pwdChanged = false;
        UserResource user = keycloak.realm(appRealm).users().get(userId);
        if(user != null) {
            CredentialRepresentation newPwd = new CredentialRepresentation();
            newPwd.setType(CredentialRepresentation.PASSWORD);
            newPwd.setValue(passwordDto.getNewPassword());
            if(passwordDto.getTemporary()!=null) {
                newPwd.setTemporary(passwordDto.getTemporary());
            }
            user.resetPassword(newPwd);
            pwdChanged = true;
        }
        return pwdChanged;
    }
}
