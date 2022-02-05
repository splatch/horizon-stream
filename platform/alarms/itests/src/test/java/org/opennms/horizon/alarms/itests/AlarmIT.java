package org.opennms.horizon.alarms.itests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class AlarmIT {

    private final GenericContainer<?> pgContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13.3-alpine"))
            .withEnv("POSTGRES_HOST_AUTH_METHOD", "trust")
            .withNetwork(Network.SHARED)
            .withNetworkAliases(CoreContainer.DB_ALIAS);

    private final CoreContainer coreContainer = new CoreContainer((PostgreSQLContainer<?>)pgContainer);

    @Rule
    public RuleChain stackChain = RuleChain
            .outerRule(pgContainer)
            .around(coreContainer);

    @Test
    public void canStartContainers() {
        // if we got this far, the container start successfully - that's all we want to check for now
    }

}
