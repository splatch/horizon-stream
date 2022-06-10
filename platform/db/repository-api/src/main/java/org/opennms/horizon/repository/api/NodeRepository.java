package org.opennms.horizon.repository.api;

import org.opennms.horizon.db.model.OnmsMonitoringLocation;
import org.opennms.horizon.db.model.OnmsNode;

public interface NodeRepository  extends BasicCRUD<OnmsNode> {
    OnmsMonitoringLocation get(String location);
    void saveMonitoringLocation(OnmsMonitoringLocation onmsMonitoringLocation);
}
