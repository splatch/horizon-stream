package org.opennms.horizon.alarms.db.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opennms.horizon.alarms.db.api.AlarmDao;
import org.opennms.horizon.alarms.db.api.AlarmRepository;
import org.opennms.horizon.alarms.db.impl.dto.AlarmDTO;

@Slf4j
@RequiredArgsConstructor
public class AlarmRepositoryImpl implements AlarmRepository {
    private final AlarmDao alarmDao;

    @Override
    public Integer save(AlarmDTO alarmDTO) {
        //TODO: fix this
//        alarmDTO.validate();
        alarmDao.save(alarmDTO);
        log.info("Alarm '{}' persisted to database", alarmDTO.getId());
        return alarmDTO.getId();
    }

    @Override
    public AlarmDTO read(Integer id) {
        return alarmDao.get(id);
    }

    @Override
    public void delete(Integer id) {
         alarmDao.delete(id);
    }

    @Override
    public Integer update(AlarmDTO alarmDTO) {
        alarmDao.saveOrUpdate(alarmDTO);
        log.info("Alarm '{}' udpated in database", alarmDTO.getId());
        return alarmDTO.getId();
    }

    @Override
    public List<AlarmDTO> read() {
        return alarmDao.findAll().stream().collect(Collectors.toList());
    }
}
