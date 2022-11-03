package org.opennms.horizon.inventory.mapper;

import org.mapstruct.Mapper;
import org.opennms.horizon.inventory.dto.MonitoredServiceTypeDTO;
import org.opennms.horizon.inventory.dto.MonitoringLocationDTO;
import org.opennms.horizon.inventory.model.MonitoredServiceType;
import org.opennms.horizon.inventory.model.MonitoringLocation;

@Mapper
public interface MonitoredServiceTypeMapper {
    MonitoredServiceType dtoToModel(MonitoredServiceTypeDTO dto);
    MonitoredServiceTypeDTO modelToDTO(MonitoredServiceType model);
}
