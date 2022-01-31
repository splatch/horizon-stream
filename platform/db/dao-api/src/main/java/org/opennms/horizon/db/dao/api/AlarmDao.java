package org.opennms.horizon.db.dao.api;

import org.opennms.horizon.db.model.OnmsAlarm;

public interface AlarmDao extends OnmsDao<OnmsAlarm, Integer> {
    OnmsAlarm findByReductionKey(String reductionKey);
}
