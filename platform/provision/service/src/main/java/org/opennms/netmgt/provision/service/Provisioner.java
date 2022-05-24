package org.opennms.netmgt.provision.service;

import java.util.Optional;
import org.opennms.netmgt.provision.persistence.dto.RequisitionDTO;

public interface Provisioner {
    String publish(final RequisitionDTO requisition) throws Exception;
    Optional<RequisitionDTO> read(String name);
    void delete(String name);

    String update(final RequisitionDTO requisitionDTO) throws Exception;
}
