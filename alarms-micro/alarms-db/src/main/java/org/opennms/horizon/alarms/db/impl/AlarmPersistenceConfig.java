package org.opennms.horizon.alarms.db.impl;

import org.opennms.horizon.alarms.db.api.AlarmDao;
import org.opennms.horizon.alarms.db.api.AlarmRepository;
import org.opennms.horizon.alarms.db.impl.dao.AlarmDaoHibernate;
import org.opennms.horizon.alarms.db.impl.dao.EntityManagerHolder;
import org.opennms.horizon.alarms.db.impl.dao.EntityManagerHolderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlarmPersistenceConfig {

    @Bean("entityManagerHolder")
    public EntityManagerHolder entityManagerHolder() {
        return new EntityManagerHolderImpl();
    }

    @Bean("alarmDao")
    public AlarmDao alarmDao(@Autowired EntityManagerHolder entityManagerHolder) {
        return new AlarmDaoHibernate(entityManagerHolder);
    }

    @Bean("alarmsRepository")
    public AlarmRepository alarmRepository(@Autowired AlarmDao alarmDao) {
        return new AlarmRepositoryImpl(alarmDao);
    }
}
