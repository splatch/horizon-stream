package org.opennms.horizon.minion.icmp;

import java.util.function.Consumer;
import org.opennms.horizon.minion.plugin.api.ServiceDetector;
import org.opennms.horizon.minion.plugin.api.ServiceDetectorManager;
import org.opennms.horizon.minion.plugin.api.ServiceDetectorResults;
import org.opennms.horizon.minion.plugin.api.annotations.HorizonConfig;

public class IcmpDetectorManager implements ServiceDetectorManager {

    @HorizonConfig(displayName = "sampleConfig")
    public String configValue;

    @Override
    public ServiceDetector create(Consumer<ServiceDetectorResults> resultProcessor) {
        return new IcmpDetector(configValue);
    }
}
