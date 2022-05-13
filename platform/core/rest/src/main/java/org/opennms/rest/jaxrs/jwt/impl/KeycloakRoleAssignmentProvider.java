package org.opennms.rest.jaxrs.jwt.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.admin.client.Keycloak;
import org.opennms.rest.jaxrs.jwt.RoleAssignmentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * RoleAssignmentProvider which loads assignments from Keycloak server.
 *
 * TODO PERFORMANCE WARNING:
 *  As currently written, every call performs HTTP requests to Keycloak to login, lookup the roles, and logout.
 */
public class KeycloakRoleAssignmentProvider implements RoleAssignmentProvider {

    public static final long DEFAULT_CACHE_STALE_TIME = 300_000;        // 5 minutes

    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(KeycloakRoleAssignmentProvider.class);

    private Logger log = DEFAULT_LOGGER;

    private String keycloakBaseUrl;
    private String keycloakRealm;
    private String keycloakAdminRealm;
    private String keycloakAdminUsername;
    private String keycloakAdminPassword;
    private long cacheStaleTime = DEFAULT_CACHE_STALE_TIME;

    private LoadingCache<CacheKey, List<String>> mappingsRepresentationCache;

//========================================
// Getters and Setters
//----------------------------------------
    public String getKeycloakBaseUrl() {
        return keycloakBaseUrl;
    }

    public void setKeycloakBaseUrl(String keycloakBaseUrl) {
        this.keycloakBaseUrl = keycloakBaseUrl;
    }

    public String getKeycloakAdminRealm() {
        return keycloakAdminRealm;
    }

    public void setKeycloakAdminRealm(String keycloakAdminRealm) {
        this.keycloakAdminRealm = keycloakAdminRealm;
    }

    public String getKeycloakRealm() {
        return keycloakRealm;
    }

    public void setKeycloakRealm(String keycloakRealm) {
        this.keycloakRealm = keycloakRealm;
    }

    public String getKeycloakAdminUsername() {
        return keycloakAdminUsername;
    }

    public void setKeycloakAdminUsername(String keycloakAdminUsername) {
        this.keycloakAdminUsername = keycloakAdminUsername;
    }

    public String getKeycloakAdminPassword() {
        return keycloakAdminPassword;
    }

    public void setKeycloakAdminPassword(String keycloakAdminPassword) {
        this.keycloakAdminPassword = keycloakAdminPassword;
    }

    public long getCacheStaleTime() {
        return cacheStaleTime;
    }

    public void setCacheStaleTime(long cacheStaleTime) {
        this.cacheStaleTime = cacheStaleTime;
    }

//========================================
// Lifecycle
//----------------------------------------

    public void init() {
        /**
         * Use refreshAfterWrite() to allow continued operations using old cache values until a refresh operation
         *  completes successfully.
         */
        mappingsRepresentationCache =
                CacheBuilder.newBuilder()
                        .refreshAfterWrite(cacheStaleTime, TimeUnit.MILLISECONDS)
                        .build(
                                new MyCacheLoader()
                        );
    }

//========================================
// RoleAssignmentProvider API
//----------------------------------------

    @Override
    public List<String> lookupUserRoles(String username) {
        return loadFromKeycloak(keycloakRealm, username);
    }

//========================================
// Internals
//----------------------------------------

    /**
     * Load the list of roles from Keycloak for the given realm + username.  Note that this method uses a short-term
     * login to Keycloak.
     *
     * @param realm
     * @param username
     * @return
     */
    private List<String> loadFromKeycloak(String realm, String username) {
        Keycloak keycloakSession = Keycloak.getInstance(keycloakBaseUrl, keycloakAdminRealm, keycloakAdminUsername, keycloakAdminPassword, "horizon-stream");
        try {
            UserResource userResource = keycloakSession.realm(realm).users().get(username);

            List<String> result;

            if (userResource != null) {
                MappingsRepresentation mappingsRepresentation = userResource.roles().getAll();

                result = mappingsRepresentation.getRealmMappings().stream().map(RoleRepresentation::getName).collect(Collectors.toList());
            } else {
                log.warn("lookup of user {}: username not matched", username);
                result = Collections.EMPTY_LIST;
            }

            return result;
        } catch (Exception exc) {
            throw new RuntimeException("failed to load user roles from keycloak", exc);
        } finally {
            try {
                keycloakSession.close();
            } catch (Exception exc) {
                log.warn("failed to logout Keycloak session", exc);
            }
        }
    }

//========================================
// Internals Classes
//----------------------------------------

    private static class CacheKey {
        private final String realm;
        private final String username;

        public CacheKey(String realm, String username) {
            this.realm = realm;
            this.username = username;
        }

        public String getRealm() {
            return realm;
        }

        public String getUsername() {
            return username;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return Objects.equals(realm, cacheKey.realm) &&
                    Objects.equals(username, cacheKey.username);
        }

        @Override
        public int hashCode() {
            return Objects.hash(realm, username);
        }
    }

    private class MyCacheLoader extends CacheLoader<CacheKey, List<String>> {
        @Override
        public List<String> load(CacheKey cacheKey) throws Exception {
            return loadFromKeycloak(cacheKey.getRealm(), cacheKey.getUsername());
        }
    }
}
