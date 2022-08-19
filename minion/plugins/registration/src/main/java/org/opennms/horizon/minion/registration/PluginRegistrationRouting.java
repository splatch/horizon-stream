package org.opennms.horizon.minion.registration;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy;
import org.opennms.horizon.minion.ignite.model.workflows.PluginMetadata;
import org.opennms.horizon.minion.registration.proto.PluginConfigMessage;
import org.opennms.horizon.minion.registration.proto.PluginConfigMessage.PluginConfigMeta;
import org.opennms.horizon.shared.ipc.sink.api.MessageDispatcherFactory;
import org.opennms.horizon.shared.ipc.sink.api.SyncDispatcher;

@Slf4j
public class PluginRegistrationRouting extends RouteBuilder {

    public static final String ROUTE_ID =  "MINION_REGISTRATION";

    private final String registrationUri;
    private final SyncDispatcher<PluginConfigMessage> dispatcher;
    private final long aggregationDelay;

    public PluginRegistrationRouting(String uri, MessageDispatcherFactory messageDispatcherFactory, long aggregationDelay) {
        this.registrationUri = uri;
        dispatcher = messageDispatcherFactory.createSyncDispatcher(new PluginConfigSinkModule());
        this.aggregationDelay = aggregationDelay;
    }

    @Override
    public void configure() throws Exception {
        from(registrationUri).routeId(ROUTE_ID).
                log(LoggingLevel.INFO, "Got a single plugin config message").
                aggregate(new PluginConfigAggregationStrategy()).constant(true).
                completionTimeout(aggregationDelay).
                process(exchange -> {
                    log.info("Got a plugin registration notice!");

                    List<PluginMetadata> pluginMetadataList = exchange.getIn().getBody(List.class);

                    log.info("Got {} configs", pluginMetadataList.size());
                    log.info("PluginMetadata {}", pluginMetadataList);

                    if (pluginMetadataList.size() > 0) {

                        // now get the builder for the protobuf message and construct it from the PluginMetadata

                        PluginConfigMessage.Builder messageBuilder = PluginConfigMessage.newBuilder();

                        //  iterate over each of the plugins that sent a config
                        pluginMetadataList.forEach(pluginMetadata -> {
                            PluginConfigMeta.Builder pluginConfigMetaBuilder = PluginConfigMeta.newBuilder().
                                setPluginName(pluginMetadata.getPluginName()).
                                setPluginType(pluginMetadata.getPluginType().toString());
                            // iterate over each field in the plugin config
                            /*
                            // TODO get schema in place
                            pluginMetadata.getFieldConfigs().forEach(fieldConfig -> {
                                    Builder fieldConfigMetaBuilder = FieldConfigMeta.newBuilder().
                                        setJavaType(fieldConfig.getJavaType()).
                                        setIsEnum(fieldConfig.isEnum()).
                                        setCustom(fieldConfig.isCustom()).
                                        setDisplayName(fieldConfig.getDisplayName()).
                                        setDeclaredFieldName(fieldConfig.getDeclaredFieldName());

                                    if (fieldConfig.getEnumConstants() != null) {
                                        fieldConfigMetaBuilder.addAllEnumValues(
                                            (Iterable<String>) Arrays.stream(fieldConfig.getEnumConstants()).iterator());
                                    }

                                    pluginConfigMetaBuilder.addConfigs(fieldConfigMetaBuilder.build());
                                }
                            );
                            */
                            messageBuilder.addPluginconfigs(pluginConfigMetaBuilder.build());
                        });
                        dispatcher.send(messageBuilder.build());
                    }
                });

        //TODO: we may need dead letter handling here if comms to horizon haven't spun up yet.
    }

    private class PluginConfigAggregationStrategy extends AbstractListAggregationStrategy<PluginMetadata> {

        @Override
        public PluginMetadata getValue(Exchange exchange) {
            return exchange.getIn().getBody(PluginMetadata.class);
        }
    }
}
