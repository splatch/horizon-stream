package org.opennms.netmgt.provision.persistence;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opennms.netmgt.provision.persistence.dto.ForeignSourceDTO;
import org.opennms.netmgt.provision.persistence.model.ForeignSourceRepository;
import org.opennms.netmgt.provision.persistence.model.HibernateForeignSourceEntity;
import org.opennms.netmgt.provision.persistence.model.HibernateRequisitionEntity;

@Slf4j
@RequiredArgsConstructor
public class ForeignSourceRepositoryImpl implements ForeignSourceRepository {
    private final ForeignSourceDao foreignSourceDao;

    @Override
    public String save(ForeignSourceDTO foreignSourceDTO) {
        foreignSourceDTO.validate();
        HibernateForeignSourceEntity foreignSourceEntity = new HibernateForeignSourceEntity(foreignSourceDTO.getName(), foreignSourceDTO);
        foreignSourceDao.save(foreignSourceEntity);
        log.info("Foreign Source '{}' persisted to database", foreignSourceEntity.getName());
        return foreignSourceEntity.getName();
    }

    @Override
    public ForeignSourceDTO read(String id) {
        HibernateForeignSourceEntity hibernateForeignSourceEntity = foreignSourceDao.get(id);
        return hibernateForeignSourceEntity == null ? null:hibernateForeignSourceEntity.getForeignSource();
    }

    @Override
    public void delete(String id) {
         foreignSourceDao.delete(id);
    }

    @Override
    public String update(ForeignSourceDTO foreignSourceDTO) {
        HibernateForeignSourceEntity foreignSourceEntity = new HibernateForeignSourceEntity(foreignSourceDTO.getName(), foreignSourceDTO);
        foreignSourceDao.saveOrUpdate(foreignSourceEntity);
        log.info("Foreign Source '{}' udpated in database", foreignSourceEntity.getName());
        return foreignSourceEntity.getName();
    }

    @Override
    public List<ForeignSourceDTO> read() {
        return foreignSourceDao.findAll().stream().map(entity -> entity.getForeignSource()).collect(Collectors.toList());
    }
}
