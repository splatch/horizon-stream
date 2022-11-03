package org.opennms.horizon.alarms.db.impl;

import javax.persistence.EntityManager;

public interface EntityManagerHolder {
    EntityManager getEntityManager();
}
