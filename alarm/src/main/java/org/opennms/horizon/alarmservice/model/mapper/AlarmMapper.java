package org.opennms.horizon.alarmservice.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.opennms.horizon.alarmservice.db.entity.Alarm;
import org.opennms.horizon.alarmservice.model.AlarmDTO;

@Mapper
public interface AlarmMapper {

    AlarmMapper INSTANCE = Mappers.getMapper( AlarmMapper.class );

    AlarmDTO alarmToAlarmDTO(Alarm alarm);
    Alarm alarmDTOToAlarm(AlarmDTO alarmDTO);
}
