package org.opennms.horizon.inventory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Any;
import lombok.RequiredArgsConstructor;
import org.opennms.horizon.inventory.dto.MonitoringLocationDTO;
import org.opennms.horizon.inventory.service.taskset.manager.TaskSetManager;
import org.opennms.horizon.inventory.service.trapconfig.TrapConfigBean;
import org.opennms.sink.traps.contract.ListenerConfig;
import org.opennms.sink.traps.contract.SnmpV3User;
import org.opennms.sink.traps.contract.TrapConfig;
import org.opennms.taskset.contract.TaskDefinition;
import org.opennms.taskset.contract.TaskType;
import org.opennms.taskset.service.api.TaskSetPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrapConfigService {
    private static final Logger LOG = LoggerFactory.getLogger(TrapConfigService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MonitoringLocationService monitoringLocationService;
    private final TaskSetManager taskSetManager;
    private final TaskSetPublisher taskSetPublisher;

    @EventListener(ApplicationReadyEvent.class)
    public void sendTrapConfigToMinionAfterStartup() {
        List<MonitoringLocationDTO> allLocations = monitoringLocationService.findAll();

        for(MonitoringLocationDTO dto : allLocations) {
            sendTrapConfigToMinion(dto.getTenantId(), dto.getLocation());
        }
    }

    public void sendTrapConfigToMinion(String tenantId, String location) {
        TrapConfigBean trapConfigBean = readTrapConfig(tenantId);
        TrapConfig trapConfig = mapBeanToProto(trapConfigBean);
        publishTrapConfig(location, trapConfig);
    }

    private TrapConfig mapBeanToProto(TrapConfigBean config) {
        return TrapConfig.newBuilder()
            .setSnmpTrapAddress(config.getSnmpTrapAddress())
            .setSnmpTrapPort(config.getSnmpTrapPort())
            .setNewSuspectOnTrap(config.getNewSuspectOnTrap())
            .setIncludeRawMessage(config.isIncludeRawMessage())
//            .setUseAddressFromVarbind(config.shouldUseAddressFromVarbind())
            .setListenerConfig(ListenerConfig.newBuilder()
                .setBatchIntervalMs(config.getBatchIntervalMs())
                .setBatchSize(config.getBatchSize())
                .setQueueSize(config.getQueueSize())
                .setNumThreads(config.getNumThreads()))
            .addAllSnmpV3User(mapSnmpV3Users(config))
            .build();
    }

    private List<SnmpV3User> mapSnmpV3Users(TrapConfigBean config) {
        return config.getSnmpV3Users().stream().map(snmpV3User -> {
            return SnmpV3User.newBuilder()
                .setEngineId(snmpV3User.getEngineId())
                .setAuthPassphrase(snmpV3User.getAuthPassphrase())
                .setAuthProtocol(snmpV3User.getAuthProtocol())
                .setPrivacyPassphrase(snmpV3User.getPrivacyPassphrase())
                .setPrivacyProtocol(snmpV3User.getPrivacyProtocol())
                .build();
        }).collect(Collectors.toList());
    }

    private void publishTrapConfig(String location, TrapConfig trapConfig) {
        TaskDefinition taskDefinition = TaskDefinition.newBuilder()
            .setId("traps-config")
            .setPluginName("trapd.listener.config")
            .setType(TaskType.LISTENER)
            .setConfiguration(Any.pack(trapConfig))
            .build();

        taskSetManager.addTaskSet(location, taskDefinition);
        taskSetPublisher.publishTaskSet(location, taskSetManager.getTaskSet(location));
    }

    private TrapConfigBean readTrapConfig(String tenantId) {
        try {
            URL url = this.getClass().getResource("/trapd-config.json");
            return objectMapper.readValue(url, TrapConfigBean.class);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
