package org.opennms.netmgt.provision.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.Getter;
import org.opennms.horizon.db.dao.api.PersistenceContextHolder;

@Getter
public class PersistenceContextHolderImpl implements PersistenceContextHolder {

    @PersistenceContext(unitName = "dao-provision")
    private EntityManager entityManager;
}
