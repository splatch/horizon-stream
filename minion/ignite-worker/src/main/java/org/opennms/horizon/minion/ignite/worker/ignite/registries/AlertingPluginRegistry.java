package org.opennms.horizon.minion.ignite.worker.ignite.registries;

import com.savoirtech.eos.pattern.whiteboard.KeyedWhiteboard;
import com.savoirtech.eos.util.ServiceProperties;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.opennms.horizon.minion.registration.RegistrationService;
import org.opennms.horizon.minion.ignite.model.workflows.PluginMetadata;
import org.opennms.horizon.minion.ignite.model.workflows.WorkflowType;
import org.opennms.horizon.minion.plugin.api.FieldConfigMeta;
import org.opennms.horizon.minion.plugin.config.PluginConfigScanner;
import org.osgi.framework.BundleContext;

@Slf4j
public class AlertingPluginRegistry<K, S> extends KeyedWhiteboard<K, S>  {
    private final RegistrationService registrationService;
    private static PluginConfigScanner scanner = new PluginConfigScanner();

    public AlertingPluginRegistry(BundleContext bundleContext, Class<S> serviceType, String id, RegistrationService alertingService) {
        super(bundleContext, serviceType, (svc, props) -> props.getProperty(id));
        this.registrationService = alertingService;
        super.start();
    }

    @Override
    protected K addService(S service, ServiceProperties props) {
        K serviceId = super.addService(service, props);

        if (serviceId != null) {
            List<FieldConfigMeta> fieldConfigMetaList = scanner.getConfigs(service.getClass());
            log.info("Performing scan on service {}", service.getClass());
            PluginMetadata pluginMetadata = new PluginMetadata(serviceId.toString(), WorkflowType.DETECTOR, fieldConfigMetaList);
            registrationService.notifyOfPluginRegistration(pluginMetadata);
        }

        return serviceId;
    }

    @Override
    public void start() {

    }
}
