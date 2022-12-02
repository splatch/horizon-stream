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

package org.opennms.horizon.inventory.component;

import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.opennms.cloud.grpc.minion.RpcRequestProto;
import org.opennms.cloud.grpc.minion.RpcResponseProto;
import org.opennms.horizon.grpc.echo.contract.EchoRequest;
import org.opennms.horizon.grpc.echo.contract.EchoResponse;
import org.opennms.horizon.inventory.model.LightMonitoringSystemDTO;
import org.opennms.horizon.inventory.service.MonitoringSystemService;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class MinionRpcManager {
    private final static int DEFAULT_MESSAGE_SIZE = 1024;
    private static final int MONITOR_INITIAL_DELAY = 3_000;
    private static final int MONITOR_PERIOD = 30_000;
    private static final long ECHO_TIMEOUT = 120_000;
    private final MinionRpcClient rpcClient;
    private final MonitoringSystemService service;
    private ScheduledThreadPoolExecutor executor;

    @PostConstruct
    public void startRpc() {
        log.info("Start RPC");
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("monitoring-system-monitor-runner-%d")
                .build();
        executor = new ScheduledThreadPoolExecutor(10, threadFactory);
        service.listAllForMonitoring().forEach(this::startMonitoring);
    }

    public void addSystem(long id) {
        service.getByIdForMonitoring(id).ifPresent(this::startMonitoring);
    }

    private void startMonitoring(LightMonitoringSystemDTO systemDTO) {
        executor.scheduleAtFixedRate(() -> runRpcMonitor(systemDTO.getSystemId(),
            systemDTO.getLocation()), MONITOR_INITIAL_DELAY, MONITOR_PERIOD, TimeUnit.MILLISECONDS);
    }
    private void runRpcMonitor(String systemId, String location) {
        log.info("Sending RPC request for minion {} with location {}", systemId, location);
        EchoRequest echoRequest = EchoRequest.newBuilder()
            .setTime(System.nanoTime())
            .setMessage(Strings.repeat("*", DEFAULT_MESSAGE_SIZE))
            .build();
        RpcRequestProto request = RpcRequestProto.newBuilder()
            .setLocation(location)
            .setSystemId(systemId)
            .setModuleId("Echo")
            .setExpirationTime(System.currentTimeMillis() + ECHO_TIMEOUT)
            .setRpcId(UUID.randomUUID().toString())
            .setPayload(Any.pack(echoRequest))
            .build();
        RpcResponseProto response = rpcClient.sendRpcRequest(request);
        try {
            EchoResponse echoResponse = response.getPayload().unpack(EchoResponse.class);
            long responseTime = (System.nanoTime() - echoResponse.getTime()) / 1000000;
            log.info("Response time for minion {} is {} msecs", systemId, responseTime);
        } catch (InvalidProtocolBufferException e) {
            log.error("Unable to parse echo response", e);
        }
    }
}
