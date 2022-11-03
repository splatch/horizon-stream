package org.opennms.horizon.alarms.db.api;

import org.opennms.horizon.alarms.db.impl.dto.AlarmDTO;
import org.opennms.horizon.db.dao.api.OnmsDao;

public interface AlarmDao extends OnmsDao<AlarmDTO, Integer> {
    AlarmDTO findByReductionKey(String reductionKey);
}
