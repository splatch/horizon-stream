package org.opennms.horizon.inventory.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.opennms.horizon.inventory.dto.MonitoringLocationsDTO;
import org.opennms.horizon.inventory.model.MonitoringLocations;

@Mapper
public interface MonitoringLocationsMapper {
    @Mapping(target="tenant_id", source="dto.tenantId")
    MonitoringLocations dtoToModel(MonitoringLocationsDTO dto);
    @Mapping(target="tenantId", source="model.tenant_id")
    MonitoringLocationsDTO modelToDTO(MonitoringLocations model);
}
