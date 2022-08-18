/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2022 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2022 The OpenNMS Group, Inc.
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

package org.opennms.horizon.core.monitor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.prometheus.client.Gauge;
import org.opennms.horizon.db.dao.api.IpInterfaceDao;
import org.opennms.horizon.db.dao.api.NodeDao;
import org.opennms.horizon.db.dao.api.SessionUtils;
import org.opennms.horizon.db.model.OnmsIpInterface;
import org.opennms.horizon.db.model.OnmsNode;
import org.opennms.horizon.events.api.EventConstants;
import org.opennms.horizon.events.api.EventListener;
import org.opennms.horizon.events.api.EventSubscriptionService;
import org.opennms.horizon.events.model.IEvent;
import org.opennms.horizon.metrics.api.OnmsMetricsAdapter;
import org.opennms.horizon.shared.snmp.SnmpAgentConfig;
import org.opennms.horizon.shared.snmp.SnmpObjId;
import org.opennms.horizon.shared.snmp.proxy.LocationAwareSnmpClient;
import org.opennms.netmgt.icmp.proxy.LocationAwarePingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class DeviceMonitorManager implements EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceMonitorManager.class);
    private static final String SYS_OBJECTID_INSTANCE = ".1.3.6.1.2.1.1.2.0";
    private static final Long INVALID_UP_TIME = -1L;
    private static final String DEFAULT_LOCATION = "Default";
    private final LocationAwarePingClient locationAwarePingClient;
    private final LocationAwareSnmpClient locationAwareSnmpClient;
    private final EventSubscriptionService eventSubscriptionService;
    private final NodeDao nodeDao;
    private final IpInterfaceDao ipInterfaceDao;
    private final SessionUtils sessionUtils;
    private final OnmsMetricsAdapter metricsAdapter;
    private final List<OnmsNode> nodeCache = new ArrayList<>();
    private final ThreadFactory monitorThreadFactory = new ThreadFactoryBuilder()
        .setNameFormat("monitor-runner-%d")
        .build();
    // Cache SNMP Uptime for each Interface.
    private final Map<String, Long> snmpUpTimeCache = new ConcurrentHashMap<>();
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(25, monitorThreadFactory);


    public DeviceMonitorManager(EventSubscriptionService eventSubscriptionService,
                                NodeDao nodeDao,
                                IpInterfaceDao ipInterfaceDao,
                                SessionUtils sessionUtils,
                                LocationAwarePingClient locationAwarePingClient,
                                LocationAwareSnmpClient locationAwareSnmpClient,
                                OnmsMetricsAdapter metricsAdapter) {
        this.eventSubscriptionService = eventSubscriptionService;
        this.nodeDao = nodeDao;
        this.ipInterfaceDao = ipInterfaceDao;
        this.sessionUtils = sessionUtils;
        this.locationAwarePingClient = locationAwarePingClient;
        this.locationAwareSnmpClient = locationAwareSnmpClient;
        this.metricsAdapter = metricsAdapter;
    }

    public void init() {
        eventSubscriptionService.addEventListener(this);
        // Add all nodes currently present in the inventory to cache.
        sessionUtils.withReadOnlyTransaction(() -> nodeCache.addAll(nodeDao.findAll()));
        nodeCache.forEach(onmsNode -> {
            LOG.info("Starting device monitoring for device with ID {}", onmsNode.getId());
            scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> runMonitors(onmsNode), 5, 120, TimeUnit.SECONDS);
        });
    }


    private void runMonitors(OnmsNode onmsNode) {
        try {
            List<OnmsIpInterface> ipInterfaces = sessionUtils.withReadOnlyTransaction(() -> ipInterfaceDao.findInterfacesByNodeId(onmsNode.getId()));
            ipInterfaces.forEach(onmsIpInterface -> {
                LOG.info("Polling ICMP/SNMP Monitor for IPAddress {}", onmsIpInterface.getIpAddress());
                pollIcmp(onmsIpInterface.getIpAddress());
                pollSnmp(onmsIpInterface.getIpAddress(), onmsNode.getSnmpCommunityString());
            });
        } catch (Exception e) {
            LOG.error("Exception while running monitors for device with Id : {}", onmsNode.getId(), e);
        }
    }

    private void pollIcmp(InetAddress inetAddress) {
        try {
            locationAwarePingClient.ping(inetAddress).withLocation(DEFAULT_LOCATION)
                .withTimeout(60, TimeUnit.SECONDS)
                .execute()
                .whenComplete(((pingSummary, throwable) -> {
                    if (throwable != null) {
                        LOG.error("Exception while pinging IpAddress {}", inetAddress.getHostAddress(), throwable);
                    } else if (pingSummary != null && pingSummary.getSequence(0) != null) {
                        // we are only pinging one IpAddress, use sequence 0
                        double icmpResponseTime = pingSummary.getSequence(0).getResponse().getRtt();
                        LOG.info("ICMP Round trip time for IPAddress {} : {}", inetAddress.getHostAddress(), icmpResponseTime);
                        addIcmpMetric(icmpResponseTime, inetAddress.getHostAddress());
                    }
                }));
        } catch (Exception e) {
            LOG.error("Exception while executing ping for {}", inetAddress, e);
        }
    }

    private void pollSnmp(InetAddress inetAddress, String snmpCommunityString) {
        try {
            final SnmpAgentConfig agentConfig = new SnmpAgentConfig();
            agentConfig.setAddress(inetAddress);
            agentConfig.setReadCommunity(snmpCommunityString);
            agentConfig.setTimeout(18000);
            agentConfig.setRetries(2);
            locationAwareSnmpClient.get(agentConfig, SnmpObjId.get(SYS_OBJECTID_INSTANCE))
                .withLocation(DEFAULT_LOCATION)
                .withDescription("Device-Monitor")
                .withTimeToLive(60000L)
                .execute().whenComplete(((snmpValue, throwable) -> {
                    if (throwable != null) {
                        LOG.error("Exception while detecting Snmp service at IpAddress {}", inetAddress.getHostAddress(), throwable);
                        addSnmpMetrics(inetAddress.getHostAddress(), false);
                    } else if (snmpValue != null && !snmpValue.isError()) {
                        LOG.info("SNMP is Up at IpAddress {}", inetAddress.getHostAddress());
                        addSnmpMetrics(inetAddress.getHostAddress(), true);
                    }
                }));
        } catch (Exception e) {
            LOG.error("Exception while detecting SNMP service on IpAddress {}", inetAddress);
        }
    }

    private void addIcmpMetric(double responseTime, String ipAddress) {
        Gauge rttGauge = Gauge.build().name("icmp_round_trip_time").help("ICMP round trip time")
            .unit("msec").labelNames("instance").create();
        rttGauge.labels(ipAddress).set(responseTime);
        metricsAdapter.push(rttGauge);
    }

    private void addSnmpMetrics(String ipAddress, boolean status) {
        Gauge upTimeGauge = Gauge.build().name("snmp_uptime").help("SNMP Up Time")
            .unit("sec").labelNames("instance").create();
        if (status) {
            Long prevUpTimeInMsec = snmpUpTimeCache.get(ipAddress);
            long totalUpTimeInMsec = 0;
            if (prevUpTimeInMsec != null && prevUpTimeInMsec.longValue() != INVALID_UP_TIME) {
                totalUpTimeInMsec = System.currentTimeMillis() - prevUpTimeInMsec;
            }
            snmpUpTimeCache.put(ipAddress, System.currentTimeMillis());

            long totalUpTimeInSec = TimeUnit.MILLISECONDS.toSeconds(totalUpTimeInMsec);
            upTimeGauge.labels(ipAddress).set(totalUpTimeInSec);
        } else {
            upTimeGauge.labels(ipAddress).set(INVALID_UP_TIME);
            snmpUpTimeCache.put(ipAddress, INVALID_UP_TIME);
        }
        metricsAdapter.push(upTimeGauge);
    }

    @Override
    public String getName() {
        return "Device-Monitor-Manager";
    }

    @Override
    public void onEvent(IEvent event) {
        if (event.getUei().equals(EventConstants.NODE_ADDED_EVENT_UEI)) {
            Long nodeId = event.getNodeid();
            if (nodeId != null) {
                OnmsNode node = sessionUtils.withReadOnlyTransaction(() -> nodeDao.get(nodeId.intValue()));
                scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> runMonitors(node), 0, 120, TimeUnit.SECONDS);
            }
        }
    }

    public void shutdown() {
        eventSubscriptionService.removeEventListener(this);
        scheduledThreadPoolExecutor.shutdown();
    }

}
