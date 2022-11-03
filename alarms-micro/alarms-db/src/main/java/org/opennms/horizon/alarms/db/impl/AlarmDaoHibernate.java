package org.opennms.horizon.alarms.db.impl;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import org.opennms.horizon.alarms.db.api.AlarmDao;
import org.opennms.horizon.alarms.db.impl.dto.AlarmDTO;

@Transactional
public class AlarmDaoHibernate extends AbstractDaoHibernate<AlarmDTO, Integer> implements AlarmDao {

    public AlarmDaoHibernate(EntityManagerHolder entityManagerHolder) {
        super(entityManagerHolder, AlarmDTO.class);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public AlarmDTO findByReductionKey(String reductionKey) {
        TypedQuery<AlarmDTO> query = getEntityManager().createQuery("SELECT a FROM alarms a WHERE a.reductionKey=:reductionKey", AlarmDTO.class);
        query.setParameter("reductionKey", reductionKey);
        AlarmDTO alarm = null;
        try {
            alarm = query.getSingleResult();
        } catch (NoResultException e) {
            // nothing to do
        }
        return alarm;
    }

}
