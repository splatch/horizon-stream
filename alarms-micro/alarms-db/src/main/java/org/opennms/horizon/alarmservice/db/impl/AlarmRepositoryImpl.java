package org.opennms.horizon.alarmservice.db.impl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import org.opennms.horizon.alarmservice.db.api.AlarmRepository;
import org.opennms.horizon.alarmservice.db.impl.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AlarmRepositoryImpl extends SimpleJpaRepository<Alarm, Integer> implements AlarmRepository {

    private EntityManager em;

    public AlarmRepositoryImpl(Class<Alarm> domainClass, EntityManager em) {
        super(domainClass, em);
        this.em  = em;
    }

    public Alarm findByReductionKey(String reductionKey) {
        TypedQuery<Alarm> query = em.createQuery("SELECT a FROM Alarm a WHERE a.reductionKey=:reductionKey", Alarm.class);
        query.setParameter("reductionKey", reductionKey);
        Alarm alarm = null;
        try {
            alarm = query.getSingleResult();
        } catch (NoResultException e) {
            // nothing to do
        }
        return alarm;
    }
}
