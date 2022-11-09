package org.opennms.horizon.alarmservice.db.impl.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.Getter;

@Getter

@Deprecated
public class EntityManagerHolderImpl implements EntityManagerHolder {

    @PersistenceContext(unitName = "dao-alarms")
    private EntityManager entityManager;
}
