package org.opennms.netmgt.provision.persistence.dao;

import java.util.List;
import org.opennms.netmgt.provision.persistence.dto.RequisitionDTO;

public interface RequisitionRepository {
    String save(RequisitionDTO requisitionNodeDTO);
    RequisitionDTO read(String id);
    void delete(String id);
    String update(RequisitionDTO requisitionDTO);
    List<RequisitionDTO> read();
}
