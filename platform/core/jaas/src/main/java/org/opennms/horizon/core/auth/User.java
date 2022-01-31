package org.opennms.horizon.core.auth;

public class User {
    public String getUserId() {
        return "x";
    }

    public String[] getRoles() {
        return new String[]{"ROLE_ADMIN", "ADMIN"};
    }
}
