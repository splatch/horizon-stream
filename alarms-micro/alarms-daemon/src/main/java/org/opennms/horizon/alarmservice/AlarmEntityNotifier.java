/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2018 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2018 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.horizon.alarmservice;

import java.util.Date;
import java.util.Set;
import org.opennms.horizon.alarms.db.impl.TroubleTicketState;
import org.opennms.horizon.alarms.db.impl.dto.AlarmDTO;
import org.opennms.horizon.alarms.db.impl.dto.MemoDTO;
import org.opennms.horizon.alarms.db.impl.dto.ReductionKeyMemoDTO;
import org.opennms.horizon.alarms.db.impl.dto.SeverityDTO;

/**
 * This interface provide functions that should be called
 * immediately after changing the alarm entities while maintaining
 * an open transaction.
 *
 * The implementation should in turn notify any interested listeners
 * i.e. northbounders, correlation engines, etc... about the state change.
 *
 * The implementation should be thread safe.
 *
 * @author jwhite
 */
public interface AlarmEntityNotifier {

    void didCreateAlarm(AlarmDTO alarm);

    void didUpdateAlarmWithReducedEvent(AlarmDTO alarm);

    void didAcknowledgeAlarm(AlarmDTO alarm, String previousAckUser, Date previousAckTime);

    void didUnacknowledgeAlarm(AlarmDTO alarm, String previousAckUser, Date previousAckTime);

    void didUpdateAlarmSeverity(AlarmDTO alarm, SeverityDTO previousSeverity);

    void didArchiveAlarm(AlarmDTO alarm, String previousReductionKey);

    void didDeleteAlarm(AlarmDTO alarm);

    void didUpdateStickyMemo(AlarmDTO onmsAlarm, String previousBody, String previousAuthor, Date previousUpdated);

    void didUpdateReductionKeyMemo(AlarmDTO onmsAlarm, String previousBody, String previousAuthor, Date previousUpdated);

    void didDeleteStickyMemo(AlarmDTO onmsAlarm, MemoDTO memo);

    void didDeleteReductionKeyMemo(AlarmDTO onmsAlarm, ReductionKeyMemoDTO memo);

    void didUpdateLastAutomationTime(AlarmDTO alarm, Date previousLastAutomationTime);

    void didUpdateRelatedAlarms(AlarmDTO alarm, Set<AlarmDTO> previousRelatedAlarms);

    void didChangeTicketStateForAlarm(AlarmDTO alarm, TroubleTicketState previousState);

}
