package org.opennms.horizon.minion.weblisten;

import org.opennms.horizon.minion.plugin.api.Listener;
import org.opennms.horizon.minion.plugin.api.ListenerFactory;
import org.opennms.horizon.minion.plugin.api.ServiceMonitorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Consumer;

/**
 * PROTOTYPE
 */
public class WebListenerFactory implements ListenerFactory {

    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(WebListenerFactory.class);

    private Logger log = DEFAULT_LOGGER;

    @Override
    public Listener create(Consumer<ServiceMonitorResponse> resultProcessor, Map<String, Object> parameters) {
        WebListener listener = new WebListener(resultProcessor, parameters);

        return listener;
    }
}
