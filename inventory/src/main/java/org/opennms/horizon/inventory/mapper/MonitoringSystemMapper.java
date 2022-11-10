package org.opennms.horizon.inventory.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.mapstruct.Mapper;
import org.opennms.horizon.inventory.dto.MonitoringSystemDTO;
import org.opennms.horizon.inventory.model.MonitoringSystem;

@Mapper(componentModel = "spring")
public interface MonitoringSystemMapper {
    MonitoringSystem dtoToModel(MonitoringSystemDTO dto);
    MonitoringSystemDTO modelToDTO(MonitoringSystem model);

    default LocalDateTime timeStampToDateTime(long timeStamp) {
        Instant instant = Instant.ofEpochMilli(timeStamp);
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    default long dateTimeToTimestamp(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
