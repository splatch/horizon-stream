package org.opennms.horizon.alarmservice.service;

import org.opennms.horizon.alarmservice.api.AlarmService;
import org.opennms.horizon.alarmservice.drools.DroolsAlarmContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlarmServiceConfig {

//    @Bean("eventForwarder")
//    public EventForwarder eventForwarder() {
//        return null;
//    }

    @Bean("alarmLifecycleListenerManger")
    public AlarmLifecycleListenerManager alarmLifecycleListenerManager(
        @Autowired DroolsAlarmContext droolsAlarmContext) {

        AlarmLifecycleListenerManager alarmLifecycleListenerManager = new AlarmLifecycleListenerManager();

        alarmLifecycleListenerManager.setListener(droolsAlarmContext);

        return alarmLifecycleListenerManager;
    }
}
