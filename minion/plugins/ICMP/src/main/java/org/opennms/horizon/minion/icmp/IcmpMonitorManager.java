package org.opennms.horizon.minion.icmp;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.opennms.netmgt.icmp.PingerFactory;
import org.opennms.horizon.minion.plugin.api.ServiceMonitor;
import org.opennms.horizon.minion.plugin.api.ServiceMonitorManager;
import org.opennms.horizon.minion.plugin.api.ServiceMonitorResponse;
import org.opennms.horizon.minion.plugin.api.annotations.HorizonConfig;

@RequiredArgsConstructor
public class IcmpMonitorManager implements ServiceMonitorManager {
    private final PingerFactory pingerFactory;

    @HorizonConfig(displayName = "hypotheticalConfig")
    public String moreConfig;

    @Override
    public ServiceMonitor create(Consumer<ServiceMonitorResponse> resultProcessor) {
        IcmpMonitor icmpMonitor =  new IcmpMonitor(pingerFactory);
        icmpMonitor.setMoreConfig(moreConfig);

        return icmpMonitor;
    }
}
