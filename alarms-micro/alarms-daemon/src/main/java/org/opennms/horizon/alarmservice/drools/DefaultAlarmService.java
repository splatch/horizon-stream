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

package org.opennms.horizon.alarmservice.drools;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.opennms.horizon.alarmservice.AlarmEntityNotifier;
import org.opennms.horizon.alarmservice.db.api.AlarmRepository;
import org.opennms.horizon.alarmservice.db.impl.entity.Alarm;
import org.opennms.horizon.alarmservice.model.AlarmDTO;
import org.opennms.horizon.alarmservice.model.AlarmSeverity;
import org.opennms.horizon.events.api.EventForwarder;
import org.opennms.horizon.events.xml.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class DefaultAlarmService implements AlarmService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultAlarmService.class);

    protected static final String DEFAULT_USER = "admin";

//    @Autowired
//    private AlarmDao alarmDao;

    @Autowired
    AlarmRepository alarmRepository;

    @Autowired
    private AlarmEntityNotifier alarmEntityNotifier;

    @Autowired
    private EventForwarder eventForwarder;

//    @Autowired
//    private SessionUtils sessionUtils;

    @Override
    @Transactional
    public void clearAlarm(Alarm alarm, Date now) {
//        sessionUtils.withTransaction(() -> {
//            LOG.info("Clearing alarm with id: {} with current severity: {} at: {}", alarm.getId(), alarm.getSeverity(), now);
            final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(alarm.getId());
            if (maybeAlarmInTrans.isEmpty()) {
                LOG.warn("Alarm disappeared: {}. Skipping clear.", alarm);
                return;
            }
            Alarm alarmInTrans = maybeAlarmInTrans.get();
            final AlarmSeverity previousSeverity = alarmInTrans.getSeverity();
            alarmInTrans.setSeverity(AlarmSeverity.CLEARED);
            updateAutomationTime(alarmInTrans, now);
            alarmRepository.save(alarmInTrans);
            alarmEntityNotifier.didUpdateAlarmSeverity(alarmInTrans, previousSeverity);
//        });
    }

    @Override
    public void deleteAlarm(Alarm alarm) {
//        sessionUtils.withTransaction(() -> {
            LOG.info("Deleting alarm with id: {} with severity: {}", alarm.getId(), alarm.getSeverity());
        final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(alarm.getId());
        if (maybeAlarmInTrans.isEmpty()) {
            LOG.warn("Alarm disappeared: {}. Skipping clear.", alarm);
            return;
        }
        Alarm alarmInTrans = maybeAlarmInTrans.get();
            // If alarm was in Situation, calculate notifications for the Situation
            Map<Alarm, Set<Alarm>> priorRelatedAlarms = new HashMap<>();
            if (alarmInTrans.isPartOfSituation()) {
                for (Alarm situation : alarmInTrans.getRelatedSituations()) {
                    priorRelatedAlarms.put(situation, new HashSet<Alarm>(situation.getRelatedAlarms()));
                }
            }
            alarmRepository.delete(alarmInTrans);
            // fire notifications after alarm has been deleted
            for (Entry<Alarm, Set<Alarm>> entry : priorRelatedAlarms.entrySet()) {
                alarmEntityNotifier.didUpdateRelatedAlarms(entry.getKey(), entry.getValue());
            }
            alarmEntityNotifier.didDeleteAlarm(alarmInTrans);
//        });
    }

    @Override
    public void unclearAlarm(Alarm alarm, Date now) {
//        sessionUtils.withTransaction(() -> {
            LOG.info("Un-clearing alarm with id: {} at: {}", alarm.getId(), now);
        final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(alarm.getId());
        if (maybeAlarmInTrans.isEmpty()) {
            LOG.warn("Alarm disappeared: {}. Skipping clear.", alarm);
            return;
        }
        Alarm alarmInTrans = maybeAlarmInTrans.get();
            final AlarmSeverity previousSeverity = alarmInTrans.getSeverity();
            //TODO:MMF fix this
//            alarmInTrans.setSeverity(AlarmSeverity.get(alarmInTrans.getLastEvent().getEventSeverity()));
            updateAutomationTime(alarmInTrans, now);
            alarmRepository.save(alarmInTrans);
            alarmEntityNotifier.didUpdateAlarmSeverity(alarmInTrans, previousSeverity);
//        });
    }

    @Override
    public void escalateAlarm(Alarm alarm, Date now) {
//        sessionUtils.withTransaction(() -> {
            LOG.info("Escalating alarm with id: {} at: {}", alarm.getId(), now);
        final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(alarm.getId());
        if (maybeAlarmInTrans.isEmpty()) {
            LOG.warn("Alarm disappeared: {}. Skipping clear.", alarm);
            return;
        }
        Alarm alarmInTrans = maybeAlarmInTrans.get();
            final AlarmSeverity previousSeverity = alarmInTrans.getSeverity();
            alarmInTrans.setSeverity(AlarmSeverity.get(previousSeverity.getId() + 1));
            updateAutomationTime(alarmInTrans, now);
            alarmRepository.save(alarmInTrans);
            alarmEntityNotifier.didUpdateAlarmSeverity(alarmInTrans, previousSeverity);
//        });
    }

    @Override
    public void acknowledgeAlarm(Alarm alarm, Date now) {
    //TODO:MMF set ack on alarm only
//        sessionUtils.withTransaction(() -> {
//            LOG.info("Acknowledging alarm with id: {} @ {}", alarm.getId(), now);
//            final Alarm alarmInTrans = alarmDao.get(alarm.getId());
//            if (alarmInTrans == null) {
//                LOG.warn("Alarm disappeared: {}. Skipping ack.", alarm);
//                return;
//            }
//            OnmsAcknowledgment ack = new OnmsAcknowledgment(alarmInTrans, DEFAULT_USER, now);
//            ack.setAckAction(AckAction.ACKNOWLEDGE);
//            acknowledgmentDao.processAck(ack);
//        });
    }

    @Override
    public void unacknowledgeAlarm(Alarm alarm, Date now) {
    //TODO: set ack value on alarm only
//        sessionUtils.withTransaction(() -> {
//            LOG.info("Un-Acknowledging alarm with id: {} @ {}", alarm.getId(), now);
//            final Alarm alarmInTrans = alarmDao.get(alarm.getId());
//            if (alarmInTrans == null) {
//                LOG.warn("Alarm disappeared: {}. Skipping un-ack.", alarm);
//                return;
//            }
//            OnmsAcknowledgment ack = new OnmsAcknowledgment(alarmInTrans, DEFAULT_USER, now);
//            ack.setAckAction(AckAction.UNACKNOWLEDGE);
//            acknowledgmentDao.processAck(ack);
//        });
    }

    @Override
    public void setSeverity(Alarm alarm, AlarmSeverity severity, Date now) {
//        sessionUtils.withTransaction(() -> {
            LOG.info("Updating severity {} on alarm with id: {}", severity, alarm.getId());
        final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(alarm.getId());
        if (maybeAlarmInTrans.isEmpty()) {
            LOG.warn("Alarm disappeared: {}. Skipping clear.", alarm);
            return;
        }
        Alarm alarmInTrans = maybeAlarmInTrans.get();
            final AlarmSeverity previousSeverity = alarmInTrans.getSeverity();
            alarmInTrans.setSeverity(severity);
            updateAutomationTime(alarm, now);
            alarmRepository.save(alarmInTrans);
            alarmEntityNotifier.didUpdateAlarmSeverity(alarmInTrans, previousSeverity);
//        });
    }

    @Override
    public void sendEvent(Event e) {
        eventForwarder.sendNow(e);
    }

    @Override
    public List<AlarmDTO> getAllAlarms(String tenantId) {
//        return alarmDao.findAll();
        //TODO:MMF
        return null;
    }

    private static void updateAutomationTime(Alarm alarm, Date now) {
        if (alarm.getFirstAutomationTime() == null) {
            alarm.setFirstAutomationTime(now);
        }
        alarm.setLastAutomationTime(now);
    }

    public void setAlarmEntityNotifier(AlarmEntityNotifier alarmEntityNotifier) {
        this.alarmEntityNotifier = alarmEntityNotifier;
    }

    public void debug(String message, Object... objects) {
        LOG.debug(message, objects);
    }

    public void info(String message, Object... objects) {
        LOG.info(message, objects);
    }

    public void warn(String message, Object... objects) {
        LOG.warn(message, objects);
    }

    public void setEventForwarder(EventForwarder eventForwarder) {
        this.eventForwarder = eventForwarder;
    }

}
