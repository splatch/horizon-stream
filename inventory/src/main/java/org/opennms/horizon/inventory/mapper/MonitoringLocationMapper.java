package org.opennms.horizon.inventory.mapper;

import org.mapstruct.Mapper;
import org.opennms.horizon.inventory.dto.MonitoringLocationDTO;
import org.opennms.horizon.inventory.model.MonitoringLocation;

@Mapper
public interface MonitoringLocationMapper {
    MonitoringLocation dtoToModel(MonitoringLocationDTO dto);
    MonitoringLocationDTO modelToDTO(MonitoringLocation model);
}
