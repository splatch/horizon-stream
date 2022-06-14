package org.opennms.netmgt.provision.scan;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.opennms.horizon.db.model.OnmsMonitoringLocation;
import org.opennms.netmgt.provision.LocationAwareDetectorClient;
import org.opennms.netmgt.provision.detector.DetectorRunner;
import org.opennms.netmgt.provision.persistence.dto.ForeignSourceDTO;
import org.opennms.netmgt.provision.persistence.dto.PluginConfigDTO;
import org.opennms.netmgt.provision.persistence.dto.RequisitionNodeDTO;
import org.opennms.netmgt.provision.persistence.model.ForeignSourceRepository;
import org.opennms.netmgt.provision.rpc.relocate.Callback;

@AllArgsConstructor
@Slf4j
public class NodeScanner {

    public static final String DIRECT_SCAN = "direct:scan";

    private final CamelContext context;
    private final LocationAwareDetectorClient locationAwareDetectorClient;
    private final ForeignSourceRepository foreignSourceRepository;

    public void scanNode(RequisitionNodeDTO node) {
        try {
            context.addRoutes(createScheduledRoute(node));
        }
        catch (Exception e) {
//            TODO: handle this correctly
            log.error(e.getMessage());
        }
    }

    private RouteBuilder createScheduledRoute(RequisitionNodeDTO node) {
        ForeignSourceDTO foreignSourceDTO = foreignSourceRepository.getForeignSource(node.getForeignId());

        String routeOrigin = String.format("timer://%s?fixedRate=true&period=%d",node.getNodeLabel(), foreignSourceDTO.getScanInterval().getMillis());
        String routeId = String.format("SCHEDULED-SCANNER-%s", node.getNodeLabel());
        
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                if (context.getRoutes().stream().anyMatch(route -> route.getId().equals(routeId))) {
                    log.info("NodeScanner :: Scheduled scan/route  for {} already exists, replacing it!", routeId);
                    context.createFluentProducerTemplate().withDefaultEndpoint(String.format("controlbus:route?routeId=%s&action=stop", routeId)).withBody(null).send();
                    context.removeRoute(routeId);
                }
                
                from(routeOrigin).
                        routeId(routeId).
                        log(LoggingLevel.INFO, String.format("NodeScanner :: performing scheduled scan for %s", routeId)).
                        process(new SimpleScanner(node));

                log.info("NodeScanner :: Created scheduled scan/route ({}) for {}", routeOrigin, routeId);

            }
        };
    }

    @RequiredArgsConstructor
    private class SimpleScanner implements Processor {

        private final RequisitionNodeDTO node;

        @Override
        public void process(Exchange exchange) throws Exception {
            PluginConfigDTO pluginConfigDTO = new PluginConfigDTO();
            pluginConfigDTO.setName("WebDetector");
            pluginConfigDTO.setPluginClass("org.opennms.netmgt.provision.detector.web.WebDetector");

            OnmsMonitoringLocation onmsMonitoringLocation = new OnmsMonitoringLocation();
            onmsMonitoringLocation.setLocationName(node.getLocation());

            //TODO: what to actually impl here? 
            Callback<Boolean> callback = new Callback<>() {
                @Override
                public void accept(Boolean t) {

                }

                @Override
                public Boolean apply(Throwable throwable) {
                    return null;
                }
            };
            node.getInterfaces().values().forEach(intrfc -> {
                log.info("Interface {}", intrfc);
                DetectorRunner detectorRunner = new DetectorRunner(locationAwareDetectorClient, pluginConfigDTO, node.getId(), intrfc.getIpAddress(), onmsMonitoringLocation, null);
                detectorRunner.supplyAsyncThenAccept(callback);
            });
        }
    }
}
