package org.opennms.horizon.alarms.itests;

import static org.opennms.horizon.alarms.itests.InternetProtocol.TCP;
import static org.opennms.horizon.alarms.itests.InternetProtocol.UDP;

import java.util.Objects;

/**
 * Network protocols used by our services.
 *
 * This includes both ports for management and communication from devices.
 *
 */
public enum NetworkProtocol {
    SSH(TCP),
    HTTP(TCP),

    // Java Debug Wire Protocol
    JDWP(TCP),

    SYSLOG(UDP),
    SNMP(UDP),
    JTI(UDP),
    NXOS(UDP),
    FLOWS(UDP),
    BMP(TCP),
    IPFIX_TCP(TCP),
    GRPC(TCP);

    private final InternetProtocol ipProtocol;

    NetworkProtocol(InternetProtocol ipProtocol) {
        this.ipProtocol = Objects.requireNonNull(ipProtocol);
    }

    public InternetProtocol getIpProtocol() {
        return ipProtocol;
    }
}