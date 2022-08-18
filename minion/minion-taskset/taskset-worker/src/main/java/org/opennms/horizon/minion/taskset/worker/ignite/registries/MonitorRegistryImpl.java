package org.opennms.horizon.minion.taskset.worker.ignite.registries;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.opennms.horizon.minion.registration.RegistrationService;
import org.opennms.horizon.minion.plugin.api.ServiceMonitorManager;
import org.osgi.framework.BundleContext;

@Slf4j
public class MonitorRegistryImpl extends AlertingPluginRegistry<String, ServiceMonitorManager> implements MonitorRegistry {

    public static final String PLUGIN_IDENTIFIER = "monitor.name";

    public MonitorRegistryImpl(BundleContext bundleContext, RegistrationService registrationService) {
        super(bundleContext, ServiceMonitorManager.class,PLUGIN_IDENTIFIER, registrationService);
    }

    @Override
    public Map<String, ServiceMonitorManager> getServices() {
        return super.asMap();
    }
}
