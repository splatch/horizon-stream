package org.opennms.horizon.alarmservice;

import org.opennms.horizon.alarmservice.api.AlarmEntityNotifier;
import org.opennms.horizon.alarmservice.api.AlarmPersister;
import org.opennms.horizon.alarmservice.drools.AlarmService;
import org.opennms.horizon.alarmservice.drools.DefaultAlarmService;
import org.opennms.horizon.alarmservice.drools.DroolsAlarmContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.opennms.horizon.alarmservice")
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
//    @Bean("eventForwarder")
//    public EventForwarder eventForwarder() {
//        return null;
//    }

    @Bean("alarmService")
    public AlarmService alarmService() {
        return new DefaultAlarmService();
    }
}
