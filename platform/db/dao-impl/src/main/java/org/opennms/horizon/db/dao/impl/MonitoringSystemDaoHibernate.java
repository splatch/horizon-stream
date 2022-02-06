package org.opennms.horizon.db.dao.impl;

import org.opennms.horizon.db.dao.api.MonitoringSystemDao;
import org.opennms.horizon.db.model.OnmsMonitoringSystem;

public class MonitoringSystemDaoHibernate extends AbstractDaoHibernate<OnmsMonitoringSystem, String> implements MonitoringSystemDao {

    public MonitoringSystemDaoHibernate() {
        super(OnmsMonitoringSystem.class);
    }
}
