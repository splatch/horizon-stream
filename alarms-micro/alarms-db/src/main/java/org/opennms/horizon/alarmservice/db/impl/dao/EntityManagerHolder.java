package org.opennms.horizon.alarmservice.db.impl.dao;

import javax.persistence.EntityManager;

@Deprecated
public interface EntityManagerHolder {
    EntityManager getEntityManager();
}
