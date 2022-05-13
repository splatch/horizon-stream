package org.opennms.netmgt.provision.persistence;

import com.google.gson.Gson;
import org.opennms.netmgt.provision.persistence.dao.RequisitionRepository;
import org.opennms.netmgt.provision.persistence.dto.RequisitionDTO;

public class HibernateRequisitionRepository implements RequisitionRepository {

    Gson gson = new Gson();

    @Override
    public String save(RequisitionDTO requisitionDTO) {
//        requisitionNodeDAO.save(requisitionNodeDTO);
        String dtoStr = gson.toJson(requisitionDTO);
        return requisitionDTO.getId();
    }

    @Override
    public RequisitionDTO read(String id) {
        String dtoStr = "blah";
        return gson.fromJson(dtoStr, RequisitionDTO.class);
//       return requisitionNodeDAO.findById(id);
    }

    @Override
    public void delete(String id) {
//        requisitionNodeDAO.delete(id);
    }
}
