package org.opennms.horizon.alarmservice;

import org.opennms.horizon.alarmservice.drools.DroolsAlarmContext;
import org.opennms.horizon.events.api.EventForwarder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlarmServiceConfig {

    @Bean("alarmEntityNotifier")
    public AlarmEntityNotifier alarmEntityNotifier() {
        return new AlarmEntityNotifierImpl();
    }

    @Bean("alarmPersister")
    public AlarmPersister alarmPersister() {
        return new AlarmPersisterImpl();
    }

    //TODO:MMF
    @Bean("droolsAlarmContext")
    public DroolsAlarmContext droolsAlarmContext() {
        return null;
    }

    //TODO:MMF
    @Bean("eventForwarder")
    public EventForwarder eventForwarder() {
        return null;
    }
}
