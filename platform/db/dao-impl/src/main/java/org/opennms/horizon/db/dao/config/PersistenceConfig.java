package org.opennms.horizon.db.dao.config;

import org.opennms.horizon.db.dao.api.AcknowledgmentDao;
import org.opennms.horizon.db.dao.api.AlarmDao;
import org.opennms.horizon.db.model.mapper.AlarmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfig {




        @Bean("taskSetIgniteReceiverService")
        public AlarmRestService
        taskSetIgniteReceiverService(
            @Autowired AlarmDao alarmDao,
            @Autowired AlarmMapper alarmMapper,
            @Autowired AcknowledgmentDao acknowledgmentDao
        ) {
            return new AlarmRestServiceImpl(alarmDao, alarmMapper, acknowledgmentDao);
        }

}
