package org.opennms.netmgt.provision.persistence;

import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.opennms.horizon.db.dao.api.EntityManagerHolder;
import org.opennms.horizon.db.dao.util.AbstractDaoHibernate;
import org.opennms.netmgt.provision.persistence.model.HibernateForeignSourceEntity;

@Transactional
@Slf4j
public class ForeignSourceDao extends AbstractDaoHibernate<HibernateForeignSourceEntity, String>  {

    public ForeignSourceDao(EntityManagerHolder entityManagerHolder) {
        super(entityManagerHolder, HibernateForeignSourceEntity.class);
    }
}
