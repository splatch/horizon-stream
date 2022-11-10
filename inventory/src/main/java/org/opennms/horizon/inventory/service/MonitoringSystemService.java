package org.opennms.horizon.inventory.service;

import lombok.RequiredArgsConstructor;

import org.opennms.cloud.grpc.minion.Identity;
import org.opennms.horizon.grpc.heartbeat.contract.HeartbeatMessage;
import org.opennms.horizon.inventory.dto.MonitoringSystemDTO;
import org.opennms.horizon.inventory.mapper.MonitoringSystemMapper;
import org.opennms.horizon.inventory.model.MonitoringLocation;
import org.opennms.horizon.inventory.model.MonitoringSystem;
import org.opennms.horizon.inventory.repository.MonitoringLocationRepository;
import org.opennms.horizon.inventory.repository.MonitoringSystemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitoringSystemService {
    //TODO: this uuid will be in the received message
    private UUID uuid = new UUID(10, 14);
    private final MonitoringSystemRepository modelRepo;
    private final MonitoringLocationRepository locationRepository;
    private final MonitoringSystemMapper mapper;

    public MonitoringSystemDTO saveMonitoringSystem(MonitoringSystemDTO dto) {
        MonitoringSystem model = mapper.dtoToModel(dto);

        MonitoringLocation location = locationRepository.getReferenceById(dto.getMonitoringLocationId());
        model.setMonitoringLocation(location);

        MonitoringSystem ret = modelRepo.save(model);
        return mapper.modelToDTO(ret);
    }

    public List<MonitoringSystemDTO> findAllMonitoringSystems() {
        List<MonitoringSystem> all = modelRepo.findAll();
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }

    public Optional<MonitoringSystemDTO> findMonitoringSystem(long id) {
        Optional<MonitoringSystem> model = modelRepo.findById(id);
        Optional<MonitoringSystemDTO> dto = Optional.empty();
        if (model.isPresent()) {
            dto = Optional.of(mapper.modelToDTO(model.get()));
        }
        return dto;
    }

    public List<MonitoringSystemDTO> findByTenantId(String tenantId) {
        UUID tenantUUID = UUID.fromString(tenantId);
        List<MonitoringSystem> all = modelRepo.findByTenantId(tenantUUID);
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }

    public Optional<MonitoringSystemDTO> findBySystemId(String systemId, String tenantId) {
        return modelRepo.findBySystemIdAndTenantId(systemId, UUID.fromString(tenantId)).map(mapper::modelToDTO);
    }

    public void addMonitoringSystemFromHeartbeat(HeartbeatMessage message) {
        Identity identity = message.getIdentity();
        Optional<MonitoringSystem> msOp = modelRepo.findBySystemId(identity.getSystemId());
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
            modelRepo.save(monitoringSystem);
        } else {
            MonitoringSystem monitoringSystem = msOp.get();
            monitoringSystem.setLastCheckedIn(LocalDateTime.now());
            modelRepo.save(monitoringSystem);
        }
    }
}
