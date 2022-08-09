package org.opennms.horizon.minion.plugin.api;

import java.util.function.Consumer;

public interface ServiceMonitorManager {
    ServiceMonitor create(Consumer<ServiceMonitorResponse> resultProcessor);
}
