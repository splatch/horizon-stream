package org.opennms.horizon.alarmservice.db.impl.dao;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import org.opennms.horizon.alarmservice.db.api.AlarmDao;
import org.opennms.horizon.alarmservice.db.impl.entity.Alarm;

@Deprecated
@Transactional
public class AlarmDaoHibernate extends AbstractDaoHibernate<Alarm, Integer> implements AlarmDao {

    public AlarmDaoHibernate(EntityManagerHolder entityManagerHolder) {
        super(entityManagerHolder, Alarm.class);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Alarm findByReductionKey(String reductionKey) {
        TypedQuery<Alarm> query = getEntityManager().createQuery("SELECT a FROM Alarm a WHERE a.reductionKey=:reductionKey", Alarm.class);
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
