package org.opennms.horizon.minion.plugin.api.config;

import java.util.Map;

public interface ConfigInjector {

    void injectConfigs(Object target, Map<String, String> parameters);

}
