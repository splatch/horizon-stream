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

import java.io.Serializable;
import java.util.Set;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpression implements MethodSecurityExpressionOperations {

    protected Authentication authentication;
    private AuthenticationTrustResolver trustResolver;
    private RoleHierarchy roleHierarchy;
    private Set<String> roles;
    private String defaultRolePrefix = "";
    private Object target;
    private Object filterObject;
    private Object returnObject;

    public final boolean permitAll = true;
    public final boolean denyAll = false;
    private PermissionEvaluator permissionEvaluator;
    public final String read = "read";
    public final String write = "write";
    public final String create = "create";
    public final String delete = "delete";
    public final String admin = "administration";

    private UserRoleProvider roleProvider;

    public void setAuthentication(Authentication authentication) {
        if(authentication == null) {
            throw new IllegalArgumentException("Authentication object cannot be null");
        }
        this.authentication = authentication;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    public void setThis(Object target) {
        this.target = target;
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public Authentication getAuthentication() {
        return authentication;
    }

    @Override
    public boolean hasAuthority(String authority) {
        return hasAnyAuthority(authority);
    }

    @Override
    public boolean hasAnyAuthority(String... authorities) {
        return hasAnyAuthorityName(null, authorities);
    }

    @Override
    public boolean hasRole(String role) {
        return hasAnyRole(role);
    }

    @Override
    public boolean hasAnyRole(String... roles) {
        return hasAnyAuthorityName(defaultRolePrefix, roles);
    }

    @Override
    public boolean permitAll() {
        return true;
    }

    @Override
    public boolean denyAll() {
        return false;
    }

    @Override
    public boolean isAnonymous() {
        return this.trustResolver.isAnonymous(authentication);
    }

    @Override
    public boolean isAuthenticated() {
        return !isAnonymous();
    }

    @Override
    public boolean isRememberMe() {
        return trustResolver.isRememberMe(authentication);
    }

    @Override
    public boolean isFullyAuthenticated() {
        return !this.trustResolver.isAnonymous(this.authentication) && !this.trustResolver.isRememberMe(this.authentication);
    }

    @Override
    public boolean hasPermission(Object target, Object permission) {
        return this.permissionEvaluator.hasPermission(this.authentication, target, permission);
    }

    @Override
    public boolean hasPermission(Object targetId, String targetType, Object permission) {
        return this.permissionEvaluator.hasPermission(this.authentication, (Serializable)targetId, targetType, permission);
    }

    public void setRoleHierarchy(RoleHierarchy roleHierarchy) {
        this.roleHierarchy = roleHierarchy;
    }

    public void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
        this.trustResolver = trustResolver;
    }

    public void setRoleProvider(UserRoleProvider roleProvider) {
        this.roleProvider = roleProvider;
    }

    private Set<String> getAuthoritySet() {
        if(this.roles == null) {
            String userId = authentication.getName();
            roles = roleProvider.lookupUserRoles(userId);
        }
        return roles;
    }

    private boolean hasAnyAuthorityName(String prefix, String... roles) {
        Set<String> roleSet = this.getAuthoritySet();
        String[] var4 = roles;
        int var5 = roles.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String role = var4[var6];
            String defaultedRole = getRoleWithDefaultPrefix(prefix, role);
            if (roleSet.contains(defaultedRole)) {
                return true;
            }
        }

        return false;
    }

    private static String getRoleWithDefaultPrefix(String defaultRolePrefix, String role) {
        if (role == null) {
            return role;
        } else if (defaultRolePrefix != null && defaultRolePrefix.length() != 0) {
            return role.startsWith(defaultRolePrefix) ? role : defaultRolePrefix + role;
        } else {
            return role;
        }
    }
}
