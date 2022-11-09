package org.opennms.horizon.inventory.service;

import lombok.RequiredArgsConstructor;
import org.opennms.horizon.inventory.dto.MonitoringSystemDTO;
import org.opennms.horizon.inventory.mapper.MonitoringSystemMapper;
import org.opennms.horizon.inventory.model.MonitoringLocation;
import org.opennms.horizon.inventory.model.MonitoringSystem;
import org.opennms.horizon.inventory.repository.MonitoringLocationRepository;
import org.opennms.horizon.inventory.repository.MonitoringSystemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitoringSystemService {
    private final MonitoringSystemRepository modelRepo;
    private final MonitoringLocationRepository monitoringLocationRepository;

    private final MonitoringSystemMapper mapper;

    public MonitoringSystemDTO saveMonitoringSystem(MonitoringSystemDTO dto) {
        MonitoringSystem model = mapper.dtoToModel(dto);

        MonitoringLocation location = monitoringLocationRepository.getReferenceById(dto.getMonitoringLocationId());
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
}
