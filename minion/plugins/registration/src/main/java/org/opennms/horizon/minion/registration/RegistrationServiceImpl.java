package org.opennms.horizon.minion.registration;

import lombok.AllArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.opennms.horizon.minion.ignite.model.workflows.PluginMetadata;

@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final ProducerTemplate producerTemplate;

    @Override
    public void notifyOfPluginRegistration(PluginMetadata pluginMetadata) {
        producerTemplate.sendBody(pluginMetadata);
    }
}
