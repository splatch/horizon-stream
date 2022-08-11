package org.opennms.horizon.minion.registration;

import org.opennms.horizon.minion.ignite.model.workflows.PluginMetadata;

public interface RegistrationService {
    void notifyOfPluginRegistration(PluginMetadata pluginMetadataJson);

}
