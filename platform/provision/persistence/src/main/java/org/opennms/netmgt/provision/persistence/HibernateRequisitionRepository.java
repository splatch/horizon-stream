package org.opennms.netmgt.provision.persistence;

import org.opennms.horizon.db.dao.util.AbstractDaoHibernate;
import org.opennms.netmgt.provision.persistence.dao.HibernateRequisitionEntity;
import org.opennms.horizon.db.dao.api.PersistenceContextHolder;
import org.opennms.netmgt.provision.persistence.dao.RequisitionRepository;
import org.opennms.netmgt.provision.persistence.dto.RequisitionDTO;

public class HibernateRequisitionRepository extends AbstractDaoHibernate<HibernateRequisitionEntity, String> implements RequisitionRepository {

    public HibernateRequisitionRepository(PersistenceContextHolder persistenceContextHolder) {
        super(persistenceContextHolder, HibernateRequisitionEntity.class);
    }

    @Override
    public String save(RequisitionDTO requisitionDTO) {
        HibernateRequisitionEntity hibernateRequisitionEntity = new HibernateRequisitionEntity(requisitionDTO.getId(), requisitionDTO);
        save(hibernateRequisitionEntity);
        return hibernateRequisitionEntity.getRequisitionName();
    }

    @Override
    public RequisitionDTO read(String id) {
        HibernateRequisitionEntity hibernateRequisitionEntity = get(id);
        return hibernateRequisitionEntity.getRequisition();
    }
}
