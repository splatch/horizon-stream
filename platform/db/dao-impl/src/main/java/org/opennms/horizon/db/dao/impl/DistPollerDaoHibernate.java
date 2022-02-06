package org.opennms.horizon.db.dao.impl;

import org.opennms.horizon.db.dao.api.DistPollerDao;
import org.opennms.horizon.db.model.OnmsDistPoller;

public class DistPollerDaoHibernate extends AbstractDaoHibernate<OnmsDistPoller, String> implements DistPollerDao {

    public DistPollerDaoHibernate() {
        super(OnmsDistPoller.class);
    }

    @Override
    public OnmsDistPoller whoami() {
        // Return the OnmsDistPoller with the default UUID
        return get(DEFAULT_DIST_POLLER_ID);
    }
}
