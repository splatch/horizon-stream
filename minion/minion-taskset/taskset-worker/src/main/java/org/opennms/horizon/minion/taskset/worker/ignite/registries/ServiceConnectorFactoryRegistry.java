package org.opennms.horizon.minion.taskset.worker.ignite.registries;

import org.opennms.horizon.minion.plugin.api.ServiceConnectorFactory;

public interface ServiceConnectorFactoryRegistry {

    ServiceConnectorFactory getService(String type);
}
