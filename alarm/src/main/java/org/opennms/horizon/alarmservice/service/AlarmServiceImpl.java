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

package org.opennms.horizon.alarmservice.service;

import com.google.common.util.concurrent.Striped;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.opennms.horizon.alarmservice.api.AlarmEntityNotifier;
import org.opennms.horizon.alarmservice.api.AlarmService;
import org.opennms.horizon.alarmservice.db.entity.Alarm;
import org.opennms.horizon.alarmservice.db.repository.AlarmRepository;
import org.opennms.horizon.alarmservice.model.AlarmDTO;
import org.opennms.horizon.alarmservice.model.AlarmSeverity;
import org.opennms.horizon.alarmservice.model.Severity;
import org.opennms.horizon.alarmservice.utils.StripedExt;
import org.opennms.horizon.alarmservice.utils.SystemProperties;
import org.opennms.horizon.events.proto.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class AlarmServiceImpl implements AlarmService {

    protected static final Integer THREADS = SystemProperties.getInteger("org.opennms.alarmd.threads", 4);
    protected static final Integer NUM_STRIPE_LOCKS = SystemProperties.getInteger("org.opennms.alarmd.stripe.locks", THREADS * 4);
    protected static boolean NEW_IF_CLEARED = Boolean.getBoolean("org.opennms.alarmd.newIfClearedAlarmExists");
    protected static boolean LEGACY_ALARM_STATE = Boolean.getBoolean("org.opennms.alarmd.legacyAlarmState");

    public static final String ALARM_RULES_NAME = "alarm";

    private boolean createNewAlarmIfClearedAlarmExists = LEGACY_ALARM_STATE == true ? false : NEW_IF_CLEARED;

    protected static final String DEFAULT_USER = "admin";

    @Autowired
    AlarmRepository alarmRepository;

    @Autowired
    private AlarmEntityNotifier alarmEntityNotifier;

    @Autowired
    private AlarmMapper alarmMapper;

    private boolean legacyAlarmState = LEGACY_ALARM_STATE;

    private Striped<Lock> lockStripes = StripedExt.fairLock(NUM_STRIPE_LOCKS);

    @Override
    @Transactional
    public AlarmDTO clearAlarm(Alarm alarm, Date now) {
            log.info("Clearing alarm with id: {} with current severity: {} at: {}", alarm.getAlarmId(), alarm.getSeverity(), now);
            final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(alarm.getAlarmId());
            if (maybeAlarmInTrans.isEmpty()) {
                log.warn("Alarm disappeared: {}. Skipping clear.", alarm);
                return null;
            }
            Alarm alarmInTrans = maybeAlarmInTrans.get();
            final AlarmSeverity previousSeverity = alarmInTrans.getSeverity();
            alarmInTrans.setSeverity(AlarmSeverity.CLEARED);
            updateAutomationTime(alarmInTrans, now);
            alarmRepository.save(alarmInTrans);

            List<Alarm> associatedAlarms = alarm.getAssociatedAlarms().stream().map(alarmAssociation -> alarmAssociation.getRelatedAlarmId()).collect(
                Collectors.toList());

            associatedAlarms.forEach(associatedAlarm -> clearAlarm(associatedAlarm, now));

            alarmEntityNotifier.didUpdateAlarmSeverity(alarmInTrans, previousSeverity);

            return alarmMapper.alarmToAlarmDTO(alarmInTrans);
    }

    @Override
    @Transactional
    public AlarmDTO clearAlarm(Long alarmId, Date now) {
        return clearAlarm(alarmRepository.getById(alarmId), now);
    }

    @Override
    @Transactional
    public AlarmDTO deleteAlarm(Alarm alarm) {
        return deleteAlarm(alarm.getAlarmId());
    }

    @Override
    @Transactional
    public AlarmDTO deleteAlarm(Long id) {
        final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(id);

        if (maybeAlarmInTrans.isEmpty()) {
            log.warn("Alarm with Id {}  disappeared. Skipping clear.", id);
            return null;
        }
        Alarm alarmInTrans = maybeAlarmInTrans.get();

        log.info("Deleting alarm with id: {} with severity: {}", alarmInTrans.getAlarmId(), alarmInTrans.getSeverity());

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

            return alarmMapper.alarmToAlarmDTO(alarmInTrans);
    }

    @Override
    @Transactional
    public AlarmDTO unclearAlarm(Alarm alarm, Date now) {
            log.info("Un-clearing alarm with id: {} at: {}", alarm.getAlarmId(), now);
        final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(alarm.getAlarmId());
        if (maybeAlarmInTrans.isEmpty()) {
            log.warn("Alarm disappeared: {}. Skipping clear.", alarm);
            return null;
        }
        return doAlarmUnclear(now, maybeAlarmInTrans.get());
    }

    @Override
    @Transactional
    public AlarmDTO unclearAlarm(Long alarmId, Date now) {
        log.info("Un-clearing alarm with id: {} at: {}", alarmId, now);
        final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(alarmId);
        if (maybeAlarmInTrans.isEmpty()) {
            log.warn("Alarm disappeared: {}. Skipping clear.", alarmId);
            return null;
        }
        return doAlarmUnclear(now, maybeAlarmInTrans.get());
    }

    private AlarmDTO doAlarmUnclear(Date now, Alarm alarm) {
        final AlarmSeverity previousSeverity = alarm.getSeverity();
        alarm.setSeverity(alarm.getLastEventSeverity());
        updateAutomationTime(alarm, now);
        alarmRepository.save(alarm);
        alarmEntityNotifier.didUpdateAlarmSeverity(alarm, previousSeverity);

        return alarmMapper.alarmToAlarmDTO(alarm);

    }

    @Override
    @Transactional
    public AlarmDTO escalateAlarm(Alarm alarm, Date now) {
            log.info("Escalating alarm with id: {} at: {}", alarm.getAlarmId(), now);
        final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(alarm.getAlarmId());
        if (maybeAlarmInTrans.isEmpty()) {
            log.warn("Alarm disappeared: {}. Skipping clear.", alarm);
            return null;
        }
        return doEscalateAlarm(now, maybeAlarmInTrans);
    }

    @Override
    @Transactional
    public AlarmDTO escalateAlarm(Long alarmId, Date now) {
        log.info("Escalating alarm with id: {} at: {}", alarmId, now);
        final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(alarmId);
        if (maybeAlarmInTrans.isEmpty()) {
            log.warn("Alarm disappeared: {}. Skipping clear.", alarmId);
            return null;
        }
        return doEscalateAlarm(now, maybeAlarmInTrans);
    }

    private AlarmDTO doEscalateAlarm(Date now, Optional<Alarm> maybeAlarmInTrans) {
        Alarm alarmInTrans = maybeAlarmInTrans.get();
        final AlarmSeverity previousSeverity = alarmInTrans.getSeverity();
        alarmInTrans.setSeverity(AlarmSeverity.escalate(previousSeverity));
        updateAutomationTime(alarmInTrans, now);
        alarmRepository.save(alarmInTrans);
        alarmEntityNotifier.didUpdateAlarmSeverity(alarmInTrans, previousSeverity);

        return alarmMapper.alarmToAlarmDTO(alarmInTrans);
    }

    @Override
    @Transactional
    public AlarmDTO acknowledgeAlarm(Alarm alarm, Date now, String userId) {
        final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(alarm.getAlarmId());
        if (maybeAlarmInTrans.isEmpty()) {
            log.warn("Alarm disappeared: {}. Skipping clear.", alarm);
            return null;
        }

        return doAcknowledgeAlarm(userId, maybeAlarmInTrans.get());
    }

    @Override
    @Transactional
    public AlarmDTO acknowledgeAlarm(Long alarmId, Date now, String userId) {
        final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(alarmId);
        if (maybeAlarmInTrans.isEmpty()) {
            log.warn("Alarm disappeared: {}. Skipping clear.", alarmId);
            return null;
        }

        return doAcknowledgeAlarm(userId, maybeAlarmInTrans.get());
    }

    private AlarmDTO doAcknowledgeAlarm(String userId, Alarm alarm) {
        alarm.setAlarmAckTime(new Date());
        alarm.setAlarmAckUser(userId);
        alarmRepository.save(alarm);

        return alarmMapper.alarmToAlarmDTO(alarm);
    }

    @Override
    @Transactional
    public AlarmDTO unAcknowledgeAlarm(Long alarmId, Date now) {
        final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(alarmId);
        if (maybeAlarmInTrans.isEmpty()) {
            log.warn("Alarm disappeared: {}. Skipping clear.", alarmId);
            return null;
        }

        return doUnacknowledgeAlarm(maybeAlarmInTrans.get());
    }

    @Override
    @Transactional
    public AlarmDTO unAcknowledgeAlarm(Alarm alarm, Date now) {
        final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(alarm.getAlarmId());
        if (maybeAlarmInTrans.isEmpty()) {
            log.warn("Alarm disappeared: {}. Skipping clear.", alarm);
            return null;
        }

        return doUnacknowledgeAlarm(maybeAlarmInTrans.get());
    }

    private AlarmDTO doUnacknowledgeAlarm(Alarm alarm) {
        alarm.setAlarmAckTime(null);
        alarm.setAlarmAckUser(null);
        alarmRepository.save(alarm);

        return alarmMapper.alarmToAlarmDTO(alarm);
    }

    @Override
    @Transactional
    public AlarmDTO setSeverity(Alarm alarm, AlarmSeverity severity, Date now) {
            log.info("Updating severity {} on alarm with id: {}", severity, alarm.getAlarmId());
        final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(alarm.getAlarmId());
        if (maybeAlarmInTrans.isEmpty()) {
            log.warn("Alarm disappeared: {}. Skipping clear.", alarm);
            return null;
        }
        return doSetSeverity(severity, now, maybeAlarmInTrans.get());
    }

    @Override
    @Transactional
    public AlarmDTO setSeverity(Long alarmId, AlarmSeverity severity, Date now) {
        log.info("Updating severity to {} on alarm with id: {}", severity, alarmId);
        final Optional<Alarm> maybeAlarmInTrans = alarmRepository.findById(alarmId);
        if (maybeAlarmInTrans.isEmpty()) {
            log.warn("Alarm disappeared: {}. Skipping clear.", alarmId);
            return null;
        }
        return doSetSeverity(severity, now, maybeAlarmInTrans.get());
    }

    private AlarmDTO doSetSeverity(AlarmSeverity severity, Date now, Alarm alarmInTrans) {
        final AlarmSeverity previousSeverity = alarmInTrans.getSeverity();
        alarmInTrans.setSeverity(severity);
        updateAutomationTime(alarmInTrans, now);
        alarmRepository.save(alarmInTrans);
        alarmEntityNotifier.didUpdateAlarmSeverity(alarmInTrans, previousSeverity);

        return alarmMapper.alarmToAlarmDTO(alarmInTrans);
    }

    @Override
    public List<AlarmDTO> getAllAlarms(String tenantId) {
        List<Alarm> alarms = alarmRepository.findAll();

        List<AlarmDTO> dtoAlarmList =
            alarms
                .stream()
                .map(alarm -> alarmMapper.alarmToAlarmDTO(alarm))
                .collect(Collectors.toList());

        return dtoAlarmList;
    }

    @Override
    public AlarmDTO process(Event event) {
        log.info("########  Received Event, processing");
        Objects.requireNonNull(event, "Cannot create alarm from null event.");

        log.debug("process: {}; nodeid: {}; ipaddr: {}", event.getUei(), event.getNodeId(), event.getIpAddress());

        // Lock both the reduction and clear keys (if set) using a fair striped lock
        // We do this to ensure that clears and triggers are processed in the same order
        // as the calls are made
        //TODO:MMF what is this doing?
//        final Iterable<Lock> locks = lockStripes.bulkGet(getLockKeys(event));
        final Alarm[] alarm = new Alarm[1];
        try {
//            locks.forEach(Lock::lock);

            alarm[0] = addOrReduceEventAsAlarm(event);
        } finally {
//            locks.forEach(Lock::unlock);
        }

        return alarmMapper.alarmToAlarmDTO(alarm[0]);
    }

    @Override
    public void removeStickyMemo(long alarmId) {

        Alarm targetAlarm = alarmRepository.getById(alarmId);

        if (targetAlarm != null) {
            if (targetAlarm.getStickyMemo() != null) {
                //TODO:MMF how do we get the memo from repo through only that one interface?
                // Will just nulling out the memo (object) field delete it from the memo table?
                // might need to specify orphan removal on field in the entity
                targetAlarm.setStickyMemo(null);
                alarmRepository.save(targetAlarm);
            }
        }

    }

    protected Alarm addOrReduceEventAsAlarm(Event event) throws IllegalStateException {

        String reductionKey = String.format("%s:%s:%s", event.getUei(), event.getNodeId(), "TODO:Need tenant id");
        log.debug("addOrReduceEventAsAlarm: looking for existing reduction key: {}", reductionKey);

        String key = reductionKey;
        String clearKey = reductionKey;

        boolean didSwapReductionKeyWithClearKey = false;
        if (!legacyAlarmState && clearKey != null && isResolutionEvent(event)) {
            key = clearKey;
            didSwapReductionKeyWithClearKey = true;
        }

        Alarm alarm = alarmRepository.findByReductionKey(key);

        if (alarm == null && didSwapReductionKeyWithClearKey) {
            // if the clearKey returns null, still need to check the reductionKey
            alarm = alarmRepository.findByReductionKey(reductionKey);
        }

        if (alarm == null || (createNewAlarmIfClearedAlarmExists && Severity.CLEARED.equals(alarm.getSeverity()))) {
            if (log.isDebugEnabled()) {
                log.debug("addOrReduceEventAsAlarm: reductionKey:{} not found, instantiating new alarm", reductionKey);
            }

            if (alarm != null) {
                log.debug("addOrReduceEventAsAlarm: \"archiving\" cleared Alarm for problem: {}; " +
                    "A new alarm will be instantiated to manage the problem.", reductionKey);
                alarm.archive();
                alarmRepository.saveAndFlush(alarm);

                alarmEntityNotifier.didArchiveAlarm(alarm, reductionKey);
            }

            alarm = createNewAlarm(event, reductionKey);

            alarmRepository.save(alarm);

            alarmEntityNotifier.didCreateAlarm(alarm);
        } else {
            log.debug("addOrReduceEventAsAlarm: reductionKey:{} found, reducing event to existing alarm: {}", reductionKey, alarm.getAlarmId());
//            reduceEvent(persistedEvent, alarm, event);

            alarmRepository.save(alarm);

//            if (event.getAlarmData().isAutoClean()) {
//                m_eventDao.deletePreviousEventsForAlarm(alarm.getId(), persistedEvent);
//            }

            alarmEntityNotifier.didUpdateAlarmWithReducedEvent(alarm);
        }
        return alarm;
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
        log.debug(message, objects);
    }

    public void info(String message, Object... objects) {
        log.info(message, objects);
    }

    public void warn(String message, Object... objects) {
        log.warn(message, objects);
    }

    private boolean isResolutionEvent(Event event) {
//        return Objects.equals(event.getAlarmData().getAlarmType(), Integer.valueOf(Alarm.RESOLUTION_TYPE));
        return false;
    }

    private static Collection<String> getLockKeys(Event event) {
//        if (event.getAlarmData().getClearKey() == null) {
//            return Collections.singletonList(event.getAlarmData().getReductionKey());
//        } else {
//            return Arrays.asList(event.getAlarmData().getReductionKey(), event.getAlarmData().getClearKey());
//        }
        return null;
    }

    private Alarm createNewAlarm(Event event, String reductionKey) {
        Alarm alarm = new Alarm();
        // Situations are denoted by the existance of related-reductionKeys
//        alarm.setRelatedAlarms(getRelatedAlarms(event.getParmCollection()), event.getTime());
        alarm.setAlarmType(1);
        alarm.setClearKey(reductionKey);
        alarm.setCounter(1);
//        alarm.setDescription(e.getEventDescr());
//        alarm.setDistPoller(e.getDistPoller());
//        alarm.setFirstEventTime(e.getEventTime());
//        alarm.setIfIndex(e.getIfIndex());
//        alarm.setIpAddr(e.getIpAddr());
        Date now = new Date();
        alarm.setLastEventTime(now);
        alarm.setLastAutomationTime(now);
        //TODO:MMF can we pull this from event parameters?
        alarm.setLastEventSeverity(AlarmSeverity.CRITICAL);
        alarm.setSeverity(AlarmSeverity.CRITICAL);
//        alarm.setLastEvent(e);
//        alarm.setLogMsg(e.getEventLogMsg());
//        alarm.setMouseOverText(e.getEventMouseOverText());
//        alarm.setNode(e.getNode());
//        alarm.setOperInstruct(e.getEventOperInstruct());
        alarm.setReductionKey(reductionKey);
//        alarm.setServiceType(e.getServiceType());
//        alarm.setSuppressedUntil(e.getEventTime()); //UI requires this be set
//        alarm.setSuppressedTime(e.getEventTime()); // UI requires this be set
        alarm.setEventUei(event.getUei() + event.getNodeId());
        alarm.setX733ProbableCause(1);
        alarm.setDetails(new HashMap<>());
        alarm.setRelatedAlarms(new HashSet<>());
//        if (event.getAlarmData().getManagedObject() != null) {
//            alarm.setManagedObjectType(event.getAlarmData().getManagedObject().getType());
//        }
//        e.setAlarm(alarm);
        return alarm;
    }

}
