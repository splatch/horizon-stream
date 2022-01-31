package org.opennms.horizon.db.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        // FIXME: OOPS:
        throw new UnsupportedOperationException("OOPS");
//
//        final String hqlQuery = "SELECT acks FROM OnmsAcknowledgment acks " +
//                "WHERE acks.ackTime = (" +
//                "SELECT MAX(filteredAcks.ackTime) " +
//                "FROM OnmsAcknowledgment filteredAcks " +
//                "WHERE filteredAcks.refId = acks.refId) " +
//                "AND acks.id = (" +
//                "SELECT MAX(filteredAcks.id) FROM OnmsAcknowledgment filteredAcks " +
//                "WHERE filteredAcks.refId = acks.refId) " +
//                "AND acks.ackTime >= (:minAckTimeParm)";
//        return (List<OnmsAcknowledgment>) getHibernateTemplate().findByNamedParam(hqlQuery, "minAckTimeParm", from);
    }

    @Override
    public Optional<OnmsAcknowledgment> findLatestAckForRefId(Integer refId) {
        // FIXME: OOPS:
        throw new UnsupportedOperationException("OOPS");
//
//        CriteriaBuilder builder = new CriteriaBuilder(OnmsAcknowledgment.class)
//                .eq("refId", refId)
//                .limit(1)
//                .orderBy("ackTime").desc()
//                .orderBy("id").desc();
//        List<OnmsAcknowledgment> acks = findMatching(builder.toCriteria());
//
//        return acks.size() == 1 ? Optional.of(acks.get(0)) : Optional.empty();
    }
}
