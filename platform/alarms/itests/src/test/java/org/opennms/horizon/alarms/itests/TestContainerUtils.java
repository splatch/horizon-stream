package org.opennms.horizon.alarms.itests;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testcontainers.containers.Container;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;

public class TestContainerUtils {

    /**
     * Workaround for UDP ports -- see https://github.com/testcontainers/testcontainers-java/issues/554
     *
     * @param cmd
     * @param ports
     */
    public static void exposePortsAsUdp(CreateContainerCmd cmd, int... ports) {
        final ExposedPort[] exposedPorts = cmd.getExposedPorts();
        if (exposedPorts == null && ports.length > 0) {
            throw new RuntimeException("There are 1+ ports to convert to UDP, but no exposed ports were found.");
        }

        // Index the ports for easy lookup
        final Map<Integer, Integer> portToIdx = new HashMap<>();
        for (int i = 0; i < exposedPorts.length; i++) {
            portToIdx.put(exposedPorts[i].getPort(), i);
        }

        for (int port : ports) {
            final Integer idx = portToIdx.get(port);
            if (idx == null) {
                throw new RuntimeException("No exposed port entry found for: " + port);
            }
            exposedPorts[idx] = new ExposedPort(port, com.github.dockerjava.api.model.InternetProtocol.UDP);
        }
    }

    /**
     * Workaround for UDP ports -- see https://github.com/testcontainers/testcontainers-java/issues/554
     *
     * @param container
     * @param port
     * @return
     */
    //
    public static int getMappedUdpPort(Container container, int port) {
        final String hostPortSpec = container.getContainerInfo().getNetworkSettings().getPorts()
                .getBindings().get(new ExposedPort(port, com.github.dockerjava.api.model.InternetProtocol.UDP))[0].getHostPortSpec();
        final int hostPort = Integer.parseInt(hostPortSpec);
        return hostPort;
    }
}
