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

package org.opennms.horizon.inventory.compnent;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.opennms.cloud.grpc.minion.Identity;
import org.opennms.horizon.grpc.heartbeat.contract.HeartbeatMessage;
import org.opennms.horizon.inventory.model.MonitoringLocation;
import org.opennms.horizon.inventory.model.MonitoringSystem;
import org.opennms.horizon.inventory.repository.MonitoringLocationRepository;
import org.opennms.horizon.inventory.repository.MonitoringSystemRepository;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.google.protobuf.InvalidProtocolBufferException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
@PropertySource("classpath:application.yml")
public class MinionHeartbeatConsumer {
    //TODO: this uuid will be in the received message
    private UUID uuid = new UUID(10, 14);
    private final MonitoringSystemRepository repository;
    private final MonitoringLocationRepository locationRepository;

    @KafkaListener(topics = "${kafka.topics.minion-heartbeat}", concurrency = "1")
    public void receiveMessage(byte[] data) {
        try {
            HeartbeatMessage message = HeartbeatMessage.parseFrom(data);
            Identity identity = message.getIdentity();
            log.debug("Received heartbeat message for minion with id {} and location {}", identity.getSystemId(), identity.getLocation());
            Optional<MonitoringSystem> msOp = repository.findBySystemId(identity.getSystemId());
            if(msOp.isEmpty()) {
                Optional<MonitoringLocation> locationOp = locationRepository.findByLocation(identity.getLocation());
                MonitoringLocation location = new MonitoringLocation();
                if(locationOp.isPresent()) {
                    location = locationOp.get();
                } else {
                    location.setLocation(identity.getLocation());
                    location.setTenantId(uuid);
                    locationRepository.save(location);
                }
                MonitoringSystem monitoringSystem = new MonitoringSystem();
                monitoringSystem.setSystemId(identity.getSystemId());
                monitoringSystem.setMonitoringLocation(location);
                monitoringSystem.setTenantId(location.getTenantId());
                monitoringSystem.setLastCheckedIn(LocalDateTime.now());
                monitoringSystem.setLabel(identity.getSystemId().toUpperCase());
                monitoringSystem.setMonitoringLocationId(location.getId());
                repository.save(monitoringSystem);
            } else {
                MonitoringSystem monitoringSystem = msOp.get();
                monitoringSystem.setLastCheckedIn(LocalDateTime.now());
                repository.save(monitoringSystem);
            }
        } catch (InvalidProtocolBufferException e) {
            log.error("Invalid data from kafka", e);
        }
    }
}
