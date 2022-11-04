package org.opennms.horizon.inventory.mapper;

import org.mapstruct.Mapper;
import org.opennms.horizon.inventory.dto.NodeDTO;
import org.opennms.horizon.inventory.model.MonitoringLocation;
import org.opennms.horizon.inventory.model.Node;

@Mapper
public interface NodeMapper {
    Node dtoToModel(NodeDTO dto);
    NodeDTO modelToDTO(Node model);
}
