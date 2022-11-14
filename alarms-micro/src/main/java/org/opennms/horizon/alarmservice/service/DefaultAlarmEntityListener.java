package org.opennms.horizon.alarmservice.service;

import java.util.Date;
import java.util.Set;
import org.opennms.horizon.alarmservice.api.AlarmEntityListener;
import org.opennms.horizon.alarmservice.db.entity.Alarm;
import org.opennms.horizon.alarmservice.db.entity.Memo;
import org.opennms.horizon.alarmservice.db.entity.ReductionKeyMemo;
import org.opennms.horizon.alarmservice.model.AlarmSeverity;
import org.opennms.horizon.alarmservice.model.TroubleTicketState;

public class DefaultAlarmEntityListener implements AlarmEntityListener {
    @Override
    public void onAlarmCreated(Alarm alarm) {

    }

    @Override
    public void onAlarmUpdatedWithReducedEvent(Alarm alarm) {

    }

    @Override
    public void onAlarmAcknowledged(Alarm alarm, String previousAckUser, Date previousAckTime) {

    }

    @Override
    public void onAlarmUnacknowledged(Alarm alarm, String previousAckUser, Date previousAckTime) {

    }

    @Override
    public void onAlarmSeverityUpdated(Alarm alarm, AlarmSeverity previousSeverity) {

    }

    @Override
    public void onAlarmArchived(Alarm alarm, String previousReductionKey) {

    }

    @Override
    public void onAlarmDeleted(Alarm alarm) {

    }

    @Override
    public void onStickyMemoUpdated(Alarm alarm, String previousBody, String previousAuthor, Date previousUpdated) {

    }

    @Override
    public void onReductionKeyMemoUpdated(Alarm alarm, String previousBody, String previousAuthor, Date previousUpdated) {

    }

    @Override
    public void onStickyMemoDeleted(Alarm alarm, Memo memo) {

    }

    @Override
    public void onReductionKeyMemoDeleted(Alarm alarm, ReductionKeyMemo memo) {

    }

    @Override
    public void onLastAutomationTimeUpdated(Alarm alarm, Date previousLastAutomationTime) {

    }

    @Override
    public void onRelatedAlarmsUpdated(Alarm alarm, Set<Alarm> previousRelatedAlarms) {

    }

    @Override
    public void onTicketStateChanged(Alarm alarm, TroubleTicketState previousState) {

    }
}
