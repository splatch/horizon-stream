package org.opennms.horizon.db.dao.api;

import org.opennms.horizon.db.model.OnmsDistPoller;

public interface DistPollerDao  extends OnmsDao<OnmsDistPoller, String> {

    public static final String DEFAULT_DIST_POLLER_ID = "00000000-0000-0000-0000-000000000000";

    OnmsDistPoller whoami();

}
