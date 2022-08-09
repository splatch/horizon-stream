package org.opennms.horizon.minion.snmp;

import java.util.function.Consumer;
import org.opennms.horizon.minion.plugin.api.ServiceMonitor;
import org.opennms.horizon.minion.plugin.api.ServiceMonitorManager;
import org.opennms.horizon.minion.plugin.api.ServiceMonitorResponse;

public class SnmpMonitorManager implements ServiceMonitorManager {

    @Override
    public ServiceMonitor create(Consumer<ServiceMonitorResponse> resultProcessor) {
        return new SnmpMonitor();
    }
}
