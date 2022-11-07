package org.opennms.horizon.inventory.mapper;

import org.mapstruct.Mapper;
import org.opennms.horizon.inventory.dto.MonitoredServiceTypeDTO;
import org.opennms.horizon.inventory.model.MonitoredServiceType;

@Mapper(componentModel = "spring")
public interface MonitoredServiceTypeMapper {
    MonitoredServiceType dtoToModel(MonitoredServiceTypeDTO dto);
    MonitoredServiceTypeDTO modelToDTO(MonitoredServiceType model);
}
