package org.opennms.horizon.inventory.mapper;

import org.mapstruct.Mapper;
import org.opennms.horizon.inventory.dto.MonitoredServiceDTO;
import org.opennms.horizon.inventory.model.MonitoredService;

@Mapper(componentModel = "spring")
public interface MonitoredServiceMapper {
    MonitoredService dtoToModel(MonitoredServiceDTO dto);
    MonitoredServiceDTO modelToDTO(MonitoredService model);
}
