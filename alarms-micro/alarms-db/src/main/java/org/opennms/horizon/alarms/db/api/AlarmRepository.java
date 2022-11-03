package org.opennms.horizon.alarms.db.api;

import java.util.List;
import org.opennms.horizon.alarms.db.impl.dto.AlarmDTO;

public interface AlarmRepository {
    Integer save(AlarmDTO alarmDTO);
    AlarmDTO read(Integer id);
    void delete(Integer id);
    Integer update(AlarmDTO alarmDTO);
    List<AlarmDTO> read();

}
