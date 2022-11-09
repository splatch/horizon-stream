package org.opennms.horizon.alarmservice.rest;

//TODO:MMF port this?  YES! Need to seperate entities and dtos
//import org.opennms.horizon.db.model.mapper.AlarmMapper;

import org.opennms.horizon.alarmservice.drools.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.opennms.horizon.alarmservice")
public class AlarmRestServiceConfig {

    @Bean("alarmRestService")
    public AlarmRestService alarmRestService (
        @Autowired AlarmService alarmService) {
        return new AlarmRestServiceImpl(alarmService);
    }
}
