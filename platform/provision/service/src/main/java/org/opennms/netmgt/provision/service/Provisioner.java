package org.opennms.netmgt.provision.service;

import java.util.Optional;
import org.opennms.netmgt.provision.persistence.dto.RequisitionDTO;

public interface Provisioner {
    String publishRequisition(final RequisitionDTO requisition) throws Exception;
    Optional<RequisitionDTO> read(String name);
    void delete(String name);
}
