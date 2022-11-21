package org.opennms.horizon.alarmservice.service;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.opennms.horizon.alarmservice.api.AlarmEntityNotifier;
import org.opennms.horizon.alarmservice.drools.DroolsAlarmContext;
import org.opennms.horizon.alarmservice.service.routing.AlarmRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlarmServiceConfig {

    @Bean("alarmMapper")
    public AlarmMapper alarmMapper() {
        return AlarmMapper.INSTANCE;
    }

    @Bean("alarmLifecycleListenerManger")
    public AlarmLifecycleListenerManager alarmLifecycleListenerManager(
        @Autowired DroolsAlarmContext droolsAlarmContext) {

        AlarmLifecycleListenerManager alarmLifecycleListenerManager = new AlarmLifecycleListenerManager();

        alarmLifecycleListenerManager.setListener(droolsAlarmContext);

        return alarmLifecycleListenerManager;
    }

    // Create a camel producer template here and wire it with a default endpoint for the alarm route, to avoid tight
    // coupling between the alarm route and the notifier impl
    @Bean("alarmEntityNotifier")
    public AlarmEntityNotifier alarmEntityNotifier(@Autowired CamelContext camelContext, @Autowired AlarmRouteBuilder alarmRouteBuilder) {
        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        producerTemplate.setDefaultEndpointUri(alarmRouteBuilder.getEndpoint());

        return new AlarmEntityNotifierImpl(producerTemplate);
    }
}
