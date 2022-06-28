package org.opennms.netmgt.provision.persistence.model;

import java.util.List;
import org.opennms.netmgt.provision.persistence.dto.ForeignSourceDTO;

public interface ForeignSourceRepository {
    String save(ForeignSourceDTO foreignSourceDTO);
    ForeignSourceDTO read(String id);
    void delete(String id);
    String update(ForeignSourceDTO foreignSourceDTO);
    List<ForeignSourceDTO> read();

}
