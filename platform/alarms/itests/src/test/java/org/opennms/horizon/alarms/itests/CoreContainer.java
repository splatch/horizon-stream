package org.opennms.horizon.alarms.itests;

import java.util.ArrayList;
import java.util.Map;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
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

    public CoreContainer() {
        super("opennms/horizon-stream-core:latest");
    }

    @Override
    protected void configure() {
        final Integer[] exposedPorts = new ArrayList<>(networkProtocolMap.values())
                .toArray(new Integer[0]);
        final int[] exposedUdpPorts = networkProtocolMap.entrySet().stream()
                .filter(e -> InternetProtocol.UDP.equals(e.getKey().getIpProtocol()))
                .mapToInt(Map.Entry::getValue)
                .toArray();

        String javaOpts = "-Djava.security.egd=file:/dev/./urandom ";
       // if (profile.isJvmDebuggingEnabled()) {
            javaOpts += String.format("-agentlib:jdwp=transport=dt_socket,server=y,address=*:%d,suspend=n", CORE_SNMP_PORT);
       // }

        String containerCommand = "-f";

        withExposedPorts(exposedPorts)
                .withEnv("JAVA_OPTS", javaOpts)
                .withNetwork(Network.SHARED)
                .withNetworkAliases(ALIAS)
                .withCommand(containerCommand)
                .waitingFor(new HostPortWaitStrategy());
//
//        withExposedPorts(exposedPorts)
//                .withCreateContainerCmdModifier(cmd -> {
//                    final CreateContainerCmd createCmd = (CreateContainerCmd)cmd;
//                    // The framework doesn't support exposing UDP ports directly, so we use this hook to map some of the exposed ports to UDP
//                    TestContainerUtils.exposePortsAsUdp(createCmd, exposedUdpPorts);
//                })
//                .withEnv("POSTGRES_HOST", DB_ALIAS)
//                .withEnv("POSTGRES_PORT", Integer.toString(PostgreSQLContainer.POSTGRESQL_PORT))
//                // User/pass are hardcoded in PostgreSQLContainer but are not exposed
//                .withEnv("POSTGRES_USER", "test")
//                .withEnv("POSTGRES_PASSWORD", "test")
//                .withEnv("OPENNMS_DBNAME", "opennms")
//                .withEnv("OPENNMS_DBUSER", "opennms")
//                .withEnv("OPENNMS_DBPASS", "opennms")
//                .withEnv("JAVA_OPTS", javaOpts)
//                .withNetwork(Network.SHARED)
//                .withNetworkAliases(ALIAS)
//                .withCommand(containerCommand)
//                .waitingFor(new HostPortWaitStrategy());
////                .addFileSystemBind(overlay.toString(),
////                        "/opt/opennms-overlay", BindMode.READ_ONLY, SelinuxContext.SINGLE);
    }
}
