package org.opennms.horizon.db.dao.api;

import org.opennms.horizon.db.model.OnmsEvent;

public interface EventDao extends OnmsDao<OnmsEvent, Integer> {

    int deletePreviousEventsForAlarm(final Integer id, final OnmsEvent e);

}
