package org.opennms.netmgt.provision.persistence.dao;

import org.opennms.netmgt.provision.persistence.dto.RequisitionDTO;

public interface RequisitionRepository {
    String save(RequisitionDTO requisitionNodeDTO);
    RequisitionDTO read(String id);
    void delete(String id);
}
