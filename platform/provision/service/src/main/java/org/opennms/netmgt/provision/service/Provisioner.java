package org.opennms.netmgt.provision.service;

import java.util.List;
import java.util.Optional;
import org.opennms.netmgt.provision.persistence.dto.RequisitionDTO;

public interface Provisioner {
    //TODO: is this the right place for these?
    String NODE_ID = "nodeId";
    String LOCATION = "location";
    String IP_ADDRESS = "ipAddress";
    String FOREIGN_ID = "foreignId";
    String FOREIGN_SOURCE = "foreignSource";
    String DETECTOR_NAME = "detectorName";
    String ABORT = "abort";
    String ERROR = "error";

    String publish(final RequisitionDTO requisition) throws Exception;
    Optional<RequisitionDTO> read(String name);
    void delete(String name);

    String update(final RequisitionDTO requisitionDTO) throws Exception;

    List<RequisitionDTO> read();

    void performNodeScan();
}
