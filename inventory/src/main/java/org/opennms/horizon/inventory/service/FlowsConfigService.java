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
package org.opennms.horizon.inventory.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.Resources;
import com.google.protobuf.Any;
import lombok.RequiredArgsConstructor;
import org.opennms.horizon.inventory.dto.MonitoringLocationDTO;
import org.opennms.horizon.inventory.service.taskset.manager.TaskSetManager;
import org.opennms.horizon.shared.protobuf.util.ProtobufUtil;
import org.opennms.sink.flows.contract.FlowsConfig;
import org.opennms.taskset.contract.TaskDefinition;
import org.opennms.taskset.contract.TaskSet;
import org.opennms.taskset.contract.TaskType;
import org.opennms.taskset.service.api.TaskSetPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlowsConfigService {
    private static final Logger LOG = LoggerFactory.getLogger(FlowsConfigService.class);
    private final MonitoringLocationService monitoringLocationService;
    private final TaskSetManager taskSetManager;
    private final TaskSetPublisher taskSetPublisher;

    @EventListener(ApplicationReadyEvent.class)
    public void sendTrapConfigToMinionAfterStartup() {
        List<MonitoringLocationDTO> allLocations = monitoringLocationService.findAll();

        for (MonitoringLocationDTO dto : allLocations) {
            sendFlowsConfigToMinion(dto.getTenantId(), dto.getLocation());
        }
    }

    public void sendFlowsConfigToMinion(String tenantId, String location) {
        FlowsConfig flowsConfig = readFlowsConfig();
        publishFlowsConfig(tenantId, location, flowsConfig);
    }

    private void publishFlowsConfig(String tenantId, String location, FlowsConfig flowsConfig) {
        TaskDefinition taskDefinition = TaskDefinition.newBuilder()
            .setId("flows-config")
            .setPluginName("flows.parsers.config")
            .setType(TaskType.LISTENER)
            .setConfiguration(Any.pack(flowsConfig))
            .build();

        taskSetManager.addTaskSet(tenantId, location, taskDefinition);

        TaskSet taskSet = taskSetManager.getTaskSet(tenantId, location);
        taskSetPublisher.publishTaskSet(tenantId, location, taskSet);
    }

    @VisibleForTesting
    FlowsConfig readFlowsConfig() {
        try {
            URL url = this.getClass().getResource("/flows-config.json");
            return ProtobufUtil.fromJson(Resources.toString(url, StandardCharsets.UTF_8), FlowsConfig.class);
        } catch (IOException ex) {
            LOG.error("Fail to read flows config: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
