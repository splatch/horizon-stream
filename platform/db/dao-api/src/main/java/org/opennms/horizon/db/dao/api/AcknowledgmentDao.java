package org.opennms.horizon.db.dao.api;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.opennms.horizon.db.model.OnmsAcknowledgment;

public interface AcknowledgmentDao extends OnmsDao<OnmsAcknowledgment, Integer> {

    void processAck(OnmsAcknowledgment ack);

    /**
     * <p>findLatestAcks</p>
     *
     * Finds the latest acknowledgement for each refId. The latest acknowledgement is selected based on the most recent
     * ackTime (and highest Id in the case of multiple occuring at the same time).
     *
     * @param from limit results to acks created on or after
     * @return the list of latest acks (empty list in the case of no acks found)
     */
    List<OnmsAcknowledgment> findLatestAcks(Date from);

    /**
     * <p>findLatestAckForRefId</p>
     *
     * Finds the latest acknowledgement for the given refId. The latest acknowledgement is selected based on the most
     * recent ackTime (and highest Id in the case of multiple occurring at the same time).
     *
     * @param refId the refId to search for
     * @return an optional containing the latest ack for the given refId or Optional.empty() if none found
     */
    Optional<OnmsAcknowledgment> findLatestAckForRefId(Integer refId);
}
