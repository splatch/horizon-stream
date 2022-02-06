package org.opennms.netmgt.eventd.processor;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.opennms.horizon.db.dao.api.DistPollerDao;
import org.opennms.horizon.db.dao.api.EventDao;
import org.opennms.horizon.db.dao.api.MonitoringSystemDao;
import org.opennms.horizon.db.dao.api.NodeDao;
import org.opennms.horizon.db.dao.api.SessionUtils;
import org.opennms.horizon.db.model.OnmsEvent;
import org.opennms.horizon.db.model.OnmsSeverity;
import org.opennms.horizon.events.api.EventDatabaseConstants;
import org.opennms.horizon.events.api.EventProcessorException;
import org.opennms.horizon.events.xml.Event;
import org.opennms.horizon.events.xml.Header;
import org.opennms.horizon.events.xml.Log;
import org.opennms.netmgt.eventd.EventUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class HibernateEventWriter implements EventWriter {
    private static final Logger LOG = LoggerFactory.getLogger(HibernateEventWriter.class);

    public static final String LOG_MSG_DEST_DO_NOT_PERSIST = "donotpersist";
    public static final String LOG_MSG_DEST_SUPRRESS = "suppress";
    public static final String LOG_MSG_DEST_LOG_AND_DISPLAY = "logndisplay";
    public static final String LOG_MSG_DEST_LOG_ONLY = "logonly";
    public static final String LOG_MSG_DEST_DISPLAY_ONLY = "displayonly";

    private final Timer writeTimer;
    private final EventUtil eventUtil;
    private final SessionUtils sessionUtils;
    private final EventDao eventDao;
    private final NodeDao nodeDao;
    private final DistPollerDao distPollerDao;
    private final MonitoringSystemDao monitoringSystemDao;

    public HibernateEventWriter(MetricRegistry registry, EventUtil eventUtil, SessionUtils sessionUtils,
                                EventDao eventDao, NodeDao nodeDao, DistPollerDao distPollerDao,
                                MonitoringSystemDao monitoringSystemDao) {
        writeTimer = Objects.requireNonNull(registry).timer("eventlogs.process.write");
        this.eventUtil = Objects.requireNonNull(eventUtil);
        this.sessionUtils = Objects.requireNonNull(sessionUtils);
        this.eventDao = Objects.requireNonNull(eventDao);
        this.nodeDao = Objects.requireNonNull(nodeDao);
        this.distPollerDao = Objects.requireNonNull(distPollerDao);
        this.monitoringSystemDao = Objects.requireNonNull(monitoringSystemDao);
    }

    /**
     * Event writing is always synchronous so this method just
     * delegates to {@link #process(Log)}.
     */
    @Override
    public void process(Log eventLog, boolean synchronous) throws EventProcessorException {
        process(eventLog);
    }

    @Override
    public void process(Log eventLog) throws EventProcessorException {
        if (eventLog != null && eventLog.getEvents() != null) {
            final List<Event> eventsInLog = eventLog.getEvents().getEventCollection();
            // This shouldn't happen, but just to be safe...
            if (eventsInLog == null) {
                return;
            }

            // Find the events in the log that need to be persisted
            final List<Event> eventsToPersist = eventsInLog.stream()
                    .filter(e -> checkEventSanityAndDoWeProcess(e, "HibernateEventWriter"))
                    .collect(Collectors.toList());

            // If there are no events to persist, avoid creating a database transaction
            if (eventsToPersist.size() < 1) {
                return;
            }

            // Time the transaction and insertions
            try (Timer.Context context = writeTimer.time()) {
                final AtomicReference<EventProcessorException> exception = new AtomicReference<>();

                sessionUtils.withTransaction(() -> {
                    for (Event eachEvent : eventsToPersist) {
                        try {
                            process(eventLog.getHeader(), eachEvent);
                        } catch (EventProcessorException e) {
                            exception.set(e);
                            return;
                        }
                    }
                });

                if (exception.get() != null) {
                    throw exception.get();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * The method that inserts the event into the database
     */
    private void process(final Header eventHeader, final Event event) throws EventProcessorException {
        LOG.debug("HibernateEventWriter: processing {}, nodeid: {}, ipaddr: {}, serviceid: {}, time: {}", event.getUei(), event.getNodeid(), event.getInterface(), event.getService(), event.getTime());

        try {
            final OnmsEvent ovent = createOnmsEvent(eventHeader, event);
            eventDao.save(ovent);

            // Update the event with the database ID of the event stored in the database
            event.setDbid(ovent.getId());
        } catch (Throwable e) {
            throw new EventProcessorException("Unexpected exception while storing event: " + event.toString(), e);
        }
    }

    /**
     * Creates OnmsEvent to be inserted afterwards.
     *
     * @exception java.lang.NullPointerException
     *                Thrown if a required resource cannot be found in the
     *                properties file.
     */
    private OnmsEvent createOnmsEvent(final Header eventHeader, final Event event) {

        OnmsEvent ovent = new OnmsEvent();

        // eventID
        //ovent.setId(event.getDbid());

        // eventUEI
        ovent.setEventUei(EventDatabaseConstants.format(event.getUei(), EVENT_UEI_FIELD_SIZE));

        // nodeID
        if (event.hasNodeid()) {
            ovent.setNode(nodeDao.get(event.getNodeid().intValue()));
        }

        // eventTime
        ovent.setEventTime(event.getTime());

        // eventHost
        // Resolve the event host to a hostname using the ipInterface table
        ovent.setEventHost(EventDatabaseConstants.format(eventUtil.getEventHost(event), EVENT_HOST_FIELD_SIZE));

        // eventSource
        ovent.setEventSource(EventDatabaseConstants.format(event.getSource(), EVENT_SOURCE_FIELD_SIZE));

        // ipAddr
        ovent.setIpAddr(event.getInterfaceAddress());

        // ifindex
        if (event.hasIfIndex()) {
            ovent.setIfIndex(event.getIfIndex());
        } else {
            ovent.setIfIndex(null);
        }

        // systemId

        // If available, use the header's distPoller
        if (eventHeader != null && eventHeader.getDpName() != null && !"".equals(eventHeader.getDpName().trim())) {
            // TODO: Should we also try a look up the value in the MinionDao and LocationMonitorDao here?
            ovent.setDistPoller(distPollerDao.get(eventHeader.getDpName()));
        }
        // Otherwise, use the event's distPoller
        if (ovent.getDistPoller() == null && event.getDistPoller() != null && !"".equals(event.getDistPoller().trim())) {
            ovent.setDistPoller(monitoringSystemDao.get(event.getDistPoller()));
        }
        // And if both are unavailable, use the local system as the event's source system
        if (ovent.getDistPoller() == null) {
            ovent.setDistPoller(distPollerDao.whoami());
        }

        // FIXME: OOPS
        /*
        // eventSnmpHost
        ovent.setEventSnmpHost(EventDatabaseConstants.format(event.getSnmphost(), EVENT_SNMPHOST_FIELD_SIZE));

        // service
        ovent.setServiceType(serviceTypeDao.findByName(event.getService()));

        // eventSnmp
        ovent.setEventSnmp(event.getSnmp() == null ? null : SnmpInfo.format(event.getSnmp(), EVENT_SNMP_FIELD_SIZE));
        */

        // eventParms
        ovent.setEventParametersFromEvent(event);

        // eventCreateTime
        // TODO: We are overriding the 'eventcreatetime' field of the event with a new Date
        // representing the storage time of the event. 'eventcreatetime' should really be
        // renamed to something like 'eventpersisttime' since that is closer to its meaning.
        ovent.setEventCreateTime(new Date());

        // eventDescr
        ovent.setEventDescr(EventDatabaseConstants.format(event.getDescr(), 0));

        // eventLoggroup
        ovent.setEventLogGroup(event.getLoggroupCount() > 0 ? EventDatabaseConstants.format(event.getLoggroup(), EVENT_LOGGRP_FIELD_SIZE) : null);

        // eventLogMsg
        // eventLog
        // eventDisplay
        if (event.getLogmsg() != null) {
            // set log message
            ovent.setEventLogMsg(EventDatabaseConstants.format(event.getLogmsg().getContent(), 0));
            String logdest = event.getLogmsg().getDest();
            if (LOG_MSG_DEST_LOG_AND_DISPLAY.equals(logdest)) {
                // if 'logndisplay' set both log and display column to yes
                ovent.setEventLog(String.valueOf(MSG_YES));
                ovent.setEventDisplay(String.valueOf(MSG_YES));
            } else if (LOG_MSG_DEST_LOG_ONLY.equals(logdest)) {
                // if 'logonly' set log column to true
                ovent.setEventLog(String.valueOf(MSG_YES));
                ovent.setEventDisplay(String.valueOf(MSG_NO));
            } else if (LOG_MSG_DEST_DISPLAY_ONLY.equals(logdest)) {
                // if 'displayonly' set display column to true
                ovent.setEventLog(String.valueOf(MSG_NO));
                ovent.setEventDisplay(String.valueOf(MSG_YES));
            } else if (LOG_MSG_DEST_SUPRRESS.equals(logdest)) {
                // if 'suppress' set both log and display to false
                ovent.setEventLog(String.valueOf(MSG_NO));
                ovent.setEventDisplay(String.valueOf(MSG_NO));
            }
        } else {
            ovent.setEventLogMsg(null);
            ovent.setEventLog(String.valueOf(MSG_YES));
            ovent.setEventDisplay(String.valueOf(MSG_YES));
        }

        // eventSeverity
        ovent.setEventSeverity(OnmsSeverity.get(event.getSeverity()).getId());

        // eventPathOutage
        ovent.setEventPathOutage(event.getPathoutage() != null ? EventDatabaseConstants.format(event.getPathoutage(), EVENT_PATHOUTAGE_FIELD_SIZE) : null);

        // FIXME: OOPS
        // eventCorrelation
        //ovent.setEventCorrelation(event.getCorrelation() != null ? Correlation.format(event.getCorrelation(), EVENT_CORRELATION_FIELD_SIZE) : null);

        // eventSuppressedCount
        ovent.setEventSuppressedCount(null);

        // eventOperInstruct
        ovent.setEventOperInstruct(EventDatabaseConstants.format(event.getOperinstruct(), 0));

        // FIXME: OOPS
        /*
        // eventAutoAction
        ovent.setEventAutoAction(event.getAutoactionCount() > 0 ? AutoAction.format(event.getAutoaction(), EVENT_AUTOACTION_FIELD_SIZE) : null);

        // eventOperAction / eventOperActionMenuText
        if (event.getOperactionCount() > 0) {
            final List<Operaction> a = new ArrayList<>();
            final List<String> b = new ArrayList<>();

            for (final Operaction eoa : event.getOperactionCollection()) {
                a.add(eoa);
                b.add(eoa.getMenutext());
            }
            ovent.setEventOperAction(OperatorAction.format(a, EVENT_OPERACTION_FIELD_SIZE));
            ovent.setEventOperActionMenuText(EventDatabaseConstants.format(b, EVENT_OPERACTION_FIELD_SIZE));
        } else {
            ovent.setEventOperAction(null);
            ovent.setEventOperActionMenuText(null);
        }
        */

        // eventNotification, this column no longer needed
        ovent.setEventNotification(null);

        // eventTroubleTicket / eventTroubleTicket state
        if (event.getTticket() != null) {
            ovent.setEventTTicket(EventDatabaseConstants.format(event.getTticket().getContent(), EVENT_TTICKET_FIELD_SIZE));
            ovent.setEventTTicketState("on".equals(event.getTticket().getState()) ? 1 : 0);
        } else {
            ovent.setEventTTicket(null);
            ovent.setEventTTicketState(null);
        }

        // FIXME: OOPS
        // eventForward
        //ovent.setEventForward(event.getForwardCount() > 0 ? Forward.format(event.getForward(), EVENT_FORWARD_FIELD_SIZE) : null);

        // eventmouseOverText
        ovent.setEventMouseOverText(EventDatabaseConstants.format(event.getMouseovertext(), EVENT_MOUSEOVERTEXT_FIELD_SIZE));

        // eventAckUser
        if (event.getAutoacknowledge() != null && "on".equals(event.getAutoacknowledge().getState())) {
            ovent.setEventAckUser(EventDatabaseConstants.format(event.getAutoacknowledge().getContent(), EVENT_ACKUSER_FIELD_SIZE));
            // eventAckTime - if autoacknowledge is present,
            // set time to event create time
            ovent.setEventAckTime(ovent.getEventCreateTime());
        } else {
            ovent.setEventAckUser(null);
            ovent.setEventAckTime(null);
        }
        return ovent;
    }

    private static boolean checkEventSanityAndDoWeProcess(Event event, String logPrefix) {
        Assert.notNull(event, "event argument must not be null");

        /*
         * Check value of <logmsg> attribute 'dest', if set to
         * "donotpersist" or "suppress" then simply return, the UEI is not to be
         * persisted to the database
         */
        Assert.notNull(event.getLogmsg(), "event does not have a logmsg");
        if (
                LOG_MSG_DEST_DO_NOT_PERSIST.equalsIgnoreCase(event.getLogmsg().getDest()) ||
                        LOG_MSG_DEST_SUPRRESS.equalsIgnoreCase(event.getLogmsg().getDest())
        ) {
            LOG.debug("{}: uei '{}' marked as '{}'; not processing event.", logPrefix, event.getUei(), event.getLogmsg().getDest());
            return false;
        }
        return true;
    }
}
