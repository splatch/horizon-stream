package org.opennms.horizon.alarmservice.db.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.aries.jpa.template.JpaTemplate;
import org.opennms.horizon.alarmservice.db.api.AlarmDao;
import org.opennms.horizon.alarmservice.db.api.AlarmRepository;
import org.opennms.horizon.alarmservice.db.api.SessionUtils;
import org.opennms.horizon.alarmservice.db.impl.dao.AlarmDaoHibernate;
import org.opennms.horizon.alarmservice.db.impl.dao.EntityManagerHolder;
import org.opennms.horizon.alarmservice.db.impl.dao.EntityManagerHolderImpl;
import org.opennms.horizon.alarmservice.db.impl.dao.SessionUtilsImpl;
import org.opennms.horizon.alarmservice.db.impl.entity.Alarm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlarmPersistenceConfig {

//    TODO:MMF still specify unitName?
    @PersistenceContext(unitName = "dao-alarms")
    private EntityManager entityManager;

    @Bean("alarmRepository")
    public AlarmRepository alarmRepository() {
        return new AlarmRepositoryImpl(Alarm.class, entityManager);
    }

    //TODO:MMF do we not need this now with teh auto generated repository?
//    @Bean("alarmDao")
//    public AlarmDao alarmDao(@Autowired EntityManagerHolder entityManagerHolder) {
//        return new AlarmDaoHibernate(entityManagerHolder);
//    }
}
