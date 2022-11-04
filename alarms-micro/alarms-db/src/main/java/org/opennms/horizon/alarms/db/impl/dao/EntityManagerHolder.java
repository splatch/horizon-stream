package org.opennms.horizon.alarms.db.impl.dao;

import javax.persistence.EntityManager;

public interface EntityManagerHolder {
    EntityManager getEntityManager();
}
