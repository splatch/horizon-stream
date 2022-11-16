package org.opennms.horizon.alarmservice.service;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.opennms.horizon.alarmservice")
@Deprecated // TODO::MMF remove
public class AlarmServiceConfig {

//    @Bean("eventForwarder")
//    public EventForwarder eventForwarder() {
//        return null;
//    }

//    @Bean("alarmService")
//    public AlarmService alarmService() {
//        return new AlarmServiceImpl();
//    }
}
