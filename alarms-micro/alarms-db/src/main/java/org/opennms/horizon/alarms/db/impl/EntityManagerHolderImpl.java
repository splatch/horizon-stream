package org.opennms.horizon.alarms.db.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.Getter;

@Getter
public class EntityManagerHolderImpl implements EntityManagerHolder {

    @PersistenceContext(unitName = "dao-alarms")
    private EntityManager entityManager;
}
