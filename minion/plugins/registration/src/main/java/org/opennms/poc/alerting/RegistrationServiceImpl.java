package org.opennms.poc.alerting;

import lombok.AllArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.opennms.poc.ignite.model.workflows.PluginMetadata;

@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final ProducerTemplate producerTemplate;

    @Override
    public void notifyOfPluginRegistration(PluginMetadata pluginMetadata) {
        producerTemplate.sendBody(pluginMetadata);
    }
}
