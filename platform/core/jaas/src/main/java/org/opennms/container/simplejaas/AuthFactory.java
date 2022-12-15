package org.opennms.container.simplejaas;

import org.opennms.horizon.core.auth.User;
import org.opennms.horizon.core.auth.UserConfig;

public class AuthFactory {

    public static UserConfig createUserManager() {
        return new UserConfig() {
            public User getUser(String user) {
                return new User();
            }

            public boolean comparePasswords(String user, String password) {
                return true;
            }
        };
    }

}

