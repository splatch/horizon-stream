package org.opennms.horizon.db.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.Getter;
import org.opennms.horizon.db.dao.api.PersistenceContextHolder;

@Getter
public class PersistenceContextHolderImpl implements PersistenceContextHolder {

    @PersistenceContext(unitName = "dao-hibernate")
    private EntityManager entityManager;
}
