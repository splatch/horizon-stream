package org.opennms.horizon.core.auth;

import java.io.IOException;

public interface UserConfig {
    User getUser(String user) throws IOException;

    boolean comparePasswords(String user, String password);
}
