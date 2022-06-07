package org.opennms.netmgt.provision.persistence;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.opennms.horizon.db.dao.util.AbstractDaoHibernate;
import org.opennms.netmgt.provision.persistence.dao.HibernateRequisitionEntity;
import org.opennms.horizon.db.dao.api.PersistenceContextHolder;
import org.opennms.netmgt.provision.persistence.dao.RequisitionRepository;
import org.opennms.netmgt.provision.persistence.dto.RequisitionDTO;

@Transactional
@Slf4j
public class HibernateRequisitionRepository extends AbstractDaoHibernate<HibernateRequisitionEntity, String> implements RequisitionRepository {

    public HibernateRequisitionRepository(PersistenceContextHolder persistenceContextHolder) {
        super(persistenceContextHolder, HibernateRequisitionEntity.class);
    }

    @Override
    public String save(RequisitionDTO requisitionDTO) {
        requisitionDTO.validate();
        HibernateRequisitionEntity hibernateRequisitionEntity = new HibernateRequisitionEntity(requisitionDTO.getId(), requisitionDTO);
        save(hibernateRequisitionEntity);
        log.info("Requisition {} persisted to database", hibernateRequisitionEntity.getRequisitionName());
        return hibernateRequisitionEntity.getRequisitionName();
    }

    @Override
    public RequisitionDTO read(String id) {
        HibernateRequisitionEntity hibernateRequisitionEntity = get(id);
        return hibernateRequisitionEntity.getRequisition();
    }

    @Override
    public String update(RequisitionDTO requisitionDTO) {
        HibernateRequisitionEntity hibernateRequisitionEntity = new HibernateRequisitionEntity(requisitionDTO.getId(), requisitionDTO);
        saveOrUpdate(hibernateRequisitionEntity);
        log.info("Requisition {} udpated in database", hibernateRequisitionEntity.getRequisitionName());
        return hibernateRequisitionEntity.getRequisitionName();
    }

    @Override
    public List<RequisitionDTO> read() {
        return super.findAll().stream().map(entity -> entity.getRequisition()).collect(Collectors.toList());
    }
}
