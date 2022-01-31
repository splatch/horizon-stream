package org.opennms.horizon.db.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.opennms.horizon.db.dao.api.AlarmDao;
import org.opennms.horizon.db.model.OnmsAlarm;

@Transactional
public class AlarmDaoHibernate extends AbstractDaoHibernate<OnmsAlarm, Integer> implements AlarmDao {

    public AlarmDaoHibernate() {
        super(OnmsAlarm.class);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public OnmsAlarm findByReductionKey(String reductionKey) {
        TypedQuery<OnmsAlarm> query = getEntityManager().createQuery("SELECT a FROM OnmsAlarm a WHERE a.reductionKey=:reductionKey", OnmsAlarm.class);
        query.setParameter("reductionKey", reductionKey);
        OnmsAlarm alarm = null;
        try {
            alarm = query.getSingleResult();
        } catch (NoResultException e) {
            // nothing to do
        }
        return alarm;
    }

}
