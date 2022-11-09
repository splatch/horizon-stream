package org.opennms.horizon.alarmservice.db.api;

import org.opennms.horizon.alarmservice.db.impl.entity.Alarm;

@Deprecated
public interface AlarmDao extends BasicDao<Alarm, Integer> {
    Alarm findByReductionKey(String reductionKey);
}
