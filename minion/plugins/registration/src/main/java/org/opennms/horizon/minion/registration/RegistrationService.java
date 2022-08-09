package org.opennms.horizon.minion.registration;

import org.opennms.poc.ignite.model.workflows.PluginMetadata;

public interface RegistrationService {
    void notifyOfPluginRegistration(PluginMetadata pluginMetadataJson);

}
