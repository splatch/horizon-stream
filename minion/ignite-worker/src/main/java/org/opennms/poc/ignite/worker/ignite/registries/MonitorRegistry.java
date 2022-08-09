package org.opennms.poc.ignite.worker.ignite.registries;

import java.util.Map;
import org.opennms.horizon.minion.plugin.api.ServiceMonitorManager;

public interface MonitorRegistry {

    ServiceMonitorManager getService(String type);
    int getServiceCount();
    Map<String, ServiceMonitorManager> getServices();
}
