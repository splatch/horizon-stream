package org.opennms.horizon.alarms.itests;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.lifecycle.TestLifecycleAware;

import com.google.common.collect.ImmutableMap;

public class CoreContainer extends GenericContainer<CoreContainer> implements TestLifecycleAware {
    public static final String ALIAS = "core";
    public static final String DB_ALIAS = "db";

    public static final int CORE_WEB_PORT = 8181;
    private static final int CORE_SSH_PORT = 8101;
    private static final int CORE_SYSLOG_PORT = 1514;
    private static final int CORE_SNMP_PORT = 1162;
    private static final int CORE_DEBUG_PORT = 5005;

    private static final Map<NetworkProtocol, Integer> networkProtocolMap = ImmutableMap.<NetworkProtocol, Integer>builder()
            .put(NetworkProtocol.SSH, CORE_SSH_PORT)
            .put(NetworkProtocol.HTTP, CORE_WEB_PORT)
            .put(NetworkProtocol.JDWP, CORE_DEBUG_PORT)
            .put(NetworkProtocol.SNMP, CORE_SNMP_PORT)
            .put(NetworkProtocol.SYSLOG, CORE_SYSLOG_PORT)
            .build();

    private final PostgreSQLContainer<?> pgContainer;

    public CoreContainer(PostgreSQLContainer<?> pgContainer) {
        // use the 'local' tag so that we don't accidentally pull 'latest' or another tag for the container registry
        super("opennms/horizon-stream-core:local");
        this.pgContainer = Objects.requireNonNull(pgContainer);
    }

    @Override
    protected void configure() {
        final Integer[] exposedPorts = new ArrayList<>(networkProtocolMap.values())
                .toArray(new Integer[0]);

        String javaOpts = "-Djava.security.egd=file:/dev/./urandom ";
        javaOpts += String.format("-agentlib:jdwp=transport=dt_socket,server=y,address=*:%d,suspend=n ", CORE_DEBUG_PORT);

        String containerCommand = "-f";

        withExposedPorts(exposedPorts)
                .withEnv("JAVA_OPTS", javaOpts)
                .withEnv("PGSQL_SERVICE_NAME", DB_ALIAS)
                .withEnv("PGSQL_ADMIN_USERNAME", pgContainer.getUsername())
                .withEnv("PGSQL_ADMIN_PASSWORD", pgContainer.getPassword())
                .withNetwork(Network.SHARED)
                .withNetworkAliases(ALIAS)
                .withCommand(containerCommand)
                .withFileSystemBind("target/test-classes/users.properties", "/opt/horizon-stream/etc/users.properties", BindMode.READ_ONLY)
                .waitingFor(new HttpWaitStrategy()
                        .forPath("/alarms/list")
                        .forPort(CORE_WEB_PORT)
                        .withBasicCredentials("admin", "admin")
                        .withStartupTimeout(Duration.ofMinutes(2)));
    }
}
