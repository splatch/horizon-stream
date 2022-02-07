package org.opennms.horizon.db.dao.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.opennms.horizon.db.dao.api.AcknowledgmentDao;
import org.opennms.horizon.db.model.OnmsAcknowledgment;

public class AcknowledgmentDaoHibernate extends AbstractDaoHibernate<OnmsAcknowledgment, Integer> implements AcknowledgmentDao {
    public AcknowledgmentDaoHibernate() {
        super(OnmsAcknowledgment.class);
    }

    @Override
    public void processAck(OnmsAcknowledgment ack) {
        // FIXME: OOPS:
        throw new UnsupportedOperationException("OOPS");
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OnmsAcknowledgment> findLatestAcks(Date from) {
        final String hqlQuery = "SELECT acks FROM OnmsAcknowledgment acks " +
                "WHERE acks.ackTime = (" +
                "SELECT MAX(filteredAcks.ackTime) " +
                "FROM OnmsAcknowledgment filteredAcks " +
                "WHERE filteredAcks.refId = acks.refId) " +
                "AND acks.id = (" +
                "SELECT MAX(filteredAcks.id) FROM OnmsAcknowledgment filteredAcks " +
                "WHERE filteredAcks.refId = acks.refId) " +
                "AND acks.ackTime >= (:minAckTimeParm)";

        TypedQuery<OnmsAcknowledgment> query = getEntityManager().createQuery(hqlQuery, OnmsAcknowledgment.class);
        query.setParameter("minAckTimeParm", from);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<OnmsAcknowledgment> findLatestAckForRefId(Integer refId) {
        TypedQuery<OnmsAcknowledgment> query = getEntityManager().createQuery("SELECT a FROM OnmsAcknowledgment  a WHERE a.refId=:refId ORDER BY a.ackTime, a.id DESC ", OnmsAcknowledgment.class);
        query.setParameter("refId", refId);
        try {
            return Optional.of(query.getSingleResult());
        }  catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
