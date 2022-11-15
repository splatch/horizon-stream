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
import org.opennms.horizon.alarmservice.api.AlarmService;
import org.opennms.horizon.alarmservice.utils.StripedExt;
import org.opennms.horizon.alarmservice.api.AlarmEntityNotifier;
import org.opennms.horizon.alarmservice.api.AlarmRepository;
import org.opennms.horizon.alarmservice.db.entity.Alarm;
import org.opennms.horizon.alarmservice.model.AlarmDTO;
import org.opennms.horizon.alarmservice.model.AlarmSeverity;
import org.opennms.horizon.alarmservice.model.Severity;
import org.opennms.horizon.alarmservice.model.mapper.AlarmMapper;
import org.opennms.horizon.alarmservice.utils.SystemProperties;
import org.opennms.horizon.events.proto.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@ComponentScan(basePackages = "org.opennms.horizon.alarmservice")
public class AlarmServiceImpl implements AlarmService {
    private static final Logger LOG = LoggerFactory.getLogger(AlarmServiceImpl.class);

    protected static final Integer THREADS = SystemProperties.getInteger("org.opennms.alarmd.threads", 4);
    protected static final Integer NUM_STRIPE_LOCKS = SystemProperties.getInteger("org.opennms.alarmd.stripe.locks", THREADS * 4);
    protected static boolean NEW_IF_CLEARED = Boolean.getBoolean("org.opennms.alarmd.newIfClearedAlarmExists");
    protected static boolean LEGACY_ALARM_STATE = Boolean.getBoolean("org.opennms.alarmd.legacyAlarmState");

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

//    @Autowired
//    private EventForwarder eventForwarder;

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
        Alarm alarm1 = alarmRepository.getById(alarm.getId());
        alarm1.setAlarmAckTime(new Date());
        alarm1.setAlarmAckUser("TODO: need a user!");
        alarmRepository.save(alarm1);
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
    public void setSeverity(Long id, AlarmSeverity severity, Date now) {
        Alarm alarm = alarmRepository.getById(id);

        alarm.setSeverity(severity);
        // where to set this date?
//        alarm.set?

        alarmRepository.save(alarm);
    }

    @Override
    public void sendEvent(Event e) {
//        eventForwarder.sendNow(e);
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
    public Alarm process(Event event) {
        Objects.requireNonNull(event, "Cannot create alarm from null event.");
        if (!checkEventSanityAndDoWeProcess(event)) {
            return null;
        }

        if (log.isDebugEnabled()) {
//            log.debug("process: {}; nodeid: {}; ipaddr: {}; serviceid: {}", event.getUei(), event.getNodeid(), event.getInterface(), event.getService());
        }

        // Lock both the reduction and clear keys (if set) using a fair striped lock
        // We do this to ensure that clears and triggers are processed in the same order
        // as the calls are made
        final Iterable<Lock> locks = lockStripes.bulkGet(getLockKeys(event));
        final Alarm[] alarm = new Alarm[1];
        try {
            locks.forEach(Lock::lock);

            alarm[0] = addOrReduceEventAsAlarm(event);
        } finally {
            locks.forEach(Lock::unlock);
        }

        return alarm[0];
    }

    @Transactional
    protected Alarm addOrReduceEventAsAlarm(Event event) throws IllegalStateException {


        //TODO:MMF
//        final String reductionKey = event.getAlarmData().getReductionKey();
        String reductionKey = "blah";
        log.debug("addOrReduceEventAsAlarm: looking for existing reduction key: {}", reductionKey);

        String key = reductionKey;
//        String clearKey = event.getAlarmData().getClearKey();
        String clearKey = "blah";

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

            alarm = createNewAlarm(event);

            alarmRepository.save(alarm);

            alarmEntityNotifier.didCreateAlarm(alarm);
        } else {
            log.debug("addOrReduceEventAsAlarm: reductionKey:{} found, reducing event to existing alarm: {}", reductionKey, alarm.getId());
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
        LOG.debug(message, objects);
    }

    public void info(String message, Object... objects) {
        LOG.info(message, objects);
    }

    public void warn(String message, Object... objects) {
        LOG.warn(message, objects);
    }

//    public void setEventForwarder(EventForwarder eventForwarder) {
//        this.eventForwarder = eventForwarder;
//    }

    private boolean isResolutionEvent(Event event) {
//        return Objects.equals(event.getAlarmData().getAlarmType(), Integer.valueOf(Alarm.RESOLUTION_TYPE));
        return false;
    }

    private static boolean checkEventSanityAndDoWeProcess(final Event event) {
//        if (event.getLogmsg() != null && LogDestType.DONOTPERSIST.toString().equalsIgnoreCase(event.getLogmsg().getDest())) {
//            if (log.isDebugEnabled()) {
//                log.debug("checkEventSanity: uei '{}' marked as '{}'; not processing event.", event.getUei(), LogDestType.DONOTPERSIST);
//            }
//            return false;
//        }
//
//        if (event.getAlarmData() == null) {
//            if (log.isDebugEnabled()) {
//                log.debug("checkEventSanity: uei '{}' has no alarm data; not processing event.", event.getUei());
//            }
//            return false;
//        }
//
//        if (event.getDbid() <= 0) {
//            throw new IllegalArgumentException("Incoming event has an illegal dbid (" + event.getDbid() + "), aborting");
//        }

        return true;
    }

    private static Collection<String> getLockKeys(Event event) {
//        if (event.getAlarmData().getClearKey() == null) {
//            return Collections.singletonList(event.getAlarmData().getReductionKey());
//        } else {
//            return Arrays.asList(event.getAlarmData().getReductionKey(), event.getAlarmData().getClearKey());
//        }
        return null;
    }

    //TODO:MMF For now just create a minimal event
    private Alarm createNewAlarm(Event event) {
        Alarm alarm = new Alarm();
        // Situations are denoted by the existance of related-reductionKeys
//        alarm.setRelatedAlarms(getRelatedAlarms(event.getParmCollection()), event.getTime());
        alarm.setAlarmType(1);
//        alarm.setClearKey(event.getAlarmData().getClearKey());
        alarm.setCounter(1);
//        alarm.setDescription(e.getEventDescr());
//        alarm.setDistPoller(e.getDistPoller());
//        alarm.setFirstEventTime(e.getEventTime());
//        alarm.setIfIndex(e.getIfIndex());
//        alarm.setIpAddr(e.getIpAddr());
//        alarm.setLastEventTime(e.getEventTime());
//        alarm.setLastEvent(e);
//        alarm.setLogMsg(e.getEventLogMsg());
//        alarm.setMouseOverText(e.getEventMouseOverText());
//        alarm.setNode(e.getNode());
//        alarm.setOperInstruct(e.getEventOperInstruct());
//        alarm.setReductionKey(event.getAlarmData().getReductionKey());
//        alarm.setServiceType(e.getServiceType());
//        alarm.setSeverity(SeverityDTO.get(e.getEventSeverity()));
//        alarm.setSuppressedUntil(e.getEventTime()); //UI requires this be set
//        alarm.setSuppressedTime(e.getEventTime()); // UI requires this be set
        alarm.setUei(event.getUei() + event.getNodeId());
//        if (event.getAlarmData().getManagedObject() != null) {
//            alarm.setManagedObjectType(event.getAlarmData().getManagedObject().getType());
//        }
//        e.setAlarm(alarm);
        return alarm;
    }

}
